
/**
 * Clips unnecessary parts of a string
 * Types: parentheses, article, punctuation, spaces, to
 */
public class StringModifier
{
    public static String clipStringParentheses(String str)
    {
        int count = 0;
        int startIndex = 0;
        int endIndex = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '(') {
                if (count == 0) {
                    startIndex = i;
                }
                count++;
            } else if (str.charAt(i) == ')') {
                count--;
                if (count == 0) {
                    endIndex = i;
                    if (startIndex > 0 && str.charAt(startIndex - 1) == ' ' && i < str.length() - 1 &&  str.charAt(i + 1) == ' ') {
                        str = str.substring(0, startIndex) + str.substring(endIndex + 2, str.length());
                        i = startIndex - 1;
                    } else {
                        str = str.substring(0, startIndex) + str.substring(endIndex + 1, str.length());
                        i = startIndex - 1;
                    }

                }
            }
        }
        return str;
    }

    public static String clipStringArticle(String str)
    {
        for (int i = 0; i < str.length(); i++) {
            if (str.substring(0, i).equalsIgnoreCase("a ") || str.substring(0, i).equalsIgnoreCase("the ") || str.substring(0, i).equalsIgnoreCase("an ")) {
                str = str.substring(i, str.length());
                return str;
            }
        }
        return str;
    }

    public static String clipStringPunctuation(String str)
    {
        for (int i = 0; i < str.length(); i++) {
            if (!str.substring(i, i + 1).matches("[a-zA-Z]|\\s")) {
                if (str.substring(i, i + 1).equals("-")) {
                    str = str.substring(0, i) + " " + str.substring(i + 1, str.length());
                    str = str.trim();
                } else {
                    str = str.substring(0, i) + str.substring(i + 1, str.length());
                    i--;
                }
            }
        }
        return str;
    }

    public static String clipStringSpaces(String str)
    {
        for (int i = 0; i < str.length(); i++) {
            if (str.substring(i, i + 1).matches("\\s")) {
                str = str.substring(0, i) + str.substring(i + 1, str.length());
                i--;
            }
        }
        return str;
    }

    public static String clipStringTo(String str)
    {
        if (str.substring(0, 3).equalsIgnoreCase("to ")) {
            str = str.substring(3, str.length());
            return str;
        }
        return str;
    }

    public static String[] clipStringFirstWord(String str)
    {
        String firstWord = "";
        String secondWord = "";
        for (int i = 0; i < str.length(); i++) {
            if (i == str.length() - 1 || str.charAt(i + 1) == ' ') {
                firstWord = str.substring(0, i + 1);
                if (firstWord.length() != str.length()) {
                    secondWord = str.substring(i + 1, str.length());
                    break;
                }
                break;
            }
        }

        return new String[]{firstWord, secondWord};
    }
}