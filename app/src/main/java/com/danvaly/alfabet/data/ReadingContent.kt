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

enum class Backdrop { MEADOW, ROOM, FARM, RAIN, RAINBOW, FOREST, EVENING, NIGHT, BEACH, GARDEN }

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
    val questions: List<StoryQuestion>
)

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

    val stories: List<Story> = listOf(
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
