package com.danvaly.alfabet.data

import com.danvaly.alfabet.data.model.ExampleWord
import com.danvaly.alfabet.data.model.Letter

object RomanianAlphabet {

    val letters: List<Letter> = listOf(
        Letter(
            uppercase = "A", lowercase = "a",
            phoneticSound = "a",
            words = listOf(
                ExampleWord("Avion", "✈️", listOf("A", "vi", "on")),
                ExampleWord("Albină", "🐝", listOf("Al", "bi", "nă")),
                ExampleWord("Arici", "🦔", listOf("A", "ri", "ci"))
            ),
            isVowel = true, colorIndex = 0,
            story = "A de la Avion! Un avion mare zboară pe cer. Albina zâmbește și zice: A! A! A!"
        ),
        Letter(
            uppercase = "Ă", lowercase = "ă",
            phoneticSound = "ă",
            words = listOf(
                ExampleWord("Măr", "🍎", listOf("Măr")),
                ExampleWord("Păr", "🍐", listOf("Păr")),
                ExampleWord("Văr", "👦", listOf("Văr"))
            ),
            isVowel = true, isDiacritic = true, colorIndex = 1,
            story = "Ă de la Măr! Un măr roșu cade din pom. Părul și mărul sunt prieteni!"
        ),
        Letter(
            uppercase = "Â", lowercase = "â",
            phoneticSound = "â",
            words = listOf(
                ExampleWord("Pâine", "🍞", listOf("Pâi", "ne")),
                ExampleWord("Câine", "🐕", listOf("Câi", "ne")),
                ExampleWord("Mâine", "📅", listOf("Mâi", "ne"))
            ),
            isVowel = true, isDiacritic = true, colorIndex = 2,
            story = "Â de la Câine! Câinele mănâncă pâine și așteaptă mâine să se joace!"
        ),
        Letter(
            uppercase = "B", lowercase = "b",
            phoneticSound = "bă",
            words = listOf(
                ExampleWord("Balon", "🎈", listOf("Ba", "lon")),
                ExampleWord("Barcă", "⛵", listOf("Bar", "că")),
                ExampleWord("Bunic", "👴", listOf("Bu", "nic"))
            ),
            isVowel = false, colorIndex = 3,
            story = "B de la Balon! Un balon roșu zboară sus. Bunicul îl prinde și zâmbește!"
        ),
        Letter(
            uppercase = "C", lowercase = "c",
            phoneticSound = "că",
            words = listOf(
                ExampleWord("Casă", "🏠", listOf("Ca", "să")),
                ExampleWord("Carte", "📖", listOf("Car", "te")),
                ExampleWord("Ciocolată", "🍫", listOf("Cio", "co", "la", "tă"))
            ),
            isVowel = false, colorIndex = 4,
            story = "C de la Casă! În casă avem cărți și ciocolată. Ce bine e acasă!"
        ),
        Letter(
            uppercase = "D", lowercase = "d",
            phoneticSound = "dă",
            words = listOf(
                ExampleWord("Delfin", "🐬", listOf("Del", "fin")),
                ExampleWord("Drum", "🛤️", listOf("Drum")),
                ExampleWord("Dinozaur", "🦕", listOf("Di", "no", "za", "ur"))
            ),
            isVowel = false, colorIndex = 5,
            story = "D de la Delfin! Delfinul sare prin valuri. Pe drum vine și dinozaurul!"
        ),
        Letter(
            uppercase = "E", lowercase = "e",
            phoneticSound = "e",
            words = listOf(
                ExampleWord("Elefant", "🐘", listOf("E", "le", "fant")),
                ExampleWord("Elicopter", "🚁", listOf("E", "li", "cop", "ter")),
                ExampleWord("Energie", "⚡", listOf("E", "ner", "gi", "e"))
            ),
            isVowel = true, colorIndex = 6,
            story = "E de la Elefant! Elefantul mare are energie. Se joacă și face: E! E! E!"
        ),
        Letter(
            uppercase = "F", lowercase = "f",
            phoneticSound = "fă",
            words = listOf(
                ExampleWord("Fluture", "🦋", listOf("Flu", "tu", "re")),
                ExampleWord("Floare", "🌸", listOf("Floa", "re")),
                ExampleWord("Fruct", "🍓", listOf("Fruct"))
            ),
            isVowel = false, colorIndex = 7,
            story = "F de la Fluture! Fluturele zboară pe flori. Fructele sunt dulci și colorate!"
        ),
        Letter(
            uppercase = "G", lowercase = "g",
            phoneticSound = "gă",
            words = listOf(
                ExampleWord("Girafă", "🦒", listOf("Gi", "ra", "fă")),
                ExampleWord("Ghindă", "🌰", listOf("Ghin", "dă")),
                ExampleWord("Grădină", "🌻", listOf("Gră", "di", "nă"))
            ),
            isVowel = false, colorIndex = 0,
            story = "G de la Girafă! Girafa înaltă mănâncă ghinde din grădină. Ce lung e gâtul ei!"
        ),
        Letter(
            uppercase = "H", lowercase = "h",
            phoneticSound = "hă",
            words = listOf(
                ExampleWord("Hamster", "🐹", listOf("Hams", "ter")),
                ExampleWord("Hartă", "🗺️", listOf("Har", "tă")),
                ExampleWord("Hârtie", "📄", listOf("Hâr", "ti", "e"))
            ),
            isVowel = false, colorIndex = 1,
            story = "H de la Hamster! Hamsterul se uită pe hartă. Apoi desenează pe hârtie!"
        ),
        Letter(
            uppercase = "I", lowercase = "i",
            phoneticSound = "i",
            words = listOf(
                ExampleWord("Inimă", "❤️", listOf("I", "ni", "mă")),
                ExampleWord("Insulă", "🏝️", listOf("In", "su", "lă")),
                ExampleWord("Iepure", "🐰", listOf("Ie", "pu", "re"))
            ),
            isVowel = true, colorIndex = 2,
            story = "I de la Iepure! Iepurașul are o inimă mare. Trăiește pe o insulă frumoasă!"
        ),
        Letter(
            uppercase = "Î", lowercase = "î",
            phoneticSound = "î",
            words = listOf(
                ExampleWord("Înger", "👼", listOf("În", "ger")),
                ExampleWord("Înghețată", "🍦", listOf("În", "ghe", "ța", "tă")),
                ExampleWord("Împărat", "👑", listOf("Îm", "pă", "rat"))
            ),
            isVowel = true, isDiacritic = true, colorIndex = 3,
            story = "Î de la Înger! Îngerul mic mănâncă înghețată la împărat. Ce răcoare!"
        ),
        Letter(
            uppercase = "J", lowercase = "j",
            phoneticSound = "jă",
            words = listOf(
                ExampleWord("Jucărie", "🎁", listOf("Ju", "că", "ri", "e")),
                ExampleWord("Junglă", "🌴", listOf("Jun", "glă")),
                ExampleWord("Jeleu", "🍬", listOf("Je", "leu"))
            ),
            isVowel = false, colorIndex = 4,
            story = "J de la Jucărie! În junglă găsim jucării și jeleu. Ce aventură!"
        ),
        Letter(
            uppercase = "K", lowercase = "k",
            phoneticSound = "ka",
            words = listOf(
                ExampleWord("Karate", "🥋", listOf("Ka", "ra", "te")),
                ExampleWord("Kiwi", "🥝", listOf("Ki", "wi")),
                ExampleWord("Koala", "🐨", listOf("Ko", "a", "la"))
            ),
            isVowel = false, colorIndex = 5,
            story = "K de la Koala! Ursulețul koala mănâncă kiwi și face karate. Hop! Hop!"
        ),
        Letter(
            uppercase = "L", lowercase = "l",
            phoneticSound = "lă",
            words = listOf(
                ExampleWord("Leu", "🦁", listOf("Leu")),
                ExampleWord("Lună", "🌙", listOf("Lu", "nă")),
                ExampleWord("Lămâie", "🍋", listOf("Lă", "mâ", "ie"))
            ),
            isVowel = false, colorIndex = 6,
            story = "L de la Leu! Leul se uită la lună și gustă o lămâie. Ce noapte frumoasă!"
        ),
        Letter(
            uppercase = "M", lowercase = "m",
            phoneticSound = "mă",
            words = listOf(
                ExampleWord("Mama", "👩", listOf("Ma", "ma")),
                ExampleWord("Minge", "⚽", listOf("Min", "ge")),
                ExampleWord("Maimuță", "🐵", listOf("Mai", "mu", "ță"))
            ),
            isVowel = false, colorIndex = 7,
            story = "M de la Mama! Mama se joacă cu mingea. Maimuța râde și bate din palme!"
        ),
        Letter(
            uppercase = "N", lowercase = "n",
            phoneticSound = "nă",
            words = listOf(
                ExampleWord("Nor", "☁️", listOf("Nor")),
                ExampleWord("Noapte", "🌙", listOf("Noap", "te")),
                ExampleWord("Nuci", "🥜", listOf("Nuci"))
            ),
            isVowel = false, colorIndex = 0,
            story = "N de la Nor! Norii albi plutesc pe cer. Noaptea mâncăm nuci sub stele!"
        ),
        Letter(
            uppercase = "O", lowercase = "o",
            phoneticSound = "o",
            words = listOf(
                ExampleWord("Ochi", "👁️", listOf("O", "chi")),
                ExampleWord("Ou", "🥚", listOf("Ou")),
                ExampleWord("Oaie", "🐑", listOf("Oa", "ie"))
            ),
            isVowel = true, colorIndex = 1,
            story = "O de la Oaie! Oaia face: O! O! Cu ochii mari se uită la ouă."
        ),
        Letter(
            uppercase = "P", lowercase = "p",
            phoneticSound = "pă",
            words = listOf(
                ExampleWord("Pisică", "🐱", listOf("Pi", "si", "că")),
                ExampleWord("Pasăre", "🐦", listOf("Pa", "să", "re")),
                ExampleWord("Pinguin", "🐧", listOf("Pin", "gu", "in"))
            ),
            isVowel = false, colorIndex = 2,
            story = "P de la Pisică! Pisica vede o pasăre. Pinguinul se joacă pe gheață!"
        ),
        Letter(
            uppercase = "Q", lowercase = "q",
            phoneticSound = "kiu",
            words = listOf(
                ExampleWord("Quasar", "✨", listOf("Qua", "sar")),
                ExampleWord("Quiz", "❓", listOf("Quiz")),
                ExampleWord("Quartet", "🎵", listOf("Quar", "tet"))
            ),
            isVowel = false, colorIndex = 3,
            story = "Q de la Quiz! Răspundem la quiz și ascultăm un quartet de stele!"
        ),
        Letter(
            uppercase = "R", lowercase = "r",
            phoneticSound = "ră",
            words = listOf(
                ExampleWord("Rac", "🦀", listOf("Rac")),
                ExampleWord("Roată", "🎡", listOf("Roa", "tă")),
                ExampleWord("Rechin", "🦈", listOf("Re", "chin"))
            ),
            isVowel = false, colorIndex = 4,
            story = "R de la Rac! Racul se plimbă pe plajă. Rechinul se învârte ca o roată!"
        ),
        Letter(
            uppercase = "S", lowercase = "s",
            phoneticSound = "să",
            words = listOf(
                ExampleWord("Soare", "☀️", listOf("Soa", "re")),
                ExampleWord("Stea", "⭐", listOf("Stea")),
                ExampleWord("Sanie", "🛷", listOf("Sa", "ni", "e"))
            ),
            isVowel = false, colorIndex = 5,
            story = "S de la Soare! Soarele strălucește pe cer. Stelele apar noaptea!"
        ),
        Letter(
            uppercase = "Ș", lowercase = "ș",
            phoneticSound = "șă",
            words = listOf(
                ExampleWord("Șarpe", "🐍", listOf("Șar", "pe")),
                ExampleWord("Școală", "🏫", listOf("Școa", "lă")),
                ExampleWord("Șoricei", "🐭", listOf("Șo", "ri", "cei"))
            ),
            isVowel = false, isDiacritic = true, colorIndex = 6,
            story = "Ș de la Școală! La școală învățăm despre șarpe și șoricei. Șșș!"
        ),
        Letter(
            uppercase = "T", lowercase = "t",
            phoneticSound = "tă",
            words = listOf(
                ExampleWord("Tren", "🚂", listOf("Tren")),
                ExampleWord("Tigru", "🐯", listOf("Ti", "gru")),
                ExampleWord("Tobă", "🥁", listOf("To", "bă"))
            ),
            isVowel = false, colorIndex = 7,
            story = "T de la Tren! Trenul merge tu-tu! Tigrul bate la tobă. Ce distracție!"
        ),
        Letter(
            uppercase = "Ț", lowercase = "ț",
            phoneticSound = "ță",
            words = listOf(
                ExampleWord("Țestoasă", "🐢", listOf("Țes", "toa", "să")),
                ExampleWord("Țară", "🇷🇴", listOf("Ța", "ră")),
                ExampleWord("Țap", "🐐", listOf("Țap"))
            ),
            isVowel = false, isDiacritic = true, colorIndex = 0,
            story = "Ț de la Țestoasă! Țestoasa merge încet prin țară. Țapul face: behehe!"
        ),
        Letter(
            uppercase = "U", lowercase = "u",
            phoneticSound = "u",
            words = listOf(
                ExampleWord("Urs", "🐻", listOf("Urs")),
                ExampleWord("Umbrelă", "☂️", listOf("Um", "bre", "lă")),
                ExampleWord("Unicorn", "🦄", listOf("U", "ni", "corn"))
            ),
            isVowel = true, colorIndex = 1,
            story = "U de la Urs! Ursul are o umbrelă. Unicornul face: U! U! U!"
        ),
        Letter(
            uppercase = "V", lowercase = "v",
            phoneticSound = "vă",
            words = listOf(
                ExampleWord("Vulpe", "🦊", listOf("Vul", "pe")),
                ExampleWord("Vapor", "🚢", listOf("Va", "por")),
                ExampleWord("Vânt", "💨", listOf("Vânt"))
            ),
            isVowel = false, colorIndex = 2,
            story = "V de la Vulpe! Vulpea aleargă cu vântul. Vaporul plutește pe mare!"
        ),
        Letter(
            uppercase = "W", lowercase = "w",
            phoneticSound = "dublu vă",
            words = listOf(
                ExampleWord("Weekend", "🏖️", listOf("Week", "end")),
                ExampleWord("Windsurf", "🏄", listOf("Wind", "surf")),
                ExampleWord("WiFi", "📶", listOf("Wi", "Fi"))
            ),
            isVowel = false, colorIndex = 3,
            story = "W de la Weekend! În weekend facem windsurf și ne jucăm pe WiFi!"
        ),
        Letter(
            uppercase = "X", lowercase = "x",
            phoneticSound = "ics",
            words = listOf(
                ExampleWord("Xilofon", "🎵", listOf("Xi", "lo", "fon")),
                ExampleWord("Saxofon", "🎷", listOf("Sa", "xo", "fon")),
                ExampleWord("Taxi", "🚕", listOf("Ta", "xi"))
            ),
            isVowel = false, colorIndex = 4,
            story = "X de la Xilofon! Cântăm la xilofon și la saxofon în taxi. Ding dong ding!"
        ),
        Letter(
            uppercase = "Y", lowercase = "y",
            phoneticSound = "igrec",
            words = listOf(
                ExampleWord("Yoga", "🧘", listOf("Yo", "ga")),
                ExampleWord("Rugby", "🏉", listOf("Rug", "by")),
                ExampleWord("Yeti", "❄️", listOf("Ye", "ti"))
            ),
            isVowel = false, colorIndex = 5,
            story = "Y de la Yoga! Facem yoga, apoi jucăm rugby. Yeti ne privește din zăpadă!"
        ),
        Letter(
            uppercase = "Z", lowercase = "z",
            phoneticSound = "ză",
            words = listOf(
                ExampleWord("Zebră", "🦓", listOf("Ze", "bră")),
                ExampleWord("Zar", "🎲", listOf("Zar")),
                ExampleWord("Zână", "🧚", listOf("Zâ", "nă"))
            ),
            isVowel = false, colorIndex = 6,
            story = "Z de la Zebră! Zebra aruncă zarul. Zâna face vrăji frumoase!"
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
        "CASĂ" to listOf("CA", "SĂ"),
        "MASĂ" to listOf("MA", "SĂ"),
        "MARE" to listOf("MA", "RE"),
        "SARE" to listOf("SA", "RE"),
        "LUNĂ" to listOf("LU", "NĂ"),
        "BINE" to listOf("BI", "NE"),
        "VACĂ" to listOf("VA", "CĂ"),
        "RATĂ" to listOf("RA", "TĂ"),
        "VALE" to listOf("VA", "LE"),
        "SUPĂ" to listOf("SU", "PĂ"),
        "NOTE" to listOf("NO", "TE"),
        "CANĂ" to listOf("CA", "NĂ"),
        "LUPI" to listOf("LU", "PI"),
        "PARĂ" to listOf("PA", "RĂ"),
        "GARĂ" to listOf("GA", "RĂ"),
        "FATĂ" to listOf("FA", "TĂ"),
        "MURĂ" to listOf("MU", "RĂ"),
        "ROZĂ" to listOf("RO", "ZĂ")
    )

    // Rhyming word pairs for the Rhyming Game
    val rhymingPairs = listOf(
        Pair("casă", "masă"),
        Pair("soare", "floare"),
        Pair("cer", "ger"),
        Pair("leu", "zmeu"),
        Pair("nor", "dor"),
        Pair("stea", "mea"),
        Pair("rac", "sac"),
        Pair("bun", "tun"),
        Pair("urs", "curs"),
        Pair("fată", "rată"),
        Pair("carte", "parte"),
        Pair("mare", "sare"),
        Pair("lac", "mac"),
        Pair("pom", "som"),
        Pair("val", "cal")
    )

    // Simple sentences for the Sentence Building game
    data class SentenceData(val words: List<String>, val emoji: String)

    val sentences = listOf(
        SentenceData(listOf("Pisica", "bea", "lapte"), "🐱"),
        SentenceData(listOf("Ursul", "mănâncă", "miere"), "🐻"),
        SentenceData(listOf("Pasărea", "zboară", "sus"), "🐦"),
        SentenceData(listOf("Copilul", "se", "joacă"), "👦"),
        SentenceData(listOf("Soarele", "strălucește", "tare"), "☀️"),
        SentenceData(listOf("Florile", "sunt", "frumoase"), "🌺"),
        SentenceData(listOf("Câinele", "aleargă", "repede"), "🐕"),
        SentenceData(listOf("Luna", "e", "rotundă"), "🌕"),
        SentenceData(listOf("Mama", "gătește", "bine"), "👩"),
        SentenceData(listOf("Iepurele", "sare", "vesel"), "🐰"),
        SentenceData(listOf("Trenul", "merge", "repede"), "🚂"),
        SentenceData(listOf("Zebra", "are", "dungi"), "🦓"),
        SentenceData(listOf("Vulpea", "e", "deșteaptă"), "🦊"),
        SentenceData(listOf("Leul", "este", "curajos"), "🦁"),
        SentenceData(listOf("Elefantul", "e", "mare"), "🐘")
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
        DiacriticExercise("Ă", "A", "Măr", "🍎", "Mar", "❓"),
        DiacriticExercise("Ă", "A", "Păr", "🍐", "Par", "❓"),
        DiacriticExercise("Â", "A", "Câine", "🐕", "Caine", "❓"),
        DiacriticExercise("Â", "A", "Pâine", "🍞", "Paine", "❓"),
        DiacriticExercise("Î", "I", "Înger", "👼", "Inger", "❓"),
        DiacriticExercise("Î", "I", "Înghețată", "🍦", "Inghetata", "❓"),
        DiacriticExercise("Ș", "S", "Șarpe", "🐍", "Sarpe", "❓"),
        DiacriticExercise("Ș", "S", "Școală", "🏫", "Scoală", "❓"),
        DiacriticExercise("Ț", "T", "Țestoasă", "🐢", "Testoasa", "❓"),
        DiacriticExercise("Ț", "T", "Țară", "🇷🇴", "Tara", "❓")
    )

    // Daily challenge word list
    data class DailyWord(val word: String, val emoji: String, val letter: String, val meaning: String)

    val dailyWords = listOf(
        DailyWord("Albină", "🐝", "A", "O insectă care face miere"),
        DailyWord("Balon", "🎈", "B", "Zboară sus în cer"),
        DailyWord("Carte", "📖", "C", "O citim și învățăm"),
        DailyWord("Delfin", "🐬", "D", "Sare prin valuri"),
        DailyWord("Elefant", "🐘", "E", "Cel mai mare animal de uscat"),
        DailyWord("Fluture", "🦋", "F", "Zboară pe flori"),
        DailyWord("Girafă", "🦒", "G", "Are gâtul foarte lung"),
        DailyWord("Iepure", "🐰", "I", "Sare și are urechi lungi"),
        DailyWord("Leu", "🦁", "L", "Regele animalelor"),
        DailyWord("Minge", "⚽", "M", "Ne jucăm cu ea"),
        DailyWord("Nor", "☁️", "N", "Plutește pe cer"),
        DailyWord("Ochi", "👁️", "O", "Vedem cu ei"),
        DailyWord("Pisică", "🐱", "P", "Face miau miau"),
        DailyWord("Rac", "🦀", "R", "Merge lateral pe plajă"),
        DailyWord("Soare", "☀️", "S", "Ne încălzește și strălucește"),
        DailyWord("Tren", "🚂", "T", "Merge pe șine tu-tu!"),
        DailyWord("Urs", "🐻", "U", "Doarme iarna"),
        DailyWord("Vulpe", "🦊", "V", "Deșteaptă și portocalie"),
        DailyWord("Zebră", "🦓", "Z", "Are dungi albe și negre"),
        DailyWord("Unicorn", "🦄", "U", "Calul magic cu corn"),
        DailyWord("Tigru", "🐯", "T", "Pisica cea mare, cu dungi"),
        DailyWord("Stea", "⭐", "S", "Strălucește noaptea pe cer"),
        DailyWord("Pinguin", "🐧", "P", "Trăiește la frig"),
        DailyWord("Lună", "🌙", "L", "O vedem noaptea pe cer"),
        DailyWord("Koala", "🐨", "K", "Mănâncă eucalipt"),
        DailyWord("Floare", "🌺", "F", "Crește în grădină"),
        DailyWord("Oaie", "🐑", "O", "Face bee bee"),
        DailyWord("Maimuță", "🐵", "M", "Se cațără în copaci"),
        DailyWord("Casă", "🏠", "C", "Locuim în ea"),
        DailyWord("Arici", "🦔", "A", "Are țepi pe spate"),
        DailyWord("Zână", "🧚", "Z", "Face vrăji frumoase")
    )

    fun getDailyWord(dayOfYear: Int): DailyWord {
        return dailyWords[dayOfYear % dailyWords.size]
    }
}
