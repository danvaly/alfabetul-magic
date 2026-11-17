package com.danvaly.alfabet.data

import com.danvaly.alfabet.data.model.LetterProgress
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgressMergeTest {

    private val stored = LetterProgress(
        letter = "M",
        learnCompleted = true,
        traceStars = 3,
        quizCorrect = 4,
        syllableCompleted = true,
        miniGamePlayed = true,
        lastPlayedTimestamp = 10L,
        wordMatchStars = 2,
        diacriticStars = 1
    )

    @Test
    fun `new letter starts from defaults`() {
        val p = ProgressMerge.traceStars(null, "A", 2, 99L)
        assertEquals("A", p.letter)
        assertTrue(p.learnCompleted)
        assertEquals(2, p.traceStars)
        assertEquals(0, p.wordMatchStars)
        assertEquals(99L, p.lastPlayedTimestamp)
    }

    @Test
    fun `trace stars keep other columns and best value`() {
        val lower = ProgressMerge.traceStars(stored, "M", 1, 20L)
        assertEquals(stored.copy(lastPlayedTimestamp = 20L), lower)
        val higher = ProgressMerge.traceStars(stored.copy(traceStars = 1), "M", 3, 20L)
        assertEquals(3, higher.traceStars)
        assertEquals(4, higher.quizCorrect)
        assertEquals(2, higher.wordMatchStars)
    }

    @Test
    fun `word match and diacritic stars keep the best`() {
        assertEquals(3, ProgressMerge.wordMatchStars(stored, "M", 3, 1L).wordMatchStars)
        assertEquals(2, ProgressMerge.wordMatchStars(stored, "M", 0, 1L).wordMatchStars)
        assertEquals(1, ProgressMerge.diacriticStars(stored, "M", 0, 1L).diacriticStars)
        assertEquals(3, ProgressMerge.diacriticStars(stored, "M", 3, 1L).traceStars)
    }

    @Test
    fun `quiz correct increments stored value`() {
        assertEquals(5, ProgressMerge.quizCorrect(stored, "M", 1L).quizCorrect)
        assertEquals(1, ProgressMerge.quizCorrect(null, "M", 1L).quizCorrect)
    }

    @Test
    fun `marking learned keeps stars`() {
        val p = ProgressMerge.learned(stored.copy(learnCompleted = false), "M", 5L)
        assertTrue(p.learnCompleted)
        assertEquals(3, p.traceStars)
        assertEquals(true, p.syllableCompleted)
    }

    @Test
    fun `streak same day is unchanged`() {
        assertEquals(4, nextStreak("2026-11-17", "2026-11-17", 4))
        assertEquals(1, nextStreak("2026-11-17", "2026-11-17", 0))
    }

    @Test
    fun `streak grows on consecutive days`() {
        assertEquals(5, nextStreak("2026-11-16", "2026-11-17", 4))
        assertEquals(2, nextStreak("2026-12-31", "2027-01-01", 1))
        assertEquals(2, nextStreak("2028-02-28", "2028-02-29", 1))
    }

    @Test
    fun `streak resets after a gap or bad data`() {
        assertEquals(1, nextStreak("2026-11-10", "2026-11-17", 7))
        assertEquals(1, nextStreak("", "2026-11-17", 3))
        assertEquals(1, nextStreak("garbage", "2026-11-17", 3))
        assertEquals(1, nextStreak("2026-11-20", "2026-11-17", 3))
    }
}
