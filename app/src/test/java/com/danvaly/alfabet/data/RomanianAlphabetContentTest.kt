package com.danvaly.alfabet.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RomanianAlphabetContentTest {

    private val consonants = listOf("B", "C", "D", "F", "G", "L", "M", "N", "P", "R", "S", "T", "V", "Z")
    private val vowels = listOf("A", "E", "I", "O", "U", "Ă")

    @Test
    fun `simple words are built from syllables offered in the syllable screen`() {
        RomanianAlphabet.simpleWords.forEach { (word, syllables) ->
            assertEquals("Syllables of $word", word, syllables.joinToString(""))
            syllables.forEach { syllable ->
                assertEquals("Syllable $syllable of $word", 2, syllable.length)
                assertTrue("$syllable starts with an offered consonant", syllable.substring(0, 1) in consonants)
                assertTrue("$syllable ends with an offered vowel", syllable.substring(1) in vowels)
            }
        }
    }

    @Test
    fun `replaced non-words are gone`() {
        val words = RomanianAlphabet.simpleWords.keys
        assertFalse("POLO" in words)
        assertFalse("RAME" in words)
        assertFalse("BILE" in words)
        assertTrue(RomanianAlphabet.rhymingPairs.none { it.first == "ver" || it.second == "ver" })
    }

    @Test
    fun `rhyming pairs end the same way`() {
        RomanianAlphabet.rhymingPairs.forEach { (a, b) ->
            assertTrue("$a / $b should rhyme", a.takeLast(2) == b.takeLast(2))
            assertFalse("$a / $b should be different words", a == b)
        }
    }

    @Test
    fun `example word syllables spell the word`() {
        RomanianAlphabet.letters.forEach { letter ->
            letter.words.forEach { word ->
                assertEquals(
                    "Syllables of ${word.word}",
                    word.word.lowercase(),
                    word.syllables.joinToString("").lowercase()
                )
            }
        }
    }
}
