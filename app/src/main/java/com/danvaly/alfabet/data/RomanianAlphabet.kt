package com.danvaly.alfabet.data

import com.danvaly.alfabet.data.model.ExampleWord
import com.danvaly.alfabet.data.model.Letter

object RomanianAlphabet {

    val letters: List<Letter> = listOf(
        Letter(
            uppercase = "A", lowercase = "a",
            phoneticSound = "a",
            words = listOf(
                ExampleWord("Avion", "\u2708\uFE0F", listOf("A", "vi", "on")),
                ExampleWord("Albin\u0103", "\uD83D\uDC1D", listOf("Al", "bi", "n\u0103")),
                ExampleWord("Arici", "\uD83E\uDD94", listOf("A", "ri", "ci"))
            ),
            isVowel = true, colorIndex = 0,
            story = "A de la Avion! Un avion mare zboar\u0103 pe cer. Albina z\u00e2mbe\u0219te \u0219i zice: A! A! A!"
        ),
        Letter(
            uppercase = "\u0102", lowercase = "\u0103",
            phoneticSound = "\u0103",
            words = listOf(
                ExampleWord("M\u0103r", "\uD83C\uDF4E", listOf("M\u0103r")),
                ExampleWord("P\u0103r", "\uD83C\uDF50", listOf("P\u0103r")),
                ExampleWord("V\u0103r", "\uD83D\uDC66", listOf("V\u0103r"))
            ),
            isVowel = true, isDiacritic = true, colorIndex = 1,
            story = "\u0102 de la M\u0103r! Un m\u0103r ro\u0219u cade din pom. P\u0103rul \u0219i m\u0103rul sunt prieteni!"
        ),
        Letter(
            uppercase = "\u00C2", lowercase = "\u00E2",
            phoneticSound = "\u00E2",
            words = listOf(
                ExampleWord("P\u00E2ine", "\uD83C\uDF5E", listOf("P\u00E2i", "ne")),
                ExampleWord("C\u00E2ine", "\uD83D\uDC15", listOf("C\u00E2i", "ne")),
                ExampleWord("M\u00E2ine", "\uD83D\uDCC5", listOf("M\u00E2i", "ne"))
            ),
            isVowel = true, isDiacritic = true, colorIndex = 2,
            story = "\u00C2 de la C\u00E2ine! C\u00E2inele manc\u0103 p\u00E2ine \u0219i a\u0219teapt\u0103 m\u00E2ine s\u0103 se joace!"
        ),
        Letter(
            uppercase = "B", lowercase = "b",
            phoneticSound = "b\u0103",
            words = listOf(
                ExampleWord("Balon", "\uD83C\uDF88", listOf("Ba", "lon")),
                ExampleWord("Barc\u0103", "\u26F5", listOf("Bar", "c\u0103")),
                ExampleWord("Bunic", "\uD83D\uDC74", listOf("Bu", "nic"))
            ),
            isVowel = false, colorIndex = 3,
            story = "B de la Balon! Un balon ro\u0219u zboar\u0103 sus. Bunicul \u00eel prinde \u0219i z\u00e2mbe\u0219te!"
        ),
        Letter(
            uppercase = "C", lowercase = "c",
            phoneticSound = "c\u0103",
            words = listOf(
                ExampleWord("Cas\u0103", "\uD83C\uDFE0", listOf("Ca", "s\u0103")),
                ExampleWord("Carte", "\uD83D\uDCD6", listOf("Car", "te")),
                ExampleWord("Ciocolat\u0103", "\uD83C\uDF6B", listOf("Ci", "o", "co", "la", "t\u0103"))
            ),
            isVowel = false, colorIndex = 4,
            story = "C de la Cas\u0103! \u00CEn cas\u0103 avem c\u0103r\u021Bi \u0219i ciocolat\u0103. Ce bine e acas\u0103!"
        ),
        Letter(
            uppercase = "D", lowercase = "d",
            phoneticSound = "d\u0103",
            words = listOf(
                ExampleWord("Delfin", "\uD83D\uDC2C", listOf("Del", "fin")),
                ExampleWord("Drum", "\uD83D\uDEE4\uFE0F", listOf("Drum")),
                ExampleWord("Dinozaur", "\uD83E\uDD95", listOf("Di", "no", "zaur"))
            ),
            isVowel = false, colorIndex = 5,
            story = "D de la Delfin! Delfinul sare prin valuri. Pe drum vine \u0219i dinozaurul!"
        ),
        Letter(
            uppercase = "E", lowercase = "e",
            phoneticSound = "e",
            words = listOf(
                ExampleWord("Elefant", "\uD83D\uDC18", listOf("E", "le", "fant")),
                ExampleWord("Evantai", "\uD83E\uDE6D", listOf("E", "van", "tai")),
                ExampleWord("Energie", "\u26A1", listOf("E", "ner", "gi", "e"))
            ),
            isVowel = true, colorIndex = 6,
            story = "E de la Elefant! Elefantul mare are energie. Se joac\u0103 \u0219i face: E! E! E!"
        ),
        Letter(
            uppercase = "F", lowercase = "f",
            phoneticSound = "f\u0103",
            words = listOf(
                ExampleWord("Fluture", "\uD83E\uDD8B", listOf("Flu", "tu", "re")),
                ExampleWord("Floare", "\uD83C\uDF38", listOf("Floa", "re")),
                ExampleWord("Fruct", "\uD83C\uDF53", listOf("Fruct"))
            ),
            isVowel = false, colorIndex = 7,
            story = "F de la Fluture! Fluturele zboar\u0103 pe flori. Fructele sunt dulci \u0219i colorate!"
        ),
        Letter(
            uppercase = "G", lowercase = "g",
            phoneticSound = "g\u0103",
            words = listOf(
                ExampleWord("Giraf\u0103", "\uD83E\uDD92", listOf("Gi", "ra", "f\u0103")),
                ExampleWord("Ghind\u0103", "\uD83C\uDF30", listOf("Ghin", "d\u0103")),
                ExampleWord("Gr\u0103din\u0103", "\uD83C\uDF3B", listOf("Gr\u0103", "di", "n\u0103"))
            ),
            isVowel = false, colorIndex = 0,
            story = "G de la Giraf\u0103! Girafa \u00eenalt\u0103 m\u0103n\u00e2nc\u0103 ghinde din gr\u0103din\u0103. Ce lung e g\u00e2tul ei!"
        ),
        Letter(
            uppercase = "H", lowercase = "h",
            phoneticSound = "h\u0103",
            words = listOf(
                ExampleWord("Hipopotam", "\uD83E\uDD9B", listOf("Hi", "po", "po", "tam")),
                ExampleWord("Hart\u0103", "\uD83D\uDDFA\uFE0F", listOf("Har", "t\u0103")),
                ExampleWord("H\u00E2rtie", "\uD83D\uDCC4", listOf("H\u00E2r", "ti", "e"))
            ),
            isVowel = false, colorIndex = 1,
            story = "H de la Hipopotam! Hipopotamul se uit\u0103 pe hart\u0103. Desenez\u0103 pe h\u00E2rtie!"
        ),
        Letter(
            uppercase = "I", lowercase = "i",
            phoneticSound = "i",
            words = listOf(
                ExampleWord("Inim\u0103", "\u2764\uFE0F", listOf("I", "ni", "m\u0103")),
                ExampleWord("Insul\u0103", "\uD83C\uDFDD\uFE0F", listOf("In", "su", "l\u0103")),
                ExampleWord("Iepure", "\uD83D\uDC30", listOf("I", "e", "pu", "re"))
            ),
            isVowel = true, colorIndex = 2,
            story = "I de la Iepure! Iepura\u0219ul are o inim\u0103 mare. Tr\u0103ie\u0219te pe o insul\u0103 frumoas\u0103!"
        ),
        Letter(
            uppercase = "\u00CE", lowercase = "\u00EE",
            phoneticSound = "\u00EE",
            words = listOf(
                ExampleWord("\u00CEnger", "\uD83D\uDC7C", listOf("\u00CEn", "ger")),
                ExampleWord("\u00CEnghe\u021Bat\u0103", "\uD83C\uDF66", listOf("\u00CEn", "ghe", "\u021Ba", "t\u0103")),
                ExampleWord("\u00CEmp\u0103rat", "\uD83D\uDC51", listOf("\u00CEm", "p\u0103", "rat"))
            ),
            isVowel = true, isDiacritic = true, colorIndex = 3,
            story = "\u00CE de la \u00CEnger! \u00CEngerul mic m\u0103n\u00E2nc\u0103 \u00EEnghe\u021Bat\u0103 la \u00EEmp\u0103rat. Ce r\u0103coare!"
        ),
        Letter(
            uppercase = "J", lowercase = "j",
            phoneticSound = "j\u0103",
            words = listOf(
                ExampleWord("Juc\u0103rie", "\uD83E\uDDF8", listOf("Ju", "c\u0103", "ri", "e")),
                ExampleWord("Jungl\u0103", "\uD83C\uDF34", listOf("Jun", "gl\u0103")),
                ExampleWord("Jeleu", "\uD83C\uDF6C", listOf("Je", "leu"))
            ),
            isVowel = false, colorIndex = 4,
            story = "J de la Juc\u0103rie! \u00CEn jungl\u0103 g\u0103sim juc\u0103rii \u0219i jeleu. Ce aventur\u0103!"
        ),
        Letter(
            uppercase = "K", lowercase = "k",
            phoneticSound = "ka",
            words = listOf(
                ExampleWord("Kangur", "\uD83E\uDD98", listOf("Kan", "gur")),
                ExampleWord("Kiwi", "\uD83E\uDD5D", listOf("Ki", "wi")),
                ExampleWord("Koala", "\uD83D\uDC28", listOf("Ko", "a", "la"))
            ),
            isVowel = false, colorIndex = 5,
            story = "K de la Kangur! Kangurul sare \u0219i m\u0103n\u00E2nc\u0103 kiwi cu koala. Hop! Hop!"
        ),
        Letter(
            uppercase = "L", lowercase = "l",
            phoneticSound = "l\u0103",
            words = listOf(
                ExampleWord("Leu", "\uD83E\uDD81", listOf("Leu")),
                ExampleWord("Luna", "\uD83C\uDF19", listOf("Lu", "na")),
                ExampleWord("L\u0103ptuc\u0103", "\uD83E\uDD6C", listOf("L\u0103p", "tu", "c\u0103"))
            ),
            isVowel = false, colorIndex = 6,
            story = "L de la Leu! Leul se uit\u0103 la lun\u0103 \u0219i manc\u0103 l\u0103ptuc\u0103. Ce noapte frumoas\u0103!"
        ),
        Letter(
            uppercase = "M", lowercase = "m",
            phoneticSound = "m\u0103",
            words = listOf(
                ExampleWord("Mama", "\uD83D\uDC69", listOf("Ma", "ma")),
                ExampleWord("Minge", "\u26BD", listOf("Min", "ge")),
                ExampleWord("Maimu\u021B\u0103", "\uD83D\uDC35", listOf("Mai", "mu", "\u021B\u0103"))
            ),
            isVowel = false, colorIndex = 7,
            story = "M de la Mama! Mama se joac\u0103 cu mingea. Maimu\u021Ba r\u00E2de \u0219i bate din palme!"
        ),
        Letter(
            uppercase = "N", lowercase = "n",
            phoneticSound = "n\u0103",
            words = listOf(
                ExampleWord("Nor", "\u2601\uFE0F", listOf("Nor")),
                ExampleWord("Noapte", "\uD83C\uDF19", listOf("Noap", "te")),
                ExampleWord("Nuci", "\uD83E\uDD5C", listOf("Nuci"))
            ),
            isVowel = false, colorIndex = 0,
            story = "N de la Nor! Norii albi plutesc pe cer. Noaptea m\u0103n\u00E2nc\u0103m nuci sub stele!"
        ),
        Letter(
            uppercase = "O", lowercase = "o",
            phoneticSound = "o",
            words = listOf(
                ExampleWord("Ochi", "\uD83D\uDC41\uFE0F", listOf("O", "chi")),
                ExampleWord("Ou", "\uD83E\uDD5A", listOf("Ou")),
                ExampleWord("Oaie", "\uD83D\uDC11", listOf("O", "a", "ie"))
            ),
            isVowel = true, colorIndex = 1,
            story = "O de la Oaie! Oaia face: O! O! Cu ochii mari se uit\u0103 la ou\u0103."
        ),
        Letter(
            uppercase = "P", lowercase = "p",
            phoneticSound = "p\u0103",
            words = listOf(
                ExampleWord("Pisic\u0103", "\uD83D\uDC31", listOf("Pi", "si", "c\u0103")),
                ExampleWord("Pas\u0103re", "\uD83D\uDC26", listOf("Pa", "s\u0103", "re")),
                ExampleWord("Pinguin", "\uD83D\uDC27", listOf("Pin", "gu", "in"))
            ),
            isVowel = false, colorIndex = 2,
            story = "P de la Pisic\u0103! Pisica vede o pas\u0103re. Pinguinul se joac\u0103 pe ghea\u021B\u0103!"
        ),
        Letter(
            uppercase = "Q", lowercase = "q",
            phoneticSound = "kiu",
            words = listOf(
                ExampleWord("Quasar", "\u2728", listOf("Qua", "sar")),
                ExampleWord("Quiz", "\u2753", listOf("Quiz")),
                ExampleWord("Quartet", "\uD83C\uDFB5", listOf("Quar", "tet"))
            ),
            isVowel = false, colorIndex = 3,
            story = "Q de la Quiz! R\u0103spundem la quiz \u0219i ascult\u0103m un quartet de stele!"
        ),
        Letter(
            uppercase = "R", lowercase = "r",
            phoneticSound = "r\u0103",
            words = listOf(
                ExampleWord("Rac", "\uD83E\uDD80", listOf("Rac")),
                ExampleWord("Roat\u0103", "\uD83C\uDFA1", listOf("Ro", "a", "t\u0103")),
                ExampleWord("Rechin", "\uD83E\uDD88", listOf("Re", "chin"))
            ),
            isVowel = false, colorIndex = 4,
            story = "R de la Rac! Racul se plimb\u0103 pe plaj\u0103. Rechinul se \u00EEnv\u00E2rte ca o roat\u0103!"
        ),
        Letter(
            uppercase = "S", lowercase = "s",
            phoneticSound = "s\u0103",
            words = listOf(
                ExampleWord("Soare", "\u2600\uFE0F", listOf("So", "a", "re")),
                ExampleWord("Stea", "\u2B50", listOf("Stea")),
                ExampleWord("Sarpe", "\uD83D\uDC0D", listOf("\u0218ar", "pe"))
            ),
            isVowel = false, colorIndex = 5,
            story = "S de la Soare! Soarele str\u0103luce\u0219te pe cer. Stelele apar noaptea!"
        ),
        Letter(
            uppercase = "\u0218", lowercase = "\u0219",
            phoneticSound = "\u0219\u0103",
            words = listOf(
                ExampleWord("\u0218arpe", "\uD83D\uDC0D", listOf("\u0218ar", "pe")),
                ExampleWord("\u0218coal\u0103", "\uD83C\uDFEB", listOf("\u0218coa", "l\u0103")),
                ExampleWord("\u0218oricei", "\uD83D\uDC2D", listOf("\u0218o", "ri", "cei"))
            ),
            isVowel = false, isDiacritic = true, colorIndex = 6,
            story = "\u0218 de la \u0218coal\u0103! La \u0219coal\u0103 \u00EEnv\u0103\u021B\u0103m despre \u0219arpe \u0219i \u0219oricei. \u0218\u0219\u0219!"
        ),
        Letter(
            uppercase = "T", lowercase = "t",
            phoneticSound = "t\u0103",
            words = listOf(
                ExampleWord("Tren", "\uD83D\uDE82", listOf("Tren")),
                ExampleWord("Tigru", "\uD83D\uDC2F", listOf("Ti", "gru")),
                ExampleWord("Tobogan", "\uD83D\uDEDD", listOf("To", "bo", "gan"))
            ),
            isVowel = false, colorIndex = 7,
            story = "T de la Tren! Trenul merge tu-tu! Tigrul se d\u0103 pe tobogan. Ce distractie!"
        ),
        Letter(
            uppercase = "\u021A", lowercase = "\u021B",
            phoneticSound = "\u021B\u0103",
            words = listOf(
                ExampleWord("\u021Aestoas\u0103", "\uD83D\uDC22", listOf("\u021Aes", "to", "a", "s\u0103")),
                ExampleWord("\u021Aar\u0103", "\uD83C\uDDF7\uD83C\uDDF4", listOf("\u021Aa", "r\u0103")),
                ExampleWord("\u021A\u00E2n\u021Bar", "\uD83E\uDD9F", listOf("\u021A\u00E2n", "\u021Bar"))
            ),
            isVowel = false, isDiacritic = true, colorIndex = 0,
            story = "\u021A de la \u021Aestoas\u0103! \u021Aestoasa merge \u00EEncet prin \u021Bar\u0103. \u021A\u00E2n\u021Barul z\u00E2\u00E2\u00E2!"
        ),
        Letter(
            uppercase = "U", lowercase = "u",
            phoneticSound = "u",
            words = listOf(
                ExampleWord("Urs", "\uD83D\uDC3B", listOf("Urs")),
                ExampleWord("Umbrel\u0103", "\u2602\uFE0F", listOf("Um", "bre", "l\u0103")),
                ExampleWord("Unicorn", "\uD83E\uDD84", listOf("U", "ni", "corn"))
            ),
            isVowel = true, colorIndex = 1,
            story = "U de la Urs! Ursul are o umbrel\u0103. Unicornul face: U! U! U!"
        ),
        Letter(
            uppercase = "V", lowercase = "v",
            phoneticSound = "v\u0103",
            words = listOf(
                ExampleWord("Vulpe", "\uD83E\uDD8A", listOf("Vul", "pe")),
                ExampleWord("Vapor", "\uD83D\uDEA2", listOf("Va", "por")),
                ExampleWord("V\u00E2nt", "\uD83D\uDCA8", listOf("V\u00E2nt"))
            ),
            isVowel = false, colorIndex = 2,
            story = "V de la Vulpe! Vulpea alearg\u0103 cu v\u00E2ntul. Vaporul pluteste pe mare!"
        ),
        Letter(
            uppercase = "W", lowercase = "w",
            phoneticSound = "dublu v\u0103",
            words = listOf(
                ExampleWord("Weekend", "\uD83C\uDFD6\uFE0F", listOf("We", "e", "kend")),
                ExampleWord("Waffle", "\uD83E\uDDC7", listOf("Wa", "ffle")),
                ExampleWord("WiFi", "\uD83D\uDCF6", listOf("Wi", "Fi"))
            ),
            isVowel = false, colorIndex = 3,
            story = "W de la Weekend! \u00CEn weekend manc\u0103m waffle \u0219i ne juc\u0103m pe WiFi!"
        ),
        Letter(
            uppercase = "X", lowercase = "x",
            phoneticSound = "ics",
            words = listOf(
                ExampleWord("Xilofon", "\uD83C\uDFB5", listOf("Xi", "lo", "fon")),
                ExampleWord("Raze X", "\uD83E\uDE7B", listOf("Ra", "ze", "X")),
                ExampleWord("Taxi", "\uD83D\uDE95", listOf("Ta", "xi"))
            ),
            isVowel = false, colorIndex = 4,
            story = "X de la Xilofon! C\u00E2nt\u0103m la xilofon \u00EEn taxi. Ding dong ding!"
        ),
        Letter(
            uppercase = "Y", lowercase = "y",
            phoneticSound = "igrec",
            words = listOf(
                ExampleWord("Yoga", "\uD83E\uDDD8", listOf("Yo", "ga")),
                ExampleWord("Iaurt", "\uD83E\uDD5B", listOf("I", "a", "urt")),
                ExampleWord("Yoyo", "\uD83E\uDE80", listOf("Yo", "yo"))
            ),
            isVowel = false, colorIndex = 5,
            story = "Y de la Yoga! Facem yoga \u0219i munc\u0103m iaurt. Yoyo-ul se \u00EEnv\u00E2rte!"
        ),
        Letter(
            uppercase = "Z", lowercase = "z",
            phoneticSound = "z\u0103",
            words = listOf(
                ExampleWord("Zebr\u0103", "\uD83E\uDD93", listOf("Ze", "br\u0103")),
                ExampleWord("Zmeu", "\uD83E\uDE81", listOf("Zmeu")),
                ExampleWord("Z\u00E2n\u0103", "\uD83E\uDDDA", listOf("Z\u00E2", "n\u0103"))
            ),
            isVowel = false, colorIndex = 6,
            story = "Z de la Zebr\u0103! Zebra alearg\u0103 cu zmeul. Z\u00E2na face vraji frumoase!"
        )
    )

    val vowels = letters.filter { it.isVowel }
    val consonants = letters.filter { !it.isVowel }
    val diacritics = letters.filter { it.isDiacritic }

    fun getLetterByUppercase(char: String): Letter? =
        letters.find { it.uppercase == char }

    fun getRandomLetters(count: Int, exclude: String? = null): List<Letter> =
        letters.filter { it.uppercase != exclude }.shuffled().take(count)

    val basicVowels = listOf("A", "E", "I", "O", "U")

    val simpleSyllableConsonants = consonants
        .filter { it.uppercase in listOf("B", "C", "D", "F", "G", "L", "M", "N", "P", "R", "S", "T", "V", "Z") }

    val simpleWords = mapOf(
        "MAMA" to listOf("MA", "MA"),
        "TATA" to listOf("TA", "TA"),
        "CAS\u0102" to listOf("CA", "S\u0102"),
        "MAS\u0102" to listOf("MA", "S\u0102"),
        "MARE" to listOf("MA", "RE"),
        "SARE" to listOf("SA", "RE"),
        "LUNA" to listOf("LU", "NA"),
        "BINE" to listOf("BI", "NE"),
        "RAME" to listOf("RA", "ME"),
        "RAT\u0102" to listOf("RA", "T\u0102"),
        "VALE" to listOf("VA", "LE"),
        "POLO" to listOf("PO", "LO"),
        "NOTE" to listOf("NO", "TE"),
        "BILE" to listOf("BI", "LE"),
        "CARE" to listOf("CA", "RE"),
        "PAR\u0102" to listOf("PA", "R\u0102"),
        "GAR\u0102" to listOf("GA", "R\u0102"),
        "FAT\u0102" to listOf("FA", "T\u0102"),
        "RO\u0218U" to listOf("RO", "\u0218U"),
        "VERDE" to listOf("VER", "DE")
    )

    // Rhyming word pairs for the Rhyming Game
    val rhymingPairs = listOf(
        Pair("cas\u0103", "mas\u0103"),
        Pair("soare", "floare"),
        Pair("cer", "ver"),
        Pair("leu", "zmeu"),
        Pair("nor", "dor"),
        Pair("stea", "mea"),
        Pair("rac", "sac"),
        Pair("bun", "tun"),
        Pair("urs", "curs"),
        Pair("fat\u0103", "rat\u0103"),
        Pair("carte", "parte"),
        Pair("mare", "sare"),
        Pair("lac", "mac"),
        Pair("pom", "som"),
        Pair("val", "cal")
    )

    // Simple sentences for the Sentence Building game
    data class SentenceData(val words: List<String>, val emoji: String)

    val sentences = listOf(
        SentenceData(listOf("Pisica", "bea", "lapte"), "\uD83D\uDC31"),
        SentenceData(listOf("Ursul", "manc\u0103", "miere"), "\uD83D\uDC3B"),
        SentenceData(listOf("Pasărea", "zboar\u0103", "sus"), "\uD83D\uDC26"),
        SentenceData(listOf("Copilul", "se", "joac\u0103"), "\uD83D\uDC66"),
        SentenceData(listOf("Soarele", "str\u0103luce\u0219te", "tare"), "\u2600\uFE0F"),
        SentenceData(listOf("Florile", "sunt", "frumoase"), "\uD83C\uDF3A"),
        SentenceData(listOf("C\u00E2inele", "alearg\u0103", "repede"), "\uD83D\uDC15"),
        SentenceData(listOf("Luna", "e", "rotund\u0103"), "\uD83C\uDF15"),
        SentenceData(listOf("Mama", "g\u0103te\u0219te", "bine"), "\uD83D\uDC69"),
        SentenceData(listOf("Iepurele", "sare", "vesel"), "\uD83D\uDC30"),
        SentenceData(listOf("Trenul", "merge", "repede"), "\uD83D\uDE82"),
        SentenceData(listOf("Zebra", "are", "dungi"), "\uD83E\uDD93"),
        SentenceData(listOf("Vulpea", "e", "de\u0219teapt\u0103"), "\uD83E\uDD8A"),
        SentenceData(listOf("Leul", "este", "curajos"), "\uD83E\uDD81"),
        SentenceData(listOf("Elefantul", "e", "mare"), "\uD83D\uDC18")
    )

    // Diacritic comparison exercises
    data class DiacriticExercise(
        val diacriticLetter: String,
        val baseLetter: String,
        val diacriticWord: String,
        val diacriticEmoji: String,
        val baseWord: String,
        val baseEmoji: String
    )

    val diacriticExercises = listOf(
        DiacriticExercise("\u0102", "A", "M\u0103r", "\uD83C\uDF4E", "Mar", "\uD83C\uDF0A"),
        DiacriticExercise("\u0102", "A", "P\u0103r", "\uD83C\uDF50", "Par", "\uD83C\uDF33"),
        DiacriticExercise("\u00C2", "A", "C\u00E2ine", "\uD83D\uDC15", "Caine", "\u2753"),
        DiacriticExercise("\u00C2", "A", "P\u00E2ine", "\uD83C\uDF5E", "Paine", "\u2753"),
        DiacriticExercise("\u00CE", "I", "\u00CEnger", "\uD83D\uDC7C", "Inger", "\u2753"),
        DiacriticExercise("\u00CE", "I", "\u00CEnghe\u021Bat\u0103", "\uD83C\uDF66", "Inghetata", "\u2753"),
        DiacriticExercise("\u0218", "S", "\u0218arpe", "\uD83D\uDC0D", "Sarpe", "\u2753"),
        DiacriticExercise("\u0218", "S", "\u0218coal\u0103", "\uD83C\uDFEB", "Scoal\u0103", "\u2753"),
        DiacriticExercise("\u021A", "T", "\u021Aestoas\u0103", "\uD83D\uDC22", "Testoasa", "\u2753"),
        DiacriticExercise("\u021A", "T", "\u021Aar\u0103", "\uD83C\uDDF7\uD83C\uDDF4", "Tara", "\u2753")
    )

    // Daily challenge word list
    data class DailyWord(val word: String, val emoji: String, val letter: String, val meaning: String)

    val dailyWords = listOf(
        DailyWord("Albina", "\uD83D\uDC1D", "A", "Un insect care face miere"),
        DailyWord("Balon", "\uD83C\uDF88", "B", "Zboar\u0103 sus \u00EEn cer"),
        DailyWord("Carte", "\uD83D\uDCD6", "C", "O citim \u0219i \u00EEnv\u0103\u021B\u0103m"),
        DailyWord("Delfin", "\uD83D\uDC2C", "D", "Sare prin valuri"),
        DailyWord("Elefant", "\uD83D\uDC18", "E", "Cel mai mare animal de uscat"),
        DailyWord("Fluture", "\uD83E\uDD8B", "F", "Zboar\u0103 pe flori"),
        DailyWord("Giraf\u0103", "\uD83E\uDD92", "G", "Are g\u00E2tul foarte lung"),
        DailyWord("Iepure", "\uD83D\uDC30", "I", "Sare \u0219i are urechi lungi"),
        DailyWord("Leu", "\uD83E\uDD81", "L", "Regele animalelor"),
        DailyWord("Minge", "\u26BD", "M", "Ne juc\u0103m cu ea"),
        DailyWord("Nor", "\u2601\uFE0F", "N", "Pluteste pe cer"),
        DailyWord("Ochi", "\uD83D\uDC41\uFE0F", "O", "Vedem cu ei"),
        DailyWord("Pisic\u0103", "\uD83D\uDC31", "P", "Face miau miau"),
        DailyWord("Rac", "\uD83E\uDD80", "R", "Merge lateral pe plaj\u0103"),
        DailyWord("Soare", "\u2600\uFE0F", "S", "Ne \u00EEnc\u0103lze\u0219te \u0219i str\u0103luce\u0219te"),
        DailyWord("Tren", "\uD83D\uDE82", "T", "Merge pe \u0219ine tu-tu!"),
        DailyWord("Urs", "\uD83D\uDC3B", "U", "Doarme iarna"),
        DailyWord("Vulpe", "\uD83E\uDD8A", "V", "De\u0219teapt\u0103 \u0219i portocalie"),
        DailyWord("Zebr\u0103", "\uD83E\uDD93", "Z", "Are dungi albe \u0219i negre"),
        DailyWord("Unicorn", "\uD83E\uDD84", "U", "Calul magic cu corn"),
        DailyWord("Tigru", "\uD83D\uDC2F", "T", "Felina cu dungi"),
        DailyWord("Stea", "\u2B50", "S", "Str\u0103luce\u0219te noaptea pe cer"),
        DailyWord("Pinguin", "\uD83D\uDC27", "P", "Tr\u0103ie\u0219te la frig"),
        DailyWord("Luna", "\uD83C\uDF19", "L", "O vedem noaptea pe cer"),
        DailyWord("Koala", "\uD83D\uDC28", "K", "M\u0103n\u00E2nc\u0103 eucalipt"),
        DailyWord("Floare", "\uD83C\uDF3A", "F", "Creste \u00EEn gr\u0103din\u0103"),
        DailyWord("Oaie", "\uD83D\uDC11", "O", "Face bee bee"),
        DailyWord("Maimu\u021B\u0103", "\uD83D\uDC35", "M", "Se ca\u021B\u0103r\u0103 \u00EEn copaci"),
        DailyWord("Cas\u0103", "\uD83C\uDFE0", "C", "Locuim \u00EEn ea"),
        DailyWord("Arici", "\uD83E\uDD94", "A", "Are \u021Bepi pe spate"),
        DailyWord("Z\u00E2n\u0103", "\uD83E\uDDDA", "Z", "Face vr\u0103ji frumoase")
    )

    fun getDailyWord(dayOfYear: Int): DailyWord {
        return dailyWords[dayOfYear % dailyWords.size]
    }
}
