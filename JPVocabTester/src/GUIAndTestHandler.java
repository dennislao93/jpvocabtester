import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.net.URL;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;

import javax.imageio.ImageIO;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;

/**
 * Handles the test and displays for user
 * 
 * @author Dennis Lao
 * @version 2.1
 */
public class GUIAndTestHandler
{
	private static boolean offline = false;
	private static final boolean selfConfigure = false;
	private static final String config = "http://dl.dropbox.com/u/22967107/JPVocabTesterMaterials/config.txt";
	private static final String offlineConfig = "C:\\Users\\Dennis\\Dropbox\\Public\\JPVocabTesterMaterials\\config.txt";
	private static String mySite = "";
	private static String myEmail = "";
	private static String motto = "";
	private static String welcomeDynamicMessage = "";
	private static JTextArea aboutText = new JTextArea(); // accounted for in reset()

	private static boolean passed = false;

	private static final String VERSION = "2.2";
	private static final String IOEXCEPTION_MSG = "IOException: Java has encountered an input/output-related exception.";
	private static final String MISPLACED_FILE_DETAILS = "This may have been caused by a loss of internet connection, changes made to a file path, or an incompatible version of a file.";

	private static final int NORTH_WIDTH = 868;
	private static final int NORTH_HEIGHT = 90;
	private static final int SOUTH_HEIGHT = 64;
	private static final int EAST_WIDTH = 220;
	private static final int EAST_HEIGHT = 474;
	private static final int CENTER_WIDTH = 428;
	private static final int CENTER_HEIGHT = 474;

	private static String baseURL;

	private static ImageIcon imageCorrect;// accounted for in reset()
	private static ImageIcon imageWrong;// accounted for in reset()

	private static String[] goodComments;// accounted for in reset()
	private static String[] badComments;// accounted for in reset()

	private static JFrame frame;

	private static ArrayList<String> vocabGroupsConfigLines;// accounted for in reset()
	private static JCheckBox[] vocabGroups;// accounted for in reset()
	private static String[] vocabGroupLinks;// accounted for in reset()

	private static boolean isKanaTest;// accounted for in reset()
	private static String kanaTestLinksConfigLink;// accounted for in reset()

	private static String typeWanted;// accounted for in reset()

	private static JLabel message;// accounted for in reset()

	private static ArrayList<String> fileTerms;// accounted for in reset()
	private static ArrayList<String> wordTypes;// accounted for in reset()
	private static TestType testType;// accounted for in reset()
	private static int numberOfQuestions;// accounted for in reset()

	private static JTextField numberQuestionField;// accounted for in submitVocabGroups()
	private static int totalNumberOfQuestions;//accounted for in reset()

	private static Test test;// accounted for in reset()

	private static int currentQuestion;// accounted for in makeTestSettings() and makeKanaTestSettings()
	private static int questionPart;// accounted for in makeTestSettings() and makeKanaTestSettings()

	private static JButton next;// accounted for in reset()

	public static void main(String[] args)
	{
		if (args.length > 0 && args[0].equals("offline")) offline = true;
		frame = new JFrame("JP Vocab Tester");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(new Dimension(880, 640));
		frame.setJMenuBar(makeMenuBar(frame));
		reset();
	}

	private static void reset()
	{
		loadFiles();

		vocabGroups = null;
		vocabGroupLinks = null;

		typeWanted = "All types";

		message = new JLabel();

		fileTerms = new ArrayList<String>();
		wordTypes = new ArrayList<String>();
		testType = null;
		numberOfQuestions = 0;

		totalNumberOfQuestions = 0;

		test = null;

		aboutText.setOpaque(false);

		frame.setContentPane(makeWelcome());
		frame.setVisible(true);
	}

	private static void loadFiles()
	{
		String imageCorrectLink = "";
		String imageWrongLink = "";
		String goodCommentsLink = "";
		String badCommentsLink = "";

		vocabGroupsConfigLines = new ArrayList<String>();

		passed = true;
		try {
			Scanner scanner;
			if (!offline) {
				scanner = new Scanner(new URL(config).openStream());
			} else {
				scanner = new Scanner(new FileInputStream(new File(offlineConfig)));
			}

			mySite = scanner.nextLine();
			myEmail = scanner.nextLine();
			motto = scanner.nextLine();
			welcomeDynamicMessage = scanner.nextLine();
			welcomeDynamicMessage = welcomeDynamicMessage.replaceAll("%", "\n");

			String baseURLLine = scanner.nextLine();
			if (!offline) {
				baseURL = baseURLLine.split("\\|")[0];
			} else {
				baseURL = baseURLLine.split("\\|")[1];
			}

			imageCorrectLink = baseURL + scanner.nextLine();
			imageWrongLink = baseURL + scanner.nextLine();
			goodCommentsLink = baseURL + scanner.nextLine();
			badCommentsLink = baseURL + scanner.nextLine();
			kanaTestLinksConfigLink = baseURL + scanner.nextLine();

			scanner.nextLine();

			while (scanner.hasNextLine()) {
				vocabGroupsConfigLines.add(scanner.nextLine());
			}
			scanner.close();
		} catch (IOException e) {
			showException("The configuration file of JPVocabTester cannot be located.", IOEXCEPTION_MSG, MISPLACED_FILE_DETAILS);
			passed = false;
		}

		try {
			if (!offline) {
				imageCorrect = new ImageIcon(ImageIO.read(new URL(imageCorrectLink)));
				imageWrong = new ImageIcon(ImageIO.read(new URL(imageWrongLink)));
			} else {
				imageCorrect = new ImageIcon(ImageIO.read(new File(imageCorrectLink)));
				imageWrong = new ImageIcon(ImageIO.read(new File(imageWrongLink)));
			}
		} catch (IOException e) {
			showException("One or more image files cannot be located.", IOEXCEPTION_MSG, MISPLACED_FILE_DETAILS);
		}

		try {
			ArrayList<String> goodCommentsList = new ArrayList<String>();
			Scanner scanner;
			if (!offline) {
				scanner = new Scanner(new URL(goodCommentsLink).openStream());
			} else {
				scanner = new Scanner(new FileInputStream(new File(goodCommentsLink)));

			}
			while (scanner.hasNextLine())
			{
				goodCommentsList.add(scanner.nextLine());
			}
			goodComments = goodCommentsList.toArray(new String[goodCommentsList.size()]);

			ArrayList<String> badCommentsList = new ArrayList<String>();
			if (!offline) {
				scanner = new Scanner(new URL(badCommentsLink).openStream());
			} else {
				scanner = new Scanner(new FileInputStream(new File(badCommentsLink)));
			}
			while (scanner.hasNextLine())
			{
				badCommentsList.add(scanner.nextLine());
			}
			badComments = badCommentsList.toArray(new String[badCommentsList.size()]);
			scanner.close();
		} catch (IOException e) {
			showException("One or more comments files cannot be located.", IOEXCEPTION_MSG, MISPLACED_FILE_DETAILS);
		}
	}

	private static void showException(String exceptionImplications, String exceptionDetails, String exceptionReasons)
	{
		JOptionPane.showMessageDialog(frame, "<HTML>" + exceptionImplications + "<BR><BR>" + exceptionDetails + "<BR>" + exceptionReasons + "<BR>Please contact us for further details.</HTML>", "Error", JOptionPane.ERROR_MESSAGE);
	}

	private static void makeMessage(String messageString)
	{
		message.setText(messageString);
	}

	private static JPanel makeWelcome()
	{
		JPanel welcome = new JPanel();
		welcome.setLayout(new BorderLayout());
		welcome.setBorder(new EmptyBorder(6, 6, 6, 6));

		Box welcomeMessage = new Box(BoxLayout.Y_AXIS);
		welcome.add(welcomeMessage);

		JPanel select = new JPanel();
		JScrollPane selectScrollPane = new JScrollPane(select, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		welcome.add(selectScrollPane, BorderLayout.WEST);
		selectScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		select.setLayout(new FlowLayout());

		Box choices = Box.createVerticalBox();
		select.add(choices);
		select.add(new JLabel("    "));

		JPanel welcomeMessagePanel = new JPanel();
		welcomeMessage.add(welcomeMessagePanel);
		JLabel welcomeMessageLabel = new JLabel("<HTML>Welcome to JP Vocab Tester...<BR>" + motto + "<BR>Version " + VERSION + "</HTML>");
		welcomeMessagePanel.add(welcomeMessageLabel);
		welcomeMessage.add(Box.createVerticalStrut(42));
		welcomeMessageLabel.setFont(new Font(welcomeMessage.getFont().getName(), Font.PLAIN, 16));
		JTextArea dynamicMessage = new JTextArea(welcomeDynamicMessage);
		welcomeMessage.add(dynamicMessage);
		dynamicMessage.setFont(welcomeMessageLabel.getFont());
		dynamicMessage.setOpaque(false);

		JButton kanaTest = new JButton("Kana Practice (For Beginners)");
		choices.add(kanaTest);

		choices.add(Box.createVerticalStrut(20));

		choices.add(new JLabel("<HTML>Pick your term groups.<BR><BR>(Terms will be randomly chosen from selected groups)</HTML>"));

		choices.add(Box.createVerticalStrut(20));

		final ArrayList<String> checkBoxLabels = new ArrayList<String>();
		if (passed) {
			ArrayList<String> links = new ArrayList<String>();

			for (String vocabGroupsConfigLine: vocabGroupsConfigLines) {
				for (int i = 0; i < vocabGroupsConfigLine.length(); i++) {
					if (vocabGroupsConfigLine.charAt(i) == ':') {
						checkBoxLabels.add(vocabGroupsConfigLine.substring(0, i));
						links.add(baseURL + vocabGroupsConfigLine.substring(i + 2, vocabGroupsConfigLine.length()));
						break;
					}
				}
			}

			int numberOfGroups = checkBoxLabels.size();

			vocabGroups = new JCheckBox[numberOfGroups];
			vocabGroupLinks = new String[numberOfGroups];

			for (int i = 0; i < numberOfGroups; i++) {
				vocabGroups[i] = new JCheckBox(checkBoxLabels.get(i));
				vocabGroupLinks[i] = links.get(i);
			}
			for (JCheckBox checkBox: vocabGroups) {
				choices.add(Box.createVerticalStrut(10));
				choices.add(checkBox);
			}

		} else {
			choices.add(new JLabel("<HTML><BR><BR>Vocabulary groups unavailable.</HTML>"));
		}

		choices.add(Box.createVerticalStrut(20));
		JButton selectAll = new JButton("Select All:");
		choices.add(selectAll);

		ArrayList<String> comboBoxOptions = new ArrayList<String>();
		comboBoxOptions.add("All levels");
		if (passed) {
			comboBoxOptions.add(checkBoxLabels.get(0).substring(1,3));
		}
		boolean hasOption = false;
		for (String label: checkBoxLabels) {
			hasOption = false;
			for (String option: comboBoxOptions) {
				if (option.equals(label.substring(1,3))) {
					hasOption = true;
					break;
				}
			}
			if (!hasOption) {
				comboBoxOptions.add(label.substring(1,3));
			}
		}
		String[] comboBoxOptionsArray = new String[comboBoxOptions.size()];
		final JComboBox selectAllCombo = new JComboBox(comboBoxOptions.toArray(comboBoxOptionsArray));
		choices.add(selectAllCombo);

		choices.add(Box.createVerticalStrut(20));
		JButton moreOptions = new JButton("More Options");
		choices.add(moreOptions);

		if (selfConfigure) {
			choices.add(Box.createVerticalStrut(20));
			choices.add(new JLabel("Or: enter file directory"));
			final JTextField userFileDirectory = new JTextField(10);
			choices.add(userFileDirectory);
			userFileDirectory.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					vocabGroupLinks = new String[]{userFileDirectory.getText()};
					submitVocabGroups(true);
				}
			});
		}

		choices.add(Box.createVerticalStrut(40));
		next = new JButton("Next");
		choices.add(next);

		for (Component component: choices.getComponents()) {
			((JComponent)component).setAlignmentX(Component.LEFT_ALIGNMENT);
		}

		isKanaTest = false;
		kanaTest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				isKanaTest = true;
				makeKanaTestSettings();
			}
		});

		selectAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (vocabGroups != null && selectAllCombo.getSelectedIndex() != -1) {
					if (selectAllCombo.getSelectedIndex() == 0) {
						for (JCheckBox cb: vocabGroups) {
							cb.setSelected(true);
						}
					} else {
						String vocabGroupLevel = (String)selectAllCombo.getSelectedItem();
						for (int i = 0; i < vocabGroups.length; i++) {
							if (vocabGroupLevel.equals(checkBoxLabels.get(i).substring(1,3))) {
								vocabGroups[i].setSelected(true);
							}
						}
					}
				}
			}
		});

		final String[] finalTermTypes = new String[]{"All types", "Verbs only", "Adjectives only", "Nouns only", "Everything else"};
		moreOptions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int termType = JOptionPane.showOptionDialog(frame, "<HTML>What category of terms would you like to be tested on?<BR>Current setting: " + typeWanted + "</HTML>", "More Options", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, finalTermTypes, finalTermTypes[0]);
				switch (termType) {
				case 0: typeWanted = "All types"; break;
				case 1: typeWanted = "Verbs only"; break;
				case 2: typeWanted = "Adjectives only"; break;
				case 3: typeWanted = "Nouns only"; break;
				case 4: typeWanted = "Everything else"; break;
				}
			}
		});

		next.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (vocabGroups != null) {
					ArrayList<String> vocabGroupLinksTemp = new ArrayList<String>();
					for (int i = 0; i < vocabGroups.length; i++) {
						if (vocabGroups[i].isSelected()) {
							vocabGroupLinksTemp.add(vocabGroupLinks[i]);
						}
					}
					if (vocabGroupLinksTemp.size() > 0) {
						vocabGroupLinks = vocabGroupLinksTemp.toArray(new String[vocabGroupLinksTemp.size()]);
						submitVocabGroups(false);
					}
				}
			}
		});

		return welcome;
	}

	private static void submitVocabGroups(boolean isFile)
	{
		JPanel contentPane = (JPanel)frame.getContentPane();
		contentPane.remove(((BorderLayout)contentPane.getLayout()).getLayoutComponent(BorderLayout.WEST));
		contentPane.remove(((BorderLayout)contentPane.getLayout()).getLayoutComponent(BorderLayout.CENTER));
		try {
			contentPane.add(makeTestSettings(isFile), BorderLayout.WEST);
			frame.setContentPane(contentPane);
			numberQuestionField.requestFocusInWindow();
		} catch (NullPointerException e) {
			reset();
		}
	}

	private static JPanel makeTestSettings(boolean isFile)
	{
		JPanel testSettings = new JPanel();
		testSettings.setLayout(new FlowLayout());

		JPanel settings = new JPanel();
		testSettings.add(settings);

		settings.setLayout(new GridLayout(0, 1, 0, 12));

		settings.add(new JLabel("Enter the number of questions."));

		boolean passed = true;
		{
			for (String vocabGroupLink: vocabGroupLinks) {
				passed = loadFileTerms(vocabGroupLink, isFile);
			}
		}

		settings.add(new JLabel("(Max " + totalNumberOfQuestions + ")"));

		numberQuestionField = new JTextField();
		settings.add(numberQuestionField);

		settings.add(new Container());

		settings.add(new JLabel("Choose a level of conjugation grammar."));

		JRadioButton[] conjLevels = new JRadioButton[3];
		ButtonGroup chooseConjLevels = new ButtonGroup();
		{
			conjLevels[0] = new JRadioButton("No conjugation, please.");
			conjLevels[1] = new JRadioButton("Some conjugation.");
			conjLevels[2] = new JRadioButton("Bring it!");
			for (JRadioButton radioButton: conjLevels) {
				chooseConjLevels.add(radioButton);
				settings.add(radioButton);
			}
		}

		JButton beginTest = new JButton("Begin Test");
		settings.add(beginTest);
		settings.add(message);

		final JRadioButton[] finalConjLevels = conjLevels;
		beginTest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean numberGood = false;
				boolean testTypeGood = false;
				try {
					numberOfQuestions = Integer.parseInt(numberQuestionField.getText());
					for (int i = 0; i < finalConjLevels.length; i++) {
						if (finalConjLevels[i].isSelected()) {
							switch (i) {
							case 0: testType = TestType.NOCONJ; break;
							case 1: testType = TestType.NEUTRAL; break;
							case 2: testType = TestType.HEAVYCONJ; break;
							default: testType = null; break;
							}
						}
					}
					if (numberOfQuestions >= 1 && numberOfQuestions <= totalNumberOfQuestions) {
						numberGood = true;
					}
					if (testType != null) {
						testTypeGood = true;
					}
					if (!numberGood) {
						makeMessage("Your number is not in range.");
					} else if (!testTypeGood) {
						makeMessage("Select a conjugation level.");
					} else {
						currentQuestion = 0;
						questionPart = 0;
						makeTest();
						showNewQuestion();
					}
				} catch (NumberFormatException nfe) {
					makeMessage("Enter an integer.");
				}
			}
		});

		if (!passed) {
			testSettings = null;
		}

		return testSettings;
	}

	private static void makeKanaTestSettings()
	{
		final String[][] kanaTestLinks = new String[17][3];
		final Object[][] kanaTestSettingsChoicesObjects = new Object[17][3];
		fileTerms = new ArrayList<String>();
		wordTypes = new ArrayList<String>();

		try {
			Scanner scanner;
			if (offline) {
				scanner = new Scanner(new File(kanaTestLinksConfigLink));
			} else {
				scanner = new Scanner(new URL(kanaTestLinksConfigLink).openStream());
			}
			ArrayList<String> links = new ArrayList<String>();
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				for (int i = 0; i < line.length(); i++) {
					if (line.charAt(i) == ':') {
						links.add(baseURL + line.substring(i + 2, line.length()));
						break;
					}
				}
			}
			scanner.close();
			for (int i = 0; i < links.size(); i++) {
				kanaTestLinks[(i / 2) + 1][((i % 2) + 1)] = links.get(i);
			}

			JPanel contentPane = (JPanel)frame.getContentPane();
			contentPane.remove(((BorderLayout)contentPane.getLayout()).getLayoutComponent(BorderLayout.WEST));

			JPanel kanaTestSettings = new JPanel(new FlowLayout());
			JScrollPane kanaTestSettingsScrollPane = new JScrollPane(kanaTestSettings, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
			contentPane.add(kanaTestSettingsScrollPane, BorderLayout.WEST);
			kanaTestSettingsScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));

			JPanel kanaTestSettingsBox = new JPanel();
			kanaTestSettings.add(kanaTestSettingsBox);
			kanaTestSettingsBox.setLayout(new BoxLayout(kanaTestSettingsBox, BoxLayout.Y_AXIS));
			kanaTestSettings.add(new JLabel("    "));

			JPanel kanaTestSettingsChoices = new JPanel(new GridLayout(17, 3, 10, 10));
			kanaTestSettingsBox.add(kanaTestSettingsChoices);

			kanaTestSettingsChoicesObjects[0][0] = new JLabel("Select All:");

			JButton selectAllHiragana = new JButton("Hiragana");
			kanaTestSettingsChoicesObjects[0][1] = selectAllHiragana;

			JButton selectAllKatakana = new JButton("Katakana");
			kanaTestSettingsChoicesObjects[0][2] = selectAllKatakana;

			for (int row = 1; row < 17; row++) {
				kanaTestSettingsChoicesObjects[row][0] = new JLabel();
			}

			((JLabel)kanaTestSettingsChoicesObjects[1][0]).setText("Vowel");
			((JLabel)kanaTestSettingsChoicesObjects[2][0]).setText("K-");
			((JLabel)kanaTestSettingsChoicesObjects[3][0]).setText("G-");
			((JLabel)kanaTestSettingsChoicesObjects[4][0]).setText("S-");
			((JLabel)kanaTestSettingsChoicesObjects[5][0]).setText("Z-");
			((JLabel)kanaTestSettingsChoicesObjects[6][0]).setText("T-");
			((JLabel)kanaTestSettingsChoicesObjects[7][0]).setText("D-");
			((JLabel)kanaTestSettingsChoicesObjects[8][0]).setText("N-");
			((JLabel)kanaTestSettingsChoicesObjects[9][0]).setText("H-");
			((JLabel)kanaTestSettingsChoicesObjects[10][0]).setText("B-");
			((JLabel)kanaTestSettingsChoicesObjects[11][0]).setText("P-");
			((JLabel)kanaTestSettingsChoicesObjects[12][0]).setText("M-");
			((JLabel)kanaTestSettingsChoicesObjects[13][0]).setText("Y-");
			((JLabel)kanaTestSettingsChoicesObjects[14][0]).setText("R-");
			((JLabel)kanaTestSettingsChoicesObjects[15][0]).setText("W-");
			((JLabel)kanaTestSettingsChoicesObjects[16][0]).setText("Youon");

			for (int row = 1; row < 17; row++) {
				kanaTestSettingsChoicesObjects[row][1] = new JCheckBox();
				kanaTestSettingsChoicesObjects[row][2] = new JCheckBox();
			}

			for (int i = 0; i < kanaTestSettingsChoicesObjects.length; i++) {
				for (int j = 0; j < kanaTestSettingsChoicesObjects[i].length; j++) {
					kanaTestSettingsChoices.add((Component)kanaTestSettingsChoicesObjects[i][j]);
				}
			}

			kanaTestSettingsBox.add(Box.createVerticalStrut(10));

			JButton beginTest = new JButton("Begin Test");
			kanaTestSettingsBox.add(beginTest);

			for (Component component: kanaTestSettingsBox.getComponents()) {
				((JComponent)component).setAlignmentX(Component.CENTER_ALIGNMENT);
			}

			{
				selectAllHiragana.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						for (int i = 1; i < kanaTestSettingsChoicesObjects.length; i++) {
							((JCheckBox)kanaTestSettingsChoicesObjects[i][1]).setSelected(true);
						}
					}
				});

				selectAllKatakana.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						for (int i = 1; i < kanaTestSettingsChoicesObjects.length; i++) {
							((JCheckBox)kanaTestSettingsChoicesObjects[i][2]).setSelected(true);
						}
					}
				});

				beginTest.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						ArrayList<String> kanaTestLinksSelected = new ArrayList<String>();

						for (int i = 1; i < kanaTestSettingsChoicesObjects.length; i++) {
							for (int j = 1; j < kanaTestSettingsChoicesObjects[i].length; j++) {
								if (((JCheckBox)kanaTestSettingsChoicesObjects[i][j]).isSelected()) {
									kanaTestLinksSelected.add(kanaTestLinks[i][j]);
								}
							}
						}

						if (kanaTestLinksSelected.size() > 0) {

							boolean fileTermsLoaded = false;
							for (String kanaTestLink: kanaTestLinksSelected) {
								fileTermsLoaded = loadFileTerms(kanaTestLink, false);
								if (!fileTermsLoaded) {
									break;
								}
							}

							if (fileTermsLoaded) {
								numberOfQuestions = fileTerms.size();
								currentQuestion = 0;
								questionPart = 0;
								makeTest();
								showNewQuestion();
							} else {
								reset();
							}
						}
					}
				});
			}

			frame.setContentPane(contentPane);
		} catch (IOException e) {
			showException("Cannot find the configuration file for kana practice.", IOEXCEPTION_MSG, MISPLACED_FILE_DETAILS);
		}
	}

	private static boolean loadFileTerms(String link, boolean isFile)
	{
		try {
			Scanner scanner;
			if (offline || isFile) {
				scanner = new Scanner(new FileInputStream(new File(link)), "UTF-8");
			} else {
				scanner = new Scanner(new URL(link).openStream(), "UTF-8");
			}
			if (!isKanaTest) {
				scanner.nextLine();
			}
			ArrayList<String> loadFileTermsArray = new ArrayList<String>();
			while (scanner.hasNextLine()) {
				loadFileTermsArray.add(scanner.nextLine());
			}
			scanner.close();
			setWordTypeAndFileTerms(loadFileTermsArray);
		} catch (IOException e) {
			showException("One or more files containing needed terms cannot be located.", IOEXCEPTION_MSG, MISPLACED_FILE_DETAILS);
			return false;
		}
		return true;
	}

	private static void setWordTypeAndFileTerms(ArrayList<String> fileTermsArray)
	{
		for (String fileTerm: fileTermsArray) {
			String fileTermCopy = fileTerm;
			String wordType = "";
			for (int i = 0; i < fileTermCopy.length(); i++) {
				if (fileTermCopy.charAt(i) == '(') {
					fileTermCopy = fileTermCopy.substring(i + 1, fileTermCopy.length());
					i = 0;
				} else if (fileTermCopy.charAt(i) == ')') {
					wordType = fileTermCopy.substring(0, i);
					break;
				}
			}
			if (typeWanted == "All types" || TermFilter.match(wordType, typeWanted)) {
				fileTerms.add(fileTerm);
				wordTypes.add(wordType);
				totalNumberOfQuestions++;
			}
		}
	}

	private static void makeTest()
	{
		test = new Test(fileTerms, wordTypes, testType, numberOfQuestions, isKanaTest);
	}

	private static void showNewQuestion()
	{
		test.setCurrentQuestion(currentQuestion);

		JPanel contentPane = (JPanel)frame.getContentPane();
		{
			Component layoutComponent;
			for (Object constraint: new Object[]{BorderLayout.NORTH, BorderLayout.WEST, BorderLayout.SOUTH, BorderLayout.EAST, BorderLayout.CENTER}) {
				layoutComponent = ((BorderLayout)contentPane.getLayout()).getLayoutComponent(constraint);
				if (layoutComponent != null) {
					contentPane.remove(((BorderLayout)contentPane.getLayout()).getLayoutComponent(constraint));
				}
			}
		}

		JPanel questionResult = new JPanel();
		contentPane.add(questionResult, BorderLayout.NORTH);
		questionResult.setPreferredSize(new Dimension(NORTH_WIDTH, NORTH_HEIGHT));
		questionResult.setBorder(new LineBorder(Color.GRAY));

		JPanel asker = new JPanel(new FlowLayout());
		JScrollPane askerScrollPane = new JScrollPane(asker, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		contentPane.add(askerScrollPane, BorderLayout.WEST);
		askerScrollPane.setPreferredSize(new Dimension(EAST_WIDTH, EAST_HEIGHT));

		JPanel askerSupp = new JPanel();
		JScrollPane askerSuppScrollPane = new JScrollPane(askerSupp, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		contentPane.add(askerSuppScrollPane, BorderLayout.CENTER);
		askerSuppScrollPane.setPreferredSize(new Dimension(CENTER_WIDTH, CENTER_HEIGHT));
		askerSupp.setLayout(new GridLayout(3, 1));

		JPanel fileTerm = new JPanel();
		contentPane.add(fileTerm, BorderLayout.SOUTH);
		fileTerm.setPreferredSize(new Dimension(NORTH_WIDTH, SOUTH_HEIGHT));
		fileTerm.setBorder(new LineBorder(Color.GRAY));

		JPanel revealedAnswers = new JPanel();
		contentPane.add(revealedAnswers, BorderLayout.EAST);
		revealedAnswers.setPreferredSize(new Dimension(EAST_WIDTH, EAST_HEIGHT));
		revealedAnswers.setBorder(new LineBorder(Color.GRAY));

		{
			JPanel askerSect = new JPanel();
			askerSect.setLayout(new BoxLayout(askerSect, BoxLayout.Y_AXIS));
			asker.add(askerSect);
			asker.add(new JLabel("    "));

			{
				int[] score = test.getScore();
				int rightPoints = score[0] + score[2];
				int wrongPoints = score[1] + score[3];

				JLabel currentScore = new JLabel("Score: " + rightPoints + "/" + (rightPoints + wrongPoints));
				askerSect.add(currentScore);
				currentScore.setAlignmentX(Component.LEFT_ALIGNMENT);
			}
			askerSect.add(Box.createVerticalStrut(20));
			JLabel ask1 = new JLabel("<HTML>Question " + (currentQuestion + 1) + " of " + numberOfQuestions + "<BR>Part " + (questionPart + 1) + " of " + (test.getQuestionParts() + 1) + "</HTML>");
			askerSect.add(ask1);
			ask1.setAlignmentX(Component.LEFT_ALIGNMENT);
			askerSect.add(Box.createVerticalStrut(20));
			{
				JTextArea question = new JTextArea(test.ask1(questionPart));
				askerSect.add(question);
				question.setAlignmentX(Component.LEFT_ALIGNMENT);

				question.setFont(UIManager.getFont("Label.font"));  
				question.setEditable(false);  
				question.setOpaque(false);    
				question.setWrapStyleWord(true);
				question.setLineWrap(true);
			}
		}

		final JTextField userAnswer = new JTextField(20);
		{
			askerSupp.add(new Container());

			Box askSuppQuestion = Box.createHorizontalBox();
			askerSupp.add(askSuppQuestion);

			askSuppQuestion.add(new JLabel("    "));
			JTextArea ask2Text = new JTextArea(test.ask2(questionPart));
			askSuppQuestion.add(ask2Text);
			{
				ask2Text.setFont(new Font("Meiryo", Font.PLAIN, 24));
				ask2Text.setEditable(false);  
				ask2Text.setOpaque(false);
				ask2Text.setWrapStyleWord(true);
				ask2Text.setLineWrap(true);
			}
			askSuppQuestion.add(new JLabel("    "));

			JPanel askerSuppInput = new JPanel();
			askerSupp.add(askerSuppInput);

			askerSuppInput.add(userAnswer);
			userAnswer.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String answer = userAnswer.getText();
					if (test.evaluate(answer, questionPart)) {
						if (test.hasConjPart() && !test.isQuestionType1() && questionPart == 1) {
							test.assignConjDefinition(answer);
						}
						showQuestionResults(true);
					} else {
						showQuestionResults(false);
					}
					userAnswer.removeActionListener(this);
				}
			});
		}

		frame.setContentPane(contentPane);
		userAnswer.requestFocusInWindow();
	}

	private static void showQuestionResults(boolean correct)
	{
		JPanel contentPane = (JPanel)frame.getContentPane();
		Component comp;
		for (String section: new String[]{BorderLayout.NORTH, BorderLayout.EAST, BorderLayout.SOUTH}) {
			comp = ((BorderLayout)contentPane.getLayout()).getLayoutComponent(section);
			if (comp != null) {
				contentPane.remove(((BorderLayout)contentPane.getLayout()).getLayoutComponent(section));
			}
		}

		JPanel questionResult = new JPanel();
		JScrollPane questionResultScrollPane = new JScrollPane(questionResult, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		contentPane.add(questionResultScrollPane, BorderLayout.NORTH);
		questionResultScrollPane.setPreferredSize(new Dimension(NORTH_WIDTH, NORTH_HEIGHT));

		JPanel revealedAnswers = new JPanel(new FlowLayout());
		JScrollPane revealedAnswersScrollPane = new JScrollPane(revealedAnswers, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		contentPane.add(revealedAnswersScrollPane, BorderLayout.EAST);
		revealedAnswersScrollPane.setPreferredSize(new Dimension(EAST_WIDTH, EAST_HEIGHT));

		JPanel fileTerm = new JPanel();
		JScrollPane fileTermScrollPane = new JScrollPane(fileTerm);
		contentPane.add(fileTermScrollPane, BorderLayout.SOUTH);
		fileTermScrollPane.setPreferredSize(new Dimension(NORTH_WIDTH, SOUTH_HEIGHT));

		if (correct) {
			questionResult.add(new JLabel("<HTML>Correct!<BR>" + goodComments[(int)(Math.random() * goodComments.length)] + "</HTML>", imageCorrect, SwingConstants.CENTER));
		} else {
			questionResult.add(new JLabel("<HTML>Oops!<BR>" + badComments[(int)(Math.random() * badComments.length)] + "</HTML>", imageWrong, SwingConstants.CENTER));
		}

		{
			Box revealedAnswersSect = Box.createVerticalBox();
			revealedAnswers.add(revealedAnswersSect);
			revealedAnswers.add(new JLabel("    "));

			JLabel acceptedResponsesLabel = new JLabel("Accepted responses:");
			revealedAnswersSect.add(acceptedResponsesLabel);
			acceptedResponsesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
			revealedAnswersSect.add(Box.createVerticalStrut(20));

			{
				String[] acceptedResponses = test.revealAnswers(questionPart);
				String acceptedResponsesChain = "";
				for (String response: acceptedResponses) {
					acceptedResponsesChain +=  response + "; ";
				}

				JTextArea revealedAnswersText = new JTextArea(acceptedResponsesChain);
				revealedAnswersSect.add(revealedAnswersText);
				revealedAnswersText.setAlignmentX(Component.LEFT_ALIGNMENT);

				revealedAnswersText.setFont(UIManager.getFont("Label.font"));  
				revealedAnswersText.setEditable(false);  
				revealedAnswersText.setOpaque(false);
				revealedAnswersText.setWrapStyleWord(true);
				revealedAnswersText.setLineWrap(true);

				revealedAnswersSect.add(Box.createVerticalStrut(20));

				next = new JButton("(Press Enter)");
				revealedAnswersSect.add(next);
				next.setAlignmentX(Component.LEFT_ALIGNMENT);
				next.registerKeyboardAction(next.getActionForKeyStroke(
						KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false)),
						KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
						JComponent.WHEN_FOCUSED);
				next.registerKeyboardAction(next.getActionForKeyStroke(
						KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true)),
						KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true),
						JComponent.WHEN_FOCUSED);
				next.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (questionPart < test.getQuestionParts()) {
							questionPart++;
							showNewQuestion();
						} else {
							if (currentQuestion < numberOfQuestions - 1) {
								currentQuestion++;
								questionPart = 0;
								showNewQuestion();
							} else {
								setResultFrame();
							}
						}
					}
				});
			}
		}

		{
			if (questionPart == test.getQuestionParts()) {
				JTextArea fileTermText = new JTextArea(test.revealFileTerm());
				fileTerm.add(fileTermText);

				fileTermText.setFont(new Font("Meiryo", Font.PLAIN, 18));
				fileTermText.setEditable(false);  
				fileTermText.setOpaque(false);
				fileTermText.setWrapStyleWord(false);
				fileTermText.setLineWrap(false);
			}
		}

		frame.setContentPane(contentPane);
		next.requestFocusInWindow();
	}

	private static void setResultFrame()
	{
		JPanel contentPane = (JPanel)frame.getContentPane();
		Component layoutComponent;
		for (Object constraint: new Object[]{BorderLayout.NORTH, BorderLayout.WEST, BorderLayout.SOUTH, BorderLayout.EAST, BorderLayout.CENTER}) {
			layoutComponent = ((BorderLayout)contentPane.getLayout()).getLayoutComponent(constraint);
			if (layoutComponent != null) {
				contentPane.remove(((BorderLayout)contentPane.getLayout()).getLayoutComponent(constraint));
			}
		}

		int[] score = test.getScore();

		Box resultFrame = Box.createVerticalBox();
		contentPane.add(resultFrame);

		resultFrame.add(Box.createVerticalStrut(64));

		resultFrame.add(new JLabel("You have completed the test."));
		resultFrame.add(Box.createVerticalStrut(20));
		resultFrame.add(new JLabel("Your total score is: "  + (score[0] + score[2]) + "/" + (score[0] + score[1] + score[2] + score[3]) + ", or " + (int)(Math.round(100 * (double)(score[0] + score[2])/(score[0] + score[1] + score[2] + score[3]))) + "%."));
		if (testType != TestType.NOCONJ && score[2] + score[3] != 0) {
			resultFrame.add(new JLabel("Vocabulary subscore: " + score[0] + "/" + (score[0] + score[1]) + ", " + (int)(Math.round(100 * (double)(score[0])/(score[0] + score[1]))) + "%"));
			resultFrame.add(new JLabel("Conjugation subscore: " + score[2] + "/" + (score[2] + score[3]) + ", " + (int)(Math.round(100 * (double)(score[2])/(score[2] + score[3]))) + "%"));
		}

		resultFrame.add(Box.createVerticalStrut(64));

		final ArrayList<String> problemTerms = test.getProblemTerms();
		if (problemTerms.size() > 0) {
			resultFrame.add(new JLabel("Review the terms you were having trouble with?"));
			resultFrame.add(Box.createVerticalStrut(32));
			JButton review = new JButton("Review");
			resultFrame.add(review);
			review.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JPanel contentPane = (JPanel)frame.getContentPane();
					contentPane.remove(((BorderLayout)contentPane.getLayout()).getLayoutComponent(BorderLayout.CENTER));

					final Box reviewFrame = Box.createVerticalBox();
					contentPane.add(reviewFrame);

					reviewFrame.add(Box.createVerticalStrut(64));

					next = new JButton("(Press Enter)");
					final JTextArea reviewMessage;

					reviewMessage = new JTextArea();
					reviewFrame.add(reviewMessage);
					reviewFrame.add(next);

					for (Component component: reviewFrame.getComponents()) {
						((JComponent)component).setAlignmentX(Component.CENTER_ALIGNMENT);
					}

					reviewMessage.setFont(new Font("Meiryo", Font.PLAIN, 18));
					reviewMessage.setEditable(false);  
					reviewMessage.setOpaque(false);
					reviewMessage.setWrapStyleWord(true);
					reviewMessage.setLineWrap(true);

					next.registerKeyboardAction(next.getActionForKeyStroke(
							KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false)),
							KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
							JComponent.WHEN_FOCUSED);
					next.registerKeyboardAction(next.getActionForKeyStroke(
							KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true)),
							KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true),
							JComponent.WHEN_FOCUSED);

					final Iterator<String> problemTermsIterator;
					final Iterator<ArrayList<String>> problemInputsIterator;

					final ArrayList<ArrayList<String>> problemInputs = test.getProblemInputs();
					problemTermsIterator = problemTerms.iterator();
					problemInputsIterator = problemInputs.iterator();

					String reviewMessageString = "1 of " + problemTerms.size() + "\n\n" + problemTermsIterator.next() + "\n\nYour input:\n";
					for (String s: problemInputsIterator.next()) {
						reviewMessageString += s + ";\n";
					}

					reviewMessage.setText(reviewMessageString);

					final AtomicInteger i = new AtomicInteger(2);
					next.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							if (problemTermsIterator.hasNext()) {
								String reviewMessageString = i.get() + " of " + problemTerms.size() + "\n" + problemTermsIterator.next() + "\n\nYour input:\n";
								for (String s: problemInputsIterator.next()) {
									reviewMessageString += s + ";\n";
								}

								reviewMessage.setText(reviewMessageString);
								i.getAndIncrement();
							} else {
								reviewMessage.setText("Review complete!");
								next.removeActionListener(this);
								JButton testProblemTerms = new JButton("Re-test these terms");
								reviewFrame.add(testProblemTerms, 1);
								testProblemTerms.setAlignmentX(Component.CENTER_ALIGNMENT);
								testProblemTerms.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent e) {
										fileTerms.clear();
										wordTypes.clear();
										setWordTypeAndFileTerms(problemTerms);
										numberOfQuestions = fileTerms.size();
										currentQuestion = 0;
										questionPart = 0;
										makeTest();
										showNewQuestion();
									}
								});
							}
						}
					});

					frame.setContentPane(contentPane);
					next.requestFocusInWindow();
				}
			});
		}

		for(Component component: resultFrame.getComponents()) {
			((JComponent)component).setAlignmentX(Component.CENTER_ALIGNMENT);
		}

		JButton reset = new JButton("Reset");
		contentPane.add(reset, BorderLayout.SOUTH);

		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reset();
			}
		});

		frame.setContentPane(contentPane);
	}

	private static JMenuBar makeMenuBar(final JFrame frame)
	{
		JMenuBar menuBar = new JMenuBar();
		JMenu file = new JMenu("File");
		menuBar.add(file);

		JMenu help = new JMenu("Help");
		menuBar.add(help);

		{
			JMenuItem reset = new JMenuItem("Reset");
			file.add(reset);
			reset.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					reset();
				}
			});
		}
		{
			JMenuItem exit = new JMenuItem("Exit");
			file.add(exit);
			exit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});
		}

		{
			JMenuItem JPVocabTesterHelp = new JMenuItem("Using JP Vocab Tester");
			help.add(JPVocabTesterHelp);
			JPVocabTesterHelp.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JOptionPane.showMessageDialog(frame,
							"<HTML>Welcome,<BR><BR>" + 
									"JP Vocab Tester uses term groups labeled \"N-\" followed by a number.<BR>" + 
									"This labeling represents a JLPT (Japanese Language Proficiency Test) ranking,<BR>" + 
									"with 5 being the lowest (easiest), and 1 being the highest (hardest).<BR><BR>" + 
									"This program may look as if it's only meant for testing terms you already know,<BR>" + 
									"but you're encouraged to also use it for learning new terms simply by repeatedly testing a group - give it a try.<BR>" + 
									"A recommended usage would be to test a fraction of the group repeatedly over a few days.<BR>" + 
									"Isolate and study the verbs, adjectives, or nouns separately, before finally combining the three.<BR>" +
									"The terms are selected at random from your selected group(s) and occur only once on a test.<BR><BR>" + 
									"The test mechanics are pretty straightforward. But if you have any questions, don't hesitate to email me at " + myEmail + ".<BR>" + 
									"Lastly, if you've had very little experience with Japanese, try reading up on Japanese particles and conjugation rules, etc.<BR>" + 
									"The material shouldn't be too hard to find on the internet.<BR>" + 
									"Don't forget, there's a \"kana practice\" button for beginners.<BR><BR>" + 
									"Enjoy.</HTML>",
									"Using JP Vocab Tester", JOptionPane.INFORMATION_MESSAGE);
				}
			});
		}

		{
			JMenuItem about = new JMenuItem("About");
			help.add(about);
			about.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					aboutText.setText("JP Vocab Tester:\r\nVocabulary compiled from http://www.manythings.org/japanese/jlpt/ and WWWJDIC.\n\nCreated by kurantoB, Feburary 2012\n" + mySite);
					JOptionPane.showMessageDialog(frame, aboutText, "About", JOptionPane.INFORMATION_MESSAGE);
				}
			});
		}

		return menuBar;
	}
}
