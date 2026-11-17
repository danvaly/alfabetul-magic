package com.danvaly.alfabet.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReadingMatcherTest {

    private fun words(marked: String): List<String> =
        ReadingText.parseSentence(marked).map { it.text }

    @Test
    fun `normalize drops case punctuation and diacritics`() {
        assertEquals("pisica", ReadingMatcher.normalize("Pisică."))
        assertEquals("catel", ReadingMatcher.normalize("cățel,"))
        assertEquals("catel", ReadingMatcher.normalize("căţel")) // cedilla form
        assertEquals("rade", ReadingMatcher.normalize("râde!"))
        assertEquals("inger", ReadingMatcher.normalize("Înger"))
        assertEquals("sa", ReadingMatcher.normalize("s-a"))
        assertEquals("stiu", ReadingMatcher.normalize("ȘTIU"))
        assertEquals("stiu", ReadingMatcher.normalize("Ştiu"))
        assertEquals("", ReadingMatcher.normalize("?!"))
    }

    @Test
    fun `whole sentence read exactly`() {
        val expected = words("A-na a-re o pi-si-că.")
        assertEquals(setOf(0, 1, 2, 3), matchRead(expected, "Ana are o pisică"))
    }

    @Test
    fun `recogniser without diacritics still counts`() {
        val expected = words("Un că-țel ve-de min-gea.")
        assertEquals(setOf(0, 1, 2, 3), matchRead(expected, "un catel vede mingea"))
    }

    @Test
    fun `missing words are not marked`() {
        val expected = words("Bu-ni-ca a-re gă-ini și ra-țe.")
        assertEquals(setOf(0, 1, 4), matchRead(expected, "bunica are rate"))
    }

    @Test
    fun `empty or silent input marks nothing`() {
        val expected = words("Pi-si-ca e mi-că.")
        assertTrue(matchRead(expected, "").isEmpty())
        assertTrue(matchRead(expected, "   ").isEmpty())
        assertTrue(matchRead(emptyList(), "pisica").isEmpty())
    }

    @Test
    fun `word read in syllables is joined`() {
        val expected = words("A-na a-re o pi-si-că.")
        assertEquals(setOf(0, 1, 2, 3), matchRead(expected, "ana are o pi si ca"))
        assertEquals(setOf(3), matchRead(expected, "pisi ca"))
    }

    @Test
    fun `glued words are split`() {
        val expected = words("A-na se joa-că cu pi-si-ca.")
        assertEquals(setOf(0, 1, 2, 3, 4), matchRead(expected, "Ana sejoacă cu pisica"))
        assertEquals(setOf(0, 1, 2, 3, 4), matchRead(expected, "Ana se joaca cupisica"))
    }

    @Test
    fun `one heard token never marks a word twice`() {
        val expected = words("Pi-si-ca și pi-si-ca.")
        assertEquals(setOf(0), matchRead(expected, "pisica"))
        assertEquals(setOf(0, 2), matchRead(expected, "pisica pisica"))
        assertEquals(setOf(0, 1, 2), matchRead(expected, "pisica si pisica"))
    }

    @Test
    fun `repeated word picks the next occurrence after the last match`() {
        val expected = listOf("ana", "vede", "pe", "ana")
        assertEquals(setOf(1, 3), matchRead(expected, "vede ana"))
        assertEquals(setOf(0, 3), matchRead(expected, "ana ana"))
        val long = listOf("mama", "are", "mere", "si", "mama", "are", "pere")
        assertEquals(long.indices.toSet(), matchRead(long, "mama are mere si mama are pere"))
    }

    @Test
    fun `order does not matter`() {
        val expected = words("Ni-cu a-re o min-ge.")
        assertEquals(setOf(0, 1, 3), matchRead(expected, "minge are Nicu"))
    }

    @Test
    fun `small slip in a long word is tolerated but short words must match`() {
        val expected = words("Mi-ra mer-ge la bu-nici.")
        assertEquals(setOf(0, 1, 2, 3), matchRead(expected, "Mira merge la bunicii"))
        // "le" is not "la": short words are not guessed
        assertEquals(setOf(0, 1, 3), matchRead(expected, "mira merge le bunici"))
        // a different long word is not accepted
        assertEquals(setOf(0, 2), matchRead(expected, "mira alearga la"))
    }

    @Test
    fun `extra words and punctuation from the recogniser are ignored`() {
        val expected = words("Ce bu-ne sunt!")
        assertEquals(setOf(0, 1, 2), matchRead(expected, "Ce bune sunt! Mmm, ce bune."))
    }

    @Test
    fun `hyphenated recogniser words`() {
        val expected = listOf("s-a", "dus", "acasă")
        assertEquals(setOf(0, 1, 2), matchRead(expected, "sa dus acasa"))
        assertEquals(setOf(0, 1, 2), matchRead(expected, "s-a dus acasă"))
    }

    @Test
    fun `every story page read aloud plainly is fully matched`() {
        ReadingContent.stories.forEach { story ->
            story.pages.forEach { page ->
                val expected = page.words.map { it.text }
                val heard = ReadingText.plain(page.words)
                assertEquals(
                    "${story.id}: $heard",
                    expected.indices.filter { ReadingMatcher.normalize(expected[it]).isNotEmpty() }.toSet(),
                    matchRead(expected, heard)
                )
            }
        }
    }

    @Test
    fun `every story page heard without diacritics is fully matched`() {
        ReadingContent.stories.forEach { story ->
            story.pages.forEach { page ->
                val expected = page.words.map { it.text }
                val heard = expected.joinToString(" ") { ReadingMatcher.normalize(it) }
                assertEquals(expected.indices.toSet(), matchRead(expected, heard))
            }
        }
    }
}
