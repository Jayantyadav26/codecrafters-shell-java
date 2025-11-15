import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    static final String INITIAL_DIR = System.getProperty("user.dir");
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("$ ");
            String input = scanner.nextLine();

            String[] words = input.split(" ");
            String command = words[0];
            String[] rest = Arrays.copyOfRange(words, 1, words.length);

            String result = String.join(" ", rest);

            if (Objects.equals(command, "exit")) {
                break;
            } else if (Objects.equals(command, "echo")) {
                System.out.println(result);
            } else if (command.equals("type")) {
                System.out.println(type(result));
            } else if (command.equals("pwd")) {
                System.out.println(currDirectory());
            } else if (command.equals("cd")) {
                if (rest[0].equals("~")) {
                    System.setProperty("user.dir", INITIAL_DIR);
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
                            executed = true;
                        }
                    }
                }
                if (!executed)
                    System.out.println(input + ": command not found");
            }
        }

        scanner.close();
    }

    public static String type(String command) {
        String[] builtins = { "exit", "echo", "type", "pwd", "cd" };
        for (String b : builtins) {
            if (Objects.equals(b, command)) {
                return command + " is a shell builtin";
            }
        }

        String pathEnv = System.getenv("PATH");
        if (pathEnv == null || pathEnv.isEmpty()) {
            return command + ": not found";
        }

        String[] pathDirs = pathEnv.split(":");

        for (String dir : pathDirs) {
            if (dir == null || dir.isEmpty())
                continue;
            File file = new File(dir.trim(), command);
            if (file.exists() && file.canExecute()) {
                return command + " is " + file.getAbsolutePath();
            }
        }

        return command + ": not found";
    }

    public static File currDirectory() {
        return new File(System.getProperty("user.dir"));
    }

}