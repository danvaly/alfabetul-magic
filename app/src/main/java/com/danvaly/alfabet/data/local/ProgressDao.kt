package com.danvaly.alfabet.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.danvaly.alfabet.data.model.AppStats
import com.danvaly.alfabet.data.model.LetterProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {

    @Query("SELECT * FROM letter_progress ORDER BY letter ASC")
    fun getAllProgress(): Flow<List<LetterProgress>>

    @Query("SELECT * FROM letter_progress WHERE letter = :letter")
    fun getLetterProgress(letter: String): Flow<LetterProgress?>

    @Query("SELECT COUNT(*) FROM letter_progress WHERE learnCompleted = 1")
    fun getLearnedCount(): Flow<Int>

    @Query("SELECT SUM(traceStars) FROM letter_progress")
    fun getTotalStars(): Flow<Int?>

    @Upsert
    suspend fun upsertProgress(progress: LetterProgress)

    @Query("SELECT * FROM app_stats WHERE id = 1")
    fun getAppStats(): Flow<AppStats?>

    @Upsert
    suspend fun upsertAppStats(stats: AppStats)

    @Query("UPDATE app_stats SET gamesPlayed = gamesPlayed + 1 WHERE id = 1")
    suspend fun incrementGamesPlayed()

    @Query("UPDATE app_stats SET totalStars = totalStars + :stars WHERE id = 1")
    suspend fun addStars(stars: Int)

    @Query("UPDATE app_stats SET difficulty = :difficulty WHERE id = 1")
    suspend fun setDifficulty(difficulty: Int)

    @Query("UPDATE app_stats SET dailyChallengeDate = :date, dailyChallengeCompleted = :completed WHERE id = 1")
    suspend fun updateDailyChallenge(date: String, completed: Boolean)

    @Query("UPDATE app_stats SET totalPlayTimeMinutes = totalPlayTimeMinutes + :minutes WHERE id = 1")
    suspend fun addPlayTime(minutes: Int)

    @Query("UPDATE app_stats SET lastPlayDate = :date, currentStreak = :streak WHERE id = 1")
    suspend fun updateStreak(date: String, streak: Int)

    @Query("SELECT SUM(traceStars) + SUM(wordMatchStars) + SUM(diacriticStars) FROM letter_progress")
    fun getAllStarsTotal(): Flow<Int?>
}
