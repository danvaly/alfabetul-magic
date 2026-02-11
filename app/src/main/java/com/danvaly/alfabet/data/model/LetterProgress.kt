package com.danvaly.alfabet.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "letter_progress")
data class LetterProgress(
    @PrimaryKey val letter: String,
    val learnCompleted: Boolean = false,
    val traceStars: Int = 0,
    val quizCorrect: Int = 0,
    val syllableCompleted: Boolean = false,
    val miniGamePlayed: Boolean = false,
    val lastPlayedTimestamp: Long = 0L
)

@Entity(tableName = "app_stats")
data class AppStats(
    @PrimaryKey val id: Int = 1,
    val totalStars: Int = 0,
    val gamesPlayed: Int = 0,
    val currentStreak: Int = 0,
    val lastPlayDate: String = "",
    val balloonsPopped: Int = 0,
    val matchesCompleted: Int = 0
)
