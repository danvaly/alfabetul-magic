package com.danvaly.alfabet.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RomanianAlphabetTest {

    @Test
    fun `alphabet contains all 31 Romanian letters`() {
        assertEquals(31, RomanianAlphabet.letters.size)
    }

    @Test
    fun `all letters have uppercase and lowercase forms`() {
        RomanianAlphabet.letters.forEach { letter ->
            assertTrue(
                "Letter ${letter.uppercase} should have non-empty uppercase",
                letter.uppercase.isNotEmpty()
            )
            assertTrue(
                "Letter ${letter.lowercase} should have non-empty lowercase",
                letter.lowercase.isNotEmpty()
            )
        }
    }

    @Test
    fun `all letters have at least one example word`() {
        RomanianAlphabet.letters.forEach { letter ->
            assertTrue(
                "Letter ${letter.uppercase} should have at least one example word",
                letter.words.isNotEmpty()
            )
        }
    }

    @Test
    fun `all letters have phonetic sound defined`() {
        RomanianAlphabet.letters.forEach { letter ->
            assertTrue(
                "Letter ${letter.uppercase} should have a phonetic sound",
                letter.phoneticSound.isNotEmpty()
            )
        }
    }

    @Test
    fun `each example word has an emoji`() {
        RomanianAlphabet.letters.forEach { letter ->
            letter.words.forEach { word ->
                assertTrue(
                    "Word '${word.word}' for letter ${letter.uppercase} should have an emoji",
                    word.emoji.isNotEmpty()
                )
            }
        }
    }

    @Test
    fun `diacritics are correctly identified`() {
        val diacritics = RomanianAlphabet.diacritics.map { it.uppercase }
        assertTrue("Ă should be diacritic", "Ă" in diacritics)
        assertTrue("Â should be diacritic", "Â" in diacritics)
        assertTrue("Î should be diacritic", "Î" in diacritics)
        assertTrue("Ș should be diacritic", "Ș" in diacritics)
        assertTrue("Ț should be diacritic", "Ț" in diacritics)
        assertEquals(5, diacritics.size)
    }

    @Test
    fun `vowels are correctly identified`() {
        val vowels = RomanianAlphabet.vowels.map { it.uppercase }
        assertTrue("A should be vowel", "A" in vowels)
        assertTrue("Ă should be vowel", "Ă" in vowels)
        assertTrue("Â should be vowel", "Â" in vowels)
        assertTrue("E should be vowel", "E" in vowels)
        assertTrue("I should be vowel", "I" in vowels)
        assertTrue("Î should be vowel", "Î" in vowels)
        assertTrue("O should be vowel", "O" in vowels)
        assertTrue("U should be vowel", "U" in vowels)
    }

    @Test
    fun `consonants do not include vowels`() {
        val consonants = RomanianAlphabet.consonants
        consonants.forEach { letter ->
            assertTrue(
                "Letter ${letter.uppercase} marked as consonant should not be a vowel",
                !letter.isVowel
            )
        }
    }

    @Test
    fun `getLetterByUppercase returns correct letter`() {
        val letterA = RomanianAlphabet.getLetterByUppercase("A")
        assertNotNull(letterA)
        assertEquals("A", letterA?.uppercase)
        assertEquals("a", letterA?.lowercase)
        assertTrue(letterA?.isVowel == true)
    }

    @Test
    fun `getLetterByUppercase returns null for invalid letter`() {
        val result = RomanianAlphabet.getLetterByUppercase("Ø")
        assertEquals(null, result)
    }

    @Test
    fun `getRandomLetters excludes specified letter`() {
        repeat(20) {
            val randomLetters = RomanianAlphabet.getRandomLetters(3, "A")
            assertTrue(
                "Random letters should not contain excluded letter A",
                randomLetters.none { it.uppercase == "A" }
            )
            assertEquals(3, randomLetters.size)
        }
    }

    @Test
    fun `simple words have correct syllable decomposition`() {
        RomanianAlphabet.simpleWords.forEach { (word, syllables) ->
            assertTrue(
                "Word '$word' should have at least one syllable",
                syllables.isNotEmpty()
            )
            assertTrue(
                "Word '$word' syllables should reconstruct close to original",
                syllables.joinToString("").length <= word.length + 2
            )
        }
    }

    @Test
    fun `basic vowels list contains A E I O U`() {
        assertEquals(listOf("A", "E", "I", "O", "U"), RomanianAlphabet.basicVowels)
    }

    @Test
    fun `all letters have unique uppercase representation`() {
        val uppercases = RomanianAlphabet.letters.map { it.uppercase }
        assertEquals(uppercases.distinct().size, uppercases.size)
    }

    @Test
    fun `Romanian specific letters are present`() {
        val allUppercase = RomanianAlphabet.letters.map { it.uppercase }
        listOf("Ă", "Â", "Î", "Ș", "Ț").forEach { letter ->
            assertTrue(
                "Romanian letter $letter should be present",
                letter in allUppercase
            )
        }
    }
}
