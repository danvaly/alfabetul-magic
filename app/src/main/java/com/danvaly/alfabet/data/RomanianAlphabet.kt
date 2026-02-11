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
            isVowel = true, colorIndex = 0
        ),
        Letter(
            uppercase = "Ă", lowercase = "ă",
            phoneticSound = "ă",
            words = listOf(
                ExampleWord("Măr", "🍎", listOf("Măr")),
                ExampleWord("Păr", "🍐", listOf("Păr")),
                ExampleWord("Văr", "👦", listOf("Văr"))
            ),
            isVowel = true, isDiacritic = true, colorIndex = 1
        ),
        Letter(
            uppercase = "Â", lowercase = "â",
            phoneticSound = "â",
            words = listOf(
                ExampleWord("Pâine", "🍞", listOf("Pâi", "ne")),
                ExampleWord("Câine", "🐕", listOf("Câi", "ne")),
                ExampleWord("Mâine", "📅", listOf("Mâi", "ne"))
            ),
            isVowel = true, isDiacritic = true, colorIndex = 2
        ),
        Letter(
            uppercase = "B", lowercase = "b",
            phoneticSound = "bă",
            words = listOf(
                ExampleWord("Balon", "🎈", listOf("Ba", "lon")),
                ExampleWord("Barcă", "⛵", listOf("Bar", "că")),
                ExampleWord("Bunic", "👴", listOf("Bu", "nic"))
            ),
            isVowel = false, colorIndex = 3
        ),
        Letter(
            uppercase = "C", lowercase = "c",
            phoneticSound = "că",
            words = listOf(
                ExampleWord("Casă", "🏠", listOf("Ca", "să")),
                ExampleWord("Carte", "📖", listOf("Car", "te")),
                ExampleWord("Ciocolată", "🍫", listOf("Ci", "o", "co", "la", "tă"))
            ),
            isVowel = false, colorIndex = 4
        ),
        Letter(
            uppercase = "D", lowercase = "d",
            phoneticSound = "dă",
            words = listOf(
                ExampleWord("Delfin", "🐬", listOf("Del", "fin")),
                ExampleWord("Drum", "🛤️", listOf("Drum")),
                ExampleWord("Dinozaur", "🦕", listOf("Di", "no", "zaur"))
            ),
            isVowel = false, colorIndex = 5
        ),
        Letter(
            uppercase = "E", lowercase = "e",
            phoneticSound = "e",
            words = listOf(
                ExampleWord("Elefant", "🐘", listOf("E", "le", "fant")),
                ExampleWord("Evantai", "🪭", listOf("E", "van", "tai")),
                ExampleWord("Energie", "⚡", listOf("E", "ner", "gi", "e"))
            ),
            isVowel = true, colorIndex = 6
        ),
        Letter(
            uppercase = "F", lowercase = "f",
            phoneticSound = "fă",
            words = listOf(
                ExampleWord("Fluture", "🦋", listOf("Flu", "tu", "re")),
                ExampleWord("Floare", "🌸", listOf("Floa", "re")),
                ExampleWord("Fruct", "🍓", listOf("Fruct"))
            ),
            isVowel = false, colorIndex = 7
        ),
        Letter(
            uppercase = "G", lowercase = "g",
            phoneticSound = "gă",
            words = listOf(
                ExampleWord("Girafă", "🦒", listOf("Gi", "ra", "fă")),
                ExampleWord("Ghindă", "🌰", listOf("Ghin", "dă")),
                ExampleWord("Grădină", "🌻", listOf("Gră", "di", "nă"))
            ),
            isVowel = false, colorIndex = 0
        ),
        Letter(
            uppercase = "H", lowercase = "h",
            phoneticSound = "hă",
            words = listOf(
                ExampleWord("Hipopotam", "🦛", listOf("Hi", "po", "po", "tam")),
                ExampleWord("Hartă", "🗺️", listOf("Har", "tă")),
                ExampleWord("Hârtie", "📄", listOf("Hâr", "ti", "e"))
            ),
            isVowel = false, colorIndex = 1
        ),
        Letter(
            uppercase = "I", lowercase = "i",
            phoneticSound = "i",
            words = listOf(
                ExampleWord("Inimă", "❤️", listOf("I", "ni", "mă")),
                ExampleWord("Insulă", "🏝️", listOf("In", "su", "lă")),
                ExampleWord("Iepure", "🐰", listOf("I", "e", "pu", "re"))
            ),
            isVowel = true, colorIndex = 2
        ),
        Letter(
            uppercase = "Î", lowercase = "î",
            phoneticSound = "î",
            words = listOf(
                ExampleWord("Înger", "👼", listOf("În", "ger")),
                ExampleWord("Înghețată", "🍦", listOf("În", "ghe", "ța", "tă")),
                ExampleWord("Împărat", "👑", listOf("Îm", "pă", "rat"))
            ),
            isVowel = true, isDiacritic = true, colorIndex = 3
        ),
        Letter(
            uppercase = "J", lowercase = "j",
            phoneticSound = "jă",
            words = listOf(
                ExampleWord("Jucărie", "🧸", listOf("Ju", "că", "ri", "e")),
                ExampleWord("Junglă", "🌴", listOf("Jun", "glă")),
                ExampleWord("Jeleu", "🍬", listOf("Je", "leu"))
            ),
            isVowel = false, colorIndex = 4
        ),
        Letter(
            uppercase = "K", lowercase = "k",
            phoneticSound = "ka",
            words = listOf(
                ExampleWord("Kangur", "🦘", listOf("Kan", "gur")),
                ExampleWord("Kiwi", "🥝", listOf("Ki", "wi")),
                ExampleWord("Koala", "🐨", listOf("Ko", "a", "la"))
            ),
            isVowel = false, colorIndex = 5
        ),
        Letter(
            uppercase = "L", lowercase = "l",
            phoneticSound = "lă",
            words = listOf(
                ExampleWord("Leu", "🦁", listOf("Leu")),
                ExampleWord("Luna", "🌙", listOf("Lu", "na")),
                ExampleWord("Lăptucă", "🥬", listOf("Lăp", "tu", "că"))
            ),
            isVowel = false, colorIndex = 6
        ),
        Letter(
            uppercase = "M", lowercase = "m",
            phoneticSound = "mă",
            words = listOf(
                ExampleWord("Mama", "👩", listOf("Ma", "ma")),
                ExampleWord("Minge", "⚽", listOf("Min", "ge")),
                ExampleWord("Maimuță", "🐵", listOf("Mai", "mu", "ță"))
            ),
            isVowel = false, colorIndex = 7
        ),
        Letter(
            uppercase = "N", lowercase = "n",
            phoneticSound = "nă",
            words = listOf(
                ExampleWord("Nor", "☁️", listOf("Nor")),
                ExampleWord("Noapte", "🌙", listOf("Noap", "te")),
                ExampleWord("Nuci", "🥜", listOf("Nuci"))
            ),
            isVowel = false, colorIndex = 0
        ),
        Letter(
            uppercase = "O", lowercase = "o",
            phoneticSound = "o",
            words = listOf(
                ExampleWord("Ochi", "👁️", listOf("O", "chi")),
                ExampleWord("Ou", "🥚", listOf("Ou")),
                ExampleWord("Oaie", "🐑", listOf("O", "a", "ie"))
            ),
            isVowel = true, colorIndex = 1
        ),
        Letter(
            uppercase = "P", lowercase = "p",
            phoneticSound = "pă",
            words = listOf(
                ExampleWord("Pisică", "🐱", listOf("Pi", "si", "că")),
                ExampleWord("Pasăre", "🐦", listOf("Pa", "să", "re")),
                ExampleWord("Pinguin", "🐧", listOf("Pin", "gu", "in"))
            ),
            isVowel = false, colorIndex = 2
        ),
        Letter(
            uppercase = "Q", lowercase = "q",
            phoneticSound = "kiu",
            words = listOf(
                ExampleWord("Quasar", "✨", listOf("Qua", "sar")),
                ExampleWord("Quiz", "❓", listOf("Quiz")),
                ExampleWord("Quartet", "🎵", listOf("Quar", "tet"))
            ),
            isVowel = false, colorIndex = 3
        ),
        Letter(
            uppercase = "R", lowercase = "r",
            phoneticSound = "ră",
            words = listOf(
                ExampleWord("Rac", "🦀", listOf("Rac")),
                ExampleWord("Roată", "🎡", listOf("Ro", "a", "tă")),
                ExampleWord("Rechin", "🦈", listOf("Re", "chin"))
            ),
            isVowel = false, colorIndex = 4
        ),
        Letter(
            uppercase = "S", lowercase = "s",
            phoneticSound = "să",
            words = listOf(
                ExampleWord("Soare", "☀️", listOf("So", "a", "re")),
                ExampleWord("Stea", "⭐", listOf("Stea")),
                ExampleWord("Sarpe", "🐍", listOf("Șar", "pe"))
            ),
            isVowel = false, colorIndex = 5
        ),
        Letter(
            uppercase = "Ș", lowercase = "ș",
            phoneticSound = "șă",
            words = listOf(
                ExampleWord("Șarpe", "🐍", listOf("Șar", "pe")),
                ExampleWord("Școală", "🏫", listOf("Școa", "lă")),
                ExampleWord("Șoricei", "🐭", listOf("Șo", "ri", "cei"))
            ),
            isVowel = false, isDiacritic = true, colorIndex = 6
        ),
        Letter(
            uppercase = "T", lowercase = "t",
            phoneticSound = "tă",
            words = listOf(
                ExampleWord("Tren", "🚂", listOf("Tren")),
                ExampleWord("Tigru", "🐯", listOf("Ti", "gru")),
                ExampleWord("Tobogan", "🛝", listOf("To", "bo", "gan"))
            ),
            isVowel = false, colorIndex = 7
        ),
        Letter(
            uppercase = "Ț", lowercase = "ț",
            phoneticSound = "ță",
            words = listOf(
                ExampleWord("Țestoasă", "🐢", listOf("Țes", "to", "a", "să")),
                ExampleWord("Țară", "🇷🇴", listOf("Ța", "ră")),
                ExampleWord("Țânțar", "🦟", listOf("Țân", "țar"))
            ),
            isVowel = false, isDiacritic = true, colorIndex = 0
        ),
        Letter(
            uppercase = "U", lowercase = "u",
            phoneticSound = "u",
            words = listOf(
                ExampleWord("Urs", "🐻", listOf("Urs")),
                ExampleWord("Umbrelă", "☂️", listOf("Um", "bre", "lă")),
                ExampleWord("Unicorn", "🦄", listOf("U", "ni", "corn"))
            ),
            isVowel = true, colorIndex = 1
        ),
        Letter(
            uppercase = "V", lowercase = "v",
            phoneticSound = "vă",
            words = listOf(
                ExampleWord("Vulpe", "🦊", listOf("Vul", "pe")),
                ExampleWord("Vapor", "🚢", listOf("Va", "por")),
                ExampleWord("Vânt", "💨", listOf("Vânt"))
            ),
            isVowel = false, colorIndex = 2
        ),
        Letter(
            uppercase = "W", lowercase = "w",
            phoneticSound = "dublu vă",
            words = listOf(
                ExampleWord("Weekend", "🏖️", listOf("We", "e", "kend")),
                ExampleWord("Waffle", "🧇", listOf("Wa", "ffle")),
                ExampleWord("WiFi", "📶", listOf("Wi", "Fi"))
            ),
            isVowel = false, colorIndex = 3
        ),
        Letter(
            uppercase = "X", lowercase = "x",
            phoneticSound = "ics",
            words = listOf(
                ExampleWord("Xilofon", "🎵", listOf("Xi", "lo", "fon")),
                ExampleWord("Raze X", "🩻", listOf("Ra", "ze", "X")),
                ExampleWord("Taxi", "🚕", listOf("Ta", "xi"))
            ),
            isVowel = false, colorIndex = 4
        ),
        Letter(
            uppercase = "Y", lowercase = "y",
            phoneticSound = "igrec",
            words = listOf(
                ExampleWord("Yoga", "🧘", listOf("Yo", "ga")),
                ExampleWord("Iaurt", "🥛", listOf("I", "a", "urt")),
                ExampleWord("Yoyo", "🪀", listOf("Yo", "yo"))
            ),
            isVowel = false, colorIndex = 5
        ),
        Letter(
            uppercase = "Z", lowercase = "z",
            phoneticSound = "ză",
            words = listOf(
                ExampleWord("Zebră", "🦓", listOf("Ze", "bră")),
                ExampleWord("Zmeu", "🪁", listOf("Zmeu")),
                ExampleWord("Zână", "🧚", listOf("Zâ", "nă"))
            ),
            isVowel = false, colorIndex = 6
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
        "LUNA" to listOf("LU", "NA"),
        "BINE" to listOf("BI", "NE"),
        "RAME" to listOf("RA", "ME"),
        "RATĂ" to listOf("RA", "TĂ"),
        "VALE" to listOf("VA", "LE"),
        "POLO" to listOf("PO", "LO"),
        "NOTE" to listOf("NO", "TE"),
        "BILE" to listOf("BI", "LE"),
        "CARE" to listOf("CA", "RE"),
        "PARĂ" to listOf("PA", "RĂ"),
        "GARĂ" to listOf("GA", "RĂ"),
        "FATĂ" to listOf("FA", "TĂ"),
        "ROȘU" to listOf("RO", "ȘU"),
        "VERDE" to listOf("VER", "DE")
    )
}
