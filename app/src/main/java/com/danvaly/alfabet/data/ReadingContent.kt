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
