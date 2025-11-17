import java.util.ArrayList;
import java.util.List;

public class Tokenize {
    public static String[] tokenize(String input) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();

        boolean inSingleQuotes = false;
        boolean indoubleQuotes = false;

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
