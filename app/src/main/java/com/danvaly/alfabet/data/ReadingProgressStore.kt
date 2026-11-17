package com.danvaly.alfabet.data

import android.content.Context
import android.content.SharedPreferences

/** Minimal key/value storage, so the store can run on SharedPreferences or in memory (unit tests). */
interface KeyValueStore {
    fun getInt(key: String, default: Int): Int
    fun putInt(key: String, value: Int)
    fun getBoolean(key: String, default: Boolean): Boolean
    fun putBoolean(key: String, value: Boolean)
    fun getString(key: String, default: String): String
    fun putString(key: String, value: String)
}

class SharedPrefsStore(private val prefs: SharedPreferences) : KeyValueStore {
    override fun getInt(key: String, default: Int): Int = prefs.getInt(key, default)
    override fun putInt(key: String, value: Int) = prefs.edit().putInt(key, value).apply()
    override fun getBoolean(key: String, default: Boolean): Boolean = prefs.getBoolean(key, default)
    override fun putBoolean(key: String, value: Boolean) = prefs.edit().putBoolean(key, value).apply()
    override fun getString(key: String, default: String): String = prefs.getString(key, default) ?: default
    override fun putString(key: String, value: String) = prefs.edit().putString(key, value).apply()
}

class InMemoryStore : KeyValueStore {
    private val values = mutableMapOf<String, Any>()
    override fun getInt(key: String, default: Int): Int = values[key] as? Int ?: default
    override fun putInt(key: String, value: Int) { values[key] = value }
    override fun getBoolean(key: String, default: Boolean): Boolean = values[key] as? Boolean ?: default
    override fun putBoolean(key: String, value: Boolean) { values[key] = value }
    override fun getString(key: String, default: String): String = values[key] as? String ?: default
    override fun putString(key: String, value: String) { values[key] = value }
}

/** How often a word was looked up, read wrong or read right. Keyed by the marked word, e.g. "pi-si-că". */
data class WordStat(val marked: String, val lookups: Int, val mistakes: Int, val successes: Int) {
    val difficulty: Int get() = mistakes * 2 + lookups - successes * 2
    val isHard: Boolean get() = difficulty > 0
}

/** The word in lowercase with its syllables joined by "-", e.g. "pi-si-că". */
fun markedOf(word: ReadingWord): String = word.syllables.joinToString("-").lowercase()

/** Sticker emoji, given out in order; the list cycles when the child has earned them all. */
object StickerCatalog {
    val all: List<String> = listOf(
        "🦋", "🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼", "🐨",
        "🐯", "🦁", "🐮", "🐷", "🐸", "🐵", "🐔", "🐧", "🐤", "🦆",
        "🦉", "🐴", "🦄", "🐝", "🐞", "🐢", "🐙", "🐠", "🐬", "🐳",
        "🦀", "🐘", "🐺", "🐑", "🐐", "🍎", "🍊", "🍋", "🍌", "🍉",
        "🍇", "🍓", "🍒", "🍑", "🍍", "🥝", "🥕", "🌽", "🍄", "🌻",
        "🌷", "🌹", "🌸", "🌈", "🎈", "🎁", "🚀", "🚂", "⚽", "🎨",
        "🍦", "🍩", "🍪", "🎂", "🍭", "🐌"
    )

    fun forIndex(index: Int): String = all[((index % all.size) + all.size) % all.size]
}

/** Small on-device store for reading stars, word stats, the daily goal, stickers and reading preferences. */
class ReadingProgressStore(private val kv: KeyValueStore) {

    constructor(context: Context, prefsName: String = DEFAULT_PREFS) : this(
        SharedPrefsStore(context.applicationContext.getSharedPreferences(prefsName, Context.MODE_PRIVATE))
    )

    // --- Stars ---------------------------------------------------------------

    fun storyStars(storyId: String): Int = kv.getInt("story_$storyId", 0)

    /** Keeps the best result, so re-reading never loses stars. */
    fun saveStoryStars(storyId: String, stars: Int) {
        if (stars > storyStars(storyId)) kv.putInt("story_$storyId", stars)
    }

    fun levelStars(levelId: Int): Int = kv.getInt("level_$levelId", 0)

    fun saveLevelStars(levelId: Int, stars: Int) {
        if (stars > levelStars(levelId)) kv.putInt("level_$levelId", stars)
    }

    fun gameStars(key: String): Int = kv.getInt("game_$key", 0)

    /** Keeps the best result for a reading game (e.g. "spell_word_1"). */
    fun saveGameStars(key: String, stars: Int) {
        if (stars <= gameStars(key)) return
        kv.putInt("game_$key", stars)
        val keys = gameKeys()
        if (clean(key) !in keys) kv.putString(KEY_GAME_KEYS, (keys + clean(key)).joinToString("\n"))
    }

    private fun gameKeys(): List<String> =
        kv.getString(KEY_GAME_KEYS, "").split("\n").filter { it.isNotEmpty() }

    /** Number of stories read to the end (at least one star). */
    fun storiesRead(): Int = ReadingContent.stories.count { storyStars(it.id) > 0 }

    /** Stars from stories, trainer levels and the reading games together. */
    fun totalReadingStars(): Int =
        ReadingContent.stories.sumOf { storyStars(it.id) } +
            ReadingContent.trainerLevels.sumOf { levelStars(it.id) } +
            gameKeys().sumOf { gameStars(it) }

    // --- Word stats ----------------------------------------------------------

    fun recordLookup(marked: String) = updateWord(marked) { it.copy(lookups = it.lookups + 1) }

    fun recordMistake(marked: String) = updateWord(marked) { it.copy(mistakes = it.mistakes + 1) }

    fun recordSuccess(marked: String) = updateWord(marked) { it.copy(successes = it.successes + 1) }

    fun wordStat(marked: String): WordStat? {
        val key = normalize(marked)
        return allWordStats().firstOrNull { it.marked == key }
    }

    /** Words the child still struggles with, hardest first. */
    fun hardWords(limit: Int = 20): List<WordStat> =
        allWordStats()
            .filter { it.isHard }
            .sortedWith(compareByDescending<WordStat> { it.difficulty }.thenBy { it.marked })
            .take(limit)

    fun allWordStats(): List<WordStat> = decodeWordStats(kv.getString(KEY_WORDS, ""))

    private fun updateWord(marked: String, change: (WordStat) -> WordStat) {
        val key = normalize(marked)
        if (key.isEmpty()) return
        val stats = allWordStats().toMutableList()
        val index = stats.indexOfFirst { it.marked == key }
        if (index >= 0) {
            stats[index] = change(stats[index])
        } else {
            stats += change(WordStat(key, 0, 0, 0))
        }
        kv.putString(KEY_WORDS, encodeWordStats(stats))
    }

    // --- Daily goal ----------------------------------------------------------

    var dailyGoalMinutes: Int
        get() = kv.getInt(KEY_GOAL, DEFAULT_GOAL)
        set(value) = kv.putInt(KEY_GOAL, value.coerceAtLeast(1))

    /** Minutes played on [date] (ISO "yyyy-MM-dd"). */
    fun minutesOn(date: String): Int = kv.getInt("min_$date", 0)

    fun addMinute(date: String) = kv.putInt("min_$date", minutesOn(date) + 1)

    fun goalReached(date: String): Boolean = minutesOn(date) >= dailyGoalMinutes

    // --- Stickers ------------------------------------------------------------

    /** Earned sticker emoji, in the order they were earned. */
    fun stickers(): List<String> = stickerEntries().map { it.second }

    /**
     * Gives a sticker for [reason] (a unique key such as "day_2026-11-17" or "story_ana").
     * Returns the new sticker, or null when that reason was already rewarded.
     */
    fun awardSticker(reason: String): String? {
        val key = clean(reason)
        val entries = stickerEntries()
        if (entries.any { it.first == key }) return null
        val sticker = StickerCatalog.forIndex(entries.size)
        val updated = entries + (key to sticker)
        kv.putString(KEY_STICKERS, updated.joinToString("\n") { "${it.first}|${it.second}" })
        return sticker
    }

    private fun stickerEntries(): List<Pair<String, String>> =
        kv.getString(KEY_STICKERS, "").split("\n").mapNotNull { line ->
            val cut = line.indexOf('|')
            if (cut <= 0 || cut == line.lastIndex) null else line.substring(0, cut) to line.substring(cut + 1)
        }

    // --- Reading preferences -------------------------------------------------

    var showSyllables: Boolean
        get() = kv.getBoolean("show_syllables", true)
        set(value) = kv.putBoolean("show_syllables", value)

    var uppercase: Boolean
        get() = kv.getBoolean("uppercase", false)
        set(value) = kv.putBoolean("uppercase", value)

    var slowVoice: Boolean
        get() = kv.getBoolean("slow_voice", false)
        set(value) = kv.putBoolean("slow_voice", value)

    companion object {
        const val DEFAULT_PREFS = "reading_progress"
        const val DEFAULT_GOAL = 10
        private const val KEY_WORDS = "word_stats"
        private const val KEY_STICKERS = "stickers"
        private const val KEY_GOAL = "daily_goal_minutes"
        private const val KEY_GAME_KEYS = "game_keys"

        private fun clean(text: String): String = text.replace('|', '_').replace('\n', '_').trim()

        private fun normalize(marked: String): String = clean(marked).lowercase()

        /** One word per line: "marked|lookups|mistakes|successes". */
        fun encodeWordStats(stats: List<WordStat>): String =
            stats.joinToString("\n") { "${it.marked}|${it.lookups}|${it.mistakes}|${it.successes}" }

        fun decodeWordStats(text: String): List<WordStat> =
            text.split("\n").mapNotNull { line ->
                val parts = line.split("|")
                if (parts.size != 4 || parts[0].isEmpty()) return@mapNotNull null
                val lookups = parts[1].toIntOrNull() ?: return@mapNotNull null
                val mistakes = parts[2].toIntOrNull() ?: return@mapNotNull null
                val successes = parts[3].toIntOrNull() ?: return@mapNotNull null
                WordStat(parts[0], lookups, mistakes, successes)
            }
    }
}
