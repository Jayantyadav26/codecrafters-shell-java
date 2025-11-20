import java.util.ArrayList;
import java.util.List;

public class Tokenize {
    public static String[] tokenize(String input) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();

        boolean inSingleQuotes = false;
        boolean indoubleQuotes = false;
        boolean escapeNext = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (c == '\'' && !indoubleQuotes) {
                inSingleQuotes = !inSingleQuotes;
                continue;
            }
            if(c == '"' && !inSingleQuotes) {
                indoubleQuotes = !indoubleQuotes;
                continue;
            }
             if (escapeNext) {
                // add character as-is
                currentToken.append(c);
                escapeNext = false;
                continue;
            }

            // Set escape flag
            if (c == '\\' && !inSingleQuotes) {
                escapeNext = true;
                continue;
            }


            if (!inSingleQuotes && !indoubleQuotes && Character.isWhitespace(c)) {
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0); // empty stringbuilder
                }
            } else {
                currentToken.append(c);
            }
        }

        if (currentToken.length() > 0) {
            tokens.add(currentToken.toString());
        }

        return tokens.toArray(new String[0]);
    }

}
