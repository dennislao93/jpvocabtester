import java.util.ArrayList;

/**
 * Conjugates using a given definition, romaji, and word type
 */
public class Conjugator
{
    private String definition;
    private String[] romaji;
    private String wordType;
    private boolean aruSC;
    private int cType;

    private String[] answers;

    /**
     * Constructor
     */
    public Conjugator(String definition, String[] romaji, String wordType, String wordTypeOriginal)
    {
        this.definition = definition;
        this.romaji = romaji;
        this.wordType = wordType;
        if (wordTypeOriginal.indexOf("v5aru") != -1) {
            aruSC = true;
        }

        int randTense = (int)(Math.random() * 2);
        int randBool = (int)(Math.random() * 2) * 2;
        int randFormality = (int)(Math.random() * 2) * 4;
        /* 000 = 0  non-past true  informal
         * 100 = 1  past     true  informal
         * 020 = 2  non-past false informal
         * 120 = 3  past     false informal
         * 004 = 4  non-past true  formal
         * 104 = 5  past     true  formal
         * 024 = 6  non-past false formal
         * 124 = 7  past     false formal
         */
        cType = randTense + randBool + randFormality;

        if (wordType.equals("verb -ru") || wordType.equals("-iku") || wordType.equals("verb -u") || wordType.equals("-suru") || wordType.equals("-kuru")) {
            int level = 2;
            if (wordTypeOriginal.indexOf("vt") != -1 && (definition.length() < 6 || !definition.substring(0, 6).equals("to be "))) {
                level++;
            }
            cType += (int)(Math.random() * level) * 8;
        }

        conjugate();
    }

    public String getExpression()
    {
        String builder;
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
        if (wordType.equals("state of being") || wordType.equals("adjective -na") || wordType.equals("adjective -i") || wordType.equals("ii")) {
            builder = "\"It";

            if (cType % 2 == 0) {
                builder += " is";
            } else {
                builder += " was";
            }
        } else {
            if (cType % 2 == 0) {
                builder = "\"To";
            } else {
                builder = "\"Did";
            }
        }
        if (cType % 4 > 1) {
            builder += " not";
        }
        if (cType >= 8) {
            if (cType <= 15) {
                builder += " make (someone/something)";
            } else {
                builder += " be";
            }
        }
        if (cType >= 16 && cType <= 23) {
            if (wordType.equals("-suru") && !romaji[0].equals("suru")) {
                builder += " \"" + definition + "(ed)\"\"";
            } else {
                definition = StringModifier.clipStringTo(definition);
                String[] passive = StringModifier.clipStringFirstWord(definition);
                if (passive[0].charAt(passive[0].length() - 1) == 'e') {
                    builder += " \"" + passive[0] + "(d/n)" + passive[1] + "\"\"";
                } else {
                    builder += " \"" + passive[0] + "(ed)" + passive[1] + "\"\"";
                }
            }
        } else {
            if (!wordType.equals("state of being") && !wordType.equals("adjective -na") && !wordType.equals("adjective -i") && !wordType.equals("ii"))
            {
                definition = StringModifier.clipStringTo(definition);
            }
            builder += " " + definition + "\"";
        }
        if (cType % 8 < 4) {
            builder += " informally.";
        } else {
            builder += " formally.";
        }

        return builder;
    }

    public String getExpression(String definition)
    {
        this.definition = definition;
        return getExpression();
    }

    public String getConjDescription()
    {
        String conjDescription;
        if (cType % 8 < 4) {
            conjDescription = "Informal";
        } else {
            conjDescription = "Formal";
        }
        if (cType % 2 == 0) {
            conjDescription += " non-past";
        } else {
            conjDescription += " past";
        }
        if (cType % 4 >= 2) {
            conjDescription += " negative";
        }
        if (cType >= 8) {
            if (cType <= 15) {
                conjDescription += " causative";
            } else {
                conjDescription += " passive";
            }
        }

        return conjDescription;
    }

    public String[] getAnswers()
    {
        return answers;
    }

    private void conjugate() {
        ArrayList<String> allConjugations = new ArrayList<String>();
        for (String romaji: this.romaji) {
            if (wordType.equals("state of being") || wordType.equals("adjective -na")) {
                switch (cType) {
                    case 0: allConjugations.add(romaji + " da"); break;
                    case 1: allConjugations.add(romaji + " datta"); break;
                    case 2: allConjugations.add(romaji + " janai"); break;
                    case 3: allConjugations.add(romaji + " janakatta"); break;
                    case 4: allConjugations.add(romaji + " desu"); break;
                    case 5: allConjugations.add(romaji + " deshita"); break;
                    case 6:
                    allConjugations.add(romaji + " janai desu"); /*allConjugations.add(romaji + " janain desu");*/ allConjugations.add(romaji + " ja arimasen");
                    break;
                    case 7:
                    allConjugations.add(romaji + " janakatta desu"); /*allConjugations.add(romaji + " janakattan desu");*/ allConjugations.add(romaji + " ja arimasen deshita");
                    break;
                }
            } else if (wordType.equals("adjective -i")) {
                switch (cType) {
                    case 0: allConjugations.add(romaji); break;
                    case 1: allConjugations.add(romaji.substring(0, romaji.length() - 1) + "katta"); break;
                    case 2: allConjugations.add(romaji.substring(0, romaji.length() - 1) + "ku nai"); break;
                    case 3: allConjugations.add(romaji.substring(0, romaji.length() - 1) + "ku nakatta"); break;
                    case 4: allConjugations.add(romaji + " desu"); /*allConjugations.add(romaji + "n desu");*/ break;
                    case 5:
                    allConjugations.add(romaji.substring(0, romaji.length() - 1) + "katta desu");
                    //allConjugations.add(romaji.substring(0, romaji.length() - 1) + "kattan desu");
                    break;
                    case 6: 
                    allConjugations.add(romaji.substring(0, romaji.length() - 1) + "ku nai desu");
                    //allConjugations.add(romaji.substring(0, romaji.length() - 1) + "ku nain desu");
                    allConjugations.add(romaji.substring(0, romaji.length() - 1) + "ku arimasen");
                    break;
                    case 7:
                    allConjugations.add(romaji.substring(0, romaji.length() - 1) + "ku nakatta desu");
                    //allConjugations.add(romaji.substring(0, romaji.length() - 1) + "ku nakattan desu");
                    allConjugations.add(romaji.substring(0, romaji.length() - 1) + "ku arimasen deshita");
                    break;
                }
            } else if (wordType.equals("ii")) {
                switch (cType) {
                    case 0: allConjugations.add("ii"); allConjugations.add("yoi"); break;
                    case 1: allConjugations.add("yokatta"); break;
                    case 2: allConjugations.add("yokunai"); break;
                    case 3: allConjugations.add("yoku nakatta"); break;
                    case 4:
                    allConjugations.add("ii desu");
                    //allConjugations.add("iin desu");
                    allConjugations.add("yoi desu");
                    //allConjugations.add("yoin desu");
                    break;
                    case 5: allConjugations.add("yokatta desu"); /*allConjugations.add("yokattan desu");*/ break;
                    case 6:
                    allConjugations.add("yoku nai desu");
                    //allConjugations.add("yoku nain desu");
                    allConjugations.add("yoku arimasen");
                    break;
                    case 7:
                    allConjugations.add("yoku nakatta desu");
                    //allConjugations.add("yoku nakattan desu");
                    allConjugations.add("yoku arimasen deshita");
                    break;
                }
            } else if (wordType.equals("verb -ru")) {
                romaji = romaji.substring(0, romaji.length() - 2);
                switch (cType) {
                    case 0: allConjugations.add(romaji + "ru"); break;
                    case 1: allConjugations.add(romaji + "ta"); break;
                    case 2: allConjugations.add(romaji + "nai"); break;
                    case 3: allConjugations.add(romaji + "nakatta"); break;
                    case 4: allConjugations.add(romaji + "masu"); break;
                    case 5: allConjugations.add(romaji + "mashita"); break;
                    case 6: allConjugations.add(romaji + "masen"); break;
                    case 7: allConjugations.add(romaji + "masen deshita"); break;
                    case 8: allConjugations.add(romaji + "saseru"); break;
                    case 9: allConjugations.add(romaji + "saseta"); break;
                    case 10: allConjugations.add(romaji + "sasenai"); break;
                    case 11: allConjugations.add(romaji + "sasenakatta"); break;
                    case 12: allConjugations.add(romaji + "sasemasu"); break;
                    case 13: allConjugations.add(romaji + "sasemashita"); break;
                    case 14: allConjugations.add(romaji + "sasemasen"); break;
                    case 15: allConjugations.add(romaji + "sasemasen deshita"); break;
                    case 16: allConjugations.add(romaji + "rareru"); break;
                    case 17: allConjugations.add(romaji + "rareta"); break;
                    case 18: allConjugations.add(romaji + "rarenai"); break;
                    case 19: allConjugations.add(romaji + "rarenakatta"); break;
                    case 20: allConjugations.add(romaji + "raremasu"); break;
                    case 21: allConjugations.add(romaji + "raremashita"); break;
                    case 22: allConjugations.add(romaji + "raremasen"); break;
                    case 23: allConjugations.add(romaji + "raremasen deshita"); break;
                }
            } else if (wordType.equals("verb -u")) {
                char uType = romaji.charAt(romaji.length() - 2);
                char spare = ' ';
                if (uType == 's' && romaji.length() >= 3) {
                    spare = romaji.charAt(romaji.length() - 3);
                }

                String uTypeSwitch = "";
                if (uType != 'k' && uType != 'g' && uType != 's' && uType != 'r' && uType != 'n' && uType != 'b' && uType != 'm') {
                    uTypeSwitch = "u";
                } else if (uType == 's' && spare == 't') {
                    uTypeSwitch = "tsu";
                } else {
                    uTypeSwitch = "" + uType + "u";
                }

                if (cType == 0) {
                    allConjugations.add(romaji);
                } else if (cType == 1) {
                    if (uTypeSwitch.equals("ku")) {
                        allConjugations.add(romaji.substring(0, romaji.length() - 2) + "ita");
                    } else if (uTypeSwitch.equals("gu")) {
                        allConjugations.add(romaji.substring(0, romaji.length() - 2) + "ida");
                    } else if (uTypeSwitch.equals("u")) {
                        allConjugations.add(romaji.substring(0, romaji.length() - 1) + "tta");
                    } else if (uTypeSwitch.equals("tsu")) {
                        allConjugations.add(romaji.substring(0, romaji.length() - 3) + "tta");
                    } else if (uTypeSwitch.equals("ru")) {
                        allConjugations.add(romaji.substring(0, romaji.length() - 2) + "tta");
                    } else if (uTypeSwitch.equals("nu")) {
                        allConjugations.add(romaji.substring(0, romaji.length() - 1) + "nda");
                    } else if (uTypeSwitch.equals("bu")) {
                        allConjugations.add(romaji.substring(0, romaji.length() - 2) + "nda");
                    } else if (uTypeSwitch.equals("mu")) {
                        allConjugations.add(romaji.substring(0, romaji.length() - 2) + "nda");
                    } else if (uTypeSwitch.equals("su")) {
                        allConjugations.add(romaji.substring(0, romaji.length() - 1) + "hita");
                    }
                } else if (cType == 2 || cType >= 8) {
                    if (!uTypeSwitch.equals("u") && !uTypeSwitch.equals("tsu")) {
                        if (cType == 2) {
                            allConjugations.add(romaji.substring(0, romaji.length() - 1) + "anai");
                        } else {
                            String thisConjugation = romaji.substring(0, romaji.length() - 1);
                            if (cType <= 15) {
                                thisConjugation += "ase";
                            } else {
                                thisConjugation += "are";
                            }
                            switch (cType) {
                                case 8: thisConjugation += "ru"; break;
                                case 9: thisConjugation += "ta"; break;
                                case 10: thisConjugation += "nai"; break;
                                case 11: thisConjugation += "nakatta"; break;
                                case 12: thisConjugation += "masu"; break;
                                case 13: thisConjugation += "mashita"; break;
                                case 14: thisConjugation += "masen"; break;
                                case 15: thisConjugation += "masen deshita"; break;
                                case 16: thisConjugation += "ru"; break;
                                case 17: thisConjugation += "ta"; break;
                                case 18: thisConjugation += "nai"; break;
                                case 19: thisConjugation += "nakatta"; break;
                                case 20: thisConjugation += "masu"; break;
                                case 21: thisConjugation += "mashita"; break;
                                case 22: thisConjugation += "masen"; break;
                                case 23: thisConjugation += "masen deshita"; break;
                            }
                            allConjugations.add(thisConjugation);
                        }
                    } else {
                        if (uTypeSwitch.equals("u")) {
                            if (cType == 2) {
                                allConjugations.add(romaji.substring(0, romaji.length() - 1) + "wanai");
                            } else {
                                String thisConjugation = romaji.substring(0, romaji.length() - 1);
                                if (cType <= 15) {
                                    thisConjugation += "wase";
                                } else {
                                    thisConjugation += "ware";
                                }
                                switch (cType) {
                                    case 8: thisConjugation += "ru"; break;
                                    case 9: thisConjugation += "ta"; break;
                                    case 10: thisConjugation += "nai"; break;
                                    case 11: thisConjugation += "nakatta"; break;
                                    case 12: thisConjugation += "masu"; break;
                                    case 13: thisConjugation += "mashita"; break;
                                    case 14: thisConjugation += "masen"; break;
                                    case 15: thisConjugation += "masen deshita"; break;
                                    case 16: thisConjugation += "ru"; break;
                                    case 17: thisConjugation += "ta"; break;
                                    case 18: thisConjugation += "nai"; break;
                                    case 19: thisConjugation += "nakatta"; break;
                                    case 20: thisConjugation += "masu"; break;
                                    case 21: thisConjugation += "mashita"; break;
                                    case 22: thisConjugation += "masen"; break;
                                    case 23: thisConjugation += "masen deshita"; break;
                                }
                                allConjugations.add(thisConjugation);
                            }
                        } else {
                            if (cType == 2) {
                                allConjugations.add(romaji.substring(0, romaji.length() - 2) + "anai");
                            } else {
                                String thisConjugation = romaji.substring(0, romaji.length() - 2);
                                if (cType <= 15) {
                                    thisConjugation += "ase";
                                } else {
                                    thisConjugation += "are";
                                }
                                switch (cType) {
                                    case 8: thisConjugation += "ru"; break;
                                    case 9: thisConjugation += "ta"; break;
                                    case 10: thisConjugation += "nai"; break;
                                    case 11: thisConjugation += "nakatta"; break;
                                    case 12: thisConjugation += "masu"; break;
                                    case 13: thisConjugation += "mashita"; break;
                                    case 14: thisConjugation += "masen"; break;
                                    case 15: thisConjugation += "masen deshita"; break;
                                    case 16: thisConjugation += "ru"; break;
                                    case 17: thisConjugation += "ta"; break;
                                    case 18: thisConjugation += "nai"; break;
                                    case 19: thisConjugation += "nakatta"; break;
                                    case 20: thisConjugation += "masu"; break;
                                    case 21: thisConjugation += "mashita"; break;
                                    case 22: thisConjugation += "masen"; break;
                                    case 23: thisConjugation += "masen deshita"; break;
                                }
                                allConjugations.add(thisConjugation);
                            }
                        }
                    }
                } else if (cType == 3) {
                    if (!uTypeSwitch.equals("u") && !uTypeSwitch.equals("tsu")) {
                        allConjugations.add(romaji.substring(0, romaji.length() - 1) + "anakatta");
                    } else {
                        if (uTypeSwitch.equals("u")) {
                            allConjugations.add(romaji.substring(0, romaji.length() - 1) + "wanakatta");
                        } else {
                            allConjugations.add(romaji.substring(0, romaji.length() - 2) + "anakatta");
                        }
                    }
                } else if (cType > 3) {
                    if (uTypeSwitch.equals("tsu")) {
                        romaji = romaji.substring(0, romaji.length() - 3) + "chi";
                    } else if (uTypeSwitch.equals("su")) {
                        romaji = romaji.substring(0, romaji.length() - 1) + "hi";
                    } else {
                        if (!aruSC) {
                            romaji = romaji.substring(0, romaji.length() - 1) + "i";
                        } else {
                            romaji = romaji.substring(0, romaji.length() - 2) + "i";
                        }
                    }
                    switch (cType) {
                        case 4: allConjugations.add(romaji + "masu"); break;
                        case 5: allConjugations.add(romaji + "mashita"); break;
                        case 6: allConjugations.add(romaji + "masen"); break;
                        case 7: allConjugations.add(romaji + "masen deshita"); break;
                    }
                }
            } else if (wordType.equals("-suru")) {
                if (romaji.substring(romaji.length() - 4, romaji.length()).equals("suru")) {
                    romaji = romaji.substring(0, romaji.length() - 4);
                }

                switch (cType) {
                    case 0: allConjugations.add(romaji + "suru"); break;
                    case 1: allConjugations.add(romaji + "shita"); break;
                    case 2: allConjugations.add(romaji + "shinai"); break;
                    case 3: allConjugations.add(romaji + "shinakatta"); break;
                    case 4: allConjugations.add(romaji + "shimasu"); break;
                    case 5: allConjugations.add(romaji + "shimashita"); break;
                    case 6: allConjugations.add(romaji + "shimasen"); break;
                    case 7: allConjugations.add(romaji + "shimasen deshita"); break;
                    case 8: allConjugations.add(romaji + "saseru"); break;
                    case 9: allConjugations.add(romaji + "saseta"); break;
                    case 10: allConjugations.add(romaji + "sasenai"); break;
                    case 11: allConjugations.add(romaji + "sasenakatta"); break;
                    case 12: allConjugations.add(romaji + "sasemasu"); break;
                    case 13: allConjugations.add(romaji + "sasemashita"); break;
                    case 14: allConjugations.add(romaji + "sasemasen"); break;
                    case 15: allConjugations.add(romaji + "sasemasen deshita"); break;
                    case 16: allConjugations.add(romaji + "sareru"); break;
                    case 17: allConjugations.add(romaji + "sareta"); break;
                    case 18: allConjugations.add(romaji + "sarenai"); break;
                    case 19: allConjugations.add(romaji + "sarenakatta"); break;
                    case 20: allConjugations.add(romaji + "saremasu"); break;
                    case 21: allConjugations.add(romaji + "saremashita"); break;
                    case 22: allConjugations.add(romaji + "saremasen"); break;
                    case 23: allConjugations.add(romaji + "saremasen deshita"); break;
                }

            } else if (wordType.equals("-kuru")) {
                romaji = romaji.substring(0, romaji.length() - 4);

                switch (cType) {
                    case 0: allConjugations.add(romaji + "kuru"); break;
                    case 1: allConjugations.add(romaji + "kita"); break;
                    case 2: allConjugations.add(romaji + "konai"); break;
                    case 3: allConjugations.add(romaji + "konakatta"); break;
                    case 4: allConjugations.add(romaji + "kimasu"); break;
                    case 5: allConjugations.add(romaji + "kimashita"); break;
                    case 6: allConjugations.add(romaji + "kimasen"); break;
                    case 7: allConjugations.add(romaji + "kimasen deshita"); break;
                    case 8: allConjugations.add(romaji + "kosaseru"); break;
                    case 9: allConjugations.add(romaji + "kosaseta"); break;
                    case 10: allConjugations.add(romaji + "kosasenai"); break;
                    case 11: allConjugations.add(romaji + "kosasenakatta"); break;
                    case 12: allConjugations.add(romaji + "kosasemasu"); break;
                    case 13: allConjugations.add(romaji + "kosasemashita"); break;
                    case 14: allConjugations.add(romaji + "kosasemasen"); break;
                    case 15: allConjugations.add(romaji + "kosasemasen deshita"); break;
                    case 16: allConjugations.add(romaji + "korareru"); break;
                    case 17: allConjugations.add(romaji + "korareta"); break;
                    case 18: allConjugations.add(romaji + "korarenai"); break;
                    case 19: allConjugations.add(romaji + "korarenakatta"); break;
                    case 20: allConjugations.add(romaji + "koraremasu"); break;
                    case 21: allConjugations.add(romaji + "koraremashita"); break;
                    case 22: allConjugations.add(romaji + "koraremasen"); break;
                    case 23: allConjugations.add(romaji + "koraremasen deshita"); break;
                }
            } else if (wordType.equals("-iku")) {
                if (romaji.length() > 3 && romaji.substring(romaji.length() - 4, romaji.length()).equals("yuku")) {
                    romaji = romaji.substring(0, romaji.length() - 4);
                } else {
                    romaji = romaji.substring(0, romaji.length() - 3);
                }
                switch (cType) {
                    case 0: allConjugations.add(romaji + "iku"); allConjugations.add(romaji + "yuku"); break;
                    case 1: allConjugations.add(romaji + "itta"); allConjugations.add(romaji + "yutta"); break;
                    case 2: allConjugations.add(romaji + "ikanai"); allConjugations.add(romaji + "yukanai"); break;
                    case 3: allConjugations.add(romaji + "ikanakatta"); allConjugations.add(romaji + "yukanakatta"); break;
                    case 4: allConjugations.add(romaji + "ikimasu"); allConjugations.add(romaji + "yukimasu"); break;
                    case 5: allConjugations.add(romaji + "ikimashita"); allConjugations.add(romaji + "yukimashita"); break;
                    case 6: allConjugations.add(romaji + "ikimasen"); allConjugations.add(romaji + "yukimasen"); break;
                    case 7: allConjugations.add(romaji + "ikimasen deshita"); allConjugations.add(romaji + "yukimasen deshita"); break;
                    case 8: allConjugations.add(romaji + "ikaseru"); allConjugations.add(romaji + "yukaseru"); break;
                    case 9: allConjugations.add(romaji + "ikaseta"); allConjugations.add(romaji + "yukaseta"); break;
                    case 10: allConjugations.add(romaji + "ikasenai"); allConjugations.add(romaji + "yukasenai"); break;
                    case 11: allConjugations.add(romaji + "ikasenakatta"); allConjugations.add(romaji + "yukasenakatta"); break;
                    case 12: allConjugations.add(romaji + "ikasemasu"); allConjugations.add(romaji + "yukasemasu"); break;
                    case 13: allConjugations.add(romaji + "ikasemashita"); allConjugations.add(romaji + "yukasemashita"); break;
                    case 14: allConjugations.add(romaji + "ikasemasen"); allConjugations.add(romaji + "yukasemasen"); break;
                    case 15: allConjugations.add(romaji + "ikasemasen deshita"); allConjugations.add(romaji + "yukasemasen deshita"); break;
                    case 16: allConjugations.add(romaji + "ikareru"); allConjugations.add(romaji + "yukareru"); break;
                    case 17: allConjugations.add(romaji + "ikareta"); allConjugations.add(romaji + "yukareta"); break;
                    case 18: allConjugations.add(romaji + "ikarenai"); allConjugations.add(romaji + "yukarenai"); break;
                    case 19: allConjugations.add(romaji + "ikarenakatta"); allConjugations.add(romaji + "yukarenakatta"); break;
                    case 20: allConjugations.add(romaji + "ikaremasu"); allConjugations.add(romaji + "yukaremasu"); break;
                    case 21: allConjugations.add(romaji + "ikaremashita"); allConjugations.add(romaji + "yukaremashita"); break;
                    case 22: allConjugations.add(romaji + "ikaremasen"); allConjugations.add(romaji + "yukaremasen"); break;
                    case 23: allConjugations.add(romaji + "ikaremasen deshita"); allConjugations.add(romaji + "yukaremasen deshita"); break;
                }
            }
        }

        String[] answers = new String[allConjugations.size()];
        for (int i = 0; i < allConjugations.size(); i++) {
            answers[i] = allConjugations.get(i);
        }
        this.answers = answers;
    }
}
