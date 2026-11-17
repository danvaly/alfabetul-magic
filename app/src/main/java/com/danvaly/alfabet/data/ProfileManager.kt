package com.danvaly.alfabet.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.danvaly.alfabet.data.local.AppDatabase

/** One child using the app. Each profile has its own Room file and its own reading prefs. */
data class Profile(val id: String, val name: String, val avatar: String) {
    val dbName: String get() = ProfileManager.dbNameFor(id)
    val prefsName: String get() = ProfileManager.prefsNameFor(id)
}

/**
 * Keeps the list of profiles and which one is active, in its own prefs file "profiles".
 * The list and the active id are snapshot state, so Compose recomposes when they change.
 */
class ProfileManager(context: Context) {

    private val appContext = context.applicationContext
    private val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private var profileList by mutableStateOf(decode(prefs.getString(KEY_LIST, null)))
    private var currentId by mutableStateOf(prefs.getString(KEY_ACTIVE, DEFAULT_ID) ?: DEFAULT_ID)

    val profiles: List<Profile> get() = profileList

    val activeId: String
        get() = if (profileList.any { it.id == currentId }) currentId else DEFAULT_ID

    val active: Profile
        get() = profileList.firstOrNull { it.id == activeId } ?: profileList.first()

    fun setActive(id: String) {
        if (profileList.none { it.id == id }) return
        currentId = id
        prefs.edit().putString(KEY_ACTIVE, id).apply()
    }

    fun add(name: String, avatar: String): Profile {
        val profile = Profile(newId(profileList.map { it.id }), cleanName(name, "Copil"), avatar.ifBlank { AVATARS.first() })
        save(profileList + profile)
        return profile
    }

    fun rename(id: String, name: String, avatar: String) {
        save(profileList.map {
            if (it.id == id) it.copy(name = cleanName(name, it.name), avatar = avatar.ifBlank { it.avatar }) else it
        })
    }

    /** Removes a profile and deletes its progress. The default profile and the last profile are never removed. */
    fun remove(id: String) {
        if (id == DEFAULT_ID || profileList.size <= 1) return
        val profile = profileList.firstOrNull { it.id == id } ?: return
        if (activeId == id) setActive(DEFAULT_ID)
        save(profileList.filter { it.id != id })
        AppDatabase.closeInstance(profile.dbName)
        appContext.deleteDatabase(profile.dbName)
        appContext.deleteSharedPreferences(profile.prefsName)
    }

    private fun save(list: List<Profile>) {
        profileList = list
        prefs.edit().putString(KEY_LIST, encode(list)).apply()
    }

    companion object {
        const val PREFS_NAME = "profiles"
        const val DEFAULT_ID = "default"
        private const val KEY_LIST = "list"
        private const val KEY_ACTIVE = "active"

        val AVATARS = listOf("🦉", "🐰", "🐻", "🦊", "🐱", "🐶", "🐼", "🦄")

        val DEFAULT_PROFILE = Profile(DEFAULT_ID, "Copil", "🦉")

        /** The default profile keeps the legacy names, so progress from older versions survives. */
        fun dbNameFor(id: String): String =
            if (id == DEFAULT_ID) "alfabet_magic.db" else "alfabet_magic_$id.db"

        fun prefsNameFor(id: String): String =
            if (id == DEFAULT_ID) ReadingProgressStore.DEFAULT_PREFS else "reading_progress_$id"

        private fun cleanName(name: String, fallback: String): String =
            name.replace('|', ' ').replace('\n', ' ').trim().take(20).ifEmpty { fallback }

        /** One profile per line: "id|name|avatar". */
        fun encode(list: List<Profile>): String =
            list.joinToString("\n") { "${it.id}|${cleanName(it.name, "Copil")}|${it.avatar}" }

        /** Parses [encode]'s output; always returns a list that starts with the default profile. */
        fun decode(text: String?): List<Profile> {
            val parsed = text.orEmpty().split("\n").mapNotNull { line ->
                val parts = line.split("|")
                if (parts.size != 3 || parts[0].isBlank()) null
                else Profile(parts[0].trim(), parts[1].ifBlank { "Copil" }, parts[2].ifBlank { AVATARS.first() })
            }.distinctBy { it.id }
            val default = parsed.firstOrNull { it.id == DEFAULT_ID } ?: DEFAULT_PROFILE
            return listOf(default) + parsed.filter { it.id != DEFAULT_ID }
        }

        /** Short ids made of letters and digits only, so they are safe in file names. */
        fun newId(existing: List<String>): String {
            var n = existing.size
            while (true) {
                val id = "p$n"
                if (id !in existing) return id
                n++
            }
        }
    }
}
