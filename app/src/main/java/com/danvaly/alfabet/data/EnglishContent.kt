package com.danvaly.alfabet.data

import kotlin.random.Random

/**
 * One English word for the "Engleză" section.
 * [emoji] is the picture; when [colorHex] is set (colours without a good emoji)
 * the screen paints a swatch in that colour instead.
 */
data class EnglishWord(
    val en: String,
    val ro: String,
    val emoji: String,
    val colorHex: Long? = null
) {
    /** The text read aloud by the English voice (no trailing "…"). */
    val spoken: String get() = en.replace("…", "").replace("...", "").trim()
}

/** A themed group of words, e.g. "Animale" / "Animals". */
data class EnglishCategory(
    val id: String,
    val titleRo: String,
    val titleEn: String,
    val emoji: String,
    val color: Long,
    val words: List<EnglishWord>
) {
    /** Key for [ReadingProgressStore.gameStars] and the category's sticker. */
    val gameKey: String get() = "english_$id"
}

/** What the child has to do in one quiz task. */
enum class EnglishTaskType {
    /** "Ascultă și alege": hears the English word, picks the picture. */
    LISTEN,

    /** "Ce înseamnă?": reads the English word, picks the picture / Romanian word. */
    MEANING,

    /** "Cum se spune în engleză?": sees the picture and the Romanian word, picks the English word. */
    SAY
}

data class EnglishTask(
    val type: EnglishTaskType,
    val word: EnglishWord,
    val options: List<EnglishWord>
) {
    fun isCorrect(option: EnglishWord): Boolean = option.en == word.en
}

/** Basic English words a Romanian first-grader meets in the English class. */
object EnglishContent {

    /** English words that are written with a capital letter on purpose. */
    val capitalized: Set<String> = setOf("My name is…", "T-shirt")

    val categories: List<EnglishCategory> = listOf(
        EnglishCategory(
            id = "salutari",
            titleRo = "Salutări și expresii",
            titleEn = "Greetings",
            emoji = "👋",
            color = 0xFF26A69A,
            words = listOf(
                EnglishWord("hello", "salut", "👋"),
                EnglishWord("goodbye", "la revedere", "🚶"),
                EnglishWord("good morning", "bună dimineața", "🌅"),
                EnglishWord("good night", "noapte bună", "🌙"),
                EnglishWord("please", "te rog", "🙏"),
                EnglishWord("thank you", "mulțumesc", "💐"),
                EnglishWord("yes", "da", "👍"),
                EnglishWord("no", "nu", "👎"),
                EnglishWord("sorry", "îmi pare rău", "😔"),
                EnglishWord("My name is…", "Numele meu este…", "📛")
            )
        ),
        EnglishCategory(
            id = "culori",
            titleRo = "Culori",
            titleEn = "Colours",
            emoji = "🎨",
            color = 0xFFEC407A,
            words = listOf(
                EnglishWord("red", "roșu", "❤️"),
                EnglishWord("blue", "albastru", "💙"),
                EnglishWord("green", "verde", "💚"),
                EnglishWord("yellow", "galben", "💛"),
                EnglishWord("purple", "mov", "💜"),
                EnglishWord("black", "negru", "🖤"),
                EnglishWord("orange", "portocaliu", "🎨", 0xFFFF9800),
                EnglishWord("pink", "roz", "🎨", 0xFFFF80AB),
                EnglishWord("white", "alb", "🎨", 0xFFFFFFFF),
                EnglishWord("brown", "maro", "🎨", 0xFF795548)
            )
        ),
        EnglishCategory(
            id = "numere",
            titleRo = "Numere",
            titleEn = "Numbers",
            emoji = "🔢",
            color = 0xFF5C6BC0,
            words = listOf(
                EnglishWord("one", "unu", "1️⃣"),
                EnglishWord("two", "doi", "2️⃣"),
                EnglishWord("three", "trei", "3️⃣"),
                EnglishWord("four", "patru", "4️⃣"),
                EnglishWord("five", "cinci", "5️⃣"),
                EnglishWord("six", "șase", "6️⃣"),
                EnglishWord("seven", "șapte", "7️⃣"),
                EnglishWord("eight", "opt", "8️⃣"),
                EnglishWord("nine", "nouă", "9️⃣"),
                EnglishWord("ten", "zece", "🔟")
            )
        ),
        EnglishCategory(
            id = "animale",
            titleRo = "Animale",
            titleEn = "Animals",
            emoji = "🐶",
            color = 0xFF8D6E63,
            words = listOf(
                EnglishWord("dog", "câine", "🐶"),
                EnglishWord("cat", "pisică", "🐱"),
                EnglishWord("bird", "pasăre", "🐦"),
                EnglishWord("fish", "pește", "🐟"),
                EnglishWord("horse", "cal", "🐴"),
                EnglishWord("cow", "vacă", "🐮"),
                EnglishWord("pig", "porc", "🐷"),
                EnglishWord("duck", "rață", "🦆"),
                EnglishWord("rabbit", "iepure", "🐰"),
                EnglishWord("mouse", "șoarece", "🐭")
            )
        ),
        EnglishCategory(
            id = "familia",
            titleRo = "Familia",
            titleEn = "Family",
            emoji = "👪",
            color = 0xFFAB47BC,
            words = listOf(
                EnglishWord("mother", "mamă", "👩"),
                EnglishWord("father", "tată", "👨"),
                EnglishWord("sister", "soră", "👧"),
                EnglishWord("brother", "frate", "👦"),
                EnglishWord("baby", "bebeluș", "👶"),
                EnglishWord("grandmother", "bunică", "👵"),
                EnglishWord("grandfather", "bunic", "👴"),
                EnglishWord("family", "familie", "👪")
            )
        ),
        EnglishCategory(
            id = "scoala",
            titleRo = "La școală",
            titleEn = "At school",
            emoji = "🎒",
            color = 0xFF1E88E5,
            words = listOf(
                EnglishWord("school", "școală", "🏫"),
                EnglishWord("book", "carte", "📖"),
                EnglishWord("notebook", "caiet", "📓"),
                EnglishWord("pencil", "creion", "✏️"),
                EnglishWord("pen", "pix", "🖊️"),
                EnglishWord("crayon", "creion colorat", "🖍️"),
                EnglishWord("ruler", "riglă", "📏"),
                EnglishWord("scissors", "foarfecă", "✂️"),
                EnglishWord("schoolbag", "ghiozdan", "🎒")
            )
        ),
        EnglishCategory(
            id = "mancare",
            titleRo = "Fructe și mâncare",
            titleEn = "Food",
            emoji = "🍎",
            color = 0xFFE53935,
            words = listOf(
                EnglishWord("apple", "măr", "🍎"),
                EnglishWord("banana", "banană", "🍌"),
                EnglishWord("orange", "portocală", "🍊"),
                EnglishWord("grapes", "struguri", "🍇"),
                EnglishWord("strawberry", "căpșună", "🍓"),
                EnglishWord("pear", "pară", "🍐"),
                EnglishWord("bread", "pâine", "🍞"),
                EnglishWord("milk", "lapte", "🥛"),
                EnglishWord("cake", "prăjitură", "🍰"),
                EnglishWord("water", "apă", "💧")
            )
        ),
        EnglishCategory(
            id = "corpul",
            titleRo = "Corpul",
            titleEn = "My body",
            emoji = "🙂",
            color = 0xFFFF7043,
            words = listOf(
                EnglishWord("face", "față", "🙂"),
                EnglishWord("eyes", "ochi", "👀"),
                EnglishWord("ear", "ureche", "👂"),
                EnglishWord("nose", "nas", "👃"),
                EnglishWord("mouth", "gură", "👄"),
                EnglishWord("tongue", "limbă", "👅"),
                EnglishWord("hand", "mână", "✋"),
                EnglishWord("finger", "deget", "☝️"),
                EnglishWord("arm", "braț", "💪")
            )
        ),
        EnglishCategory(
            id = "jucarii",
            titleRo = "Jucării",
            titleEn = "Toys",
            emoji = "⚽",
            color = 0xFF43A047,
            words = listOf(
                EnglishWord("ball", "minge", "⚽"),
                EnglishWord("car", "mașină", "🚗"),
                EnglishWord("train", "tren", "🚂"),
                EnglishWord("plane", "avion", "✈️"),
                EnglishWord("boat", "barcă", "⛵"),
                EnglishWord("bike", "bicicletă", "🚲"),
                EnglishWord("balloon", "balon", "🎈"),
                EnglishWord("robot", "robot", "🤖"),
                EnglishWord("drum", "tobă", "🥁")
            )
        ),
        EnglishCategory(
            id = "casa",
            titleRo = "Casa",
            titleEn = "My house",
            emoji = "🏠",
            color = 0xFF00897B,
            words = listOf(
                EnglishWord("house", "casă", "🏠"),
                EnglishWord("door", "ușă", "🚪"),
                EnglishWord("bed", "pat", "🛏️"),
                EnglishWord("sofa", "canapea", "🛋️"),
                EnglishWord("bath", "cadă", "🛁"),
                EnglishWord("key", "cheie", "🔑"),
                EnglishWord("clock", "ceas", "⏰"),
                EnglishWord("phone", "telefon", "📱"),
                EnglishWord("computer", "calculator", "💻")
            )
        )
    )

    fun category(id: String): EnglishCategory? = categories.firstOrNull { it.id == id }

    /** Best stars a category can give, summed over all categories. */
    val maxStars: Int get() = categories.size * 3
}

/** Builds the quiz rounds of the "Engleză" section. */
object EnglishQuiz {
    const val ROUND_SIZE = 8
    const val PICTURE_OPTIONS = 4
    const val WORD_OPTIONS = 3

    /**
     * A round of [size] tasks over different words of [category], mixing the three task types
     * (each type appears at least once when size >= 3).
     */
    fun round(category: EnglishCategory, random: Random = Random.Default, size: Int = ROUND_SIZE): List<EnglishTask> {
        val words = category.words.shuffled(random)
        val picked = List(size) { words[it % words.size] }
        val types = List(size) { EnglishTaskType.values()[it % EnglishTaskType.values().size] }.shuffled(random)
        return picked.mapIndexed { index, word -> task(types[index], word, category.words, random) }
    }

    /** One task: the correct [word] plus distinct distractors from [pool], in random order. */
    fun task(type: EnglishTaskType, word: EnglishWord, pool: List<EnglishWord>, random: Random = Random.Default): EnglishTask {
        val count = if (type == EnglishTaskType.SAY) WORD_OPTIONS else PICTURE_OPTIONS
        val distractors = pool
            .filter { it.en != word.en && it.ro != word.ro && it.emoji.isNotEmpty() }
            .distinctBy { it.en }
            .let { list -> if (type == EnglishTaskType.SAY) list else list.distinctBy { pictureKey(it) } }
            .filter { type == EnglishTaskType.SAY || pictureKey(it) != pictureKey(word) }
            .shuffled(random)
            .take(count - 1)
        return EnglishTask(type, word, (distractors + word).shuffled(random))
    }

    /** What the child sees as the picture: a colour swatch or the emoji. */
    private fun pictureKey(word: EnglishWord): String = word.colorHex?.let { "#$it" } ?: word.emoji
}
