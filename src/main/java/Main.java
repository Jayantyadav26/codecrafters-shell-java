import java.io.File;
import java.util.*;

public class Main {
    static final String INITIAL_DIR = System.getProperty("user.dir");

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        while (true) {

            // print prompt
            System.out.print("$ ");
            System.out.flush();

            String input = scanner.nextLine();
            if (input.trim().isEmpty())
                continue;

            String[] words = Tokenize.tokenize(input);
            if (words.length == 0)
                continue;

            String command = words[0];
            String[] rest = Arrays.copyOfRange(words, 1, words.length);
            String joinedArgs = String.join(" ", rest);

            if (command.equals("exit")) {
                break;
            }

            else if (command.equals("echo")) {
                System.out.println(joinedArgs);
                continue;
            }

            else if (command.equals("type")) {
                System.out.println(Type.type(joinedArgs));
                continue;
            }

            else if (command.equals("pwd")) {
                System.out.println(currDirectory().getAbsolutePath());
                continue;
            }

            else if (command.equals("cd")) {
                handleCd(rest);
                continue;
            }

            // --------------------------------------------
            // HANDLE EXTERNAL COMMANDS (cat, ls, etc.)
            // --------------------------------------------

            boolean executed = false;
            String pathEnv = System.getenv("PATH");

            if (pathEnv != null && !pathEnv.isEmpty()) {
                String[] pathDirs = pathEnv.split(":");

                for (String dir : pathDirs) {
                    if (dir == null || dir.isEmpty())
                        continue;

                    File file = new File(dir.trim(), command);

                    if (file.exists() && file.canExecute()) {

                        Process process = Runtime.getRuntime().exec(words);

                        // print process output
                        process.getInputStream().transferTo(System.out);
                        process.getErrorStream().transferTo(System.err);

                        process.waitFor();
                        System.out.flush();

                        executed = true;
                        break;
                    }
                }
            }

            if (!executed) {
                System.out.println(input + ": command not found");
                System.out.flush();
            }
        }

        scanner.close();
    }

    // ---------------------------
    // CD Handling
    // ---------------------------
    public static void handleCd(String[] args) {
        if (args.length == 0)
            return;

        String path = args[0];

        if (path.equals("~")) {
            String home = System.getenv("HOME");
            if (home == null) home = INITIAL_DIR;
            System.setProperty("user.dir", home);
            return;
        }

        File newDir;

        if (path.startsWith("/")) {
            newDir = new File(path);
        } else {
            newDir = new File(currDirectory(), path);
        }

        try {
            newDir = newDir.getCanonicalFile();
            if (newDir.exists() && newDir.isDirectory()) {
                System.setProperty("user.dir", newDir.getAbsolutePath());
            } else {
                System.out.println("cd: " + path + ": No such file or directory");
            }
        }
        catch (Exception e) {
            System.out.println("cd: " + path + ": No such file or directory");
        }
    }

    public static File currDirectory() {
        return new File(System.getProperty("user.dir"));
    }
}
