import java.io.File;
import java.util.*;

public class Main {
    static final String INITIAL_DIR = System.getProperty("user.dir");

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("$ ");
            String input = scanner.nextLine();
            if (input.trim().isEmpty())
                continue;

            // *** NEW: proper tokenizer ***
            String[] words = Tokenize.tokenize(input);

            if (words.length == 0)
                continue;
            String command = words[0];
            String[] rest = Arrays.copyOfRange(words, 1, words.length);
            String result = String.join(" ", rest);
            if (Objects.equals(command, "exit")) {
                break;
            } else if (Objects.equals(command, "echo")) {
                System.out.println(result);
            } else if (command.equals("type")) {
                System.out.println(Type.type(result));
            } else if (command.equals("pwd")) {
                System.out.println(currDirectory());
            } else if (command.equals("cd")) {
                if (rest[0].equals("~")) {
                    String home = System.getenv("HOME");
                    if (home == null) {
                        home = INITIAL_DIR; // fallback (rare)
                    }
                    System.setProperty("user.dir", new File(home).getAbsolutePath());
                    continue;
                } else if (rest[0].charAt(0) == '/') {
                    // root directory or absolute path....
                    File newDirectory = new File(rest[0]);
                    if (newDirectory.exists() && newDirectory.isDirectory()) {
                        System.setProperty("user.dir", newDirectory.getAbsolutePath());
                    } else {
                        System.out.println("cd: " + rest[0] + ": No such file or directory");
                    }
                } else if (rest[0].substring(0, 2).equals("./")) {
                    // in current directory
                    File currentDirectory = currDirectory();
                    File newDirectory = new File(currentDirectory, rest[0].substring(2));
                    if (newDirectory.exists() && newDirectory.isDirectory()) {
                        System.setProperty("user.dir", newDirectory.getAbsolutePath());
                    } else {
                        System.out.println("cd: " + rest[0] + ": No such file or directory");
                    }
                } else if (rest[0].substring(0, 2).equals("..") && rest[0].substring(0, 3).equals("../")) {
                    // parent directory
                    File currentDirectory = currDirectory();
                    File newDirectory;

                    try {
                        // Use canonical resolution for paths like ../../../
                        newDirectory = new File(currentDirectory, rest[0]).getCanonicalFile();

                        if (newDirectory.exists() && newDirectory.isDirectory()) {
                            System.setProperty("user.dir", newDirectory.getAbsolutePath());
                        } else {
                            System.out.println("cd: " + rest[0] + ": No such file or directory");
                        }

                    } catch (Exception e) {
                        System.out.println("cd: " + rest[0] + ": No such file or directory");
                    }
                }
            } else {
                boolean executed = false;
                String pathEnv = System.getenv("PATH");
                if (pathEnv == null || pathEnv.isEmpty()) {
                    System.out.println(input + ": command not found");
                    continue;
                } else if (pathEnv != null && !pathEnv.isEmpty()) {
                    String[] pathDirs = pathEnv.split(":");

                    for (String dir : pathDirs) {
                        if (dir == null || dir.isEmpty())
                            continue;

                        File file = new File(dir.trim(), command);
                        if (file.exists() && file.canExecute()) {
                            Process process = Runtime.getRuntime().exec(words);

                            process.getInputStream().transferTo(System.out);
                            process.waitFor(); // wait until fully done

                            executed = true;
                            break;
                        }
                    }
                }
                if (!executed) {
                    System.out.println(input + ": command not found");
                } else {
                    // IMPORTANT: print the prompt after command output
                    System.out.flush();
                }
            }
        }

        scanner.close();
    }

   

    public static File currDirectory() {
        return new File(System.getProperty("user.dir"));
    }

    
}