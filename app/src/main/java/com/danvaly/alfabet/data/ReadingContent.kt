package com.danvaly.alfabet.data

/** How a letter (or group of letters) is taught in first grade. */
enum class SoundKind { VOWEL, CONSONANT, GROUP }

/** One tile in the "letters" step: a single letter or a letter group (ce, ci, ge, gi, che, chi, ghe, ghi). */
data class LetterUnit(val text: String, val kind: SoundKind)

/** A word split into syllables, plus the punctuation that follows it in the sentence. */
data class ReadingWord(
    val syllables: List<String>,
    val trailing: String = ""
) {
    val text: String get() = syllables.joinToString("")
    val letterCount: Int get() = text.count { it.isLetter() }
    val units: List<LetterUnit> get() = syllables.flatMap { ReadingText.letterUnits(it) }
    val vowelCount: Int get() = text.count { ReadingText.isVowel(it) }
    val consonantCount: Int get() = letterCount - vowelCount
}

object ReadingText {
    private const val VOWELS = "aăâeiîou"
    private val GROUPS = listOf("che", "chi", "ghe", "ghi", "ce", "ci", "ge", "gi")

    fun isVowel(c: Char): Boolean = c.lowercaseChar() in VOWELS

    /** Splits a syllable into letter tiles, keeping the Romanian letter groups together. */
    fun letterUnits(syllable: String): List<LetterUnit> {
        val units = mutableListOf<LetterUnit>()
        var i = 0
        while (i < syllable.length) {
            val group = GROUPS.firstOrNull { syllable.startsWith(it, i, ignoreCase = true) }
            if (group != null) {
                units += LetterUnit(syllable.substring(i, i + group.length), SoundKind.GROUP)
                i += group.length
            } else {
                val c = syllable[i]
                units += LetterUnit(
                    c.toString(),
                    if (isVowel(c)) SoundKind.VOWEL else SoundKind.CONSONANT
                )
                i++
            }
        }
        return units
    }

    /**
     * Parses a sentence written with syllable hyphens, e.g. "A-na a-re o pi-si-că."
     * Punctuation stays attached to the word it follows.
     */
    fun parseSentence(marked: String): List<ReadingWord> =
        marked.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }.map { token ->
            val core = token.trimEnd { !it.isLetter() }
            ReadingWord(
                syllables = core.split("-").filter { it.isNotEmpty() },
                trailing = token.substring(core.length)
            )
        }

    fun parseWord(marked: String): ReadingWord = parseSentence(marked).first()

    /** The sentence as normal text, without syllable marks. */
    fun plain(words: List<ReadingWord>): String =
        words.joinToString(" ") { it.text + it.trailing }
}

// ---------------------------------------------------------------------------
// Illustrations: a painted backdrop plus emoji "actors" placed on top of it.
// ---------------------------------------------------------------------------

enum class Backdrop {
    MEADOW, ROOM, FARM, RAIN, RAINBOW, FOREST, EVENING, NIGHT, BEACH, GARDEN,
    /** Snowy hills, snowy fir trees, a frozen lake, falling snow. */
    WINTER,
    /** A pond with reeds and lily pads; the water fills the lower half (y ≈ 0.5..0.94). */
    POND,
    /** A wooden cottage at the edge of a forest. Door at x 0.75, chimney top at (0.83, 0.16). */
    COTTAGE,
    /** The same house, made of straw. Door at x 0.75, no chimney. */
    STRAW_HOUSE,
    /** The same house, made of bricks. Door at x 0.75, chimney top at (0.83, 0.16). */
    BRICK_HOUSE,
    /** A snowy village at night: lit windows, stars, falling snow. Snow ground from y 0.8. */
    SNOW_NIGHT,
    /** A festive street with Romanian tricolour flags (blue, yellow, red) and bunting. Street from y 0.66. */
    PARADE
}

enum class Motion { NONE, BOUNCE, FLOAT, SWAY, PULSE, RUN }

/**
 * @param x horizontal centre, 0 = left edge, 1 = right edge
 * @param y vertical centre, 0 = top, 1 = bottom
 * @param size emoji height as a fraction of the picture height
 */
data class Actor(
    val emoji: String,
    val x: Float,
    val y: Float,
    val size: Float = 0.24f,
    val motion: Motion = Motion.NONE,
    val flip: Boolean = false
)

data class Scene(val backdrop: Backdrop, val actors: List<Actor>)

data class StoryPage(val scene: Scene, val markedText: String) {
    val words: List<ReadingWord> by lazy { ReadingText.parseSentence(markedText) }
}

data class StoryChoice(val emoji: String, val label: String)

/** A comprehension question; the first choice in [choices] is the correct one. */
data class StoryQuestion(val question: String, val choices: List<StoryChoice>) {
    val answer: StoryChoice get() = choices.first()
}

data class Story(
    val id: String,
    val title: String,
    val cover: String,
    val level: Int,
    val pages: List<StoryPage>,
    val questions: List<StoryQuestion>,
    val category: StoryCategory = StoryCategory.EVERYDAY
)

/** Everyday stories written for the app, well-known classic tales retold for beginners, or holiday stories. */
enum class StoryCategory { EVERYDAY, CLASSIC, HOLIDAY }

/** A picture word for the step-by-step reading trainer. */
data class TrainerWord(val marked: String, val emoji: String) {
    val word: ReadingWord by lazy { ReadingText.parseWord(marked) }
}

data class TrainerLevel(
    val id: Int,
    val title: String,
    val description: String,
    val emoji: String,
    val words: List<TrainerWord>
)

object ReadingContent {

    private fun page(backdrop: Backdrop, text: String, vararg actors: Actor) =
        StoryPage(Scene(backdrop, actors.toList()), text)

    private fun q(question: String, vararg choices: Pair<String, String>) =
        StoryQuestion(question, choices.map { StoryChoice(it.first, it.second) })

    private fun everydayStories(): List<Story> = listOf(
        Story(
            id = "ana_pisica", title = "Ana și pisica", cover = "🐱", level = 1,
            pages = listOf(
                page(
                    Backdrop.ROOM, "A-na a-re o pi-si-că.",
                    Actor("👧", 0.32f, 0.62f, 0.36f),
                    Actor("🐱", 0.66f, 0.72f, 0.26f, Motion.BOUNCE)
                ),
                page(
                    Backdrop.ROOM, "Pi-si-ca e mi-că.",
                    Actor("🐱", 0.5f, 0.68f, 0.22f, Motion.PULSE),
                    Actor("🎾", 0.76f, 0.82f, 0.12f, Motion.SWAY)
                ),
                page(
                    Backdrop.ROOM, "Pi-si-ca bea lap-te.",
                    Actor("🐱", 0.42f, 0.7f, 0.28f),
                    Actor("🥛", 0.66f, 0.76f, 0.2f, Motion.PULSE)
                ),
                page(
                    Backdrop.ROOM, "A-na se joa-că cu pi-si-ca.",
                    Actor("👧", 0.28f, 0.62f, 0.36f, Motion.SWAY),
                    Actor("🎾", 0.52f, 0.82f, 0.12f, Motion.RUN),
                    Actor("🐱", 0.74f, 0.72f, 0.26f, Motion.BOUNCE)
                ),
                page(
                    Backdrop.NIGHT, "Sea-ra, pi-si-ca doar-me pe pat.",
                    Actor("🛏️", 0.5f, 0.7f, 0.42f),
                    Actor("🐱", 0.56f, 0.56f, 0.18f, Motion.PULSE),
                    Actor("💤", 0.7f, 0.36f, 0.14f, Motion.FLOAT)
                )
            ),
            questions = listOf(
                q("Ce are Ana?", "🐱" to "o pisică", "🐶" to "un cățel", "🐟" to "un pește"),
                q("Ce bea pisica?", "🥛" to "lapte", "🍵" to "ceai", "🍲" to "supă"),
                q("Unde doarme pisica?", "🛏️" to "pe pat", "🌳" to "în copac", "🚗" to "în mașină")
            )
        ),
        Story(
            id = "nicu_mingea", title = "Nicu și mingea", cover = "⚽", level = 1,
            pages = listOf(
                page(
                    Backdrop.MEADOW, "Ni-cu a-re o min-ge.",
                    Actor("👦", 0.34f, 0.66f, 0.36f),
                    Actor("⚽", 0.62f, 0.8f, 0.16f)
                ),
                page(
                    Backdrop.MEADOW, "Min-gea sa-re sus.",
                    Actor("👦", 0.28f, 0.68f, 0.34f),
                    Actor("⚽", 0.62f, 0.4f, 0.18f, Motion.BOUNCE)
                ),
                page(
                    Backdrop.MEADOW, "Un că-țel ve-de min-gea.",
                    Actor("🐶", 0.3f, 0.72f, 0.28f, Motion.SWAY),
                    Actor("⚽", 0.68f, 0.8f, 0.16f)
                ),
                page(
                    Backdrop.MEADOW, "Că-țe-lul fu-ge cu ea.",
                    Actor("🐕", 0.5f, 0.72f, 0.3f, Motion.RUN, flip = true),
                    Actor("⚽", 0.62f, 0.8f, 0.12f, Motion.RUN)
                ),
                page(
                    Backdrop.MEADOW, "Ni-cu râ-de ta-re.",
                    Actor("😄", 0.36f, 0.62f, 0.34f, Motion.BOUNCE),
                    Actor("🐶", 0.7f, 0.74f, 0.24f, Motion.SWAY),
                    Actor("⚽", 0.84f, 0.82f, 0.12f)
                )
            ),
            questions = listOf(
                q("Ce are Nicu?", "⚽" to "o minge", "🚲" to "o bicicletă", "🎈" to "un balon"),
                q("Cine fuge cu mingea?", "🐶" to "cățelul", "🐱" to "pisica", "🐦" to "pasărea")
            )
        ),
        Story(
            id = "la_bunici", title = "La bunici", cover = "🏡", level = 2,
            pages = listOf(
                page(
                    Backdrop.FARM, "Mi-ra mer-ge la bu-nici.",
                    Actor("🏡", 0.72f, 0.56f, 0.4f),
                    Actor("👧", 0.24f, 0.72f, 0.3f, Motion.RUN)
                ),
                page(
                    Backdrop.FARM, "Bu-ni-ca a-re gă-ini și ra-țe.",
                    Actor("👵", 0.2f, 0.62f, 0.34f),
                    Actor("🐔", 0.46f, 0.78f, 0.2f, Motion.BOUNCE),
                    Actor("🐔", 0.6f, 0.82f, 0.16f, Motion.SWAY),
                    Actor("🦆", 0.78f, 0.78f, 0.2f, Motion.SWAY)
                ),
                page(
                    Backdrop.FARM, "Bu-ni-cul a-re un cal alb.",
                    Actor("👴", 0.24f, 0.64f, 0.34f),
                    Actor("🐎", 0.64f, 0.68f, 0.4f, Motion.SWAY)
                ),
                page(
                    Backdrop.FARM, "Mi-ra dă ca-lu-lui un măr.",
                    Actor("👧", 0.24f, 0.68f, 0.32f),
                    Actor("🍎", 0.44f, 0.6f, 0.14f, Motion.PULSE),
                    Actor("🐴", 0.7f, 0.6f, 0.36f, Motion.BOUNCE)
                ),
                page(
                    Backdrop.EVENING, "Sea-ra, bu-ni-ca fa-ce plă-cin-te.",
                    Actor("👵", 0.3f, 0.64f, 0.36f),
                    Actor("🥧", 0.62f, 0.74f, 0.26f, Motion.PULSE),
                    Actor("♨️", 0.62f, 0.5f, 0.12f, Motion.FLOAT)
                ),
                page(
                    Backdrop.EVENING, "Ce bu-ne sunt!",
                    Actor("😋", 0.4f, 0.6f, 0.36f, Motion.BOUNCE),
                    Actor("🥧", 0.7f, 0.74f, 0.22f)
                )
            ),
            questions = listOf(
                q("Unde merge Mira?", "🏡" to "la bunici", "🏫" to "la școală", "🏖️" to "la mare"),
                q("Ce îi dă Mira calului?", "🍎" to "un măr", "🥕" to "un morcov", "🍞" to "o pâine"),
                q("Ce face bunica seara?", "🥧" to "plăcinte", "🍲" to "supă", "🎂" to "un tort")
            )
        ),
        Story(
            id = "ploaia", title = "Ploaia", cover = "🌧️", level = 2,
            pages = listOf(
                page(
                    Backdrop.RAIN, "Pe cer sunt nori gri.",
                    Actor("👧", 0.4f, 0.7f, 0.32f)
                ),
                page(
                    Backdrop.RAIN, "Pic, pic, pic! Plo-uă.",
                    Actor("👧", 0.4f, 0.7f, 0.32f),
                    Actor("💧", 0.68f, 0.5f, 0.12f, Motion.BOUNCE)
                ),
                page(
                    Backdrop.RAIN, "Da-ra ia um-bre-la.",
                    Actor("👧", 0.46f, 0.72f, 0.3f),
                    Actor("☂️", 0.48f, 0.46f, 0.34f, Motion.SWAY)
                ),
                page(
                    Backdrop.RAIN, "O broas-că sa-re în bal-tă.",
                    Actor("🐸", 0.5f, 0.66f, 0.28f, Motion.BOUNCE),
                    Actor("💦", 0.72f, 0.78f, 0.16f, Motion.PULSE)
                ),
                page(
                    Backdrop.MEADOW, "A-poi ie-se soa-re-le.",
                    Actor("👧", 0.34f, 0.7f, 0.32f, Motion.BOUNCE),
                    Actor("🐸", 0.7f, 0.82f, 0.16f)
                ),
                page(
                    Backdrop.RAINBOW, "Pe cer e un cur-cu-beu!",
                    Actor("👧", 0.3f, 0.74f, 0.28f, Motion.BOUNCE),
                    Actor("🐸", 0.72f, 0.82f, 0.18f, Motion.BOUNCE)
                )
            ),
            questions = listOf(
                q("Ce ia Dara?", "☂️" to "umbrela", "🎩" to "pălăria", "🧤" to "mănușile"),
                q("Cine sare în baltă?", "🐸" to "o broască", "🐟" to "un pește", "🐱" to "o pisică"),
                q("Ce apare pe cer după ploaie?", "🌈" to "un curcubeu", "🌙" to "luna", "⭐" to "o stea")
            )
        ),
        Story(
            id = "iepurasul", title = "Iepurașul și morcovul", cover = "🐰", level = 2,
            pages = listOf(
                page(
                    Backdrop.GARDEN, "Un ie-pu-raș sa-pă în gră-di-nă.",
                    Actor("🐰", 0.42f, 0.56f, 0.32f, Motion.BOUNCE)
                ),
                page(
                    Backdrop.GARDEN, "El ca-u-tă un mor-cov.",
                    Actor("🐰", 0.32f, 0.56f, 0.3f, Motion.SWAY),
                    Actor("🥕", 0.7f, 0.8f, 0.16f)
                ),
                page(
                    Backdrop.GARDEN, "Mor-co-vul e ma-re și por-to-ca-liu.",
                    Actor("🥕", 0.5f, 0.6f, 0.46f, Motion.PULSE)
                ),
                page(
                    Backdrop.GARDEN, "Ie-pu-ra-șul tra-ge, tra-ge, tra-ge!",
                    Actor("🐇", 0.36f, 0.56f, 0.3f, Motion.RUN),
                    Actor("🥕", 0.62f, 0.7f, 0.24f, Motion.RUN)
                ),
                page(
                    Backdrop.GARDEN, "Hop! Mor-co-vul ie-se din pă-mânt.",
                    Actor("🐰", 0.3f, 0.56f, 0.3f, Motion.BOUNCE),
                    Actor("🥕", 0.64f, 0.44f, 0.34f, Motion.BOUNCE),
                    Actor("✨", 0.8f, 0.3f, 0.14f, Motion.PULSE)
                ),
                page(
                    Backdrop.MEADOW, "Ie-pu-ra-șul îl îm-par-te cu pri-e-te-nii.",
                    Actor("🐰", 0.24f, 0.72f, 0.26f),
                    Actor("🥕", 0.44f, 0.8f, 0.16f, Motion.PULSE),
                    Actor("🐿️", 0.62f, 0.76f, 0.2f, Motion.SWAY),
                    Actor("🦔", 0.8f, 0.8f, 0.18f, Motion.SWAY)
                )
            ),
            questions = listOf(
                q("Ce caută iepurașul?", "🥕" to "un morcov", "🍎" to "un măr", "🌰" to "o nucă"),
                q("Cum este morcovul?", "🥕" to "mare", "🐜" to "mic", "❄️" to "rece"),
                q("Ce face iepurașul cu morcovul?", "🤝" to "îl împarte", "🙈" to "îl ascunde", "🗑️" to "îl aruncă")
            )
        ),
        Story(
            id = "ursul_mierea", title = "Ursul și mierea", cover = "🐻", level = 3,
            pages = listOf(
                page(
                    Backdrop.FOREST, "Un urs mic mer-ge prin pă-du-re.",
                    Actor("🐻", 0.42f, 0.72f, 0.3f, Motion.RUN)
                ),
                page(
                    Backdrop.FOREST, "Lui îi e foa-me.",
                    Actor("🐻", 0.46f, 0.7f, 0.32f, Motion.SWAY),
                    Actor("💭", 0.68f, 0.36f, 0.22f, Motion.FLOAT)
                ),
                page(
                    Backdrop.FOREST, "El ve-de un stup în co-pac.",
                    Actor("🐻", 0.28f, 0.74f, 0.28f),
                    Actor("🍯", 0.7f, 0.34f, 0.2f, Motion.SWAY)
                ),
                page(
                    Backdrop.FOREST, "În stup e mie-re dul-ce.",
                    Actor("🍯", 0.5f, 0.52f, 0.4f, Motion.PULSE),
                    Actor("🐝", 0.76f, 0.3f, 0.12f, Motion.FLOAT)
                ),
                page(
                    Backdrop.FOREST, "Al-bi-ne-le zboa-ră: bâz, bâz!",
                    Actor("🐝", 0.28f, 0.34f, 0.16f, Motion.FLOAT),
                    Actor("🐝", 0.52f, 0.5f, 0.2f, Motion.BOUNCE),
                    Actor("🐝", 0.76f, 0.3f, 0.14f, Motion.FLOAT),
                    Actor("🐻", 0.5f, 0.8f, 0.24f, Motion.SWAY)
                ),
                page(
                    Backdrop.FOREST, "Ur-sul fu-ge re-pe-de a-ca-să.",
                    Actor("🐻", 0.5f, 0.72f, 0.3f, Motion.RUN, flip = true),
                    Actor("🐝", 0.22f, 0.5f, 0.12f, Motion.RUN)
                ),
                page(
                    Backdrop.ROOM, "Ma-ma îi dă mie-re din bor-can.",
                    Actor("🐻", 0.3f, 0.6f, 0.38f),
                    Actor("🍯", 0.54f, 0.76f, 0.2f, Motion.PULSE),
                    Actor("🐻", 0.76f, 0.76f, 0.22f, Motion.BOUNCE)
                )
            ),
            questions = listOf(
                q("Pe unde merge ursul?", "🌲" to "prin pădure", "🏖️" to "pe plajă", "🏙️" to "prin oraș"),
                q("Ce este în stup?", "🍯" to "miere", "🥛" to "lapte", "💧" to "apă"),
                q("Cine îi dă miere ursului?", "🐻" to "mama", "🐝" to "albinele", "🦊" to "vulpea")
            )
        ),
        Story(
            id = "la_mare", title = "La mare", cover = "🏖️", level = 3,
            pages = listOf(
                page(
                    Backdrop.BEACH, "Va-ra, So-fi-a mer-ge la ma-re.",
                    Actor("👧", 0.3f, 0.74f, 0.3f, Motion.BOUNCE),
                    Actor("⛱️", 0.74f, 0.7f, 0.34f)
                ),
                page(
                    Backdrop.BEACH, "Ma-rea e al-bas-tră.",
                    Actor("⛵", 0.6f, 0.4f, 0.2f, Motion.SWAY),
                    Actor("🐬", 0.3f, 0.5f, 0.18f, Motion.BOUNCE)
                ),
                page(
                    Backdrop.BEACH, "So-fi-a fa-ce un cas-tel din ni-sip.",
                    Actor("👧", 0.28f, 0.74f, 0.3f),
                    Actor("🏰", 0.62f, 0.76f, 0.3f, Motion.PULSE)
                ),
                page(
                    Backdrop.BEACH, "Un val ma-re vi-ne.",
                    Actor("🌊", 0.36f, 0.6f, 0.44f, Motion.RUN),
                    Actor("🏰", 0.76f, 0.78f, 0.24f)
                ),
                page(
                    Backdrop.BEACH, "Cas-te-lul dis-pa-re!",
                    Actor("🌊", 0.6f, 0.72f, 0.4f, Motion.SWAY),
                    Actor("😮", 0.24f, 0.72f, 0.26f, Motion.PULSE)
                ),
                page(
                    Backdrop.BEACH, "So-fi-a râ-de și fa-ce al-tul.",
                    Actor("😄", 0.28f, 0.72f, 0.28f, Motion.BOUNCE),
                    Actor("🏰", 0.64f, 0.76f, 0.3f, Motion.PULSE),
                    Actor("🐚", 0.84f, 0.86f, 0.1f)
                )
            ),
            questions = listOf(
                q("Când merge Sofia la mare?", "☀️" to "vara", "⛄" to "iarna", "🍂" to "toamna"),
                q("Din ce face Sofia castelul?", "🏖️" to "din nisip", "🍫" to "din ciocolată", "❄️" to "din zăpadă"),
                q("Ce strică castelul?", "🌊" to "un val", "🐕" to "un cățel", "💨" to "vântul")
            )
        ),
        Story(
            id = "robotul_bip", title = "Robotul Bip", cover = "🤖", level = 3,
            pages = listOf(
                page(
                    Backdrop.ROOM, "Bip e un ro-bot mic.",
                    Actor("🤖", 0.5f, 0.66f, 0.36f, Motion.BOUNCE)
                ),
                page(
                    Backdrop.ROOM, "El a-re ochi verzi.",
                    Actor("🤖", 0.5f, 0.62f, 0.46f, Motion.PULSE),
                    Actor("✨", 0.76f, 0.34f, 0.12f, Motion.PULSE)
                ),
                page(
                    Backdrop.ROOM, "Bip nu poa-te ci-ti.",
                    Actor("🤖", 0.36f, 0.66f, 0.34f, Motion.SWAY),
                    Actor("📕", 0.66f, 0.78f, 0.2f),
                    Actor("❓", 0.56f, 0.34f, 0.16f, Motion.FLOAT)
                ),
                page(
                    Backdrop.ROOM, "Ma-ra îi a-ra-tă li-te-re-le.",
                    Actor("👧", 0.24f, 0.64f, 0.34f),
                    Actor("🔤", 0.5f, 0.5f, 0.22f, Motion.PULSE),
                    Actor("🤖", 0.76f, 0.68f, 0.3f)
                ),
                page(
                    Backdrop.ROOM, "Bip ci-teș-te: ma-ma.",
                    Actor("🤖", 0.34f, 0.66f, 0.34f, Motion.BOUNCE),
                    Actor("📖", 0.64f, 0.74f, 0.24f, Motion.PULSE)
                ),
                page(
                    Backdrop.NIGHT, "A-cum Bip ci-teș-te po-vești!",
                    Actor("🤖", 0.34f, 0.7f, 0.32f, Motion.BOUNCE),
                    Actor("📚", 0.64f, 0.78f, 0.26f),
                    Actor("🎉", 0.8f, 0.4f, 0.16f, Motion.FLOAT)
                )
            ),
            questions = listOf(
                q("Cine este Bip?", "🤖" to "un robot", "🐶" to "un cățel", "👦" to "un băiat"),
                q("Ce îi arată Mara lui Bip?", "🔤" to "literele", "🔢" to "cifrele", "🎨" to "culorile"),
                q("Ce citește Bip la final?", "📚" to "povești", "📰" to "ziare", "✉️" to "scrisori")
            )
        ),

        // --- Level 1: short words, mostly open syllables -------------------------
        Story(
            id = "rata_mica", title = "Rața mică", cover = "🦆", level = 1,
            pages = listOf(
                page(
                    Backdrop.FARM, "Ra-ța mi-că se joa-că.",
                    Actor("🦆", 0.46f, 0.76f, 0.2f, Motion.BOUNCE),
                    Actor("🌼", 0.7f, 0.84f, 0.12f, Motion.SWAY)
                ),
                page(
                    Backdrop.MEADOW, "Un-de e ma-ma ra-ță?",
                    Actor("🦆", 0.42f, 0.78f, 0.2f, Motion.SWAY),
                    Actor("❓", 0.56f, 0.46f, 0.16f, Motion.FLOAT)
                ),
                page(
                    Backdrop.MEADOW, "Ra-ța mi-că o ca-u-tă.",
                    Actor("🦆", 0.5f, 0.78f, 0.2f, Motion.RUN),
                    Actor("👀", 0.5f, 0.56f, 0.12f, Motion.PULSE)
                ),
                page(
                    Backdrop.MEADOW, "Ea ve-de o va-că ma-re.",
                    Actor("🦆", 0.22f, 0.8f, 0.16f),
                    Actor("🐄", 0.62f, 0.66f, 0.42f, Motion.SWAY)
                ),
                page(
                    Backdrop.MEADOW, "Va-ca zi-ce: mu, mu, ma-ma e a-ca-să!",
                    Actor("🐄", 0.62f, 0.66f, 0.4f, Motion.PULSE),
                    Actor("💬", 0.4f, 0.32f, 0.18f, Motion.FLOAT),
                    Actor("🦆", 0.22f, 0.8f, 0.16f, Motion.BOUNCE)
                ),
                page(
                    Backdrop.FARM, "Ra-ța mi-că fu-ge a-ca-să.",
                    Actor("🦆", 0.34f, 0.8f, 0.18f, Motion.RUN),
                    Actor("🏠", 0.74f, 0.56f, 0.36f)
                ),
                page(
                    Backdrop.FARM, "Ma-ma ra-ță o ia sub a-ri-pă.",
                    Actor("🦆", 0.48f, 0.7f, 0.36f, Motion.PULSE),
                    Actor("🦆", 0.62f, 0.8f, 0.16f, Motion.BOUNCE),
                    Actor("❤️", 0.56f, 0.36f, 0.14f, Motion.FLOAT)
                )
            ),
            questions = listOf(
                q("Pe cine caută rața mică?", "🦆" to "pe mama rață", "🐄" to "pe vacă", "🐶" to "pe cățel"),
                q("Ce zice vaca?", "🐄" to "mu, mu", "🐶" to "ham, ham", "🐱" to "miau"),
                q("Unde fuge rața mică?", "🏠" to "acasă", "🌲" to "în pădure", "🏫" to "la școală")
            )
        ),
        Story(
            id = "tortul_radu", title = "Tortul lui Radu", cover = "🎂", level = 1,
            pages = listOf(
                page(
                    Backdrop.ROOM, "Azi e zi-ua lui Ra-du.",
                    Actor("👦", 0.46f, 0.64f, 0.36f, Motion.BOUNCE),
                    Actor("🎈", 0.72f, 0.3f, 0.2f, Motion.FLOAT),
                    Actor("🎈", 0.86f, 0.4f, 0.16f, Motion.FLOAT)
                ),
                page(
                    Backdrop.ROOM, "Ra-du a-re șa-se ani.",
                    Actor("👦", 0.4f, 0.64f, 0.36f, Motion.PULSE),
                    Actor("6️⃣", 0.68f, 0.4f, 0.2f, Motion.BOUNCE)
                ),
                page(
                    Backdrop.ROOM, "Ma-ma a-du-ce un tort ma-re.",
                    Actor("👩", 0.3f, 0.62f, 0.38f),
                    Actor("🎂", 0.64f, 0.72f, 0.28f, Motion.PULSE)
                ),
                page(
                    Backdrop.ROOM, "Pi-si-ca vi-ne la tort.",
                    Actor("🐱", 0.3f, 0.74f, 0.24f, Motion.RUN),
                    Actor("🎂", 0.68f, 0.72f, 0.28f)
                ),
                page(
                    Backdrop.ROOM, "Pi-si-ca a-re cre-mă pe nas!",
                    Actor("😹", 0.42f, 0.62f, 0.36f, Motion.BOUNCE),
                    Actor("🎂", 0.74f, 0.74f, 0.24f)
                ),
                page(
                    Backdrop.ROOM, "Ra-du râ-de și ia o fe-li-e.",
                    Actor("😄", 0.3f, 0.6f, 0.34f, Motion.BOUNCE),
                    Actor("🍰", 0.56f, 0.76f, 0.2f, Motion.PULSE),
                    Actor("🐱", 0.8f, 0.76f, 0.2f, Motion.SWAY)
                )
            ),
            questions = listOf(
                q("Ce aduce mama?", "🎂" to "un tort", "🍲" to "o supă", "🍎" to "un măr"),
                q("Câți ani are Radu?", "6️⃣" to "șase", "3️⃣" to "trei", "9️⃣" to "nouă"),
                q("Ce are pisica pe nas?", "🎂" to "cremă", "💧" to "apă", "🌸" to "o floare")
            )
        ),
        Story(
            id = "balonul_inei", title = "Balonul Inei", cover = "🎈", level = 1,
            pages = listOf(
                page(
                    Backdrop.MEADOW, "I-na a-re un ba-lon.",
                    Actor("👧", 0.36f, 0.66f, 0.36f),
                    Actor("🎈", 0.56f, 0.34f, 0.24f, Motion.SWAY)
                ),
                page(
                    Backdrop.MEADOW, "Ba-lo-nul e ro-șu.",
                    Actor("🎈", 0.5f, 0.48f, 0.44f, Motion.PULSE)
                ),
                page(
                    Backdrop.MEADOW, "Vi-ne vân-tul: vâj!",
                    Actor("💨", 0.24f, 0.36f, 0.22f, Motion.RUN),
                    Actor("🎈", 0.62f, 0.32f, 0.22f, Motion.SWAY),
                    Actor("👧", 0.62f, 0.72f, 0.28f, Motion.PULSE)
                ),
                page(
                    Backdrop.MEADOW, "Ba-lo-nul zboa-ră sus, sus.",
                    Actor("🎈", 0.56f, 0.14f, 0.16f, Motion.FLOAT),
                    Actor("😮", 0.42f, 0.74f, 0.26f)
                ),
                page(
                    Backdrop.FOREST, "Ba-lo-nul e sus în co-pac.",
                    Actor("🌳", 0.5f, 0.5f, 0.45f),
                    Actor("🎈", 0.56f, 0.32f, 0.16f, Motion.SWAY)
                ),
                page(
                    Backdrop.FOREST, "Ta-ta ia ba-lo-nul.",
                    Actor("👨", 0.4f, 0.64f, 0.38f, Motion.BOUNCE),
                    Actor("🎈", 0.6f, 0.3f, 0.2f, Motion.SWAY)
                ),
                page(
                    Backdrop.MEADOW, "I-na îi dă ta-tei un pu-pic.",
                    Actor("👨", 0.32f, 0.62f, 0.38f),
                    Actor("👧", 0.58f, 0.7f, 0.3f, Motion.BOUNCE),
                    Actor("😘", 0.46f, 0.32f, 0.14f, Motion.FLOAT),
                    Actor("🎈", 0.8f, 0.36f, 0.2f, Motion.SWAY)
                )
            ),
            questions = listOf(
                q("Ce are Ina?", "🎈" to "un balon", "⚽" to "o minge", "📕" to "o carte"),
                q("Ce culoare are balonul?", "❤️" to "roșu", "💙" to "albastru", "💚" to "verde"),
                q("Cine ia balonul din copac?", "👨" to "tata", "👵" to "bunica", "🐦" to "o pasăre")
            )
        ),

        // --- Level 2 ---------------------------------------------------------------
        Story(
            id = "trenul", title = "Cu trenul la mare", cover = "🚂", level = 2,
            pages = listOf(
                page(
                    Backdrop.MEADOW, "Ma-tei mer-ge cu tre-nul.",
                    Actor("👦", 0.24f, 0.68f, 0.32f, Motion.BOUNCE),
                    Actor("🚂", 0.64f, 0.7f, 0.36f)
                ),
                page(
                    Backdrop.MEADOW, "Tre-nul plea-că din ga-ră.",
                    Actor("🚂", 0.5f, 0.72f, 0.36f, Motion.RUN),
                    Actor("💨", 0.24f, 0.5f, 0.18f, Motion.FLOAT)
                ),
                page(
                    Backdrop.FARM, "Ma-tei ve-de vaci și oi.",
                    Actor("🐄", 0.3f, 0.7f, 0.3f, Motion.SWAY),
                    Actor("🐑", 0.56f, 0.76f, 0.22f, Motion.BOUNCE),
                    Actor("🐑", 0.74f, 0.78f, 0.18f, Motion.SWAY)
                ),
                page(
                    Backdrop.FOREST, "Tre-nul tre-ce prin pă-du-re.",
                    Actor("🚂", 0.5f, 0.76f, 0.34f, Motion.RUN)
                ),
                page(
                    Backdrop.NIGHT, "În tu-nel e în-tu-ne-ric.",
                    Actor("🚂", 0.42f, 0.74f, 0.32f, Motion.RUN),
                    Actor("😮", 0.72f, 0.46f, 0.2f, Motion.PULSE)
                ),
                page(
                    Backdrop.BEACH, "Ma-tei a-jun-ge la ma-re!",
                    Actor("👦", 0.3f, 0.76f, 0.3f, Motion.BOUNCE),
                    Actor("⛱️", 0.72f, 0.72f, 0.32f),
                    Actor("🌊", 0.56f, 0.46f, 0.2f, Motion.SWAY)
                )
            ),
            questions = listOf(
                q("Cu ce merge Matei?", "🚂" to "cu trenul", "🚗" to "cu mașina", "✈️" to "cu avionul"),
                q("Ce animale vede Matei?", "🐄" to "vaci și oi", "🦁" to "lei", "🐘" to "elefanți"),
                q("Unde ajunge Matei?", "🌊" to "la mare", "🏔️" to "la munte", "🏫" to "la școală")
            )
        ),
        Story(
            id = "pestele_mic", title = "Peștele cel mic", cover = "🐠", level = 2,
            pages = listOf(
                page(
                    Backdrop.BEACH, "Un peș-te mic î-noa-tă în ma-re.",
                    Actor("🐠", 0.46f, 0.5f, 0.2f, Motion.RUN)
                ),
                page(
                    Backdrop.BEACH, "Peș-te-le e sin-gur.",
                    Actor("🐠", 0.5f, 0.5f, 0.2f, Motion.SWAY),
                    Actor("💭", 0.66f, 0.24f, 0.18f, Motion.FLOAT)
                ),
                page(
                    Backdrop.BEACH, "El ve-de un crab ro-șu.",
                    Actor("🐠", 0.32f, 0.48f, 0.18f, Motion.SWAY, flip = true),
                    Actor("🦀", 0.62f, 0.8f, 0.24f, Motion.BOUNCE)
                ),
                page(
                    Backdrop.BEACH, "Cra-bul zi-ce: Hai la joa-că!",
                    Actor("🦀", 0.56f, 0.8f, 0.24f, Motion.SWAY),
                    Actor("💬", 0.72f, 0.56f, 0.16f, Motion.PULSE),
                    Actor("🐠", 0.32f, 0.5f, 0.18f, Motion.BOUNCE)
                ),
                page(
                    Backdrop.BEACH, "Vi-ne și un del-fin.",
                    Actor("🐬", 0.66f, 0.46f, 0.26f, Motion.BOUNCE),
                    Actor("🐠", 0.3f, 0.52f, 0.16f),
                    Actor("🦀", 0.44f, 0.82f, 0.18f)
                ),
                page(
                    Backdrop.BEACH, "A-cum peș-te-le a-re doi pri-e-teni.",
                    Actor("🐠", 0.5f, 0.5f, 0.2f, Motion.PULSE),
                    Actor("🐬", 0.76f, 0.44f, 0.22f, Motion.SWAY),
                    Actor("🦀", 0.26f, 0.8f, 0.18f, Motion.SWAY)
                ),
                page(
                    Backdrop.BEACH, "Ei se joa-că toa-tă zi-ua.",
                    Actor("🐠", 0.36f, 0.48f, 0.18f, Motion.RUN),
                    Actor("🐬", 0.66f, 0.44f, 0.22f, Motion.BOUNCE),
                    Actor("🦀", 0.5f, 0.82f, 0.18f, Motion.BOUNCE)
                )
            ),
            questions = listOf(
                q("Unde înoată peștele?", "🌊" to "în mare", "🌲" to "în pădure", "🛁" to "în cadă"),
                q("Ce culoare are crabul?", "❤️" to "roșu", "💛" to "galben", "💚" to "verde"),
                q("Câți prieteni are peștele la final?", "2️⃣" to "doi", "1️⃣" to "unul", "5️⃣" to "cinci")
            )
        ),
        Story(
            id = "ariciul_marul", title = "Ariciul și mărul", cover = "🦔", level = 2,
            pages = listOf(
                page(
                    Backdrop.FOREST, "Un a-rici mic se plim-bă prin pă-du-re.",
                    Actor("🦔", 0.4f, 0.8f, 0.22f, Motion.RUN)
                ),
                page(
                    Backdrop.FOREST, "El ve-de un măr ro-șu în pom.",
                    Actor("🌳", 0.64f, 0.5f, 0.45f),
                    Actor("🍎", 0.68f, 0.36f, 0.12f, Motion.SWAY),
                    Actor("🦔", 0.26f, 0.82f, 0.2f)
                ),
                page(
                    Backdrop.FOREST, "Poc! Mă-rul ca-de pe spa-te-le lui.",
                    Actor("🦔", 0.46f, 0.8f, 0.24f, Motion.PULSE),
                    Actor("🍎", 0.46f, 0.62f, 0.14f, Motion.BOUNCE),
                    Actor("💥", 0.62f, 0.5f, 0.12f, Motion.PULSE)
                ),
                page(
                    Backdrop.FOREST, "A-ri-ciul du-ce mă-rul a-ca-să.",
                    Actor("🦔", 0.46f, 0.8f, 0.24f, Motion.RUN, flip = true),
                    Actor("🍎", 0.46f, 0.64f, 0.12f, Motion.RUN)
                ),
                page(
                    Backdrop.EVENING, "Ma-ma fa-ce o plă-cin-tă cu me-re.",
                    Actor("🦔", 0.26f, 0.72f, 0.3f),
                    Actor("🥧", 0.6f, 0.76f, 0.24f, Motion.PULSE),
                    Actor("♨️", 0.6f, 0.54f, 0.12f, Motion.FLOAT)
                ),
                page(
                    Backdrop.EVENING, "A-ri-ciul mă-nân-că și zâm-beș-te.",
                    Actor("🦔", 0.36f, 0.74f, 0.26f, Motion.BOUNCE),
                    Actor("😋", 0.36f, 0.42f, 0.16f, Motion.FLOAT),
                    Actor("🥧", 0.66f, 0.78f, 0.2f)
                )
            ),
            questions = listOf(
                q("Ce vede ariciul în pom?", "🍎" to "un măr", "🍐" to "o pară", "🍌" to "o banană"),
                q("Unde cade mărul?", "🦔" to "pe spatele ariciului", "💧" to "în apă", "🏠" to "pe casă"),
                q("Ce face mama?", "🥧" to "o plăcintă", "🍲" to "o supă", "🎂" to "un tort")
            )
        ),

        // --- Level 3 ---------------------------------------------------------------
        Story(
            id = "dragonul_fricos", title = "Dragonul fricos", cover = "🐉", level = 3,
            pages = listOf(
                page(
                    Backdrop.FOREST, "Bu-bu e un dra-gon mic și ver-de.",
                    Actor("🐉", 0.48f, 0.68f, 0.38f, Motion.BOUNCE)
                ),
                page(
                    Backdrop.FOREST, "Toți dra-go-nii scui-pă foc.",
                    Actor("🐲", 0.24f, 0.56f, 0.26f, Motion.PULSE),
                    Actor("🔥", 0.44f, 0.58f, 0.2f, Motion.PULSE),
                    Actor("🐲", 0.78f, 0.56f, 0.26f, Motion.PULSE, flip = true),
                    Actor("🔥", 0.6f, 0.62f, 0.16f, Motion.PULSE)
                ),
                page(
                    Backdrop.FOREST, "Dar lui Bu-bu îi e fri-că de foc.",
                    Actor("🐉", 0.3f, 0.7f, 0.3f, Motion.SWAY, flip = true),
                    Actor("😨", 0.3f, 0.36f, 0.14f, Motion.PULSE),
                    Actor("🔥", 0.74f, 0.74f, 0.2f, Motion.PULSE)
                ),
                page(
                    Backdrop.MEADOW, "Bu-bu su-flă și ies flori!",
                    Actor("🐉", 0.26f, 0.66f, 0.32f, Motion.PULSE),
                    Actor("🌸", 0.5f, 0.5f, 0.14f, Motion.FLOAT),
                    Actor("🌼", 0.64f, 0.38f, 0.14f, Motion.FLOAT),
                    Actor("🌷", 0.78f, 0.54f, 0.14f, Motion.FLOAT)
                ),
                page(
                    Backdrop.MEADOW, "O fe-ti-ță pri-meș-te o floa-re.",
                    Actor("🐉", 0.26f, 0.68f, 0.3f),
                    Actor("🌸", 0.5f, 0.56f, 0.14f, Motion.PULSE),
                    Actor("👧", 0.72f, 0.66f, 0.34f, Motion.BOUNCE)
                ),
                page(
                    Backdrop.MEADOW, "Ea zâm-beș-te: Mul-țu-mesc, Bu-bu!",
                    Actor("😊", 0.66f, 0.56f, 0.3f, Motion.BOUNCE),
                    Actor("🌸", 0.48f, 0.64f, 0.12f),
                    Actor("🐉", 0.26f, 0.68f, 0.3f, Motion.SWAY)
                ),
                page(
                    Backdrop.EVENING, "A-cum Bu-bu e cel mai fe-ri-cit dra-gon.",
                    Actor("🐉", 0.42f, 0.66f, 0.36f, Motion.BOUNCE),
                    Actor("💐", 0.7f, 0.74f, 0.22f, Motion.PULSE),
                    Actor("✨", 0.64f, 0.34f, 0.14f, Motion.FLOAT)
                )
            ),
            questions = listOf(
                q("De ce îi este frică lui Bubu?", "🔥" to "de foc", "💧" to "de apă", "🌙" to "de întuneric"),
                q("Ce iese când suflă Bubu?", "🌸" to "flori", "🔥" to "foc", "💨" to "fum"),
                q("Cine primește o floare?", "👧" to "o fetiță", "👵" to "o bunică", "🐱" to "o pisică")
            )
        ),
        Story(
            id = "prima_zi_scoala", title = "Prima zi de școală", cover = "🎒", level = 3,
            pages = listOf(
                page(
                    Backdrop.ROOM, "E-ma a-re un ghioz-dan nou.",
                    Actor("👧", 0.36f, 0.62f, 0.36f, Motion.BOUNCE),
                    Actor("🎒", 0.66f, 0.72f, 0.26f, Motion.PULSE)
                ),
                page(
                    Backdrop.ROOM, "În ghioz-dan sunt cre-ioa-ne și ca-ie-te.",
                    Actor("🎒", 0.3f, 0.7f, 0.3f),
                    Actor("✏️", 0.56f, 0.6f, 0.16f, Motion.SWAY),
                    Actor("📚", 0.76f, 0.72f, 0.22f, Motion.PULSE)
                ),
                page(
                    Backdrop.MEADOW, "E-ma mer-ge la școa-lă cu ma-ma.",
                    Actor("👩", 0.2f, 0.64f, 0.34f, Motion.RUN),
                    Actor("👧", 0.36f, 0.72f, 0.26f, Motion.RUN),
                    Actor("🏫", 0.74f, 0.56f, 0.4f)
                ),
                page(
                    Backdrop.ROOM, "E-ma e pu-țin spe-ri-a-tă.",
                    Actor("😟", 0.5f, 0.6f, 0.36f, Motion.SWAY),
                    Actor("🎒", 0.74f, 0.76f, 0.2f)
                ),
                page(
                    Backdrop.ROOM, "Doam-na în-vă-ță-toa-re zâm-beș-te.",
                    Actor("👩‍🏫", 0.46f, 0.6f, 0.4f, Motion.PULSE),
                    Actor("😊", 0.72f, 0.34f, 0.14f, Motion.FLOAT)
                ),
                page(
                    Backdrop.ROOM, "Lân-gă E-ma stă o fe-ti-ță ve-se-lă.",
                    Actor("👧", 0.36f, 0.66f, 0.32f),
                    Actor("👧", 0.64f, 0.66f, 0.32f, Motion.BOUNCE, flip = true),
                    Actor("😄", 0.64f, 0.34f, 0.12f, Motion.FLOAT)
                ),
                page(
                    Backdrop.ROOM, "A-cum E-ma a-re o pri-e-te-nă.",
                    Actor("👧", 0.36f, 0.66f, 0.32f, Motion.BOUNCE),
                    Actor("👧", 0.62f, 0.66f, 0.32f, Motion.BOUNCE, flip = true),
                    Actor("❤️", 0.49f, 0.36f, 0.14f, Motion.PULSE)
                )
            ),
            questions = listOf(
                q("Ce are Ema nou?", "🎒" to "un ghiozdan", "👟" to "pantofi", "🎩" to "o pălărie"),
                q("Cu cine merge Ema la școală?", "👩" to "cu mama", "👴" to "cu bunicul", "🐶" to "cu cățelul"),
                q("Pe cine are Ema la final?", "👧" to "o prietenă", "🐱" to "o pisică", "🤖" to "un robot")
            )
        )
    )

    // ---------------------------------------------------------------------------
    // Classic tales, retold in short sentences for a beginning reader.
    // ---------------------------------------------------------------------------

    private fun classic(
        id: String,
        title: String,
        cover: String,
        level: Int,
        pages: List<StoryPage>,
        questions: List<StoryQuestion>
    ) = Story(id, title, cover, level, pages, questions, StoryCategory.CLASSIC)

    private fun classicStories(): List<Story> = listOf(
        // --- Level 1 -----------------------------------------------------------------
        classic(
            id = "ridichea_uriasa", title = "Ridichea uriașă", cover = "🌱", level = 1,
            pages = listOf(
                page(
                    Backdrop.GARDEN, "Bu-ni-cul pu-ne o ri-di-che.",
                    Actor("👴", 0.3f, 0.56f, 0.36f),
                    Actor("🌱", 0.66f, 0.76f, 0.14f, Motion.PULSE)
                ),
                page(
                    Backdrop.GARDEN, "Ri-di-chea creș-te ma-re, ma-re.",
                    Actor("🔴", 0.5f, 0.84f, 0.3f),
                    Actor("🌱", 0.5f, 0.6f, 0.26f, Motion.SWAY),
                    Actor("✨", 0.72f, 0.4f, 0.12f, Motion.PULSE)
                ),
                page(
                    Backdrop.GARDEN, "Bu-ni-cul tra-ge, tra-ge, și nu poa-te.",
                    Actor("👴", 0.36f, 0.56f, 0.34f, Motion.SWAY),
                    Actor("🔴", 0.64f, 0.84f, 0.28f),
                    Actor("🌱", 0.64f, 0.62f, 0.24f, Motion.SWAY)
                ),
                page(
                    Backdrop.GARDEN, "Vi-ne bu-ni-ca și tra-ge cu el.",
                    Actor("👵", 0.16f, 0.58f, 0.3f, Motion.SWAY),
                    Actor("👴", 0.38f, 0.56f, 0.32f, Motion.SWAY),
                    Actor("🔴", 0.68f, 0.84f, 0.28f),
                    Actor("🌱", 0.68f, 0.62f, 0.24f, Motion.SWAY)
                ),
                page(
                    Backdrop.GARDEN, "Trag, trag, și nu pot.",
                    Actor("👵", 0.16f, 0.58f, 0.3f, Motion.SWAY),
                    Actor("👴", 0.38f, 0.56f, 0.32f, Motion.SWAY),
                    Actor("💦", 0.46f, 0.32f, 0.12f, Motion.PULSE),
                    Actor("🔴", 0.68f, 0.84f, 0.28f),
                    Actor("🌱", 0.68f, 0.62f, 0.24f)
                ),
                page(
                    Backdrop.GARDEN, "Vi-ne ne-poa-ta, a-poi vi-ne că-țe-lul.",
                    Actor("🐶", 0.08f, 0.66f, 0.14f, Motion.BOUNCE),
                    Actor("👧", 0.24f, 0.62f, 0.18f, Motion.SWAY),
                    Actor("👵", 0.41f, 0.6f, 0.2f, Motion.SWAY),
                    Actor("👴", 0.59f, 0.58f, 0.22f, Motion.SWAY),
                    Actor("🔴", 0.84f, 0.84f, 0.22f),
                    Actor("🌱", 0.84f, 0.66f, 0.18f)
                ),
                page(
                    Backdrop.GARDEN, "Vi-ne și pi-si-ca, dar nu pot.",
                    Actor("🐱", 0.07f, 0.68f, 0.12f, Motion.BOUNCE),
                    Actor("🐶", 0.2f, 0.66f, 0.13f, Motion.SWAY),
                    Actor("👧", 0.34f, 0.62f, 0.16f, Motion.SWAY),
                    Actor("👵", 0.5f, 0.6f, 0.18f, Motion.SWAY),
                    Actor("👴", 0.67f, 0.58f, 0.2f, Motion.SWAY),
                    Actor("🔴", 0.88f, 0.84f, 0.2f),
                    Actor("🌱", 0.88f, 0.68f, 0.16f)
                ),
                page(
                    Backdrop.GARDEN, "La ur-mă vi-ne un șo-ri-cel mic.",
                    Actor("🐭", 0.2f, 0.62f, 0.16f, Motion.RUN),
                    Actor("🐱", 0.48f, 0.6f, 0.2f, Motion.PULSE),
                    Actor("🔴", 0.8f, 0.84f, 0.24f),
                    Actor("🌱", 0.8f, 0.66f, 0.2f)
                ),
                page(
                    Backdrop.GARDEN, "Toți trag îm-pre-u-nă: hop!",
                    Actor("🐭", 0.06f, 0.7f, 0.1f, Motion.SWAY),
                    Actor("🐱", 0.17f, 0.68f, 0.11f, Motion.SWAY),
                    Actor("🐶", 0.29f, 0.66f, 0.12f, Motion.SWAY),
                    Actor("👧", 0.42f, 0.62f, 0.15f, Motion.SWAY),
                    Actor("👵", 0.56f, 0.6f, 0.16f, Motion.SWAY),
                    Actor("👴", 0.71f, 0.58f, 0.18f, Motion.SWAY),
                    Actor("🔴", 0.89f, 0.84f, 0.18f),
                    Actor("🌱", 0.89f, 0.7f, 0.15f, Motion.SWAY)
                ),
                page(
                    Backdrop.GARDEN, "Ri-di-chea ie-se din pă-mânt!",
                    Actor("👵", 0.14f, 0.62f, 0.26f, Motion.BOUNCE),
                    Actor("👴", 0.32f, 0.6f, 0.3f, Motion.BOUNCE),
                    Actor("🔴", 0.66f, 0.5f, 0.32f),
                    Actor("🌱", 0.66f, 0.28f, 0.2f),
                    Actor("✨", 0.86f, 0.3f, 0.14f, Motion.PULSE)
                ),
                page(
                    Backdrop.MEADOW, "Îm-pre-u-nă e mai u-șor!",
                    Actor("👴", 0.14f, 0.6f, 0.3f, Motion.BOUNCE),
                    Actor("👵", 0.3f, 0.62f, 0.28f, Motion.BOUNCE),
                    Actor("👧", 0.45f, 0.66f, 0.24f, Motion.BOUNCE),
                    Actor("🐶", 0.6f, 0.74f, 0.18f, Motion.BOUNCE),
                    Actor("🐱", 0.72f, 0.76f, 0.16f, Motion.BOUNCE),
                    Actor("🐭", 0.82f, 0.8f, 0.12f, Motion.BOUNCE),
                    Actor("❤️", 0.46f, 0.3f, 0.14f, Motion.PULSE)
                )
            ),
            questions = listOf(
                q("Ce pune bunicul?", "🌱" to "o ridiche", "🍎" to "un măr", "🌻" to "o floare"),
                q("Cine vine la urmă?", "🐭" to "un șoricel", "🐻" to "un urs", "🐴" to "un cal"),
                q("Cum scot ridichea?", "🤝" to "împreună", "💪" to "doar bunicul", "🐶" to "doar cățelul")
            )
        ),
        classic(
            id = "turtita", title = "Turtița", cover = "🍪", level = 1,
            pages = listOf(
                page(
                    Backdrop.ROOM, "Bu-ni-ca fa-ce o tur-ti-ță.",
                    Actor("👵", 0.3f, 0.6f, 0.38f),
                    Actor("🍪", 0.64f, 0.72f, 0.2f, Motion.PULSE),
                    Actor("♨️", 0.64f, 0.5f, 0.12f, Motion.FLOAT)
                ),
                page(
                    Backdrop.ROOM, "Tur-ti-ța stă la fe-res-tră.",
                    Actor("🍪", 0.21f, 0.4f, 0.16f, Motion.PULSE),
                    Actor("👵", 0.7f, 0.62f, 0.34f)
                ),
                page(
                    Backdrop.MEADOW, "Hop! Tur-ti-ța fu-ge pe drum.",
                    Actor("🍪", 0.52f, 0.76f, 0.18f, Motion.RUN),
                    Actor("💨", 0.3f, 0.72f, 0.12f, Motion.FLOAT)
                ),
                page(
                    Backdrop.FOREST, "Un ie-pu-re zi-ce: Te mă-nânc!",
                    Actor("🍪", 0.3f, 0.78f, 0.16f, Motion.PULSE),
                    Actor("🐰", 0.66f, 0.7f, 0.3f, Motion.BOUNCE),
                    Actor("💬", 0.8f, 0.4f, 0.14f, Motion.FLOAT)
                ),
                page(
                    Backdrop.FOREST, "Tur-ti-ța cân-tă: Fug și de ti-ne!",
                    Actor("🍪", 0.3f, 0.78f, 0.16f, Motion.RUN),
                    Actor("🎵", 0.3f, 0.5f, 0.12f, Motion.FLOAT),
                    Actor("🐰", 0.74f, 0.72f, 0.24f)
                ),
                page(
                    Backdrop.FOREST, "Vi-ne un lup, a-poi un urs.",
                    Actor("🍪", 0.14f, 0.8f, 0.14f),
                    Actor("🐺", 0.42f, 0.7f, 0.3f, Motion.SWAY),
                    Actor("🐻", 0.74f, 0.68f, 0.34f, Motion.SWAY)
                ),
                page(
                    Backdrop.FOREST, "Tur-ti-ța fu-ge și de ei.",
                    Actor("🐺", 0.18f, 0.7f, 0.24f),
                    Actor("🍪", 0.5f, 0.78f, 0.16f, Motion.RUN),
                    Actor("🎵", 0.5f, 0.5f, 0.12f, Motion.FLOAT),
                    Actor("🐻", 0.84f, 0.68f, 0.28f)
                ),
                page(
                    Backdrop.FOREST, "La ur-mă vi-ne o vul-pe vi-clea-nă.",
                    Actor("🍪", 0.26f, 0.8f, 0.14f),
                    Actor("🦊", 0.62f, 0.7f, 0.32f, Motion.SWAY)
                ),
                page(
                    Backdrop.FOREST, "Vul-pea zi-ce: Vi-no pe na-sul meu!",
                    Actor("🍪", 0.24f, 0.8f, 0.14f, Motion.BOUNCE),
                    Actor("🦊", 0.56f, 0.68f, 0.36f, Motion.PULSE),
                    Actor("💬", 0.78f, 0.34f, 0.14f, Motion.FLOAT)
                ),
                page(
                    Backdrop.FOREST, "Tur-ti-ța sa-re pe nas.",
                    Actor("🦊", 0.52f, 0.68f, 0.38f),
                    Actor("🍪", 0.52f, 0.4f, 0.14f, Motion.BOUNCE)
                ),
                page(
                    Backdrop.FOREST, "Hap! Vul-pea o mă-nân-că.",
                    Actor("🦊", 0.48f, 0.66f, 0.4f, Motion.PULSE),
                    Actor("😋", 0.74f, 0.34f, 0.14f, Motion.FLOAT)
                ),
                page(
                    Backdrop.EVENING, "Nu as-cul-ta de vul-pea vi-clea-nă!",
                    Actor("👵", 0.28f, 0.62f, 0.34f),
                    Actor("☝️", 0.44f, 0.38f, 0.14f, Motion.PULSE),
                    Actor("🦊", 0.72f, 0.72f, 0.28f, Motion.SWAY)
                )
            ),
            questions = listOf(
                q("Cine face turtița?", "👵" to "bunica", "👴" to "bunicul", "🐻" to "ursul"),
                q("Pe cine întâlnește întâi turtița?", "🐰" to "un iepure", "🦊" to "o vulpe", "🐻" to "un urs"),
                q("Cine o păcălește pe turtiță?", "🦊" to "vulpea", "🐺" to "lupul", "🐰" to "iepurele")
            )
        ),
        classic(
            id = "manusa", title = "Mănușa", cover = "🧤", level = 1,
            pages = listOf(
                page(
                    Backdrop.WINTER, "Bu-ni-cul pier-de o mă-nu-șă în ză-pa-dă.",
                    Actor("👴", 0.3f, 0.62f, 0.36f, Motion.RUN),
                    Actor("🧤", 0.72f, 0.84f, 0.14f)
                ),
                page(
                    Backdrop.WINTER, "Un șo-ri-cel in-tră în mă-nu-șă.",
                    Actor("🐭", 0.26f, 0.82f, 0.14f, Motion.RUN),
                    Actor("🧤", 0.58f, 0.74f, 0.34f, Motion.PULSE)
                ),
                page(
                    Backdrop.WINTER, "Vi-ne o broas-că: Pot in-tra și eu?",
                    Actor("🐸", 0.22f, 0.8f, 0.16f, Motion.BOUNCE),
                    Actor("❓", 0.24f, 0.56f, 0.1f, Motion.FLOAT),
                    Actor("🧤", 0.6f, 0.72f, 0.36f),
                    Actor("🐭", 0.6f, 0.7f, 0.1f, Motion.PULSE)
                ),
                page(
                    Backdrop.WINTER, "Vi-ne un ie-pu-re: Pot in-tra și eu?",
                    Actor("🐰", 0.2f, 0.76f, 0.24f, Motion.BOUNCE),
                    Actor("🧤", 0.62f, 0.7f, 0.4f),
                    Actor("🐭", 0.56f, 0.7f, 0.1f, Motion.PULSE),
                    Actor("🐸", 0.67f, 0.68f, 0.1f, Motion.PULSE)
                ),
                page(
                    Backdrop.WINTER, "Vi-ne o vul-pe: Pot in-tra și eu?",
                    Actor("🦊", 0.2f, 0.74f, 0.26f, Motion.SWAY),
                    Actor("🧤", 0.62f, 0.68f, 0.42f),
                    Actor("🐭", 0.54f, 0.7f, 0.1f, Motion.PULSE),
                    Actor("🐸", 0.62f, 0.66f, 0.1f, Motion.PULSE),
                    Actor("🐰", 0.7f, 0.68f, 0.11f, Motion.PULSE)
                ),
                page(
                    Backdrop.WINTER, "Vi-ne un lup și un mis-treț.",
                    Actor("🐺", 0.16f, 0.72f, 0.26f, Motion.RUN),
                    Actor("🐗", 0.4f, 0.76f, 0.22f, Motion.RUN),
                    Actor("🧤", 0.74f, 0.7f, 0.42f, Motion.PULSE)
                ),
                page(
                    Backdrop.WINTER, "La ur-mă vi-ne un urs ma-re.",
                    Actor("🐻", 0.28f, 0.64f, 0.42f, Motion.SWAY),
                    Actor("🧤", 0.74f, 0.72f, 0.38f, Motion.PULSE)
                ),
                page(
                    Backdrop.WINTER, "Toți se strâng și e loc pen-tru toți.",
                    Actor("🧤", 0.5f, 0.64f, 0.45f, Motion.PULSE),
                    Actor("🐭", 0.33f, 0.6f, 0.1f, Motion.BOUNCE),
                    Actor("🐸", 0.39f, 0.58f, 0.1f, Motion.BOUNCE),
                    Actor("🐰", 0.45f, 0.57f, 0.1f, Motion.BOUNCE),
                    Actor("🦊", 0.51f, 0.57f, 0.1f, Motion.BOUNCE),
                    Actor("🐺", 0.57f, 0.57f, 0.1f, Motion.BOUNCE),
                    Actor("🐗", 0.63f, 0.58f, 0.1f, Motion.BOUNCE),
                    Actor("🐻", 0.69f, 0.6f, 0.11f, Motion.BOUNCE)
                ),
                page(
                    Backdrop.WINTER, "Vi-ne că-țe-lul bu-ni-cu-lui: ham, ham!",
                    Actor("🐕", 0.24f, 0.74f, 0.3f, Motion.BOUNCE),
                    Actor("💬", 0.36f, 0.4f, 0.14f, Motion.PULSE),
                    Actor("🧤", 0.72f, 0.7f, 0.4f, Motion.SWAY)
                ),
                page(
                    Backdrop.WINTER, "A-ni-ma-le-le fug în pă-du-re.",
                    Actor("🧤", 0.12f, 0.86f, 0.14f),
                    Actor("🐭", 0.26f, 0.84f, 0.1f, Motion.RUN),
                    Actor("🐰", 0.38f, 0.8f, 0.14f, Motion.RUN),
                    Actor("🦊", 0.52f, 0.78f, 0.16f, Motion.RUN),
                    Actor("🐺", 0.66f, 0.74f, 0.2f, Motion.RUN),
                    Actor("🐻", 0.84f, 0.66f, 0.28f, Motion.RUN)
                ),
                page(
                    Backdrop.WINTER, "Bu-ni-cul gă-seș-te mă-nu-șa cal-dă.",
                    Actor("👴", 0.36f, 0.6f, 0.38f, Motion.BOUNCE),
                    Actor("🧤", 0.62f, 0.62f, 0.18f, Motion.PULSE),
                    Actor("🐕", 0.8f, 0.76f, 0.22f, Motion.SWAY)
                ),
                page(
                    Backdrop.WINTER, "Când îm-păr-țim, e loc pen-tru toți!",
                    Actor("🧤", 0.5f, 0.66f, 0.3f, Motion.PULSE),
                    Actor("🐭", 0.12f, 0.84f, 0.1f, Motion.BOUNCE),
                    Actor("🐸", 0.22f, 0.82f, 0.12f, Motion.BOUNCE),
                    Actor("🐰", 0.32f, 0.78f, 0.16f, Motion.BOUNCE),
                    Actor("🦊", 0.68f, 0.78f, 0.16f, Motion.BOUNCE),
                    Actor("🐺", 0.78f, 0.78f, 0.16f, Motion.BOUNCE),
                    Actor("🐻", 0.9f, 0.74f, 0.2f, Motion.BOUNCE),
                    Actor("❤️", 0.5f, 0.3f, 0.14f, Motion.PULSE)
                )
            ),
            questions = listOf(
                q("Ce pierde bunicul?", "🧤" to "o mănușă", "🎩" to "o pălărie", "👢" to "o cizmă"),
                q("Cine intră primul în mănușă?", "🐭" to "un șoricel", "🐻" to "un urs", "🐸" to "o broască"),
                q("Cine sperie animalele?", "🐕" to "cățelul", "🐱" to "pisica", "🐓" to "cocoșul")
            )
        ),
        classic(
            id = "iepurele_testoasa", title = "Iepurele și broasca țestoasă", cover = "🐢", level = 1,
            pages = listOf(
                page(
                    Backdrop.MEADOW, "Ie-pu-re-le a-lear-gă foar-te re-pe-de.",
                    Actor("🐇", 0.52f, 0.74f, 0.28f, Motion.RUN),
                    Actor("💨", 0.24f, 0.72f, 0.14f, Motion.FLOAT)
                ),
                page(
                    Backdrop.MEADOW, "Broas-ca țes-toa-să mer-ge în-cet.",
                    Actor("🐢", 0.5f, 0.78f, 0.26f, Motion.SWAY)
                ),
                page(
                    Backdrop.MEADOW, "Ie-pu-re-le râ-de de ea.",
                    Actor("🐰", 0.3f, 0.7f, 0.3f, Motion.BOUNCE),
                    Actor("😂", 0.3f, 0.4f, 0.12f, Motion.FLOAT),
                    Actor("🐢", 0.7f, 0.8f, 0.22f)
                ),
                page(
                    Backdrop.MEADOW, "Ea zi-ce: Hai la în-tre-ce-re!",
                    Actor("🐢", 0.32f, 0.78f, 0.24f, Motion.PULSE),
                    Actor("💬", 0.4f, 0.5f, 0.14f, Motion.FLOAT),
                    Actor("🐰", 0.66f, 0.7f, 0.28f),
                    Actor("🏁", 0.88f, 0.64f, 0.14f, Motion.SWAY)
                ),
                page(
                    Backdrop.MEADOW, "Ie-pu-re-le zboa-ră ca vân-tul.",
                    Actor("🐢", 0.12f, 0.8f, 0.18f),
                    Actor("💨", 0.42f, 0.72f, 0.14f, Motion.FLOAT),
                    Actor("🐇", 0.68f, 0.74f, 0.26f, Motion.RUN)
                ),
                page(
                    Backdrop.MEADOW, "Broas-ca mer-ge în-cet, în-cet, în-cet.",
                    Actor("🐢", 0.42f, 0.8f, 0.24f, Motion.SWAY)
                ),
                page(
                    Backdrop.MEADOW, "Ie-pu-re-le doar-me sub un co-pac.",
                    Actor("🌳", 0.6f, 0.5f, 0.45f),
                    Actor("🐰", 0.56f, 0.78f, 0.22f),
                    Actor("💤", 0.74f, 0.6f, 0.12f, Motion.FLOAT)
                ),
                page(
                    Backdrop.MEADOW, "Broas-ca mer-ge în-cet, dar nu se o-preș-te.",
                    Actor("🐢", 0.3f, 0.82f, 0.2f, Motion.SWAY),
                    Actor("🌳", 0.74f, 0.5f, 0.4f),
                    Actor("🐰", 0.72f, 0.78f, 0.18f),
                    Actor("💤", 0.86f, 0.62f, 0.1f, Motion.FLOAT)
                ),
                page(
                    Backdrop.MEADOW, "Ie-pu-re-le se tre-zeș-te și fu-ge.",
                    Actor("🐇", 0.5f, 0.74f, 0.28f, Motion.RUN),
                    Actor("😱", 0.5f, 0.42f, 0.12f, Motion.PULSE)
                ),
                page(
                    Backdrop.MEADOW, "Dar broas-ca a-jun-ge pri-ma la fi-niș!",
                    Actor("🐇", 0.18f, 0.76f, 0.22f, Motion.RUN),
                    Actor("🏁", 0.76f, 0.6f, 0.22f, Motion.SWAY),
                    Actor("🐢", 0.66f, 0.8f, 0.22f, Motion.BOUNCE)
                ),
                page(
                    Backdrop.RAINBOW, "Ci-ne mer-ge în-cet, dar si-gur, câș-ti-gă!",
                    Actor("🐰", 0.22f, 0.76f, 0.22f),
                    Actor("🐢", 0.5f, 0.76f, 0.28f, Motion.BOUNCE),
                    Actor("🏆", 0.76f, 0.6f, 0.2f, Motion.PULSE)
                )
            ),
            questions = listOf(
                q("Cine aleargă repede?", "🐰" to "iepurele", "🐢" to "broasca", "🐌" to "melcul"),
                q("Ce face iepurele în timpul întrecerii?", "💤" to "doarme", "🥕" to "mănâncă morcovi", "🏊" to "înoată"),
                q("Cine câștigă?", "🐢" to "broasca", "🐰" to "iepurele", "🦊" to "vulpea")
            )
        ),

        // --- Level 2 -----------------------------------------------------------------
        classic(
            id = "capra_trei_iezi", title = "Capra cu trei iezi", cover = "🐐", level = 2,
            pages = listOf(
                page(
                    Backdrop.COTTAGE, "Ca-pra a-re trei iezi.",
                    Actor("🐐", 0.26f, 0.66f, 0.34f),
                    Actor("🐐", 0.08f, 0.8f, 0.16f, Motion.BOUNCE),
                    Actor("🐐", 0.44f, 0.8f, 0.14f, Motion.BOUNCE),
                    Actor("🐐", 0.56f, 0.84f, 0.12f, Motion.BOUNCE)
                ),
                page(
                    Backdrop.COTTAGE, "Ca-pra plea-că du-pă mân-ca-re.",
                    Actor("🐐", 0.3f, 0.7f, 0.3f, Motion.RUN),
                    Actor("🐐", 0.66f, 0.82f, 0.12f, Motion.SWAY),
                    Actor("🐐", 0.76f, 0.84f, 0.11f, Motion.SWAY),
                    Actor("🐐", 0.86f, 0.86f, 0.1f, Motion.SWAY)
                ),
                page(
                    Backdrop.ROOM, "Ea zi-ce: Nu des-chi-deți la ni-meni!",
                    Actor("🐐", 0.28f, 0.64f, 0.36f, Motion.PULSE),
                    Actor("☝️", 0.46f, 0.36f, 0.12f, Motion.PULSE),
                    Actor("🐐", 0.6f, 0.78f, 0.16f),
                    Actor("🐐", 0.73f, 0.8f, 0.14f),
                    Actor("🐐", 0.85f, 0.82f, 0.12f)
                ),
                page(
                    Backdrop.COTTAGE, "Vi-ne lu-pul și ba-te la u-șă.",
                    Actor("🐺", 0.54f, 0.68f, 0.3f),
                    Actor("👊", 0.68f, 0.6f, 0.1f, Motion.PULSE)
                ),
                page(
                    Backdrop.COTTAGE, "Lu-pul zi-ce: Des-chi-deți, sunt ma-ma!",
                    Actor("🐺", 0.5f, 0.68f, 0.32f, Motion.SWAY),
                    Actor("💬", 0.34f, 0.36f, 0.14f, Motion.FLOAT)
                ),
                page(
                    Backdrop.COTTAGE, "Ie-dul cel mic se as-cun-de în horn.",
                    Actor("🐐", 0.83f, 0.17f, 0.1f, Motion.PULSE),
                    Actor("🐺", 0.44f, 0.7f, 0.28f)
                ),
                page(
                    Backdrop.COTTAGE, "Cei mari des-chid u-șa și lu-pul in-tră.",
                    Actor("🐐", 0.24f, 0.8f, 0.14f, Motion.PULSE),
                    Actor("🐐", 0.36f, 0.8f, 0.14f, Motion.PULSE),
                    Actor("😱", 0.3f, 0.6f, 0.1f, Motion.FLOAT),
                    Actor("🐺", 0.72f, 0.7f, 0.28f, Motion.RUN)
                ),
                page(
                    Backdrop.ROOM, "Ie-zii mari se as-cund sub pat.",
                    Actor("🛏️", 0.5f, 0.7f, 0.42f),
                    Actor("🐐", 0.42f, 0.86f, 0.11f, Motion.PULSE),
                    Actor("🐐", 0.56f, 0.86f, 0.11f, Motion.PULSE)
                ),
                page(
                    Backdrop.ROOM, "Lu-pul mă-nân-că toa-tă mân-ca-rea și plea-că.",
                    Actor("🐺", 0.36f, 0.62f, 0.36f, Motion.PULSE),
                    Actor("🍲", 0.64f, 0.76f, 0.18f),
                    Actor("🍞", 0.8f, 0.78f, 0.14f)
                ),
                page(
                    Backdrop.COTTAGE, "Ca-pra sa-pă o groa-pă pen-tru lup.",
                    Actor("🐐", 0.26f, 0.66f, 0.32f, Motion.SWAY),
                    Actor("⛏️", 0.4f, 0.58f, 0.14f, Motion.SWAY),
                    Actor("🕳️", 0.5f, 0.86f, 0.18f)
                ),
                page(
                    Backdrop.COTTAGE, "Lu-pul ca-de în groa-pă și fu-ge de-par-te.",
                    Actor("🕳️", 0.42f, 0.86f, 0.2f),
                    Actor("💥", 0.42f, 0.66f, 0.12f, Motion.PULSE),
                    Actor("🐺", 0.14f, 0.7f, 0.26f, Motion.RUN, flip = true)
                ),
                page(
                    Backdrop.COTTAGE, "A-cum ie-zii as-cul-tă de ma-ma lor.",
                    Actor("🐐", 0.26f, 0.64f, 0.34f),
                    Actor("🐐", 0.08f, 0.8f, 0.14f, Motion.BOUNCE),
                    Actor("🐐", 0.44f, 0.8f, 0.14f, Motion.BOUNCE),
                    Actor("🐐", 0.56f, 0.84f, 0.12f, Motion.BOUNCE),
                    Actor("❤️", 0.26f, 0.3f, 0.12f, Motion.PULSE)
                )
            ),
            questions = listOf(
                q("Câți iezi are capra?", "3️⃣" to "trei", "2️⃣" to "doi", "5️⃣" to "cinci"),
                q("Unde se ascunde iedul cel mic?", "🏠" to "în horn", "🌳" to "în copac", "🛁" to "în cadă"),
                q("Unde cade lupul?", "🕳️" to "în groapă", "💧" to "în apă", "🌲" to "în pădure")
            )
        ),
        classic(
            id = "trei_purcelusi", title = "Cei trei purceluși", cover = "🐷", level = 2,
            pages = listOf(
                page(
                    Backdrop.MEADOW, "Trei pur-ce-luși fac trei că-su-țe.",
                    Actor("🐷", 0.28f, 0.72f, 0.22f, Motion.BOUNCE),
                    Actor("🐷", 0.5f, 0.74f, 0.22f, Motion.BOUNCE),
                    Actor("🐷", 0.72f, 0.72f, 0.22f, Motion.BOUNCE),
                    Actor("🔨", 0.86f, 0.44f, 0.12f, Motion.SWAY)
                ),
                page(
                    Backdrop.STRAW_HOUSE, "Pri-mul fa-ce o ca-să din pa-ie.",
                    Actor("🐷", 0.28f, 0.72f, 0.26f, Motion.BOUNCE),
                    Actor("🌾", 0.46f, 0.74f, 0.16f, Motion.SWAY)
                ),
                page(
                    Backdrop.COTTAGE, "Al doi-lea fa-ce o ca-să din lemn.",
                    Actor("🐷", 0.3f, 0.72f, 0.26f, Motion.BOUNCE),
                    Actor("🔨", 0.46f, 0.58f, 0.12f, Motion.SWAY)
                ),
                page(
                    Backdrop.BRICK_HOUSE, "Al trei-lea fa-ce o ca-să din că-ră-mi-dă.",
                    Actor("🐷", 0.3f, 0.72f, 0.26f, Motion.BOUNCE),
                    Actor("🔨", 0.46f, 0.58f, 0.12f, Motion.SWAY)
                ),
                page(
                    Backdrop.STRAW_HOUSE, "Vi-ne lu-pul și su-flă: fu, fu!",
                    Actor("🐺", 0.32f, 0.68f, 0.32f, Motion.PULSE),
                    Actor("💨", 0.52f, 0.6f, 0.16f, Motion.FLOAT)
                ),
                page(
                    Backdrop.MEADOW, "Ca-sa din pa-ie zboa-ră!",
                    Actor("🌾", 0.3f, 0.3f, 0.14f, Motion.FLOAT),
                    Actor("🌾", 0.52f, 0.2f, 0.12f, Motion.FLOAT),
                    Actor("🌾", 0.72f, 0.34f, 0.14f, Motion.FLOAT),
                    Actor("💨", 0.16f, 0.5f, 0.16f, Motion.RUN),
                    Actor("🐷", 0.66f, 0.76f, 0.2f, Motion.RUN)
                ),
                page(
                    Backdrop.COTTAGE, "Lu-pul su-flă și ca-sa din lemn ca-de.",
                    Actor("🐺", 0.28f, 0.68f, 0.32f, Motion.PULSE),
                    Actor("💨", 0.48f, 0.58f, 0.16f, Motion.FLOAT),
                    Actor("💥", 0.75f, 0.48f, 0.28f, Motion.PULSE)
                ),
                page(
                    Backdrop.BRICK_HOUSE, "Pur-ce-lu-șii fug la ca-sa din că-ră-mi-dă.",
                    Actor("🐷", 0.3f, 0.78f, 0.18f, Motion.RUN),
                    Actor("🐷", 0.5f, 0.8f, 0.18f, Motion.RUN),
                    Actor("🐷", 0.75f, 0.72f, 0.16f, Motion.BOUNCE)
                ),
                page(
                    Backdrop.BRICK_HOUSE, "Lu-pul su-flă, su-flă, dar ca-sa nu ca-de.",
                    Actor("🐺", 0.28f, 0.68f, 0.32f, Motion.PULSE),
                    Actor("💨", 0.48f, 0.58f, 0.16f, Motion.FLOAT)
                ),
                page(
                    Backdrop.ROOM, "Lu-pul ca-de pe horn în a-pă fier-bin-te.",
                    Actor("🐺", 0.5f, 0.36f, 0.24f, Motion.BOUNCE),
                    Actor("🍲", 0.5f, 0.76f, 0.3f),
                    Actor("♨️", 0.7f, 0.6f, 0.12f, Motion.FLOAT),
                    Actor("🐷", 0.18f, 0.74f, 0.2f, Motion.PULSE)
                ),
                page(
                    Backdrop.BRICK_HOUSE, "Au! Lu-pul fu-ge și nu mai vi-ne.",
                    Actor("🐺", 0.26f, 0.72f, 0.28f, Motion.RUN, flip = true),
                    Actor("💨", 0.46f, 0.7f, 0.12f, Motion.FLOAT),
                    Actor("🐷", 0.66f, 0.86f, 0.13f, Motion.BOUNCE),
                    Actor("🐷", 0.75f, 0.86f, 0.13f, Motion.BOUNCE),
                    Actor("🐷", 0.84f, 0.86f, 0.13f, Motion.BOUNCE)
                ),
                page(
                    Backdrop.BRICK_HOUSE, "Mun-ca bi-ne fă-cu-tă îi a-pă-ră pe toți.",
                    Actor("🐷", 0.18f, 0.74f, 0.2f, Motion.BOUNCE),
                    Actor("🐷", 0.34f, 0.76f, 0.2f, Motion.BOUNCE),
                    Actor("🐷", 0.5f, 0.74f, 0.2f, Motion.BOUNCE),
                    Actor("🎉", 0.34f, 0.36f, 0.14f, Motion.FLOAT)
                )
            ),
            questions = listOf(
                q("Ce casă nu cade?", "🏠" to "cea din cărămidă", "🌾" to "cea din paie", "🌲" to "cea din lemn"),
                q("Ce face lupul la case?", "💨" to "suflă", "🔨" to "bate cuie", "🎵" to "cântă"),
                q("Pe unde intră lupul la final?", "🏠" to "pe horn", "🚪" to "pe ușă", "🕳️" to "pe sub pământ")
            )
        ),
        classic(
            id = "leul_soarecele", title = "Leul și șoarecele", cover = "🦁", level = 2,
            pages = listOf(
                page(
                    Backdrop.FOREST, "Un leu ma-re doar-me în pă-du-re.",
                    Actor("🦁", 0.5f, 0.68f, 0.4f, Motion.PULSE),
                    Actor("💤", 0.74f, 0.36f, 0.14f, Motion.FLOAT)
                ),
                page(
                    Backdrop.FOREST, "Un șoa-re-ce mic îi sa-re pe nas.",
                    Actor("🦁", 0.5f, 0.66f, 0.42f),
                    Actor("🐭", 0.5f, 0.6f, 0.1f, Motion.BOUNCE)
                ),
                page(
                    Backdrop.FOREST, "Le-ul se tre-zeș-te și îl prin-de.",
                    Actor("🦁", 0.38f, 0.64f, 0.4f, Motion.PULSE),
                    Actor("🐭", 0.68f, 0.82f, 0.12f, Motion.SWAY),
                    Actor("🐾", 0.68f, 0.72f, 0.14f)
                ),
                page(
                    Backdrop.FOREST, "Șoa-re-ce-le zi-ce: Și eu te pot a-ju-ta!",
                    Actor("🦁", 0.32f, 0.64f, 0.38f),
                    Actor("🐭", 0.66f, 0.8f, 0.16f, Motion.BOUNCE),
                    Actor("💬", 0.74f, 0.5f, 0.12f, Motion.PULSE)
                ),
                page(
                    Backdrop.FOREST, "Le-ul râ-de și îl la-să să ple-ce.",
                    Actor("🦁", 0.34f, 0.64f, 0.38f, Motion.SWAY),
                    Actor("🐭", 0.74f, 0.82f, 0.12f, Motion.RUN)
                ),
                page(
                    Backdrop.FOREST, "Le-ul ca-de în pla-sa u-nui vâ-nă-tor.",
                    Actor("🦁", 0.44f, 0.6f, 0.38f, Motion.SWAY),
                    Actor("🕸️", 0.6f, 0.76f, 0.3f)
                ),
                page(
                    Backdrop.FOREST, "Le-ul ra-ge ta-re: A-ju-tor!",
                    Actor("🦁", 0.42f, 0.6f, 0.38f, Motion.PULSE),
                    Actor("🕸️", 0.58f, 0.76f, 0.3f),
                    Actor("📢", 0.78f, 0.34f, 0.14f, Motion.PULSE)
                ),
                page(
                    Backdrop.FOREST, "Șoa-re-ce-le vi-ne în fu-gă.",
                    Actor("🐭", 0.22f, 0.82f, 0.14f, Motion.RUN),
                    Actor("🦁", 0.6f, 0.6f, 0.34f),
                    Actor("🕸️", 0.74f, 0.76f, 0.28f)
                ),
                page(
                    Backdrop.FOREST, "El ro-de pla-sa cu din-ții lui mici.",
                    Actor("🦁", 0.5f, 0.6f, 0.34f),
                    Actor("🕸️", 0.64f, 0.76f, 0.28f),
                    Actor("🐭", 0.46f, 0.84f, 0.14f, Motion.SWAY)
                ),
                page(
                    Backdrop.FOREST, "Le-ul e li-ber și mul-țu-meș-te.",
                    Actor("🦁", 0.38f, 0.64f, 0.38f, Motion.BOUNCE),
                    Actor("🐭", 0.7f, 0.8f, 0.14f, Motion.BOUNCE),
                    Actor("❤️", 0.56f, 0.34f, 0.12f, Motion.PULSE)
                ),
                page(
                    Backdrop.MEADOW, "Și cei mici pot a-ju-ta mult!",
                    Actor("🦁", 0.36f, 0.62f, 0.38f),
                    Actor("🐭", 0.64f, 0.78f, 0.16f, Motion.BOUNCE),
                    Actor("🤝", 0.52f, 0.34f, 0.14f, Motion.PULSE)
                )
            ),
            questions = listOf(
                q("Cine sare pe nasul leului?", "🐭" to "un șoarece", "🐸" to "o broască", "🐦" to "o pasăre"),
                q("Unde cade leul?", "🕸️" to "într-o plasă", "🕳️" to "într-o groapă", "💧" to "în apă"),
                q("Cum îl ajută șoarecele?", "🐭" to "roade plasa", "🔥" to "arde plasa", "📢" to "cheamă ajutor")
            )
        ),
        classic(
            id = "greierele_furnica", title = "Greierele și furnica", cover = "🦗", level = 2,
            pages = listOf(
                page(
                    Backdrop.MEADOW, "Va-ra, fur-ni-ca mun-ceș-te mult.",
                    Actor("🐜", 0.4f, 0.8f, 0.18f, Motion.RUN),
                    Actor("🌾", 0.7f, 0.74f, 0.18f, Motion.SWAY)
                ),
                page(
                    Backdrop.MEADOW, "Ea du-ce boa-be în mu-șu-roi.",
                    Actor("🐜", 0.38f, 0.82f, 0.16f, Motion.RUN),
                    Actor("🌾", 0.46f, 0.74f, 0.1f, Motion.RUN),
                    Actor("⛰️", 0.76f, 0.74f, 0.26f)
                ),
                page(
                    Backdrop.MEADOW, "Gre-ie-re-le cân-tă toa-tă zi-ua.",
                    Actor("🦗", 0.38f, 0.74f, 0.24f, Motion.BOUNCE),
                    Actor("🎻", 0.6f, 0.66f, 0.18f, Motion.SWAY),
                    Actor("🎵", 0.52f, 0.38f, 0.12f, Motion.FLOAT)
                ),
                page(
                    Backdrop.MEADOW, "Fur-ni-ca zi-ce: Strân-ge și tu mân-ca-re!",
                    Actor("🐜", 0.28f, 0.8f, 0.18f, Motion.PULSE),
                    Actor("💬", 0.36f, 0.5f, 0.12f, Motion.FLOAT),
                    Actor("🦗", 0.7f, 0.74f, 0.22f, Motion.SWAY)
                ),
                page(
                    Backdrop.MEADOW, "Gre-ie-re-le râ-de și cân-tă mai de-par-te.",
                    Actor("🦗", 0.46f, 0.72f, 0.26f, Motion.BOUNCE),
                    Actor("🎻", 0.68f, 0.66f, 0.16f, Motion.SWAY),
                    Actor("🎶", 0.6f, 0.36f, 0.14f, Motion.FLOAT)
                ),
                page(
                    Backdrop.WINTER, "Vi-ne iar-na cu ză-pa-dă și frig.",
                    Actor("⛄", 0.64f, 0.68f, 0.32f, Motion.SWAY),
                    Actor("❄️", 0.3f, 0.4f, 0.14f, Motion.FLOAT)
                ),
                page(
                    Backdrop.WINTER, "Gre-ie-re-lui îi e frig și foa-me.",
                    Actor("🦗", 0.46f, 0.78f, 0.2f, Motion.SWAY),
                    Actor("😢", 0.46f, 0.52f, 0.12f, Motion.PULSE),
                    Actor("❄️", 0.74f, 0.4f, 0.12f, Motion.FLOAT)
                ),
                page(
                    Backdrop.WINTER, "El ba-te la u-șa fur-ni-cii.",
                    Actor("🦗", 0.36f, 0.78f, 0.2f),
                    Actor("👊", 0.5f, 0.62f, 0.1f, Motion.PULSE),
                    Actor("🚪", 0.66f, 0.62f, 0.38f)
                ),
                page(
                    Backdrop.ROOM, "Fur-ni-ca îl pri-meș-te în ca-să.",
                    Actor("🐜", 0.36f, 0.76f, 0.2f, Motion.BOUNCE),
                    Actor("🦗", 0.64f, 0.74f, 0.22f),
                    Actor("❤️", 0.5f, 0.42f, 0.12f, Motion.PULSE)
                ),
                page(
                    Backdrop.ROOM, "Ea îm-par-te mân-ca-rea cu el.",
                    Actor("🐜", 0.28f, 0.76f, 0.2f),
                    Actor("🍞", 0.5f, 0.8f, 0.14f, Motion.PULSE),
                    Actor("🦗", 0.72f, 0.74f, 0.22f, Motion.BOUNCE)
                ),
                page(
                    Backdrop.ROOM, "Gre-ie-re-le cân-tă pen-tru fur-ni-că.",
                    Actor("🦗", 0.34f, 0.72f, 0.24f, Motion.BOUNCE),
                    Actor("🎻", 0.5f, 0.64f, 0.14f, Motion.SWAY),
                    Actor("🎶", 0.5f, 0.36f, 0.12f, Motion.FLOAT),
                    Actor("🐜", 0.72f, 0.8f, 0.18f, Motion.BOUNCE)
                ),
                page(
                    Backdrop.MEADOW, "La va-ră, gre-ie-re-le mun-ceș-te și el.",
                    Actor("🦗", 0.34f, 0.74f, 0.24f, Motion.RUN),
                    Actor("🐜", 0.6f, 0.8f, 0.18f, Motion.RUN),
                    Actor("🌾", 0.8f, 0.74f, 0.16f, Motion.SWAY)
                )
            ),
            questions = listOf(
                q("Ce face furnica vara?", "🐜" to "muncește", "💤" to "doarme", "🎻" to "cântă la vioară"),
                q("Ce face greierele vara?", "🎻" to "cântă", "🐜" to "strânge boabe", "🏊" to "înoată"),
                q("Ce face furnica iarna?", "🤝" to "împarte mâncarea", "🚪" to "închide ușa", "😂" to "râde de greiere")
            )
        ),

        // --- Level 3 -----------------------------------------------------------------
        classic(
            id = "punguta_doi_bani", title = "Punguța cu doi bani", cover = "👛", level = 3,
            pages = listOf(
                page(
                    Backdrop.FARM, "Un bu-nic a-re un co-coș.",
                    Actor("👴", 0.3f, 0.6f, 0.38f),
                    Actor("🐓", 0.66f, 0.74f, 0.26f, Motion.BOUNCE)
                ),
                page(
                    Backdrop.FARM, "Co-co-șul gă-seș-te o pun-gu-ță cu doi bani.",
                    Actor("🐓", 0.36f, 0.72f, 0.28f, Motion.SWAY),
                    Actor("👛", 0.64f, 0.82f, 0.14f, Motion.PULSE)
                ),
                page(
                    Backdrop.MEADOW, "Un bo-ier tre-ce și ia pun-gu-ța.",
                    Actor("🐓", 0.22f, 0.76f, 0.22f, Motion.PULSE),
                    Actor("🤴", 0.62f, 0.62f, 0.36f, Motion.RUN),
                    Actor("👛", 0.74f, 0.66f, 0.1f, Motion.RUN)
                ),
                page(
                    Backdrop.MEADOW, "Cu-cu-ri-gu, dă pun-gu-ța cu doi bani!",
                    Actor("🐓", 0.36f, 0.72f, 0.3f, Motion.PULSE),
                    Actor("💬", 0.54f, 0.36f, 0.14f, Motion.FLOAT),
                    Actor("🤴", 0.78f, 0.62f, 0.3f)
                ),
                page(
                    Backdrop.FARM, "Bo-ie-rul îl a-run-că în fân-tâ-nă.",
                    Actor("🤴", 0.24f, 0.62f, 0.34f),
                    Actor("⛲", 0.62f, 0.7f, 0.34f),
                    Actor("🐓", 0.62f, 0.38f, 0.16f, Motion.BOUNCE)
                ),
                page(
                    Backdrop.FARM, "Co-co-șul bea a-pa și stri-gă iar.",
                    Actor("🐓", 0.46f, 0.7f, 0.3f, Motion.PULSE),
                    Actor("💧", 0.68f, 0.5f, 0.12f, Motion.BOUNCE),
                    Actor("💬", 0.3f, 0.36f, 0.12f, Motion.FLOAT)
                ),
                page(
                    Backdrop.ROOM, "Bo-ie-rul îl a-run-că în cup-tor.",
                    Actor("🤴", 0.24f, 0.62f, 0.34f),
                    Actor("🔥", 0.62f, 0.76f, 0.28f, Motion.PULSE),
                    Actor("🐓", 0.62f, 0.5f, 0.16f, Motion.SWAY)
                ),
                page(
                    Backdrop.ROOM, "Co-co-șul var-să a-pa și stin-ge fo-cul.",
                    Actor("🐓", 0.4f, 0.68f, 0.3f, Motion.BOUNCE),
                    Actor("💦", 0.64f, 0.76f, 0.16f, Motion.PULSE)
                ),
                page(
                    Backdrop.ROOM, "Co-co-șul în-ghi-te toți ba-nii bo-ie-ru-lui.",
                    Actor("🐓", 0.36f, 0.66f, 0.32f, Motion.PULSE),
                    Actor("💰", 0.64f, 0.76f, 0.2f),
                    Actor("💰", 0.8f, 0.78f, 0.16f)
                ),
                page(
                    Backdrop.MEADOW, "Co-co-șul fu-ge a-ca-să la bu-nic.",
                    Actor("🐓", 0.5f, 0.74f, 0.28f, Motion.RUN)
                ),
                page(
                    Backdrop.FARM, "El scoa-te ba-nii și bu-ni-cul e bo-gat.",
                    Actor("👴", 0.28f, 0.6f, 0.36f, Motion.BOUNCE),
                    Actor("💰", 0.54f, 0.76f, 0.2f, Motion.PULSE),
                    Actor("🐓", 0.76f, 0.72f, 0.26f, Motion.BOUNCE),
                    Actor("✨", 0.54f, 0.5f, 0.12f, Motion.PULSE)
                ),
                page(
                    Backdrop.FARM, "Ci-ne nu re-nun-ță, câș-ti-gă!",
                    Actor("🐓", 0.5f, 0.66f, 0.36f, Motion.BOUNCE),
                    Actor("👑", 0.5f, 0.34f, 0.14f, Motion.FLOAT)
                )
            ),
            questions = listOf(
                q("Ce găsește cocoșul?", "👛" to "o punguță", "🥚" to "un ou", "🍎" to "un măr"),
                q("Cum stinge cocoșul focul?", "💧" to "cu apă", "💨" to "suflă", "❄️" to "cu zăpadă"),
                q("Cine are banii la final?", "👴" to "bunicul", "🤴" to "boierul", "🦊" to "vulpea")
            )
        ),
        classic(
            id = "ursul_pacalit", title = "Ursul păcălit de vulpe", cover = "🦊", level = 3,
            pages = listOf(
                page(
                    Backdrop.WINTER, "E iar-nă și e foar-te frig.",
                    Actor("🦊", 0.3f, 0.74f, 0.26f, Motion.SWAY),
                    Actor("❄️", 0.6f, 0.36f, 0.14f, Motion.FLOAT)
                ),
                page(
                    Backdrop.WINTER, "Un om du-ce pești în că-ru-ță.",
                    Actor("👴", 0.3f, 0.64f, 0.32f, Motion.RUN),
                    Actor("🐴", 0.6f, 0.66f, 0.3f, Motion.RUN),
                    Actor("🐟", 0.8f, 0.74f, 0.14f),
                    Actor("🐟", 0.9f, 0.7f, 0.12f)
                ),
                page(
                    Backdrop.WINTER, "Vul-pea se fa-ce moar-tă pe drum.",
                    Actor("🦊", 0.5f, 0.82f, 0.26f),
                    Actor("😵", 0.5f, 0.56f, 0.12f, Motion.FLOAT)
                ),
                page(
                    Backdrop.WINTER, "O-mul o pu-ne în că-ru-ță.",
                    Actor("👴", 0.28f, 0.64f, 0.34f),
                    Actor("🦊", 0.58f, 0.72f, 0.2f, Motion.SWAY),
                    Actor("🐟", 0.74f, 0.76f, 0.14f),
                    Actor("🐟", 0.86f, 0.74f, 0.12f)
                ),
                page(
                    Backdrop.WINTER, "Vul-pea a-run-că peș-tii jos și fu-ge.",
                    Actor("🐟", 0.2f, 0.88f, 0.12f, Motion.BOUNCE),
                    Actor("🐟", 0.32f, 0.86f, 0.14f, Motion.BOUNCE),
                    Actor("🐟", 0.44f, 0.88f, 0.12f, Motion.BOUNCE),
                    Actor("🦊", 0.68f, 0.74f, 0.26f, Motion.RUN)
                ),
                page(
                    Backdrop.WINTER, "Ur-sul vrea și el pești.",
                    Actor("🐻", 0.3f, 0.66f, 0.36f, Motion.SWAY),
                    Actor("💭", 0.58f, 0.34f, 0.24f),
                    Actor("🐟", 0.58f, 0.32f, 0.1f),
                    Actor("🦊", 0.82f, 0.76f, 0.2f)
                ),
                page(
                    Backdrop.WINTER, "Vul-pea zi-ce: Pu-ne coa-da în lac!",
                    Actor("🦊", 0.22f, 0.74f, 0.26f, Motion.PULSE),
                    Actor("💬", 0.34f, 0.44f, 0.12f, Motion.FLOAT),
                    Actor("🐻", 0.62f, 0.68f, 0.34f)
                ),
                page(
                    Backdrop.NIGHT, "Ur-sul stă toa-tă noap-tea pe ghea-ță.",
                    Actor("🐻", 0.46f, 0.7f, 0.36f),
                    Actor("❄️", 0.24f, 0.4f, 0.12f, Motion.FLOAT),
                    Actor("❄️", 0.66f, 0.3f, 0.1f, Motion.FLOAT)
                ),
                page(
                    Backdrop.WINTER, "Di-mi-nea-ța, coa-da e prin-să în ghea-ță.",
                    Actor("🐻", 0.5f, 0.66f, 0.36f, Motion.SWAY),
                    Actor("❄️", 0.66f, 0.88f, 0.14f, Motion.PULSE)
                ),
                page(
                    Backdrop.WINTER, "Ur-sul tra-ge și coa-da ră-mâ-ne a-co-lo.",
                    Actor("🐻", 0.38f, 0.66f, 0.36f, Motion.RUN),
                    Actor("💥", 0.66f, 0.88f, 0.12f, Motion.PULSE)
                ),
                page(
                    Backdrop.WINTER, "De a-tunci, ur-sul a-re coa-da scur-tă.",
                    Actor("🐻", 0.48f, 0.66f, 0.4f, Motion.BOUNCE),
                    Actor("😳", 0.72f, 0.36f, 0.12f, Motion.FLOAT)
                ),
                page(
                    Backdrop.FOREST, "Nu cre-de tot ce zi-ce vul-pea!",
                    Actor("🐻", 0.28f, 0.66f, 0.34f),
                    Actor("☝️", 0.46f, 0.38f, 0.12f, Motion.PULSE),
                    Actor("🦊", 0.7f, 0.72f, 0.26f, Motion.SWAY)
                )
            ),
            questions = listOf(
                q("Ce duce omul în căruță?", "🐟" to "pești", "🍎" to "mere", "🥚" to "ouă"),
                q("Unde pune ursul coada?", "❄️" to "în lac", "🔥" to "în foc", "🌳" to "în copac"),
                q("Cum are ursul coada acum?", "✂️" to "scurtă", "📏" to "lungă", "🌈" to "colorată")
            )
        ),
        classic(
            id = "scufita_rosie", title = "Scufița Roșie", cover = "👧", level = 3,
            pages = listOf(
                page(
                    Backdrop.ROOM, "Scu-fi-ța Ro-și-e mer-ge la bu-ni-ca.",
                    Actor("👧", 0.36f, 0.66f, 0.32f, Motion.BOUNCE),
                    Actor("👩", 0.68f, 0.6f, 0.38f)
                ),
                page(
                    Backdrop.ROOM, "Ea du-ce un coș cu plă-cin-te.",
                    Actor("👧", 0.36f, 0.66f, 0.32f),
                    Actor("🥧", 0.62f, 0.76f, 0.18f, Motion.PULSE)
                ),
                page(
                    Backdrop.ROOM, "Ma-ma zi-ce: Nu te o-pri pe drum!",
                    Actor("👩", 0.28f, 0.6f, 0.38f, Motion.PULSE),
                    Actor("☝️", 0.46f, 0.36f, 0.12f, Motion.PULSE),
                    Actor("👧", 0.68f, 0.68f, 0.3f)
                ),
                page(
                    Backdrop.FOREST, "În pă-du-re, Scu-fi-ța în-tâl-neș-te un lup.",
                    Actor("👧", 0.28f, 0.72f, 0.3f),
                    Actor("🐺", 0.7f, 0.7f, 0.32f, Motion.SWAY)
                ),
                page(
                    Backdrop.FOREST, "Lu-pul în-trea-bă: Un-de mergi, fe-ti-ță?",
                    Actor("👧", 0.28f, 0.72f, 0.3f),
                    Actor("🐺", 0.66f, 0.68f, 0.34f, Motion.PULSE),
                    Actor("💬", 0.82f, 0.36f, 0.12f, Motion.FLOAT)
                ),
                page(
                    Backdrop.MEADOW, "Scu-fi-ța se o-preș-te să cu-lea-gă flori.",
                    Actor("👧", 0.42f, 0.7f, 0.3f, Motion.SWAY),
                    Actor("🌼", 0.22f, 0.84f, 0.12f, Motion.SWAY),
                    Actor("🌷", 0.62f, 0.84f, 0.12f, Motion.SWAY),
                    Actor("🌸", 0.74f, 0.86f, 0.12f, Motion.SWAY)
                ),
                page(
                    Backdrop.COTTAGE, "Lu-pul a-lear-gă la ca-sa bu-ni-cii.",
                    Actor("🐺", 0.36f, 0.7f, 0.3f, Motion.RUN)
                ),
                page(
                    Backdrop.ROOM, "Bu-ni-ca se as-cun-de în du-lap.",
                    Actor("👵", 0.38f, 0.66f, 0.3f, Motion.RUN),
                    Actor("🚪", 0.68f, 0.6f, 0.42f)
                ),
                page(
                    Backdrop.ROOM, "Lu-pul se pu-ne în pa-tul bu-ni-cii.",
                    Actor("🛏️", 0.5f, 0.7f, 0.44f),
                    Actor("🐺", 0.46f, 0.54f, 0.2f, Motion.PULSE)
                ),
                page(
                    Backdrop.ROOM, "Scu-fi-ța zi-ce: Ce ochi mari ai!",
                    Actor("👧", 0.22f, 0.66f, 0.3f),
                    Actor("🛏️", 0.64f, 0.72f, 0.4f),
                    Actor("🐺", 0.6f, 0.56f, 0.2f, Motion.PULSE),
                    Actor("👀", 0.6f, 0.32f, 0.12f, Motion.PULSE)
                ),
                page(
                    Backdrop.FOREST, "Vi-ne vâ-nă-to-rul și lu-pul fu-ge de-par-te.",
                    Actor("👨", 0.24f, 0.66f, 0.34f, Motion.RUN),
                    Actor("🏹", 0.4f, 0.6f, 0.14f, Motion.SWAY),
                    Actor("🐺", 0.74f, 0.72f, 0.24f, Motion.RUN, flip = true)
                ),
                page(
                    Backdrop.COTTAGE, "Scu-fi-ța nu mai vor-beș-te cu stră-i-nii.",
                    Actor("👧", 0.24f, 0.7f, 0.3f, Motion.BOUNCE),
                    Actor("👵", 0.44f, 0.66f, 0.32f, Motion.BOUNCE),
                    Actor("❤️", 0.34f, 0.34f, 0.12f, Motion.PULSE)
                )
            ),
            questions = listOf(
                q("Ce duce Scufița?", "🥧" to "plăcinte", "🍬" to "bomboane", "🍉" to "un pepene"),
                q("Unde se ascunde bunica?", "🚪" to "în dulap", "🛏️" to "sub pat", "🌳" to "după copac"),
                q("Cine alungă lupul?", "🏹" to "vânătorul", "👵" to "bunica", "🐻" to "ursul")
            )
        ),
        classic(
            id = "ratusca_urata", title = "Rățușca cea urâtă", cover = "🕊️", level = 3,
            pages = listOf(
                page(
                    Backdrop.POND, "Ma-ma ra-ță stă pe cinci o-uă.",
                    Actor("🦆", 0.46f, 0.6f, 0.3f, Motion.PULSE),
                    Actor("🥚", 0.3f, 0.78f, 0.1f),
                    Actor("🥚", 0.4f, 0.8f, 0.1f),
                    Actor("🥚", 0.5f, 0.8f, 0.1f),
                    Actor("🥚", 0.6f, 0.78f, 0.1f),
                    Actor("🥚", 0.72f, 0.76f, 0.15f, Motion.SWAY)
                ),
                page(
                    Backdrop.POND, "Din o-uă ies pa-tru ră-țuș-te gal-be-ne.",
                    Actor("🐥", 0.2f, 0.62f, 0.14f, Motion.BOUNCE),
                    Actor("🐥", 0.32f, 0.64f, 0.14f, Motion.BOUNCE),
                    Actor("🐥", 0.44f, 0.62f, 0.14f, Motion.BOUNCE),
                    Actor("🐥", 0.56f, 0.64f, 0.14f, Motion.BOUNCE),
                    Actor("🦆", 0.78f, 0.62f, 0.26f),
                    Actor("🥚", 0.78f, 0.84f, 0.15f, Motion.SWAY)
                ),
                page(
                    Backdrop.POND, "Din o-ul cel ma-re ie-se o ră-țuș-că gri.",
                    Actor("🦆", 0.5f, 0.68f, 0.2f, Motion.PULSE),
                    Actor("❓", 0.64f, 0.44f, 0.12f, Motion.FLOAT),
                    Actor("🐥", 0.24f, 0.64f, 0.12f),
                    Actor("🐥", 0.8f, 0.66f, 0.12f)
                ),
                page(
                    Backdrop.POND, "Toți râd de ea: Ce u-râ-tă e!",
                    Actor("🐥", 0.16f, 0.64f, 0.14f, Motion.BOUNCE),
                    Actor("🐥", 0.28f, 0.66f, 0.14f, Motion.BOUNCE),
                    Actor("🐥", 0.4f, 0.64f, 0.14f, Motion.BOUNCE),
                    Actor("😂", 0.28f, 0.4f, 0.12f, Motion.FLOAT),
                    Actor("🦆", 0.72f, 0.72f, 0.18f)
                ),
                page(
                    Backdrop.MEADOW, "Ră-țuș-ca e tris-tă și plea-că de a-ca-să.",
                    Actor("🦆", 0.5f, 0.78f, 0.18f, Motion.RUN),
                    Actor("😢", 0.5f, 0.56f, 0.1f, Motion.FLOAT)
                ),
                page(
                    Backdrop.WINTER, "Ea stă sin-gu-ră toa-tă iar-na.",
                    Actor("🦆", 0.5f, 0.8f, 0.18f, Motion.SWAY),
                    Actor("❄️", 0.3f, 0.4f, 0.12f, Motion.FLOAT),
                    Actor("❄️", 0.7f, 0.3f, 0.1f, Motion.FLOAT)
                ),
                page(
                    Backdrop.POND, "A-poi vi-ne pri-mă-va-ra.",
                    Actor("🦆", 0.42f, 0.68f, 0.2f, Motion.SWAY),
                    Actor("🦋", 0.62f, 0.34f, 0.12f, Motion.FLOAT),
                    Actor("🌸", 0.2f, 0.38f, 0.12f, Motion.FLOAT),
                    Actor("🌷", 0.86f, 0.54f, 0.12f, Motion.SWAY)
                ),
                page(
                    Backdrop.POND, "Pe lac î-noa-tă le-be-de al-be.",
                    Actor("🕊️", 0.3f, 0.64f, 0.2f, Motion.SWAY),
                    Actor("🕊️", 0.52f, 0.68f, 0.22f, Motion.SWAY),
                    Actor("🕊️", 0.74f, 0.64f, 0.2f, Motion.SWAY)
                ),
                page(
                    Backdrop.POND, "Ră-țuș-ca se ui-tă în a-pă.",
                    Actor("🦆", 0.4f, 0.6f, 0.22f),
                    Actor("🕊️", 0.4f, 0.82f, 0.16f, Motion.PULSE),
                    Actor("✨", 0.6f, 0.78f, 0.1f, Motion.PULSE)
                ),
                page(
                    Backdrop.POND, "Sur-pri-ză: ea e o le-bă-dă fru-moa-să!",
                    Actor("🕊️", 0.5f, 0.64f, 0.32f, Motion.PULSE),
                    Actor("✨", 0.72f, 0.36f, 0.14f, Motion.PULSE),
                    Actor("✨", 0.28f, 0.38f, 0.12f, Motion.FLOAT)
                ),
                page(
                    Backdrop.POND, "Le-be-de-le o pri-mesc cu drag.",
                    Actor("🕊️", 0.26f, 0.66f, 0.2f, Motion.SWAY),
                    Actor("🕊️", 0.5f, 0.68f, 0.24f, Motion.BOUNCE),
                    Actor("🕊️", 0.74f, 0.66f, 0.2f, Motion.SWAY),
                    Actor("❤️", 0.5f, 0.36f, 0.12f, Motion.PULSE)
                ),
                page(
                    Backdrop.POND, "Fi-e-ca-re e fru-mos în fe-lul lui.",
                    Actor("🕊️", 0.26f, 0.64f, 0.24f, Motion.SWAY),
                    Actor("🦆", 0.5f, 0.68f, 0.2f, Motion.SWAY),
                    Actor("🐥", 0.7f, 0.7f, 0.14f, Motion.BOUNCE),
                    Actor("❤️", 0.5f, 0.34f, 0.12f, Motion.PULSE)
                )
            ),
            questions = listOf(
                q("Ce spun toți despre rățușcă?", "😖" to "că e urâtă", "😍" to "că e frumoasă", "😴" to "că e leneșă"),
                q("Cum se simte rățușca când pleacă?", "😢" to "tristă", "😄" to "veselă", "😠" to "furioasă"),
                q("Ce este rățușca la final?", "🕊️" to "o lebădă", "🐔" to "o găină", "🦉" to "o bufniță")
            )
        )
    )

    // ---------------------------------------------------------------------------
    // Romanian holidays and traditions, as a family celebrates them.
    // ---------------------------------------------------------------------------

    private fun holiday(
        id: String,
        title: String,
        cover: String,
        level: Int,
        pages: List<StoryPage>,
        questions: List<StoryQuestion>
    ) = Story(id, title, cover, level, pages, questions, StoryCategory.HOLIDAY)

    private fun holidayStories(): List<Story> = listOf(
        // --- Level 1 -----------------------------------------------------------------
        holiday(
            id = "mos_nicolae", title = "Moș Nicolae", cover = "👢", level = 1,
            pages = listOf(
                page(
                    Backdrop.ROOM, "Azi e sea-ra lui Moș Ni-co-la-e.",
                    Actor("👦", 0.4f, 0.64f, 0.36f, Motion.BOUNCE),
                    Actor("✨", 0.66f, 0.36f, 0.14f, Motion.PULSE)
                ),
                page(
                    Backdrop.ROOM, "Ra-du cu-ră-ță ghe-te-le bi-ne.",
                    Actor("👦", 0.32f, 0.64f, 0.34f),
                    Actor("👢", 0.6f, 0.76f, 0.2f, Motion.SWAY),
                    Actor("✨", 0.72f, 0.6f, 0.1f, Motion.PULSE)
                ),
                page(
                    Backdrop.ROOM, "El le pu-ne la u-șă.",
                    Actor("🚪", 0.66f, 0.56f, 0.42f),
                    Actor("👢", 0.5f, 0.82f, 0.16f),
                    Actor("👦", 0.24f, 0.66f, 0.32f, Motion.PULSE)
                ),
                page(
                    Backdrop.NIGHT, "Noap-tea vi-ne Moș Ni-co-la-e.",
                    Actor("👴", 0.4f, 0.64f, 0.36f, Motion.RUN),
                    Actor("🎁", 0.62f, 0.76f, 0.16f, Motion.RUN),
                    Actor("✨", 0.6f, 0.4f, 0.12f, Motion.FLOAT)
                ),
                page(
                    Backdrop.ROOM, "Di-mi-nea-ța, în ghe-te sunt dul-ciuri!",
                    Actor("👢", 0.5f, 0.76f, 0.26f),
                    Actor("🍬", 0.42f, 0.5f, 0.12f, Motion.BOUNCE),
                    Actor("🍫", 0.54f, 0.46f, 0.12f, Motion.BOUNCE),
                    Actor("🍭", 0.66f, 0.52f, 0.12f, Motion.BOUNCE),
                    Actor("😄", 0.2f, 0.62f, 0.28f, Motion.BOUNCE)
                ),
                page(
                    Backdrop.ROOM, "Și o nu-ie-lu-șă mi-că, de glu-mă.",
                    Actor("👢", 0.5f, 0.76f, 0.24f),
                    Actor("🌿", 0.5f, 0.5f, 0.16f, Motion.SWAY),
                    Actor("😆", 0.24f, 0.62f, 0.26f, Motion.BOUNCE)
                ),
                page(
                    Backdrop.ROOM, "Ra-du râ-de: Sunt cu-min-te!",
                    Actor("👦", 0.36f, 0.64f, 0.36f, Motion.BOUNCE),
                    Actor("🍬", 0.62f, 0.74f, 0.14f, Motion.PULSE),
                    Actor("⭐", 0.66f, 0.36f, 0.14f, Motion.PULSE)
                )
            ),
            questions = listOf(
                q("Unde pune Radu ghetele?", "🚪" to "la ușă", "🛏️" to "sub pat", "🌳" to "în curte"),
                q("Ce găsește Radu în ghete?", "🍬" to "dulciuri", "🥔" to "cartofi", "📕" to "o carte"),
                q("De ce e nuielușa?", "😆" to "de glumă", "😠" to "de supărare", "🔥" to "pentru foc")
            )
        ),
        holiday(
            id = "martisor", title = "Mărțișorul", cover = "🎀", level = 1,
            pages = listOf(
                page(
                    Backdrop.MEADOW, "Azi e în-tâi mar-ti-e.",
                    Actor("📅", 0.5f, 0.56f, 0.3f, Motion.PULSE)
                ),
                page(
                    Backdrop.MEADOW, "Vi-ne pri-mă-va-ra!",
                    Actor("🌷", 0.24f, 0.8f, 0.16f, Motion.SWAY),
                    Actor("🦋", 0.5f, 0.4f, 0.14f, Motion.FLOAT),
                    Actor("🌼", 0.76f, 0.82f, 0.14f, Motion.SWAY)
                ),
                page(
                    Backdrop.WINTER, "Ghio-ce-lul ie-se din ză-pa-dă.",
                    Actor("💮", 0.5f, 0.74f, 0.2f, Motion.SWAY),
                    Actor("🌱", 0.5f, 0.86f, 0.12f)
                ),
                page(
                    Backdrop.ROOM, "Ma-ra fa-ce un măr-ți-șor.",
                    Actor("👧", 0.34f, 0.64f, 0.36f),
                    Actor("🎀", 0.64f, 0.7f, 0.18f, Motion.PULSE)
                ),
                page(
                    Backdrop.ROOM, "E un șnur alb și ro-șu.",
                    Actor("🎀", 0.5f, 0.56f, 0.3f, Motion.PULSE),
                    Actor("⚪", 0.28f, 0.76f, 0.12f),
                    Actor("🔴", 0.72f, 0.76f, 0.12f)
                ),
                page(
                    Backdrop.ROOM, "Ea dă măr-ți-șo-rul ma-mei.",
                    Actor("👧", 0.28f, 0.66f, 0.3f),
                    Actor("🎀", 0.48f, 0.6f, 0.12f, Motion.PULSE),
                    Actor("👩", 0.7f, 0.6f, 0.38f)
                ),
                page(
                    Backdrop.ROOM, "Un măr-ți-șor e și pen-tru doam-na în-vă-ță-toa-re.",
                    Actor("👧", 0.28f, 0.66f, 0.3f, Motion.BOUNCE),
                    Actor("🎀", 0.48f, 0.6f, 0.12f, Motion.PULSE),
                    Actor("👩‍🏫", 0.72f, 0.6f, 0.38f)
                ),
                page(
                    Backdrop.MEADOW, "Ma-ma zâm-beș-te: Mul-țu-mesc, Ma-ra!",
                    Actor("👩", 0.32f, 0.6f, 0.38f, Motion.PULSE),
                    Actor("👧", 0.6f, 0.7f, 0.3f, Motion.BOUNCE),
                    Actor("❤️", 0.46f, 0.32f, 0.12f, Motion.PULSE)
                )
            ),
            questions = listOf(
                q("Când dăm mărțișoare?", "🌸" to "pe 1 martie", "🎄" to "de Crăciun", "🎂" to "de ziua mea"),
                q("Ce culori are șnurul?", "🎀" to "alb și roșu", "💙" to "albastru", "💚" to "verde și galben"),
                q("Cui dă Mara mărțișoare?", "👩" to "mamei și învățătoarei", "🐶" to "cățelului", "🚗" to "mașinii")
            )
        ),
        holiday(
            id = "ziua_copilului", title = "Ziua Copilului", cover = "🎈", level = 1,
            pages = listOf(
                page(
                    Backdrop.MEADOW, "Azi e în-tâi iu-ni-e.",
                    Actor("📅", 0.5f, 0.56f, 0.3f, Motion.PULSE),
                    Actor("☀️", 0.2f, 0.3f, 0.14f, Motion.PULSE)
                ),
                page(
                    Backdrop.MEADOW, "E Zi-ua Co-pi-lu-lui!",
                    Actor("👧", 0.34f, 0.66f, 0.32f, Motion.BOUNCE),
                    Actor("👦", 0.62f, 0.66f, 0.32f, Motion.BOUNCE),
                    Actor("🎉", 0.48f, 0.3f, 0.14f, Motion.FLOAT)
                ),
                page(
                    Backdrop.MEADOW, "A-na și Ni-cu merg în parc.",
                    Actor("👧", 0.28f, 0.68f, 0.3f, Motion.RUN),
                    Actor("👦", 0.46f, 0.68f, 0.3f, Motion.RUN),
                    Actor("🌳", 0.8f, 0.5f, 0.42f)
                ),
                page(
                    Backdrop.MEADOW, "Ei pri-mesc ba-loa-ne co-lo-ra-te.",
                    Actor("👧", 0.3f, 0.7f, 0.3f),
                    Actor("👦", 0.66f, 0.7f, 0.3f),
                    Actor("🎈", 0.38f, 0.32f, 0.18f, Motion.SWAY),
                    Actor("🎈", 0.58f, 0.28f, 0.18f, Motion.SWAY)
                ),
                page(
                    Backdrop.MEADOW, "Co-pi-ii se joa-că și râd.",
                    Actor("👧", 0.24f, 0.7f, 0.28f, Motion.BOUNCE),
                    Actor("⚽", 0.48f, 0.8f, 0.14f, Motion.RUN),
                    Actor("👦", 0.72f, 0.7f, 0.28f, Motion.BOUNCE),
                    Actor("😄", 0.48f, 0.34f, 0.12f, Motion.FLOAT)
                ),
                page(
                    Backdrop.MEADOW, "Ta-ta le ia în-ghe-ța-tă.",
                    Actor("👨", 0.28f, 0.62f, 0.38f),
                    Actor("🍦", 0.5f, 0.6f, 0.16f, Motion.PULSE),
                    Actor("🍦", 0.64f, 0.62f, 0.16f, Motion.PULSE),
                    Actor("👧", 0.78f, 0.72f, 0.26f, Motion.BOUNCE)
                ),
                page(
                    Backdrop.RAINBOW, "Ce zi fru-moa-să pen-tru co-pii!",
                    Actor("👧", 0.32f, 0.74f, 0.28f, Motion.BOUNCE),
                    Actor("👦", 0.62f, 0.74f, 0.28f, Motion.BOUNCE),
                    Actor("🎈", 0.2f, 0.4f, 0.16f, Motion.FLOAT),
                    Actor("🎈", 0.8f, 0.36f, 0.16f, Motion.FLOAT)
                )
            ),
            questions = listOf(
                q("Ce zi este pe 1 iunie?", "🎈" to "Ziua Copilului", "🎄" to "Crăciunul", "🌸" to "Mărțișorul"),
                q("Unde merg Ana și Nicu?", "🌳" to "în parc", "🏫" to "la școală", "🏥" to "la spital"),
                q("Ce le ia tata?", "🍦" to "înghețată", "🥦" to "broccoli", "🍲" to "supă")
            )
        ),

        // --- Level 2 -----------------------------------------------------------------
        holiday(
            id = "craciunul", title = "Crăciunul", cover = "🎄", level = 2,
            pages = listOf(
                page(
                    Backdrop.WINTER, "A-fa-ră nin-ge și e frig.",
                    Actor("⛄", 0.62f, 0.68f, 0.32f, Motion.SWAY),
                    Actor("❄️", 0.3f, 0.36f, 0.14f, Motion.FLOAT)
                ),
                page(
                    Backdrop.ROOM, "Ta-ta a-du-ce a-ca-să un brad.",
                    Actor("👨", 0.3f, 0.6f, 0.38f, Motion.RUN),
                    Actor("🎄", 0.6f, 0.56f, 0.42f, Motion.SWAY)
                ),
                page(
                    Backdrop.ROOM, "Noi îm-po-do-bim bra-dul cu glo-buri.",
                    Actor("🎄", 0.5f, 0.54f, 0.45f),
                    Actor("👧", 0.22f, 0.68f, 0.28f, Motion.BOUNCE),
                    Actor("👦", 0.78f, 0.68f, 0.28f, Motion.BOUNCE),
                    Actor("✨", 0.5f, 0.24f, 0.12f, Motion.PULSE)
                ),
                page(
                    Backdrop.ROOM, "Ma-ma coa-ce un co-zo-nac pu-fos.",
                    Actor("👩", 0.3f, 0.6f, 0.38f),
                    Actor("🍞", 0.62f, 0.74f, 0.22f, Motion.PULSE),
                    Actor("♨️", 0.62f, 0.52f, 0.12f, Motion.FLOAT)
                ),
                page(
                    Backdrop.SNOW_NIGHT, "Noap-tea vi-ne Moș Cră-ciun.",
                    Actor("🎅", 0.36f, 0.4f, 0.26f, Motion.FLOAT),
                    Actor("🦌", 0.6f, 0.44f, 0.2f, Motion.FLOAT),
                    Actor("🛷", 0.36f, 0.56f, 0.16f, Motion.FLOAT)
                ),
                page(
                    Backdrop.ROOM, "Sub brad sunt ca-do-uri mul-te.",
                    Actor("🎄", 0.5f, 0.5f, 0.42f),
                    Actor("🎁", 0.36f, 0.8f, 0.16f, Motion.PULSE),
                    Actor("🎁", 0.5f, 0.82f, 0.14f, Motion.PULSE),
                    Actor("🎁", 0.64f, 0.8f, 0.16f, Motion.PULSE)
                ),
                page(
                    Backdrop.ROOM, "A-na pri-meș-te o car-te, Ra-du o min-ge.",
                    Actor("👧", 0.2f, 0.66f, 0.3f, Motion.BOUNCE),
                    Actor("📚", 0.36f, 0.76f, 0.16f),
                    Actor("⚽", 0.64f, 0.8f, 0.14f, Motion.BOUNCE),
                    Actor("👦", 0.8f, 0.66f, 0.3f, Motion.BOUNCE)
                ),
                page(
                    Backdrop.ROOM, "Toți spun: Cră-ciun fe-ri-cit!",
                    Actor("🎄", 0.5f, 0.5f, 0.4f, Motion.PULSE),
                    Actor("👩", 0.16f, 0.62f, 0.32f),
                    Actor("👧", 0.3f, 0.7f, 0.24f, Motion.BOUNCE),
                    Actor("👦", 0.7f, 0.7f, 0.24f, Motion.BOUNCE),
                    Actor("👨", 0.84f, 0.62f, 0.32f)
                )
            ),
            questions = listOf(
                q("Cu ce împodobesc bradul?", "✨" to "cu globuri", "🥕" to "cu morcovi", "🍌" to "cu banane"),
                q("Ce coace mama?", "🍞" to "un cozonac", "🍕" to "o pizza", "🍪" to "biscuiți"),
                q("Cine vine noaptea?", "🎅" to "Moș Crăciun", "🐰" to "iepurașul", "🐻" to "ursul")
            )
        ),
        holiday(
            id = "anul_nou", title = "Anul Nou", cover = "🎆", level = 2,
            pages = listOf(
                page(
                    Backdrop.SNOW_NIGHT, "E ul-ti-ma sea-ră din an.",
                    Actor("🌙", 0.24f, 0.3f, 0.16f, Motion.PULSE),
                    Actor("⛄", 0.5f, 0.76f, 0.24f, Motion.SWAY)
                ),
                page(
                    Backdrop.ROOM, "Toa-tă fa-mi-li-a stă la ma-să.",
                    Actor("👵", 0.16f, 0.62f, 0.3f),
                    Actor("👩", 0.34f, 0.62f, 0.3f),
                    Actor("🍲", 0.5f, 0.8f, 0.16f, Motion.PULSE),
                    Actor("👨", 0.66f, 0.62f, 0.3f),
                    Actor("👧", 0.84f, 0.68f, 0.24f, Motion.BOUNCE)
                ),
                page(
                    Backdrop.ROOM, "La mie-zul nop-ții, cea-sul ba-te: dang!",
                    Actor("🕛", 0.5f, 0.5f, 0.4f, Motion.PULSE),
                    Actor("🔔", 0.78f, 0.34f, 0.14f, Motion.SWAY)
                ),
                page(
                    Backdrop.NIGHT, "Pe cer sunt ar-ti-fi-cii co-lo-ra-te!",
                    Actor("🎆", 0.28f, 0.3f, 0.24f, Motion.PULSE),
                    Actor("🎇", 0.64f, 0.24f, 0.2f, Motion.PULSE),
                    Actor("🎆", 0.84f, 0.42f, 0.16f, Motion.PULSE),
                    Actor("👧", 0.46f, 0.76f, 0.24f, Motion.BOUNCE)
                ),
                page(
                    Backdrop.WINTER, "Di-mi-nea-ța, co-pi-ii merg cu sor-co-va.",
                    Actor("👧", 0.28f, 0.7f, 0.28f, Motion.RUN),
                    Actor("💐", 0.42f, 0.54f, 0.16f, Motion.SWAY),
                    Actor("👦", 0.6f, 0.7f, 0.28f, Motion.RUN)
                ),
                page(
                    Backdrop.ROOM, "Ei u-rea-ză: Sor-co-va, ve-se-la!",
                    Actor("👦", 0.26f, 0.66f, 0.3f, Motion.BOUNCE),
                    Actor("💐", 0.42f, 0.5f, 0.16f, Motion.SWAY),
                    Actor("👵", 0.72f, 0.62f, 0.34f, Motion.PULSE)
                ),
                page(
                    Backdrop.ROOM, "Ei le do-resc tu-tu-ror să-nă-ta-te.",
                    Actor("👧", 0.24f, 0.68f, 0.28f),
                    Actor("👦", 0.42f, 0.68f, 0.28f),
                    Actor("❤️", 0.54f, 0.34f, 0.14f, Motion.PULSE),
                    Actor("👴", 0.74f, 0.62f, 0.34f, Motion.BOUNCE)
                ),
                page(
                    Backdrop.NIGHT, "La mulți ani, an nou fe-ri-cit!",
                    Actor("🎉", 0.3f, 0.6f, 0.24f, Motion.BOUNCE),
                    Actor("🎆", 0.6f, 0.3f, 0.24f, Motion.PULSE),
                    Actor("🎊", 0.76f, 0.62f, 0.2f, Motion.SWAY)
                )
            ),
            questions = listOf(
                q("Ce este pe cer la miezul nopții?", "🎆" to "artificii", "🌈" to "un curcubeu", "☀️" to "soarele"),
                q("Cu ce merg copiii dimineața?", "💐" to "cu sorcova", "⚽" to "cu mingea", "🎈" to "cu baloane"),
                q("Ce le doresc copiii tuturor?", "❤️" to "sănătate", "🌧️" to "ploaie", "😴" to "somn")
            )
        ),
        holiday(
            id = "paste", title = "Paștele", cover = "🥚", level = 2,
            pages = listOf(
                page(
                    Backdrop.MEADOW, "Vi-ne Paș-te-le, săr-bă-toa-rea pri-mă-ve-rii.",
                    Actor("🌷", 0.26f, 0.8f, 0.16f, Motion.SWAY),
                    Actor("🐣", 0.5f, 0.76f, 0.2f, Motion.BOUNCE),
                    Actor("🌼", 0.74f, 0.8f, 0.16f, Motion.SWAY)
                ),
                page(
                    Backdrop.ROOM, "Bu-ni-ca și Ra-du vop-sesc o-uă ro-șii.",
                    Actor("👵", 0.22f, 0.62f, 0.34f),
                    Actor("🔴", 0.46f, 0.8f, 0.12f, Motion.PULSE),
                    Actor("🔴", 0.56f, 0.82f, 0.12f, Motion.PULSE),
                    Actor("🥚", 0.66f, 0.8f, 0.12f),
                    Actor("👦", 0.82f, 0.66f, 0.3f, Motion.BOUNCE)
                ),
                page(
                    Backdrop.ROOM, "Ma-ma coa-ce co-zo-nac și pas-că.",
                    Actor("👩", 0.3f, 0.6f, 0.38f),
                    Actor("🍞", 0.6f, 0.76f, 0.18f, Motion.PULSE),
                    Actor("🥧", 0.76f, 0.78f, 0.16f, Motion.PULSE)
                ),
                page(
                    Backdrop.NIGHT, "Noap-tea mer-gem cu lu-mâ-na-rea la bi-se-ri-că.",
                    Actor("⛪", 0.68f, 0.6f, 0.42f),
                    Actor("👦", 0.28f, 0.72f, 0.28f),
                    Actor("🕯️", 0.4f, 0.64f, 0.14f, Motion.PULSE)
                ),
                page(
                    Backdrop.NIGHT, "Toți spun: Hris-tos a în-vi-at!",
                    Actor("🕯️", 0.24f, 0.66f, 0.16f, Motion.PULSE),
                    Actor("🕯️", 0.42f, 0.7f, 0.14f, Motion.PULSE),
                    Actor("🕯️", 0.6f, 0.66f, 0.16f, Motion.PULSE),
                    Actor("🕯️", 0.78f, 0.7f, 0.14f, Motion.PULSE),
                    Actor("✨", 0.5f, 0.3f, 0.14f, Motion.FLOAT)
                ),
                page(
                    Backdrop.ROOM, "Ra-du cioc-neș-te un ou cu bu-ni-ca.",
                    Actor("👦", 0.22f, 0.66f, 0.3f),
                    Actor("🔴", 0.44f, 0.66f, 0.12f, Motion.PULSE),
                    Actor("🔴", 0.56f, 0.66f, 0.12f, Motion.PULSE),
                    Actor("👵", 0.78f, 0.62f, 0.34f)
                ),
                page(
                    Backdrop.ROOM, "Bu-ni-ca zi-ce: A-de-vă-rat a în-vi-at!",
                    Actor("👵", 0.36f, 0.62f, 0.36f, Motion.PULSE),
                    Actor("💬", 0.56f, 0.34f, 0.14f, Motion.FLOAT),
                    Actor("👦", 0.74f, 0.68f, 0.28f)
                ),
                page(
                    Backdrop.ROOM, "O-ul lui Ra-du nu se spar-ge!",
                    Actor("🔴", 0.5f, 0.58f, 0.26f, Motion.BOUNCE),
                    Actor("💪", 0.74f, 0.4f, 0.14f, Motion.PULSE),
                    Actor("😄", 0.24f, 0.66f, 0.26f, Motion.BOUNCE)
                ),
                page(
                    Backdrop.MEADOW, "În gră-di-nă, Ra-du gă-seș-te un ie-pu-raș.",
                    Actor("👦", 0.3f, 0.68f, 0.3f, Motion.BOUNCE),
                    Actor("🐰", 0.66f, 0.76f, 0.22f, Motion.BOUNCE),
                    Actor("🌷", 0.84f, 0.84f, 0.12f, Motion.SWAY)
                )
            ),
            questions = listOf(
                q("Ce culoare au ouăle?", "🔴" to "roșii", "💙" to "albastre", "⚫" to "negre"),
                q("Ce spune bunica?", "🕯️" to "Adevărat a înviat!", "🎄" to "Crăciun fericit!", "🎂" to "La mulți ani!"),
                q("Ce face Radu cu oul?", "🥚" to "îl ciocnește", "⚽" to "îl aruncă", "🍳" to "îl prăjește")
            )
        ),

        // --- Level 3 -----------------------------------------------------------------
        holiday(
            id = "colindul", title = "Colindul", cover = "🔔", level = 3,
            pages = listOf(
                page(
                    Backdrop.SNOW_NIGHT, "E A-ju-nul Cră-ciu-nu-lui și nin-ge.",
                    Actor("❄️", 0.3f, 0.36f, 0.14f, Motion.FLOAT),
                    Actor("⛄", 0.5f, 0.78f, 0.24f, Motion.SWAY)
                ),
                page(
                    Backdrop.SNOW_NIGHT, "Co-pi-ii plea-că cu co-lin-dul.",
                    Actor("👧", 0.22f, 0.76f, 0.24f, Motion.RUN),
                    Actor("👦", 0.38f, 0.76f, 0.24f, Motion.RUN),
                    Actor("🔔", 0.3f, 0.52f, 0.12f, Motion.SWAY),
                    Actor("⭐", 0.48f, 0.5f, 0.14f, Motion.SWAY)
                ),
                page(
                    Backdrop.SNOW_NIGHT, "Ei bat la u-șa ve-ci-ni-lor.",
                    Actor("🚪", 0.66f, 0.66f, 0.34f),
                    Actor("👊", 0.52f, 0.64f, 0.1f, Motion.PULSE),
                    Actor("👧", 0.24f, 0.76f, 0.24f),
                    Actor("👦", 0.38f, 0.76f, 0.24f)
                ),
                page(
                    Backdrop.SNOW_NIGHT, "Co-lin-dă-to-rii cân-tă fru-mos.",
                    Actor("👧", 0.24f, 0.76f, 0.24f, Motion.SWAY),
                    Actor("👦", 0.42f, 0.76f, 0.24f, Motion.SWAY),
                    Actor("🎶", 0.34f, 0.46f, 0.14f, Motion.FLOAT),
                    Actor("🔔", 0.6f, 0.6f, 0.12f, Motion.SWAY)
                ),
                page(
                    Backdrop.ROOM, "Bu-ni-ca le dă me-re și nuci.",
                    Actor("👵", 0.26f, 0.62f, 0.36f),
                    Actor("🍎", 0.5f, 0.74f, 0.14f, Motion.BOUNCE),
                    Actor("🌰", 0.62f, 0.76f, 0.12f, Motion.BOUNCE),
                    Actor("👧", 0.8f, 0.68f, 0.28f, Motion.BOUNCE)
                ),
                page(
                    Backdrop.ROOM, "Pri-mesc și co-laci calzi.",
                    Actor("🥯", 0.5f, 0.66f, 0.24f, Motion.PULSE),
                    Actor("♨️", 0.5f, 0.42f, 0.12f, Motion.FLOAT),
                    Actor("😋", 0.22f, 0.64f, 0.24f, Motion.BOUNCE)
                ),
                page(
                    Backdrop.SNOW_NIGHT, "Co-pi-ii mul-țu-mesc și merg mai de-par-te.",
                    Actor("👧", 0.3f, 0.76f, 0.24f, Motion.RUN),
                    Actor("👦", 0.46f, 0.76f, 0.24f, Motion.RUN),
                    Actor("⭐", 0.56f, 0.5f, 0.14f, Motion.SWAY)
                ),
                page(
                    Backdrop.SNOW_NIGHT, "Ce fru-moa-să e sea-ra de co-lin-de!",
                    Actor("⭐", 0.3f, 0.36f, 0.16f, Motion.PULSE),
                    Actor("🔔", 0.62f, 0.4f, 0.14f, Motion.SWAY),
                    Actor("👧", 0.36f, 0.76f, 0.24f, Motion.BOUNCE),
                    Actor("👦", 0.54f, 0.76f, 0.24f, Motion.BOUNCE)
                )
            ),
            questions = listOf(
                q("Când merg copiii cu colindul?", "🎄" to "în Ajunul Crăciunului", "🏖️" to "vara", "🐣" to "de Paște"),
                q("Ce primesc colindătorii?", "🍎" to "mere, nuci și colaci", "🍦" to "înghețată", "🎈" to "baloane"),
                q("Ce fac colindătorii?", "🎶" to "cântă", "💤" to "dorm", "⚽" to "joacă fotbal")
            )
        ),
        holiday(
            id = "ziua_nationala", title = "Ziua României", cover = "🥁", level = 3,
            pages = listOf(
                page(
                    Backdrop.PARADE, "Azi e în-tâi de-cem-bri-e.",
                    Actor("📅", 0.5f, 0.66f, 0.28f, Motion.PULSE)
                ),
                page(
                    Backdrop.PARADE, "E săr-bă-toa-rea Ro-mâ-ni-ei.",
                    Actor("🎉", 0.3f, 0.66f, 0.22f, Motion.BOUNCE),
                    Actor("✨", 0.66f, 0.62f, 0.14f, Motion.PULSE)
                ),
                page(
                    Backdrop.PARADE, "Pe stra-dă flu-tu-ră stea-guri tri-co-lo-re.",
                    Actor("👧", 0.3f, 0.76f, 0.24f, Motion.BOUNCE),
                    Actor("👦", 0.66f, 0.76f, 0.24f, Motion.BOUNCE)
                ),
                page(
                    Backdrop.PARADE, "Stea-gul e al-bas-tru, gal-ben și ro-șu.",
                    Actor("💙", 0.3f, 0.74f, 0.14f, Motion.PULSE),
                    Actor("💛", 0.5f, 0.74f, 0.14f, Motion.PULSE),
                    Actor("❤️", 0.7f, 0.74f, 0.14f, Motion.PULSE)
                ),
                page(
                    Backdrop.PARADE, "Sol-da-ții de-fi-lea-ză la pa-ra-dă.",
                    Actor("💂", 0.2f, 0.72f, 0.26f, Motion.RUN),
                    Actor("🥁", 0.4f, 0.78f, 0.16f, Motion.PULSE),
                    Actor("💂", 0.56f, 0.72f, 0.26f, Motion.RUN),
                    Actor("🎺", 0.76f, 0.7f, 0.16f, Motion.SWAY)
                ),
                page(
                    Backdrop.PARADE, "Ma-tei stă pe u-me-rii ta-tei.",
                    Actor("👨", 0.44f, 0.7f, 0.34f),
                    Actor("👦", 0.44f, 0.44f, 0.2f, Motion.BOUNCE),
                    Actor("👏", 0.62f, 0.4f, 0.12f, Motion.PULSE)
                ),
                page(
                    Backdrop.PARADE, "Sea-ra, toa-tă lu-mea joa-că ho-ra.",
                    Actor("👫", 0.2f, 0.74f, 0.24f, Motion.SWAY),
                    Actor("💃", 0.4f, 0.74f, 0.24f, Motion.BOUNCE),
                    Actor("👭", 0.6f, 0.74f, 0.24f, Motion.SWAY),
                    Actor("👫", 0.8f, 0.74f, 0.24f, Motion.SWAY),
                    Actor("🎶", 0.5f, 0.46f, 0.12f, Motion.FLOAT)
                ),
                page(
                    Backdrop.NIGHT, "La mulți ani, Ro-mâ-ni-a!",
                    Actor("🎆", 0.3f, 0.3f, 0.24f, Motion.PULSE),
                    Actor("🎇", 0.68f, 0.26f, 0.2f, Motion.PULSE),
                    Actor("💙", 0.4f, 0.74f, 0.12f, Motion.BOUNCE),
                    Actor("💛", 0.5f, 0.74f, 0.12f, Motion.BOUNCE),
                    Actor("❤️", 0.6f, 0.74f, 0.12f, Motion.BOUNCE)
                )
            ),
            questions = listOf(
                q("Ce culori are steagul României?", "🎨" to "albastru, galben și roșu", "🌈" to "verde și alb", "⚫" to "negru și alb"),
                q("Ce dans joacă toată lumea?", "💃" to "hora", "🎻" to "valsul", "🏃" to "leapșa"),
                q("Ce văd copiii pe stradă?", "🥁" to "parada", "🐘" to "un elefant", "🚀" to "o rachetă")
            )
        )
    )

    val stories: List<Story> = everydayStories() + classicStories() + holidayStories()

    fun getStory(id: String): Story? = stories.firstOrNull { it.id == id }

    private fun w(marked: String, emoji: String) = TrainerWord(marked, emoji)

    val trainerLevels: List<TrainerLevel> = listOf(
        TrainerLevel(
            1, "Silabe simple", "Cuvinte scurte: ca-să, lu-nă", "🐣",
            listOf(
                w("ca-să", "🏠"), w("lu-nă", "🌙"), w("ra-ță", "🦆"), w("va-că", "🐄"),
                w("pa-ră", "🍐"), w("to-bă", "🥁"), w("ca-nă", "☕"), w("ma-re", "🌊"),
                w("su-pă", "🍲"), w("fa-tă", "👧"), w("ro-ză", "🌹"), w("be-be", "👶")
            )
        ),
        TrainerLevel(
            2, "Cuvinte noi", "Silabe mai grele: tren, floa-re", "🐥",
            listOf(
                w("măr", "🍎"), w("cal", "🐴"), w("urs", "🐻"), w("nor", "☁️"),
                w("tren", "🚂"), w("stea", "⭐"), w("ba-lon", "🎈"), w("soa-re", "☀️"),
                w("floa-re", "🌸"), w("car-te", "📖"), w("bar-că", "⛵"), w("mor-cov", "🥕"),
                w("broas-că", "🐸"), w("lap-te", "🥛"), w("pâi-ne", "🍞")
            )
        ),
        TrainerLevel(
            3, "Cuvinte lungi", "Trei silabe sau mai multe", "🦉",
            listOf(
                w("pi-si-că", "🐱"), w("ba-na-nă", "🍌"), w("ma-și-nă", "🚗"), w("al-bi-nă", "🐝"),
                w("flu-tu-re", "🦋"), w("ie-pu-re", "🐰"), w("e-le-fant", "🐘"), w("lă-mâ-ie", "🍋"),
                w("cro-co-dil", "🐊"), w("um-bre-lă", "☂️"), w("te-le-fon", "📱"), w("cur-cu-beu", "🌈")
            )
        ),
        TrainerLevel(
            4, "Grupuri de litere", "ce, ci, ge, gi, che, chi, ghe, ghi", "🦄",
            listOf(
                w("ceas", "⏰"), w("ci-re-șe", "🍒"), w("a-rici", "🦔"), w("gi-ra-fă", "🦒"),
                w("min-ge", "⚽"), w("gean-tă", "👜"), w("che-ie", "🔑"), w("ra-che-tă", "🚀"),
                w("o-che-lari", "👓"), w("u-re-che", "👂"), w("ghioz-dan", "🎒"), w("în-ghe-ța-tă", "🍦"),
                w("ciu-per-că", "🍄"), w("bi-ci-cle-tă", "🚲")
            )
        )
    )

    fun getTrainerLevel(id: Int): TrainerLevel? = trainerLevels.firstOrNull { it.id == id }

    /** Stars for a round: 3 = everything right first try, 2 = most of it, 1 = finished. */
    fun starsFor(correctFirstTry: Int, total: Int): Int = when {
        total <= 0 -> 0
        correctFirstTry >= total -> 3
        correctFirstTry * 3 >= total * 2 -> 2
        else -> 1
    }
}
