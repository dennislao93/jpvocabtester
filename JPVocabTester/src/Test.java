import java.util.ArrayList;

/**
 * A test
 */
public class Test
{
    private Question[] questions;
    private Question currentQuestion;
    private TestType testType;

    private boolean isProblemTerm;
    private ArrayList<String> problemTerms;
    private ArrayList<ArrayList<String>> problemInputs;

    private int rightVocabPoints;
    private int wrongVocabPoints;
    private int rightConjPoints;
    private int wrongConjPoints;

    public Test(ArrayList<String> fileTerms, ArrayList<String> wordTypes, TestType testType, int numberOfQuestions, boolean isKanaTest)
    {
        this.testType = testType;

        Term[] terms = new Term[fileTerms.size()];
        for (int i = 0; i < terms.length; i++) {
            terms[i] = addTerm(fileTerms.get(i), wordTypes.get(i));
        }

        for (int i = 0; i < terms.length; i++) {
            Term temp = terms[i];
            int randomPosition = (int)(Math.random() * terms.length);
            terms[i] = terms[randomPosition];
            terms[randomPosition] = temp;
        }

        if (numberOfQuestions < terms.length) {
            Term[] reducedTerms = new Term[numberOfQuestions];
            for (int i = 0; i < numberOfQuestions; i++) {
                reducedTerms[i] = terms[i];
            }
            terms = reducedTerms;
        }

        rightVocabPoints = 0;
        wrongVocabPoints = 0;
        rightConjPoints = 0;
        wrongConjPoints = 0;

        isProblemTerm = false;
        problemTerms = new ArrayList<String>();
        problemInputs = new ArrayList<ArrayList<String>>();

        makeQuestions(terms, isKanaTest);
    }

    public void setCurrentQuestion(int i)
    {
        currentQuestion = questions[i];
    }

    public int getQuestionParts()
    {
        return currentQuestion.getNumParts();
    }

    public String ask1(int questionPart)
    {
        return currentQuestion.getProblem(questionPart);
    }

    public String ask2(int questionPart)
    {
        return currentQuestion.getProblemSupp(questionPart);
    }

    public boolean evaluate(String input, int questionPart)
    {
        if (questionPart == 0) {
            isProblemTerm = false;
        }

        if (currentQuestion.processAnswer(input, questionPart)) {
            if (questionPart != currentQuestion.getNumParts() || !currentQuestion.hasConjPart()) {
                rightVocabPoints++;
            } else {
                rightConjPoints++;
            }
            return true;
        } else {
            if (questionPart != currentQuestion.getNumParts() || !currentQuestion.hasConjPart()) {
                wrongVocabPoints++;
            } else {
                wrongConjPoints++;
            }

            if (isProblemTerm == false) {
                isProblemTerm = true;
                problemTerms.add(currentQuestion.getFileTerm());
                ArrayList<String> thisArrayList = new ArrayList<String>();
                thisArrayList.add(input);
                problemInputs.add(thisArrayList);
            } else {
                ArrayList<String> tempArrayList = problemInputs.get(problemInputs.size() - 1);
                tempArrayList.add(input);
                problemInputs.set(problemInputs.size() - 1, tempArrayList);
            }

            return false;
        }
    }

    public String[] revealAnswers(int questionPart)
    {
        return currentQuestion.getAnswers(questionPart);
    }

    public String revealFileTerm()
    {
        return currentQuestion.getFileTerm();
    }

    public int[] getScore()
    {
        return new int[]{rightVocabPoints, wrongVocabPoints, rightConjPoints, wrongConjPoints};
    }

    public ArrayList<String> getProblemTerms()
    {
        return problemTerms;
    }
    
    public ArrayList<ArrayList<String>> getProblemInputs()
    {
        return problemInputs;
    }

    public boolean hasConjPart()
    {
        return currentQuestion.hasConjPart();
    }

    public boolean isQuestionType1()
    {
        return currentQuestion.isQuestionType1();
    }

    public void assignConjDefinition(String conjDefinition)
    {
        currentQuestion.assignConjDefinition(conjDefinition);
    }

    private Term addTerm(String fileTerm, String wordType)
    {
        Term term;
        String fileTermCopy = fileTerm;

        String vocabulary = new String();
        ArrayList<String> romajiList = new ArrayList<String>();
        ArrayList<String> definitionsList = new ArrayList<String>();

        for (int i = 0; i < fileTermCopy.length(); i++) {
            if (fileTermCopy.charAt(i) == '[') {
                vocabulary = fileTermCopy.substring(0, i);
                fileTermCopy = fileTermCopy.substring(i + 1, fileTermCopy.length());
                break;
            }
        }
        for (int i = 0; i < fileTermCopy.length(); i++) {
            if (fileTermCopy.charAt(i) == ',') {
                romajiList.add(fileTermCopy.substring(0, i).trim());
                fileTermCopy = fileTermCopy.substring(i + 1, fileTermCopy.length());
                i = 0;
            } else if (fileTermCopy.charAt(i) == ']') {
                romajiList.add(fileTermCopy.substring(0, i).trim());
                fileTermCopy = fileTermCopy.substring(i + 2, fileTermCopy.length());
                break;
            }
        }
        fileTermCopy = StringModifier.clipStringParentheses(fileTermCopy);
        for (int i = 0; i < fileTermCopy.length(); i++) {
            if (fileTermCopy.charAt(i) == ';') {
                definitionsList.add(fileTermCopy.substring(0, i).trim());
                fileTermCopy = fileTermCopy.substring(i + 1, fileTermCopy.length());
                i = 0;
            }
        }

        String[] romaji = romajiList.toArray(new String[romajiList.size()]);
        String[] definitions = definitionsList.toArray(new String[definitionsList.size()]);

        term = new Term(vocabulary, romaji, definitions, wordType, fileTerm);

        return term;
    }

    private void makeQuestions(Term[] terms, boolean isKanaTest)
    {
        questions = new Question[terms.length];
        Question temp;
        for (int i = 0; i < terms.length; i++) {
            temp = new Question(terms[i], testType, isKanaTest);
            questions[i] = temp;
        }
    }
}
