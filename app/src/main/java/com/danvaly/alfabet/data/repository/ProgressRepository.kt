package com.danvaly.alfabet.data.repository

import com.danvaly.alfabet.data.ProgressMerge
import com.danvaly.alfabet.data.local.ProgressDao
import com.danvaly.alfabet.data.nextStreak
import com.danvaly.alfabet.data.model.AppStats
import com.danvaly.alfabet.data.model.LetterProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDate

class ProgressRepository(private val dao: ProgressDao) {

    val allProgress: Flow<List<LetterProgress>> = dao.getAllProgress()
    val learnedCount: Flow<Int> = dao.getLearnedCount()
    val totalStars: Flow<Int?> = dao.getTotalStars()
    val appStats: Flow<AppStats?> = dao.getAppStats()
    val allStarsTotal: Flow<Int?> = dao.getAllStarsTotal()

    fun getLetterProgress(letter: String): Flow<LetterProgress?> =
        dao.getLetterProgress(letter)

    private suspend fun stored(letter: String): LetterProgress? =
        dao.getLetterProgress(letter).firstOrNull()

    suspend fun markLetterLearned(letter: String) {
        dao.upsertProgress(ProgressMerge.learned(stored(letter), letter, System.currentTimeMillis()))
    }

    suspend fun updateTraceStars(letter: String, stars: Int) {
        dao.upsertProgress(
            ProgressMerge.traceStars(stored(letter), letter, stars, System.currentTimeMillis())
        )
    }

    suspend fun updateWordMatchStars(letter: String, stars: Int) {
        dao.upsertProgress(
            ProgressMerge.wordMatchStars(stored(letter), letter, stars, System.currentTimeMillis())
        )
    }

    suspend fun updateDiacriticStars(letter: String, stars: Int) {
        dao.upsertProgress(
            ProgressMerge.diacriticStars(stored(letter), letter, stars, System.currentTimeMillis())
        )
    }

    /**
     * Counts one more correct quiz answer for [letter]. The count is taken from the
     * database; [currentCorrect] is kept only for signature compatibility.
     */
    @Suppress("UNUSED_PARAMETER")
    suspend fun incrementQuizCorrect(letter: String, currentCorrect: Int) {
        dao.upsertProgress(ProgressMerge.quizCorrect(stored(letter), letter, System.currentTimeMillis()))
    }

    /** Call once per app start: records today's play date and updates the daily streak. */
    suspend fun onAppOpened() {
        ensureStatsExist()
        val stats = dao.getAppStats().firstOrNull()
        val today = LocalDate.now().toString()
        val streak = nextStreak(stats?.lastPlayDate ?: "", today, stats?.currentStreak ?: 0)
        dao.updateStreak(today, streak)
    }

    suspend fun onGamePlayed(starsEarned: Int) {
        ensureStatsExist()
        dao.incrementGamesPlayed()
        if (starsEarned > 0) {
            dao.addStars(starsEarned)
        }
    }

    suspend fun setDifficulty(difficulty: Int) {
        ensureStatsExist()
        dao.setDifficulty(difficulty)
    }

    suspend fun completeDailyChallenge() {
        ensureStatsExist()
        dao.updateDailyChallenge(LocalDate.now().toString(), true)
    }

    suspend fun isDailyChallengeCompleted(): Boolean {
        val stats = dao.getAppStats().firstOrNull() ?: return false
        return stats.dailyChallengeDate == LocalDate.now().toString() && stats.dailyChallengeCompleted
    }

    suspend fun addPlayTime(minutes: Int) {
        ensureStatsExist()
        dao.addPlayTime(minutes)
    }

    private suspend fun ensureStatsExist() {
        val existing = dao.getAppStats().firstOrNull()
        if (existing == null) {
            dao.upsertAppStats(
                AppStats(
                    id = 1,
                    lastPlayDate = LocalDate.now().toString()
                )
            )
        }
    }
}
