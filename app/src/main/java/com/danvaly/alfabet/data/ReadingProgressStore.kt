package com.danvaly.alfabet.data

import android.content.Context

/** Small on-device store for reading stars and the child's reading preferences. */
class ReadingProgressStore(context: Context) {

    private val prefs =
        context.applicationContext.getSharedPreferences("reading_progress", Context.MODE_PRIVATE)

    fun storyStars(storyId: String): Int = prefs.getInt("story_$storyId", 0)

    /** Keeps the best result, so re-reading never loses stars. */
    fun saveStoryStars(storyId: String, stars: Int) {
        if (stars > storyStars(storyId)) prefs.edit().putInt("story_$storyId", stars).apply()
    }

    fun levelStars(levelId: Int): Int = prefs.getInt("level_$levelId", 0)

    fun saveLevelStars(levelId: Int, stars: Int) {
        if (stars > levelStars(levelId)) prefs.edit().putInt("level_$levelId", stars).apply()
    }

    var showSyllables: Boolean
        get() = prefs.getBoolean("show_syllables", true)
        set(value) = prefs.edit().putBoolean("show_syllables", value).apply()

    var uppercase: Boolean
        get() = prefs.getBoolean("uppercase", false)
        set(value) = prefs.edit().putBoolean("uppercase", value).apply()

    var slowVoice: Boolean
        get() = prefs.getBoolean("slow_voice", false)
        set(value) = prefs.edit().putBoolean("slow_voice", value).apply()
}
