package com.danvaly.alfabet.data

import com.danvaly.alfabet.data.model.AppStats
import com.danvaly.alfabet.data.model.LetterProgress
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LetterProgressTest {

    @Test
    fun `default LetterProgress has no progress`() {
        val progress = LetterProgress(letter = "A")
        assertFalse(progress.learnCompleted)
        assertEquals(0, progress.traceStars)
        assertEquals(0, progress.quizCorrect)
        assertFalse(progress.syllableCompleted)
        assertFalse(progress.miniGamePlayed)
        assertEquals(0L, progress.lastPlayedTimestamp)
    }

    @Test
    fun `LetterProgress can be updated`() {
        val progress = LetterProgress(
            letter = "B",
            learnCompleted = true,
            traceStars = 3,
            quizCorrect = 5
        )
        assertTrue(progress.learnCompleted)
        assertEquals(3, progress.traceStars)
        assertEquals(5, progress.quizCorrect)
    }

    @Test
    fun `default AppStats are zeroed`() {
        val stats = AppStats()
        assertEquals(1, stats.id)
        assertEquals(0, stats.totalStars)
        assertEquals(0, stats.gamesPlayed)
        assertEquals(0, stats.currentStreak)
        assertEquals("", stats.lastPlayDate)
    }
}
