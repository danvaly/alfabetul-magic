package com.danvaly.alfabet.data

import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReadingGamesTest {

    private fun word(marked: String) = ReadingText.parseWord(marked)

    // --- Spell word ----------------------------------------------------------

    @Test
    fun `tiles are the word's letter units without distractors`() {
        val w = word("ra-che-tă")
        val tiles = ReadingGames.spellTiles(w, 0, Random(1))
        assertEquals(w.units.map { it.text }.sorted(), tiles.map { it.text }.sorted())
        assertTrue(tiles.any { it.text == "che" })
        assertEquals(tiles.size, tiles.map { it.id }.toSet().size)
    }

    @Test
    fun `distractors are extra letters that are not in the word`() {
        for (seed in 0 until 30) {
            val w = word("ba-lon")
            val tiles = ReadingGames.spellTiles(w, 2, Random(seed))
            assertEquals(w.units.size + 2, tiles.size)
            val extra = tiles.filter { it.id >= w.units.size }
            assertEquals(2, extra.size)
            extra.forEach { tile ->
                assertEquals(1, tile.text.length)
                assertFalse(tile.text in w.text)
            }
            assertEquals(2, extra.map { it.text }.toSet().size)
        }
    }

    @Test
    fun `slot groups follow the syllables`() {
        assertEquals(listOf(2, 2), ReadingGames.slotGroups(word("ca-să")))
        assertEquals(listOf(2, 1, 2), ReadingGames.slotGroups(word("ra-che-tă")))
        val w = word("în-ghe-ța-tă")
        assertEquals(w.units.size, ReadingGames.slotGroups(w).sum())
    }

    @Test
    fun `placing the right tiles solves the board`() {
        val w = word("be-be")
        var board = ReadingGames.spellBoard(w, 2, Random(3))
        // Equal letters are interchangeable: place any tile with the needed text.
        board.target.forEach { needed ->
            val tile = board.tray.first { it.text == needed }
            board = board.place(tile.id)
        }
        assertTrue(board.isFull)
        assertTrue(board.isSolved)
        assertEquals(2, board.tray.size)
    }

    @Test
    fun `wrong tiles are found and go back to the tray`() {
        val w = word("ca-să")
        var board = ReadingGames.spellBoard(w, 0, Random(5))
        // Put the tiles in the order c, a, ă, s  (last two swapped)
        listOf("c", "a", "ă", "s").forEach { text ->
            board = board.place(board.tray.first { it.text == text }.id)
        }
        assertTrue(board.isFull)
        assertFalse(board.isSolved)
        assertEquals(setOf(2, 3), board.wrongSlots)
        board = board.clearWrong()
        assertEquals(listOf("c", "a"), listOf(board.tileAt(0)?.text, board.tileAt(1)?.text))
        assertEquals(null, board.tileAt(2))
        assertEquals(2, board.tray.size)
    }

    @Test
    fun `removing a tile frees its slot and the next tile fills it`() {
        val w = word("lu-nă")
        var board = ReadingGames.spellBoard(w, 0, Random(7))
        val first = board.tray.first()
        board = board.place(first.id)
        board = board.remove(0)
        assertEquals(null, board.tileAt(0))
        board = board.place(board.tray.last().id)
        assertTrue(board.tileAt(0) != null)
    }

    @Test
    fun `hint fills the next correct letter and locks it`() {
        val w = word("ma-re")
        var board = ReadingGames.spellBoard(w, 2, Random(9))
        board = board.hint()
        assertEquals("m", board.tileAt(0)?.text)
        assertTrue(0 in board.locked)
        assertEquals(board, board.remove(0)) // hint letters stay
        board = board.hint()
        assertEquals("a", board.tileAt(1)?.text)
    }

    @Test
    fun `hint takes the needed letter out of a wrong slot`() {
        val w = word("ca-să")
        var board = ReadingGames.spellBoard(w, 0, Random(11))
        listOf("s", "ă").forEach { text -> board = board.place(board.tray.first { it.text == text }.id) }
        // slots: s, ă, _, _  -> first hint must put "c" in slot 0
        board = board.hint()
        assertEquals("c", board.tileAt(0)?.text)
        board = board.hint()
        assertEquals("a", board.tileAt(1)?.text)
        board = board.hint()
        board = board.hint()
        assertTrue(board.isSolved)
    }

    @Test
    fun `every trainer word can be solved with hints only`() {
        ReadingContent.trainerLevels.flatMap { it.words }.forEach { tw ->
            var board = ReadingGames.spellBoard(tw.word, 2, Random(tw.marked.hashCode()))
            repeat(board.target.size) { board = board.hint() }
            assertTrue(tw.marked, board.isSolved)
        }
    }

    // --- Syllable split ------------------------------------------------------

    @Test
    fun `cut positions follow syllable borders`() {
        assertEquals(setOf(2), ReadingGames.cutPositions(word("ca-să")))
        assertEquals(setOf(2, 4), ReadingGames.cutPositions(word("pi-si-că")))
        assertEquals(setOf(3), ReadingGames.cutPositions(word("car-te")))
        assertEquals(emptySet<Int>(), ReadingGames.cutPositions(word("tren")))
        assertEquals(setOf(2, 5, 7), ReadingGames.cutPositions(word("în-ghe-ța-tă")))
    }

    @Test
    fun `cut positions are inside the word`() {
        (ReadingContent.trainerLevels.flatMap { it.words } + ReadingGames.oneSyllableWords).forEach { tw ->
            val cuts = ReadingGames.cutPositions(tw.word)
            assertEquals(tw.marked, tw.word.syllables.size - 1, cuts.size)
            cuts.forEach { assertTrue(tw.marked, it in 1 until tw.word.text.length) }
        }
    }

    @Test
    fun `checking cuts reports wrong and missing ones`() {
        val w = word("pi-si-că")
        assertTrue(ReadingGames.checkCuts(w, setOf(2, 4)).isCorrect)
        val check = ReadingGames.checkCuts(w, setOf(1, 4))
        assertFalse(check.isCorrect)
        assertEquals(setOf(1), check.wrong)
        assertEquals(setOf(2), check.missing)
        assertTrue(ReadingGames.checkCuts(word("urs"), emptySet()).isCorrect)
        assertEquals(setOf(1), ReadingGames.checkCuts(word("urs"), setOf(1)).wrong)
    }

    @Test
    fun `letters map to their syllable`() {
        val w = word("pi-si-că")
        assertEquals(listOf(0, 0, 1, 1, 2, 2), (0 until 6).map { ReadingGames.syllableOfLetter(w, it) })
    }

    @Test
    fun `syllable rounds always contain a one-syllable word`() {
        ReadingContent.trainerLevels.forEach { level ->
            repeat(10) { seed ->
                val round = ReadingGames.syllableRound(level, Random(seed))
                assertEquals(ReadingGames.ROUND_SIZE, round.size)
                assertTrue(round.any { it.word.syllables.size == 1 })
            }
        }
        ReadingGames.oneSyllableWords.forEach { assertEquals(1, it.word.syllables.size) }
    }

    // --- Confusable letters --------------------------------------------------

    private val allConfusableTexts: Set<String> by lazy {
        (ReadingGames.confusableSets.flatMap { set -> set.words.map { it.text } } +
            ReadingContent.trainerLevels.flatMap { l -> l.words.map { it.word.text } }).toSet()
    }

    @Test
    fun `the nine sets exist`() {
        assertEquals(
            listOf("b_d", "p_b", "m_n", "s_sh", "t_tz", "a_ah", "i_ih", "d_t", "f_v"),
            ReadingGames.confusableSets.map { it.id }
        )
        assertEquals(9, ReadingGames.confusableSets.map { it.id }.toSet().size)
    }

    @Test
    fun `each set has at least six words with emoji`() {
        ReadingGames.confusableSets.forEach { set ->
            assertTrue(set.id, set.words.size >= 6)
            assertEquals(set.id, set.words.size, set.words.map { it.text }.toSet().size)
            set.words.forEach { assertTrue(it.text, it.emoji.isNotBlank()) }
            // both letters are practised
            assertTrue(set.id, set.words.any { it.answer == set.first })
            assertTrue(set.id, set.words.any { it.answer == set.second })
        }
    }

    @Test
    fun `missing letter is one of the set letters`() {
        ReadingGames.confusableSets.forEach { set ->
            set.words.forEach { w ->
                assertTrue(w.text, w.index in w.text.indices)
                assertTrue("${w.text} in ${set.id}", w.text[w.index] in set.letters)
                assertEquals(w.text, w.withLetter(w.answer))
            }
        }
    }

    @Test
    fun `the other letter does not make the same or another known word`() {
        ReadingGames.confusableSets.forEach { set ->
            set.words.forEach { w ->
                val swapped = w.withLetter(set.other(w.answer))
                assertNotEquals(w.text, swapped)
                assertFalse("$swapped (${set.id})", swapped in allConfusableTexts)
            }
        }
    }

    @Test
    fun `marked syllables join into the word and use comma-below diacritics`() {
        ReadingGames.confusableSets.flatMap { it.words }.forEach { w ->
            assertEquals(w.text, w.word.text)
            assertFalse(w.text, w.text.any { it == 'ş' || it == 'ţ' || it == 'Ş' || it == 'Ţ' })
            assertTrue(w.text, w.text.all { it.isLetter() && it.isLowerCase() })
        }
    }

    @Test
    fun `letter grid holds three to five targets`() {
        ReadingGames.confusableSets.forEach { set ->
            set.letters.forEach { target ->
                repeat(10) { seed ->
                    val grid = ReadingGames.letterGrid(set, target, Random(seed))
                    assertEquals(12, grid.size)
                    assertTrue(grid.count { it == target } in 3..5)
                    assertTrue(grid.contains(set.other(target)))
                }
            }
        }
    }

    @Test
    fun `round alternates the two task types`() {
        val set = ReadingGames.confusableSets.first()
        val round = ReadingGames.confusableRound(set, Random(2))
        assertEquals(ReadingGames.ROUND_SIZE, round.size)
        assertTrue(round[0] is ConfusableTask.Complete)
        assertTrue(round[1] is ConfusableTask.Find)
        val completes = round.filterIsInstance<ConfusableTask.Complete>()
        assertEquals(completes.size, completes.map { it.word }.toSet().size)
        completes.forEach { assertEquals(set.letters.toSet(), it.choices.toSet()) }
    }
}
