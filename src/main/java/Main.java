import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws Exception {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("$ ");
                String input = scanner.nextLine().trim();
                String parts[] = tokenize(input);
                switch (parts[0]) {
                    case Constant.EXIT -> processExit(parts);
                    case Constant.ECHO -> processEcho(parts);
                    case Constant.TYPE -> processType(parts);
                    case Constant.PWD -> processPwd(parts);
                    case Constant.CD -> processCd(parts);

                    default -> processExternal(parts);
                }
            }

        } catch (Exception e) {
         
        }
    }

    private static void processCd(String[] parts) {
        if (parts.length < 2) {
            return;
        }

        String target = parts[1];
        java.io.File newDir;

        if (target.equals("~")) {
            String home = System.getenv("HOME");
            if (home == null) {
                System.out.println("cd: HOME not set");
                return;
            }
            newDir = new java.io.File(home);
        } else if (target.startsWith("/")) {
            // Absolute path
            newDir = new java.io.File(target);
        } else {
            // Relative path
            newDir = new java.io.File(currentDirectory, target);
        }

        try {
            newDir = newDir.getCanonicalFile();
            if (newDir.exists() && newDir.isDirectory()) {
                currentDirectory = newDir;
            } else {
                System.out.println("cd: " + target + ": No such file or directory");
            }
        } catch (Exception e) {
            System.out.println("cd: " + target + ": No such file or directory");
        }
    }

    private static void processPwd(String[] parts) {
        System.out.println(currentDirectory.getAbsolutePath());
    }

    private static void processExternal(String[] parts) {
        try {
            if (parts.length == 0) return;

            ProcessBuilder pb = new ProcessBuilder(parts);
            pb.directory(currentDirectory); // respect cd
            pb.inheritIO(); // forward output/error
            Process process = pb.start();
            process.waitFor();
        } catch (Exception e) {
            System.out.println(parts[0] + ": command not found");
        }
    }

    private static void processType(String[] parts) {
        for (int a = 1; a < parts.length; a++) {
            String input = parts[a];
            if (builtin.contains(input)) {
                System.out.println(input + " is a shell builtin");
                continue;
            }
            String pathEnv = System.getenv("PATH");
            if (pathEnv != null) {
                String[] paths = pathEnv.split(java.io.File.pathSeparator);
                boolean found = false;

                for (String dirPath : paths) {
                    java.io.File candidate = new java.io.File(dirPath, input);
                    if (candidate.exists()) {
                        if (candidate.canExecute()) {
                            System.out.println(input + " is " + candidate.getAbsolutePath());
                            found = true;
                            break; // stop after first match
                        }
                    }
                }

                if (!found) {
                    System.out.println(input + ": not found");
                }
            } else {
                System.out.println(input + ": not found");
            }
        }
    }

    private static void processEcho(String[] parts) {
        if (parts.length > 1) {
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < parts.length; i++) {
                if (i > 1) sb.append(" ");
                sb.append(parts[i]);
            }
            System.out.println(sb.toString());
        } else {
            System.out.println();
        }
    }

    private static void processExit(String[] parts) {
        int status = 0;
        boolean correctCommand = true;
        if (parts.length > 1 && parts[1].length() > 1) {
            try {
                status = Integer.parseInt(parts[1]);
            } catch (Exception e) {
                correctCommand = false;
            }
        }
        if (correctCommand) {
            System.exit(status);
        }
    }

    private static java.io.File currentDirectory = new java.io.File(System.getProperty("user.dir"));

    private static String[] tokenize(String input) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (c == '\'' && !inDoubleQuote) {
                inSingleQuote = !inSingleQuote;
                continue;
            } else if (c == '"' && !inSingleQuote) {
                inDoubleQuote = !inDoubleQuote;
                continue;
            } else if (c == '\\') {
                // Backslash handling varies by context
                if (i + 1 < input.length()) {
                    char next = input.charAt(i + 1);
                    if (inSingleQuote) {
                        // Single quotes: backslash is literal
                        current.append('\\').append(next);
                    } else if (inDoubleQuote) {
                        // Double quotes: preserve backslash + next char literally
                        if(next =='"' || next=='\\' || next == '$' || next=='`'){
                            current.append(next);
                        }
                    } else {
                        // Outside quotes: escape next char (do NOT include the backslash)
                        current.append(next);
                    }
                    i++; // consume next char
                } else {
                    // Trailing backslash
                    current.append(inSingleQuote || inDoubleQuote ? '\\' : '\\');
                }
                continue;
            } else if (Character.isWhitespace(c) && !inSingleQuote && !inDoubleQuote) {
                if (current.length() > 0) {
                    tokens.add(current.toString());
                    current.setLength(0);
                }
                continue;
            } else {
                current.append(c);
            }
        }

        if (current.length() > 0) {
            tokens.add(current.toString());
        }

        return tokens.toArray(new String[0]);
    }

    private static final Set<String> builtin = new HashSet<String>();

    static {
        try {
            for (Field field : Constant.class.getDeclaredFields()) {
                if (field.getType().equals(String.class)) {
                    builtin.add((String) field.get(null));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
