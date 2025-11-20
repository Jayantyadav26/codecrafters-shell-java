import java.util.ArrayList;
import java.util.List;

public class Tokenize {

    private static boolean isOctalDigit(char c) {
        return c >= '0' && c <= '7';
    }

    public static String[] tokenize(String input) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        boolean inSingle = false;
        boolean inDouble = false;
        boolean escape = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            // If previous char was backslash
            if (escape) {
                if (inDouble) {
                    // process escapes inside double quotes
                    switch (c) {
                        case 'n': current.append('\n'); break;
                        case 't': current.append('\t'); break;
                        case 'r': current.append('\r'); break;
                        case '"': current.append('"'); break;
                        case '\\': current.append('\\'); break;

                        default:
                            // handle octal escapes
                            if (isOctalDigit(c)) {
                                int j = i;
                                StringBuilder oct = new StringBuilder();
                                oct.append(c);

                                if (j + 1 < input.length() && isOctalDigit(input.charAt(j + 1))) {
                                    oct.append(input.charAt(++j));
                                }
                                if (j + 1 < input.length() && isOctalDigit(input.charAt(j + 1))) {
                                    oct.append(input.charAt(++j));
                                }

                                i = j;
                                current.append((char) Integer.parseInt(oct.toString(), 8));
                            } else {
                                // literal if unknown escape
                                current.append(c);
                            }
                            break;
                    }
                } else {
                    // outside double quotes: just literal
                    current.append(c);
                }
                escape = false;
                continue;
            }

            // Backslash escape (except in single quotes)
            if (c == '\\' && !inSingle) {
                escape = true;
                continue;
            }

            // Single quotes toggle only when not in double quotes
            if (c == '\'' && !inDouble) {
                inSingle = !inSingle;
                continue;
            }

            // Double quotes toggle only when not in single quotes
            if (c == '"' && !inSingle) {
                inDouble = !inDouble;
                continue;
            }

            // Whitespace ends token only when not in quotes
            if (!inSingle && !inDouble && Character.isWhitespace(c)) {
                if (current.length() > 0) {
                    tokens.add(current.toString());
                    current.setLength(0);
                }
                continue;
            }

            current.append(c);
        }

        if (current.length() > 0)
            tokens.add(current.toString());

        return tokens.toArray(new String[0]);
    }
}
