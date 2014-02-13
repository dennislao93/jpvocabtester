
/**
 * A question
 */
public class Question
{
    private Term term;
    private String[] problems;
    private String[] problemsSupp;
    private String[][] correctAnswers;
    private boolean conjPart;
    private int numParts;
    boolean questionType1;

    public Question(Term term, TestType testType, boolean isKanaTest)
    {
        if (!isKanaTest) {
            this.term = term;
            numParts = 0;
            questionType1 = false;
            conjPart = false;

            int randSwitch = (int)(Math.random() * 6);
            if (randSwitch < 3) {
                questionType1 = true;
                numParts = 0;
            } else {
                numParts = 1;
            }
            randSwitch = (int)(Math.random() * 3);
            if ((randSwitch == 0 || testType == TestType.HEAVYCONJ) && testType != TestType.NOCONJ && !term.getWordType().equals(""))
            {
                conjPart = true;
                numParts++;
            }

            problems = new String[numParts + 1];
            problemsSupp = new String[numParts + 1];
            correctAnswers = new String[numParts + 1][];

            assignProblemsAndAnswers(questionType1, conjPart);
        } else {
            this.term = term;
            numParts = 1;
            problems = new String[2];
            problemsSupp = new String[2];
            correctAnswers = new String[2][];
            
            assignKanaTestProblemAndAnswer();
        }
    }

    public int getNumParts()
    {
        return numParts;
    }

    public boolean hasConjPart()
    {
        return conjPart;
    }

    public boolean isQuestionType1()
    {
        return questionType1;
    }

    public String getProblem(int questionPart)
    {
        return problems[questionPart];
    }

    public String getProblemSupp(int questionPart)
    {
        return problemsSupp[questionPart];
    }

    public boolean processAnswer(String input, int questionPart)
    {
        for (String correctAnswer: correctAnswers[questionPart]) {
            if (input.trim().equalsIgnoreCase(correctAnswer.trim()) ||
            StringModifier.clipStringArticle(input).trim().equalsIgnoreCase(StringModifier.clipStringArticle(correctAnswer).trim()) ||
            StringModifier.clipStringSpaces(input).trim().equalsIgnoreCase(StringModifier.clipStringSpaces(correctAnswer).trim()) ||
            StringModifier.clipStringPunctuation(input).trim().equalsIgnoreCase(StringModifier.clipStringPunctuation(correctAnswer).trim())
            ) {
                return true;
            }
        }
        return false;
    }

    public String[] getAnswers(int questionPart)
    {
        return correctAnswers[questionPart];
    }

    public String getFileTerm()
    {
        return term.getFileTerm();
    }

    public void assignConjDefinition(String conjDefinition)
    {
        problemsSupp[numParts] = term.getConjExpression(conjDefinition) + " (" + term.getConjDescription() + ")";
    }

    public String getConjDescription()
    {
        return term.getConjDescription();
    }

    public void assignKanaTestProblemAndAnswer()
    {
        problems[0] = "Enter the romaji of the given kana:";
        correctAnswers[0] = term.getRomaji();
        problemsSupp[0] = term.getVocabulary();
        
        problems[1] = "Of the following choices, enter the letter that describes this character: h (hiragana), k (katakana).";
        correctAnswers[1] = new String[]{"" + term.getDefinitions()[0].charAt(0)};
        problemsSupp[1] = term.getVocabulary();
    }

    private void assignProblemsAndAnswers(boolean questionType1, boolean conjPart)
    {
        String[] definitions = term.getDefinitions();
        String[] romaji = term.getRomaji();

        if (questionType1) {
            problems[0] = "Enter the romaji of the phrase with the given definition:";
            correctAnswers[0] = romaji;
            problemsSupp[0] = "";
            for (int i = 0; i < definitions.length; i++) {
                problemsSupp[0] += definitions[i] + "; ";
            }
        } else {
            String vocabulary = term.getVocabulary();
            problems[0] = "Enter the romaji of the following term.";
            problemsSupp[0] = vocabulary;
            correctAnswers[0] = romaji;

            problems[1] = "Enter one meaning of the following term. Be sure to start with a \"to \" when defining verbs.";
            correctAnswers[1] = definitions;
            problemsSupp[1] = vocabulary;
        }

        if (conjPart) {
            term.conjugate();
            problems[numParts] = "Enter the romaji for the following expression.";
            problemsSupp[numParts] = term.getConjExpression() + " (" + term.getConjDescription() + ")";
            correctAnswers[numParts] = term.getConjAnswers();
        }
    }
}
