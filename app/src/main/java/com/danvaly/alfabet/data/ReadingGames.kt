package com.danvaly.alfabet.data

import kotlin.random.Random

// ---------------------------------------------------------------------------
// "Scrie cuvântul": letter tiles dropped into slots.
// ---------------------------------------------------------------------------

/** One letter tile in the tray; [id] keeps two equal letters apart. */
data class SpellTile(val id: Int, val unit: LetterUnit) {
    val text: String get() = unit.text
}

/**
 * The spelling board: [target] are the letter units of the word, [slots] hold the tile ids
 * placed so far (null = free) and [locked] are the slots filled by a hint.
 */
data class SpellBoard(
    val target: List<String>,
    val tiles: List<SpellTile>,
    val slots: List<Int?>,
    val locked: Set<Int> = emptySet()
) {
    private fun textOf(id: Int?): String? = tiles.firstOrNull { it.id == id }?.text

    fun tileAt(slot: Int): SpellTile? {
        val id = slots.getOrNull(slot) ?: return null
        return tiles.firstOrNull { it.id == id }
    }

    /** Tiles that are not in a slot, in their shuffled order. */
    val tray: List<SpellTile> get() = tiles.filter { it.id !in slots }

    val isFull: Boolean get() = slots.all { it != null }

    /** Filled slots whose letter is not the one the word needs there. */
    val wrongSlots: Set<Int>
        get() = slots.indices.filter { i ->
            val text = textOf(slots[i])
            text != null && !text.equals(target[i], ignoreCase = true)
        }.toSet()

    val isSolved: Boolean get() = isFull && wrongSlots.isEmpty()

    /** Puts a tray tile into the first free slot. */
    fun place(tileId: Int): SpellBoard {
        if (tileId in slots || tiles.none { it.id == tileId }) return this
        val free = slots.indexOfFirst { it == null }
        if (free < 0) return this
        return copy(slots = slots.toMutableList().also { it[free] = tileId })
    }

    /** Sends the tile in [slot] back to the tray (hint letters stay). */
    fun remove(slot: Int): SpellBoard {
        if (slot !in slots.indices || slot in locked || slots[slot] == null) return this
        return copy(slots = slots.toMutableList().also { it[slot] = null })
    }

    /** All wrong tiles go back to the tray; the right ones stay. */
    fun clearWrong(): SpellBoard {
        val wrong = wrongSlots
        return copy(slots = slots.mapIndexed { i, id -> if (i in wrong) null else id })
    }

    /** The first slot that is not right yet, or null when every slot is right. */
    fun nextHintSlot(): Int? =
        slots.indices.firstOrNull { i -> i !in locked && (slots[i] == null || i in wrongSlots) }

    /** Puts the correct letter into the first slot that is not right yet and locks it. */
    fun hint(): SpellBoard {
        val slot = nextHintSlot() ?: return this
        var board = if (slots[slot] != null) remove(slot) else this
        val needed = target[slot]
        var tileId = board.tray.firstOrNull { it.text.equals(needed, ignoreCase = true) }?.id
        if (tileId == null) {
            // The letter sits in a wrong slot further on: take it from there.
            val wrong = board.wrongSlots
            val donor = board.slots.indices.firstOrNull { i ->
                i != slot && i !in board.locked && i in wrong &&
                    board.textOf(board.slots[i]).equals(needed, ignoreCase = true)
            } ?: return board
            tileId = board.slots[donor]
            board = board.remove(donor)
        }
        if (tileId == null) return board
        return board.copy(
            slots = board.slots.toMutableList().also { it[slot] = tileId },
            locked = board.locked + slot
        )
    }
}

// ---------------------------------------------------------------------------
// "Desparte în silabe"
// ---------------------------------------------------------------------------

/** Result of checking the cuts the child made. */
data class CutCheck(val wrong: Set<Int>, val missing: Set<Int>) {
    val isCorrect: Boolean get() = wrong.isEmpty() && missing.isEmpty()
}

// ---------------------------------------------------------------------------
// "Litere care seamănă"
// ---------------------------------------------------------------------------

/** A picture word with one letter to fill in; [index] counts letters in the word without hyphens. */
data class ConfusableWord(val marked: String, val emoji: String, val index: Int) {
    val text: String get() = marked.replace("-", "")
    val answer: Char get() = text[index]
    val word: ReadingWord by lazy { ReadingText.parseWord(marked) }

    /** The word with [letter] in the missing place. */
    fun withLetter(letter: Char): String =
        text.substring(0, index) + letter + text.substring(index + 1)
}

data class ConfusableSet(
    val id: String,
    val first: Char,
    val second: Char,
    val words: List<ConfusableWord>
) {
    val title: String get() = "$first · $second"
    val letters: List<Char> get() = listOf(first, second)
    fun other(letter: Char): Char = if (letter == first) second else first
}

sealed interface ConfusableTask {
    /** "Completează cuvântul": pick the missing letter. */
    data class Complete(val word: ConfusableWord, val choices: List<Char>) : ConfusableTask

    /** "Găsește litera": tap every [target] in the grid. */
    data class Find(val target: Char, val grid: List<Char>) : ConfusableTask {
        val targetCount: Int get() = grid.count { it == target }
    }
}

object ReadingGames {

    const val ROUND_SIZE = 6

    /** Letters that children often mix up; used for distractor tiles and letter grids. */
    private val lookalikePairs = listOf(
        "bd", "pb", "mn", "sș", "tț", "aă", "iî", "âî", "dt", "fv",
        "ou", "eo", "rl", "cg", "zs", "nu"
    )

    private const val FILLER = "abcdefghilmnoprstuvzăîșț"

    fun lookalikesOf(c: Char): List<Char> {
        val lower = c.lowercaseChar()
        return lookalikePairs.mapNotNull { pair ->
            when (lower) {
                pair[0] -> pair[1]
                pair[1] -> pair[0]
                else -> null
            }
        }.distinct()
    }

    // --- Spell word ----------------------------------------------------------

    /** How many letter slots each syllable gets (letter groups like "che" count as one). */
    fun slotGroups(word: ReadingWord): List<Int> =
        word.syllables.map { ReadingText.letterUnits(it).size }

    /**
     * The word's letter units plus [distractors] extra single letters that are not in the word
     * (look-alikes of its letters first), shuffled.
     */
    fun spellTiles(word: ReadingWord, distractors: Int, random: Random = Random.Default): List<SpellTile> {
        val units = word.units
        val used = units.map { it.text.lowercase() }.toSet()
        val letters = word.text.lowercase()
        val preferred = letters.flatMap { lookalikesOf(it) }
            .distinct()
            .filter { it.toString() !in used && it !in letters }
            .shuffled(random)
        val general = FILLER
            .filter { it.toString() !in used && it !in letters && it !in preferred }
            .toList()
            .shuffled(random)
        val extra = (preferred + general).take(distractors.coerceAtLeast(0))

        val tiles = units.mapIndexed { i, unit -> SpellTile(i, unit) } +
            extra.mapIndexed { j, c ->
                SpellTile(
                    units.size + j,
                    LetterUnit(c.toString(), if (ReadingText.isVowel(c)) SoundKind.VOWEL else SoundKind.CONSONANT)
                )
            }
        var shuffled = tiles.shuffled(random)
        var tries = 0
        // Avoid handing the child the word already in order.
        while (tiles.size > 2 && shuffled.take(units.size).map { it.text } == units.map { it.text } && tries < 8) {
            shuffled = tiles.shuffled(random)
            tries++
        }
        return shuffled
    }

    fun spellBoard(word: ReadingWord, distractors: Int, random: Random = Random.Default): SpellBoard {
        val units = word.units
        return SpellBoard(
            target = units.map { it.text },
            tiles = spellTiles(word, distractors, random),
            slots = List(units.size) { null }
        )
    }

    // --- Syllable split ------------------------------------------------------

    /**
     * Where the word breaks into syllables. A cut at position p sits between letter p-1 and
     * letter p of [ReadingWord.text]; a one-syllable word has no cuts.
     */
    fun cutPositions(word: ReadingWord): Set<Int> {
        val cuts = mutableSetOf<Int>()
        var position = 0
        word.syllables.dropLast(1).forEach { syllable ->
            position += syllable.length
            cuts += position
        }
        return cuts
    }

    fun checkCuts(word: ReadingWord, chosen: Set<Int>): CutCheck {
        val real = cutPositions(word)
        return CutCheck(wrong = chosen - real, missing = real - chosen)
    }

    /** The syllable that letter [letterIndex] belongs to. */
    fun syllableOfLetter(word: ReadingWord, letterIndex: Int): Int {
        var end = 0
        word.syllables.forEachIndexed { i, syllable ->
            end += syllable.length
            if (letterIndex < end) return i
        }
        return word.syllables.lastIndex
    }

    /** One-syllable picture words, so "no cut at all" also comes up. */
    val oneSyllableWords: List<TrainerWord> = listOf(
        TrainerWord("pom", "🌳"), TrainerWord("nas", "👃"), TrainerWord("lup", "🐺"),
        TrainerWord("melc", "🐌"), TrainerWord("brad", "🌲"), TrainerWord("pat", "🛏️")
    )

    /** [size] words from [level], always with at least one one-syllable word. */
    fun syllableRound(level: TrainerLevel, random: Random = Random.Default, size: Int = ROUND_SIZE): List<TrainerWord> {
        val round = level.words.shuffled(random).take(size).toMutableList()
        if (round.isNotEmpty() && round.none { it.word.syllables.size == 1 }) {
            round[round.lastIndex] = oneSyllableWords.random(random)
        }
        return round.shuffled(random)
    }

    // --- Confusable letters --------------------------------------------------

    private fun cw(marked: String, emoji: String, index: Int) = ConfusableWord(marked, emoji, index)

    val confusableSets: List<ConfusableSet> = listOf(
        ConfusableSet(
            "b_d", 'b', 'd', listOf(
                cw("ba-na-nă", "🍌", 0), cw("ba-lon", "🎈", 0), cw("broas-că", "🐸", 0), cw("ze-bră", "🦓", 2),
                cw("del-fin", "🐬", 0), cw("dov-leac", "🎃", 0), cw("dra-gon", "🐉", 0), cw("ca-dou", "🎁", 2)
            )
        ),
        ConfusableSet(
            "p_b", 'p', 'b', listOf(
                cw("pi-si-că", "🐱", 0), cw("peș-te", "🐟", 0), cw("pom", "🌳", 0), cw("pâi-ne", "🍞", 0),
                cw("ba-lon", "🎈", 0), cw("bec", "💡", 0), cw("broas-că", "🐸", 0), cw("ba-na-nă", "🍌", 0)
            )
        ),
        ConfusableSet(
            "m_n", 'm', 'n', listOf(
                cw("măr", "🍎", 0), cw("melc", "🐌", 0), cw("ma-și-nă", "🚗", 0), cw("că-mi-lă", "🐫", 2),
                cw("nu-că", "🌰", 0), cw("ni-sip", "🏖️", 0), cw("lu-nă", "🌙", 2), cw("ba-na-nă", "🍌", 2)
            )
        ),
        ConfusableSet(
            "s_sh", 's', 'ș', listOf(
                cw("soa-re", "☀️", 0), cw("sa-ni-e", "🛷", 0), cw("cas-tel", "🏰", 2), cw("pi-si-că", "🐱", 2),
                cw("urs", "🐻", 2), cw("șoa-re-ce", "🐭", 0), cw("șar-pe", "🐍", 0), cw("șo-se-te", "🧦", 0),
                cw("ma-și-nă", "🚗", 2)
            )
        ),
        ConfusableSet(
            "t_tz", 't', 'ț', listOf(
                cw("tren", "🚂", 0), cw("to-bă", "🥁", 0), cw("te-le-fon", "📱", 0), cw("tort", "🎂", 0),
                cw("țes-toa-să", "🐢", 0), cw("că-țel", "🐶", 2), cw("mai-mu-ță", "🐒", 5), cw("în-ghe-ța-tă", "🍦", 5)
            )
        ),
        ConfusableSet(
            "a_ah", 'a', 'ă', listOf(
                cw("cal", "🐴", 1), cw("ca-să", "🏠", 1), cw("va-că", "🐄", 1), cw("ba-na-nă", "🍌", 1),
                cw("măr", "🍎", 1), cw("pă-lă-ri-e", "🎩", 1), cw("căp-șu-ni", "🍓", 1), cw("bă-iat", "👦", 1)
            )
        ),
        ConfusableSet(
            "i_ih", 'i', 'î', listOf(
                cw("i-ni-mă", "❤️", 0), cw("ie-pu-re", "🐰", 0), cw("i-nel", "💍", 0), cw("pi-si-că", "🐱", 1),
                cw("a-rici", "🦔", 2), cw("în-ghe-ța-tă", "🍦", 0), cw("în-ger", "👼", 0), cw("îm-pă-rat", "🤴", 0)
            )
        ),
        ConfusableSet(
            "d_t", 'd', 't', listOf(
                cw("dov-leac", "🎃", 0), cw("del-fin", "🐬", 0), cw("dra-gon", "🐉", 0), cw("ca-dou", "🎁", 2),
                cw("tort", "🎂", 0), cw("te-le-fon", "📱", 0), cw("tran-da-fir", "🌹", 0), cw("pan-tof", "👞", 3)
            )
        ),
        ConfusableSet(
            "f_v", 'f', 'v', listOf(
                cw("flu-tu-re", "🦋", 0), cw("floa-re", "🌸", 0), cw("foc", "🔥", 0), cw("fur-cu-li-ță", "🍴", 0),
                cw("vul-pe", "🦊", 0), cw("va-por", "🚢", 0), cw("ve-ve-ri-ță", "🐿️", 0), cw("val", "🌊", 0)
            )
        )
    )

    fun getConfusableSet(id: String): ConfusableSet? = confusableSets.firstOrNull { it.id == id }

    /**
     * A grid of [size] look-alike letters with 3–5 copies of [target]; the rest are mostly the
     * other letter of the set, plus a couple of further look-alikes.
     */
    fun letterGrid(set: ConfusableSet, target: Char, random: Random = Random.Default, size: Int = 12): List<Char> {
        val other = set.other(target)
        val targets = random.nextInt(3, 6).coerceAtMost(size)
        val extras = (lookalikesOf(target) + lookalikesOf(other))
            .filter { it != target && it != other }
            .distinct()
            .shuffled(random)
            .take(2)
        val rest = size - targets
        val fill = List(rest) { i -> if (i < extras.size) extras[i] else other }
        return (List(targets) { target } + fill).shuffled(random)
    }

    /** Six tasks that alternate "complete the word" and "find the letter". */
    fun confusableRound(set: ConfusableSet, random: Random = Random.Default): List<ConfusableTask> {
        val words = set.words.shuffled(random)
        val findTargets = set.letters.shuffled(random)
        val pattern = listOf(true, false, true, true, false, true) // true = complete the word
        var wordIndex = 0
        var findIndex = 0
        return pattern.map { complete ->
            if (complete) {
                val word = words[wordIndex++ % words.size]
                ConfusableTask.Complete(word, set.letters.shuffled(random))
            } else {
                val target = findTargets[findIndex++ % findTargets.size]
                ConfusableTask.Find(target, letterGrid(set, target, random))
            }
        }
    }
}
