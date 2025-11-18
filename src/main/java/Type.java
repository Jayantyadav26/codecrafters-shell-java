import java.io.File;
import java.util.Objects;

public class Type {
    //dsad
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
}
