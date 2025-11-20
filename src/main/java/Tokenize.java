import java.util.ArrayList;
import java.util.List;

public class Tokenize {
    public static String[] tokenize(String input) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();

        boolean inSingleQuotes = false;
        boolean inDoubleQuotes = false;
        boolean escapeNext = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            // If the previous character was a backslash (outside single quotes),
            // we treat this character literally.
            if (escapeNext) {
                currentToken.append(c);
                escapeNext = false;
                continue;
            }

            // Backslash escape outside single quotes
            if (c == '\\' && !inSingleQuotes) {
                // Enable escape for next character
                escapeNext = true;
                continue;
            }

            // Handle single quotes (only toggle when not in double quotes)
            if (c == '\'' && !inDoubleQuotes) {
                inSingleQuotes = !inSingleQuotes;
                continue;
            }

            // Handle double quotes (only toggle when not in single quotes)
            if (c == '"' && !inSingleQuotes) {
                inDoubleQuotes = !inDoubleQuotes;
                continue;
            }

            // Whitespace splits tokens ONLY when not in quotes
            if (!inSingleQuotes && !inDoubleQuotes && Character.isWhitespace(c)) {
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0);
                }
                continue;
            }

            // Normal character
            currentToken.append(c);
        }

        // Final token
        if (currentToken.length() > 0) {
            tokens.add(currentToken.toString());
        }

        return tokens.toArray(new String[0]);
    }
}
