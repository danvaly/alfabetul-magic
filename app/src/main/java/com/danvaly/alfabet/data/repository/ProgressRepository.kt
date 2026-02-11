package com.danvaly.alfabet.data.repository

import com.danvaly.alfabet.data.local.ProgressDao
import com.danvaly.alfabet.data.model.AppStats
import com.danvaly.alfabet.data.model.LetterProgress
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class ProgressRepository(private val dao: ProgressDao) {

    val allProgress: Flow<List<LetterProgress>> = dao.getAllProgress()
    val learnedCount: Flow<Int> = dao.getLearnedCount()
    val totalStars: Flow<Int?> = dao.getTotalStars()
    val appStats: Flow<AppStats?> = dao.getAppStats()

    fun getLetterProgress(letter: String): Flow<LetterProgress?> =
        dao.getLetterProgress(letter)

    suspend fun markLetterLearned(letter: String) {
        val existing = LetterProgress(
            letter = letter,
            learnCompleted = true,
            lastPlayedTimestamp = System.currentTimeMillis()
        )
        dao.upsertProgress(existing)
    }

    suspend fun updateTraceStars(letter: String, stars: Int) {
        dao.upsertProgress(
            LetterProgress(
                letter = letter,
                learnCompleted = true,
                traceStars = stars,
                lastPlayedTimestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun incrementQuizCorrect(letter: String, currentCorrect: Int) {
        dao.upsertProgress(
            LetterProgress(
                letter = letter,
                learnCompleted = true,
                quizCorrect = currentCorrect + 1,
                lastPlayedTimestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun onGamePlayed(starsEarned: Int) {
        ensureStatsExist()
        dao.incrementGamesPlayed()
        if (starsEarned > 0) {
            dao.addStars(starsEarned)
        }
        updateStreak()
    }

    private suspend fun ensureStatsExist() {
        dao.upsertAppStats(
            AppStats(
                id = 1,
                lastPlayDate = LocalDate.now().toString()
            )
        )
    }

    private suspend fun updateStreak() {
        val today = LocalDate.now().toString()
        dao.upsertAppStats(AppStats(id = 1, lastPlayDate = today))
    }
}
