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
                if (rest.length == 0) continue;

                handleCd(rest[0]);
                continue;

            } else {
                boolean executed = false;
                String pathEnv = System.getenv("PATH");

                if (pathEnv != null && !pathEnv.isEmpty()) {
                    String[] dirs = pathEnv.split(":");

                    for (String dir : dirs) {
                        if (dir.isEmpty())
                            continue;
                        File file = new File(dir.trim(), command);

                        if (file.exists() && file.canExecute()) {
                            Process proc = Runtime.getRuntime().exec(words);
                            proc.getInputStream().transferTo(System.out);
                            proc.waitFor();
                            executed = true;
                            break;
                        }
                    }
                }

                if (!executed)
                    System.out.println(input + ": command not found");

                System.out.flush();
            }
        }

        scanner.close();
    }

    public static void handleCd(String path) {
        try {
            if (path.equals("~")) {
                String home = System.getenv("HOME");
                if (home == null) home = INITIAL_DIR;
                System.setProperty("user.dir", new File(home).getAbsolutePath());
                return;
            }

            File current = currDirectory();
            File newDir;

            if (path.startsWith("/")) {
                newDir = new File(path);
            } else {
                newDir = new File(current, path).getCanonicalFile();
            }

            if (newDir.exists() && newDir.isDirectory()) {
                System.setProperty("user.dir", newDir.getAbsolutePath());
            } else {
                System.out.println("cd: " + path + ": No such file or directory");
            }
        } catch (Exception e) {
            System.out.println("cd: " + path + ": No such file or directory");
        }
    }

    public static File currDirectory() {
        return new File(System.getProperty("user.dir"));
    }
}
