package com.danvaly.alfabet.data

enum class DifficultyLevel(
    val label: String,
    val emoji: String,
    val speedMultiplier: Float,
    val optionCount: Int,
    val hintEnabled: Boolean
) {
    BEGINNER("Începător", "\uD83C\uDF31", 0.7f, 3, true),
    INTERMEDIATE("Intermediar", "\uD83C\uDF3F", 1.0f, 4, false),
    ADVANCED("Avansat", "\uD83C\uDF33", 1.3f, 5, false)
}

object DifficultyManager {
    fun fromInt(value: Int): DifficultyLevel = when (value) {
        1 -> DifficultyLevel.BEGINNER
        2 -> DifficultyLevel.INTERMEDIATE
        3 -> DifficultyLevel.ADVANCED
        else -> DifficultyLevel.BEGINNER
    }

    fun toInt(level: DifficultyLevel): Int = when (level) {
        DifficultyLevel.BEGINNER -> 1
        DifficultyLevel.INTERMEDIATE -> 2
        DifficultyLevel.ADVANCED -> 3
    }
}
