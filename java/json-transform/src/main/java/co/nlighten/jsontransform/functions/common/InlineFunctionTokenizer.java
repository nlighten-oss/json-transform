package co.nlighten.jsontransform.functions.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InlineFunctionTokenizer {

    public record InlineFunctionArgToken(String value, int index, int length) {
    }

    public record TokenizedInlineFunction(String name, int keyLength, List<InlineFunctionArgToken> args,
                                          InlineFunctionArgToken input) {
    }

    private static final Map<Character, String> JSON_ESCAPE_CHARACTERS = Map.of(
            'b', "\b",
            'f', "\f",
            'n', "\n",
            'r', "\r",
            't', "\t",
            'v', "\u000B" // vertical tab
    );

    public static TokenizedInlineFunction tokenize(String input) {
        if (input == null || !input.startsWith("$$")) {
            return null;
        }

        int i = 2;
        int len = input.length();

        int nameStart = i;
        while (i < len && Character.toString(input.charAt(i)).matches("[\\w-]")) {
            i++;
        }
        String functionName = input.substring(nameStart, i);
        if (functionName.isEmpty()) {
            return null; // syntax error, no function name found
        }

        int keyLength = i;

        List<InlineFunctionArgToken> args = null;

        if (i < len && input.charAt(i) == '(') {
            i++;
            args = new ArrayList<>();
            StringBuilder current = new StringBuilder();
            int currentStart = i;
            boolean inQuote = false;
            boolean escape = false;
            boolean finished = false;

            for (; i < len; i++) {
                char ch = input.charAt(i);

                if (escape) {
                    String escapedChar = JSON_ESCAPE_CHARACTERS.get(ch);
                    if (escapedChar == null) {
                        if (ch == 'u' || ch == 'x') {
                            try {
                                int width = ch == 'u' ? 4 : 2;
                                String hex = input.substring(i + 1, i + 1 + width);
                                int codePoint = Integer.parseInt(hex, 16);
                                escapedChar = new String(Character.toChars(codePoint));
                                i += width;
                            } catch (Exception e) {
                                return null;
                            }
                        } else {
                            escapedChar = String.valueOf(ch);
                        }
                    }
                    current.append(escapedChar);
                    escape = false;
                    continue;
                }

                if (ch == '\\' && inQuote) {
                    escape = true;
                    continue;
                }

                if (ch == '\'') {
                    if (inQuote) {
                        while (i + 1 < len && input.charAt(i + 1) == ' ') {
                            i++;
                        }
                    } else if (current.toString().trim().isEmpty()) {
                        current.setLength(0);
                    }
                    inQuote = !inQuote;
                    continue;
                }

                if (!inQuote && (ch == ',' || ch == ')')) {
                    int length = i - currentStart;
                    args.add(new InlineFunctionArgToken(length > 0 ? current.toString() : null, currentStart, length));
                    current.setLength(0);
                    currentStart = i + 1;
                    if (ch == ')') {
                        i++;
                        finished = true;
                        break;
                    }
                } else {
                    current.append(ch);
                }
            }

            if (!finished || (i < len && input.charAt(i) != ':')) {
                return null;
            }
        }

        InlineFunctionArgToken fnInput = null;
        if (i < len && input.charAt(i) == ':') {
            i++;
            String inputValue = input.substring(i);
            fnInput = new InlineFunctionArgToken(inputValue, i, input.length() - i);
        }

        return new TokenizedInlineFunction(functionName, keyLength, args, fnInput);
    }
}
