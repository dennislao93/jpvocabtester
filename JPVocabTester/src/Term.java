
/**
 * Stores a term, its romaji, definition, and methods associated with it
 */
public class Term
{
    private String vocabulary;
    private String[] romaji;
    private String[] definitions;
    private String wordType;
    private String wordTypeOriginal;
    private String fileTerm;
    private Conjugator conjugator;
    private String conjExpression;
    public Term(String vocabulary, String[] romaji, String[] definitions, String wordType, String fileTerm)
    {
        this.vocabulary = vocabulary;
        this.romaji = romaji;
        this.definitions = definitions;
        this.wordType = makeWordType(wordType);
        this.wordTypeOriginal = wordType;
        this.fileTerm = fileTerm;
    }

    public String getVocabulary()
    {
        return vocabulary;
    }

    public String[] getRomaji()
    {
        return romaji;
    }

    public String[] getDefinitions()
    {
        return definitions;
    }
    
    public String getWordType() {
        return wordType;
    }
    
    public String getFileTerm()
    {
        return fileTerm;
    }
    
    public void conjugate()
    {
        conjugator = new Conjugator(definitions[0], romaji, wordType, wordTypeOriginal);
        conjExpression = conjugator.getExpression();
        conjugator.getAnswers();
    }
    
    public String getConjExpression()
    {
        return conjExpression;
    }
    
    public String getConjExpression(String conjDefinition)
    {
        return conjugator.getExpression(conjDefinition);
    }
    
    public String getConjDescription()
    {
        return conjugator.getConjDescription();
    }
    
    public String[] getConjAnswers()
    {
        return conjugator.getAnswers();
    }
    
    private String makeWordType(String type)
    {
        String wordType = "";
        {
            //wordTypes:
            //state of being
            //adjective -na
            //ii
            //adjective -i
            //verb -ru
            //-iku
            //verb -u
            //-suru
            //-kuru
            for (int i = 1; i < type.length(); i++) {
                if (type.substring(i, i + 1).equals(",")) {
                    type = type.substring(0, i);
                }
            }

            {
                if (vocabulary.charAt(0) == '~') {
                    return "";
                } else if (type.equals("n") || type.indexOf("n-") != -1 || type.equals("pn")) {
                    return "state of being";
                } else if (type.equals("adj-na")) {
                    return "adjective -na";
                } else if (type.equals("adj-i")) {
                    if (romaji.length == 2 && romaji[0].equals("ii") && romaji[1].equals("yoi")) {
                        return "ii";
                    } else {
                        return "adjective -i";
                    }
                } else if (type.equals("v1")) {
                    return "verb -ru";
                } else if (type.indexOf("v5") != -1 && !wordType.equals("v5aru")) {
                    if (wordType.equals("v5k-s")) {
                        return "-iku";
                    } else {
                        return "verb -u";
                    }
                } else if (type.indexOf("vs") != -1) {
                    return "-suru";
                } else if (type.equals("vk")) {
                    return "-kuru";
                } else {
                    return "";
                }
            }
        }
    }
}
