
/**
 * Checks if a term is a type (i.e. verb, adjective, or noun)
 */
public class TermFilter
{
    public static boolean match (String wordType, String typeWanted)
    {
        if (wordType.indexOf(",") != -1) {
            for (int i = 1; i < wordType.length(); i++) {
                if (wordType.charAt(i) == ',') {
                    wordType = wordType.substring(0, i);
                    break;
                }
            }
        }

        {
            if (wordType.equals("n") || wordType.equals("pn") || wordType.indexOf("n-") != -1) {
                if (typeWanted.equals("Nouns only")) {
                    return true;
                }
            } else if (wordType.equals("adj-na") || wordType.equals("adj-i")) {
                if (typeWanted.equals("Adjectives only")) {
                    return true;
                }
            } else if (wordType.equals("v1") || wordType.indexOf("v5") != -1 || wordType.indexOf("vs") != -1 || wordType.equals("vk")) {
                if (typeWanted.equals("Verbs only")) {
                    return true;
                }
            } else {
                if (typeWanted.equals("Everything else")) {
                    return true;
                }
            }
            return false;
        }
    }
}
