import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("$ ");
            String input = scanner.nextLine();
            if (input == null) break;
            input = input.trim();
            if (input.isEmpty()) continue;

            String[] words = input.split("\\s+");
            String command = words[0];
            String[] rest = Arrays.copyOfRange(words, 1, words.length);
            String result = String.join(" ", rest);

            // BUILTINS
            if (Objects.equals(command, "exit")) {
                break;
            } else if (Objects.equals(command, "echo")) {
                System.out.println(result);
                continue;
            } else if (Objects.equals(command, "type")) {
                System.out.println(type(result));
                continue;
            } 
            // EXTERNAL COMMANDS (PATH lookup)
            else {
                String pathEnv = System.getenv("PATH");
                if (pathEnv == null || pathEnv.isEmpty()) {
                    System.out.println(input + ": command not found");
                    continue;
                }

                String[] pathDirs = pathEnv.split(":");
                boolean found = false;

                for (String dir : pathDirs) {
                    if (dir == null || dir.isEmpty()) continue;

                    File file = new File(dir.trim(), command);
                    if (file.exists() && file.canExecute()) {
                        found = true;

                        // Build command array where first element is absolute path
                        String[] cmdArray = new String[words.length];
                        cmdArray[0] = file.getAbsolutePath();
                        if (words.length > 1) {
                            System.arraycopy(words, 1, cmdArray, 1, words.length - 1);
                        }

                        Process process = Runtime.getRuntime().exec(cmdArray);
                        process.getInputStream().transferTo(System.out);
                        process.getErrorStream().transferTo(System.err);
                        process.waitFor();
                        break;
                    }
                }

                if (!found) {
                    System.out.println(input + ": command not found");
                }
            }
        }

        scanner.close();
    }

    public static String type(String command) {
        if (command == null || command.isEmpty()) return ": not found";

        String[] builtins = { "exit", "echo", "type" };
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
            if (dir == null || dir.isEmpty()) continue;
            File file = new File(dir.trim(), command);
            if (file.exists() && file.canExecute()) {
                return command + " is " + file.getAbsolutePath();
            }
        }

        return command + ": not found";
    }
}
