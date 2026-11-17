package com.danvaly.alfabet.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProfileManagerTest {

    @Test
    fun `empty storage gives only the default profile`() {
        val list = ProfileManager.decode(null)
        assertEquals(listOf(ProfileManager.DEFAULT_PROFILE), list)
        assertEquals(listOf(ProfileManager.DEFAULT_PROFILE), ProfileManager.decode(""))
    }

    @Test
    fun `profiles survive encode and decode`() {
        val list = listOf(
            Profile("default", "Ana", "🐰"),
            Profile("p1", "Mihai", "🦊"),
            Profile("p2", "Ioana Maria", "🐼")
        )
        assertEquals(list, ProfileManager.decode(ProfileManager.encode(list)))
    }

    @Test
    fun `default profile is always first and present`() {
        val decoded = ProfileManager.decode("p1|Mihai|🦊\ndefault|Ana|🐰")
        assertEquals(listOf("default", "p1"), decoded.map { it.id })
        assertEquals("Ana", decoded.first().name)

        val withoutDefault = ProfileManager.decode("p1|Mihai|🦊")
        assertEquals(listOf("default", "p1"), withoutDefault.map { it.id })
    }

    @Test
    fun `broken lines and duplicates are skipped`() {
        val decoded = ProfileManager.decode("p1|Mihai|🦊\ngarbage\np1|Altul|🐻\n|x|y")
        assertEquals(listOf("default", "p1"), decoded.map { it.id })
        assertEquals("Mihai", decoded[1].name)
    }

    @Test
    fun `names cannot break the format`() {
        val list = listOf(ProfileManager.DEFAULT_PROFILE, Profile("p1", "Mi|hai\nX", "🦊"))
        val decoded = ProfileManager.decode(ProfileManager.encode(list))
        assertEquals(2, decoded.size)
        assertFalse(decoded[1].name.contains("|"))
        assertFalse(decoded[1].name.contains("\n"))
    }

    @Test
    fun `default profile keeps the legacy storage names`() {
        assertEquals("alfabet_magic.db", ProfileManager.dbNameFor("default"))
        assertEquals("reading_progress", ProfileManager.prefsNameFor("default"))
        assertEquals("alfabet_magic_p1.db", ProfileManager.dbNameFor("p1"))
        assertEquals("reading_progress_p1", ProfileManager.prefsNameFor("p1"))
        assertEquals("alfabet_magic_p1.db", Profile("p1", "Mihai", "🦊").dbName)
    }

    @Test
    fun `new ids are unique and file-name safe`() {
        val id = ProfileManager.newId(listOf("default", "p1"))
        assertTrue(id !in listOf("default", "p1"))
        assertTrue(id.all { it.isLetterOrDigit() })
        assertEquals("p3", ProfileManager.newId(listOf("default", "p2")))
    }

    @Test
    fun `there are eight avatars`() {
        assertEquals(8, ProfileManager.AVATARS.size)
        assertEquals(8, ProfileManager.AVATARS.toSet().size)
    }
}
