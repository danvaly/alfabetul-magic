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
    fun `library has sixteen everyday stories spread over three levels`() {
        val stories = ReadingContent.stories
        assertEquals(stories.size, stories.map { it.id }.toSet().size)
        assertEquals(stories.size, stories.map { it.title }.toSet().size)
        assertTrue(stories.all { it.level in 1..3 })
        val everyday = stories.filter { it.category == StoryCategory.EVERYDAY }
        assertTrue("expected at least 16 everyday stories, got ${everyday.size}", everyday.size >= 16)
        val byLevel = everyday.groupingBy { it.level }.eachCount()
        assertTrue("level 1: ${byLevel[1]}", (byLevel[1] ?: 0) >= 5)
        assertTrue("level 2: ${byLevel[2]}", (byLevel[2] ?: 0) >= 5)
        assertTrue("level 3: ${byLevel[3]}", (byLevel[3] ?: 0) >= 5)
        stories.forEach { assertEquals(it, ReadingContent.getStory(it.id)) }
    }

    private fun assertBeginnerStory(story: Story, pageRange: IntRange) {
        val id = story.id
        assertTrue("$id: ${story.pages.size} pages, expected $pageRange", story.pages.size in pageRange)
        assertEquals("$id: 3 questions", 3, story.questions.size)
        story.pages.forEach { page ->
            assertTrue("$id: '${page.markedText}' has more than 8 words", page.words.size <= 8)
            assertTrue("$id: one sentence per page", page.markedText.count { it == '.' } <= 1)
            assertTrue("$id: no actors on '${page.markedText}'", page.scene.actors.isNotEmpty())
            page.words.forEach { word ->
                word.syllables.forEach { syllable ->
                    assertTrue(
                        "$id: '$syllable' in '${page.markedText}' must be letters with a vowel",
                        syllable.isNotEmpty() && syllable.all { it.isLetter() } &&
                            syllable.any { ReadingText.isVowel(it) }
                    )
                }
            }
            page.scene.actors.forEach { actor ->
                assertTrue("$id: actor size ${actor.size}", actor.size in 0.1f..0.45f)
                assertTrue("$id: actor position", actor.x in 0f..1f && actor.y in 0f..1f)
            }
        }
        story.questions.forEach { question ->
            assertEquals("$id: '${question.question}' needs 3 choices", 3, question.choices.size)
            assertEquals("$id: '${question.question}' choices must differ", 3, question.choices.map { it.label }.toSet().size)
        }
    }

    @Test
    fun `classic tales follow the beginner format`() {
        val classics = ReadingContent.stories.filter { it.category == StoryCategory.CLASSIC }
        assertTrue("expected at least 12 classics, got ${classics.size}", classics.size >= 12)
        assertEquals(classics.size, classics.map { it.id }.toSet().size)
        assertEquals(classics.size, classics.map { it.title }.toSet().size)
        classics.forEach { assertBeginnerStory(it, 8..12) }
        val byLevel = classics.groupingBy { it.level }.eachCount()
        (1..3).forEach { level ->
            assertTrue("classics on level $level: ${byLevel[level]}", (byLevel[level] ?: 0) >= 3)
        }
    }

    @Test
    fun `holiday stories follow the beginner format`() {
        val holidays = ReadingContent.stories.filter { it.category == StoryCategory.HOLIDAY }
        assertTrue("expected at least 8 holiday stories, got ${holidays.size}", holidays.size >= 8)
        assertEquals(holidays.size, holidays.map { it.id }.toSet().size)
        holidays.forEach { assertBeginnerStory(it, 6..9) }
        assertEquals(setOf(1, 2, 3), holidays.map { it.level }.toSet())
    }

    @Test
    fun `classic and holiday texts avoid hyphenated words and dialogue dashes`() {
        ReadingContent.stories.filter { it.category != StoryCategory.EVERYDAY }.forEach { story ->
            story.pages.forEach { page ->
                // every hyphen must sit between two letters of one word (a syllable mark)
                Regex("-").findAll(page.markedText).forEach { match ->
                    val i = match.range.first
                    assertTrue(
                        "${story.id}: stray dash in '${page.markedText}'",
                        i > 0 && i < page.markedText.lastIndex &&
                            page.markedText[i - 1].isLetter() && page.markedText[i + 1].isLetter()
                    )
                }
                // "într-o" would pass the vowel check, so look for it explicitly
                assertTrue(
                    "${story.id}: hyphenated word in '${page.markedText}'",
                    !ReadingText.plain(page.words).lowercase().contains("întro")
                )
            }
        }
    }

    @Test
    fun `new stories follow the beginner format`() {
        val newIds = listOf(
            "rata_mica", "tortul_radu", "balonul_inei", "trenul",
            "pestele_mic", "ariciul_marul", "dragonul_fricos", "prima_zi_scoala"
        )
        newIds.forEach { id ->
            val story = ReadingContent.getStory(id)
            assertTrue("missing story $id", story != null)
            story!!
            assertTrue("$id: 5-7 pages", story.pages.size in 5..7)
            assertEquals("$id: 3 questions", 3, story.questions.size)
            story.pages.forEach { page ->
                assertTrue("$id: one sentence per page", page.markedText.count { it == '.' } <= 1)
                page.scene.actors.forEach { actor ->
                    assertTrue("$id: actor size ${actor.size}", actor.size in 0.1f..0.45f)
                }
            }
        }
    }

    @Test
    fun `syllables never contain separators or spaces`() {
        ReadingContent.stories.flatMap { it.pages }.flatMap { it.words }.forEach { word ->
            assertTrue(word.syllables.none { it.contains('-') || it.isBlank() })
            assertTrue("'${word.text}' joins back", word.text == word.syllables.joinToString(""))
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
