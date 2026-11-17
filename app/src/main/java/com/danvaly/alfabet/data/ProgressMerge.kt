package com.danvaly.alfabet.data

import com.danvaly.alfabet.data.model.LetterProgress
import java.time.LocalDate
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit

/**
 * Pure helpers that merge a new result into the stored progress of a letter,
 * so one game never wipes what another game saved.
 */
object ProgressMerge {

    private fun base(existing: LetterProgress?, letter: String, now: Long): LetterProgress =
        (existing ?: LetterProgress(letter = letter)).copy(
            learnCompleted = true,
            lastPlayedTimestamp = now
        )

    fun learned(existing: LetterProgress?, letter: String, now: Long): LetterProgress =
        base(existing, letter, now)

    fun traceStars(existing: LetterProgress?, letter: String, stars: Int, now: Long): LetterProgress {
        val b = base(existing, letter, now)
        return b.copy(traceStars = maxOf(b.traceStars, stars))
    }

    fun wordMatchStars(existing: LetterProgress?, letter: String, stars: Int, now: Long): LetterProgress {
        val b = base(existing, letter, now)
        return b.copy(wordMatchStars = maxOf(b.wordMatchStars, stars))
    }

    fun diacriticStars(existing: LetterProgress?, letter: String, stars: Int, now: Long): LetterProgress {
        val b = base(existing, letter, now)
        return b.copy(diacriticStars = maxOf(b.diacriticStars, stars))
    }

    fun quizCorrect(existing: LetterProgress?, letter: String, now: Long): LetterProgress {
        val b = base(existing, letter, now)
        return b.copy(quizCorrect = b.quizCorrect + 1)
    }
}

/**
 * Next value of the daily streak when the app is opened on [today]
 * (ISO dates, "yyyy-MM-dd"). Same day: unchanged (at least 1);
 * the day after [lastPlayDate]: +1; anything else (older, empty, unparsable): 1.
 */
fun nextStreak(lastPlayDate: String, today: String, current: Int): Int {
    val last = parseDate(lastPlayDate) ?: return 1
    val now = parseDate(today) ?: return maxOf(current, 1)
    return when (ChronoUnit.DAYS.between(last, now)) {
        0L -> maxOf(current, 1)
        1L -> maxOf(current, 0) + 1
        else -> 1
    }
}

private fun parseDate(value: String): LocalDate? =
    if (value.isBlank()) null
    else try {
        LocalDate.parse(value)
    } catch (e: DateTimeParseException) {
        null
    }
