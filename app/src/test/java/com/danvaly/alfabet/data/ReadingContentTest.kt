package com.danvaly.alfabet.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReadingContentTest {

    @Test
    fun `sentence is split into words and syllables`() {
        val words = ReadingText.parseSentence("A-na a-re o pi-si-că.")
        assertEquals(listOf("Ana", "are", "o", "pisică"), words.map { it.text })
        assertEquals(listOf("pi", "si", "că"), words.last().syllables)
        assertEquals(".", words.last().trailing)
        assertEquals("Ana are o pisică.", ReadingText.plain(words))
    }

    @Test
    fun `punctuation inside a sentence stays with its word`() {
        val words = ReadingText.parseSentence("Hop! Mor-co-vul ie-se, bâz!")
        assertEquals("Hop! Morcovul iese, bâz!", ReadingText.plain(words))
        assertEquals("!", words.first().trailing)
    }

    @Test
    fun `letter groups are kept together`() {
        assertEquals(listOf("che", "i", "e"), ReadingText.parseWord("che-ie").units.map { it.text })
        assertEquals(listOf("ghi", "o", "z", "d", "a", "n"), ReadingText.parseWord("ghioz-dan").units.map { it.text })
        assertEquals(listOf("m", "i", "n", "ge"), ReadingText.parseWord("min-ge").units.map { it.text })
        assertEquals(SoundKind.GROUP, ReadingText.parseWord("ceas").units.first().kind)
        // "c" before other letters is a plain consonant
        assertEquals(SoundKind.CONSONANT, ReadingText.parseWord("ca-să").units.first().kind)
    }

    @Test
    fun `vowels and consonants are counted`() {
        val word = ReadingText.parseWord("pi-si-că")
        assertEquals(6, word.letterCount)
        assertEquals(3, word.vowelCount)
        assertEquals(3, word.consonantCount)
        assertTrue(ReadingText.isVowel('Î'))
    }

    @Test
    fun `every story is well formed`() {
        val ids = ReadingContent.stories.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
        ReadingContent.stories.forEach { story ->
            assertTrue("${story.id} needs pages", story.pages.size >= 4)
            assertTrue("${story.id} needs questions", story.questions.size >= 2)
            story.pages.forEach { page ->
                assertTrue("${story.id}: empty page", page.words.isNotEmpty())
                assertTrue("${story.id}: page too long for a beginner", page.words.size <= 8)
                assertTrue("${story.id}: no actors", page.scene.actors.isNotEmpty())
                page.words.forEach { word ->
                    assertTrue("${story.id}: bad word in '${page.markedText}'", word.syllables.isNotEmpty())
                    word.syllables.forEach { syllable ->
                        assertTrue(
                            "${story.id}: '$syllable' in '${page.markedText}' must be letters with a vowel",
                            syllable.all { it.isLetter() } && syllable.any { ReadingText.isVowel(it) }
                        )
                    }
                }
                page.scene.actors.forEach { actor ->
                    assertTrue(actor.x in 0f..1f && actor.y in 0f..1f && actor.size in 0.05f..0.6f)
                }
            }
            story.questions.forEach { question ->
                assertEquals(3, question.choices.size)
                assertEquals(3, question.choices.map { it.label }.toSet().size)
            }
        }
    }

    @Test
    fun `trainer levels have enough distinct picture words`() {
        ReadingContent.trainerLevels.forEach { level ->
            assertTrue(level.words.size >= 8)
            assertEquals(level.words.size, level.words.map { it.emoji }.toSet().size)
            assertEquals(level.words.size, level.words.map { it.word.text }.toSet().size)
            level.words.forEach { word ->
                word.word.syllables.forEach { syllable ->
                    assertTrue(
                        "'$syllable' in ${word.marked}",
                        syllable.all { it.isLetter() } && syllable.any { ReadingText.isVowel(it) }
                    )
                }
            }
        }
        assertTrue(ReadingContent.getTrainerLevel(1)!!.words.all { it.word.syllables.size == 2 })
    }

    @Test
    fun `stars reward first try answers`() {
        assertEquals(3, ReadingContent.starsFor(6, 6))
        assertEquals(2, ReadingContent.starsFor(4, 6))
        assertEquals(1, ReadingContent.starsFor(2, 6))
        assertEquals(0, ReadingContent.starsFor(0, 0))
    }
}
