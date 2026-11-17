package com.danvaly.alfabet.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class EnglishContentTest {

    private val categories = EnglishContent.categories

    @Test
    fun hasTheExpectedCategories() {
        val ids = categories.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
        for (id in listOf("salutari", "culori", "numere", "animale", "familia", "scoala", "mancare", "corpul", "jucarii", "casa")) {
            assertNotNull("missing category $id", EnglishContent.category(id))
        }
        assertEquals(categories.size * 3, EnglishContent.maxStars)
    }

    @Test
    fun everyCategoryHasEightToTenWords() {
        for (category in categories) {
            assertTrue("${category.id} has ${category.words.size} words", category.words.size in 8..10)
        }
    }

    @Test
    fun wordsArePicturesAndTranslationsAreUniqueInEachCategory() {
        for (category in categories) {
            val words = category.words
            assertEquals("duplicate English in ${category.id}", words.size, words.map { it.en.lowercase() }.toSet().size)
            assertEquals("duplicate Romanian in ${category.id}", words.size, words.map { it.ro.lowercase() }.toSet().size)
            val pictures = words.map { it.colorHex?.toString() ?: it.emoji }
            assertEquals("duplicate picture in ${category.id}", words.size, pictures.toSet().size)
        }
    }

    @Test
    fun noEmptyFields() {
        for (category in categories) {
            assertTrue(category.titleRo.isNotBlank() && category.titleEn.isNotBlank() && category.emoji.isNotBlank())
            assertEquals("english_${category.id}", category.gameKey)
            for (word in category.words) {
                assertTrue("empty field in $word", word.en.isNotBlank() && word.ro.isNotBlank() && word.emoji.isNotBlank())
                assertTrue(word.spoken.isNotBlank())
            }
        }
    }

    @Test
    fun englishIsLowercaseExceptProperUsage() {
        for (word in categories.flatMap { it.words }) {
            if (word.en in EnglishContent.capitalized) continue
            assertEquals("'${word.en}' should be lowercase", word.en.lowercase(), word.en)
            assertTrue("'${word.en}' has odd characters", word.en.all { it.isLetter() || it == ' ' || it == '-' })
        }
    }

    @Test
    fun romanianUsesCommaBelowDiacritics() {
        for (word in categories.flatMap { it.words }) {
            assertTrue("cedilla in ${word.ro}", word.ro.none { it == 'ş' || it == 'ţ' || it == 'Ş' || it == 'Ţ' })
        }
        for (category in categories) {
            assertTrue(category.titleRo.none { it == 'ş' || it == 'ţ' })
        }
    }

    @Test
    fun colourSwatchesAreOnlyInColours() {
        for (category in categories) {
            val swatches = category.words.count { it.colorHex != null }
            if (category.id == "culori") assertTrue(swatches > 0) else assertEquals(0, swatches)
        }
        assertEquals("pink", EnglishContent.category("culori")!!.words.first { it.ro == "roz" }.en)
        assertEquals("My name is", EnglishContent.category("salutari")!!.words.last().spoken)
    }

    @Test
    fun roundMixesAllThreeTaskTypes() {
        for (seed in 0 until 30) {
            for (category in categories) {
                val round = EnglishQuiz.round(category, Random(seed))
                assertEquals(EnglishQuiz.ROUND_SIZE, round.size)
                assertEquals(EnglishTaskType.values().toSet(), round.map { it.type }.toSet())
                assertEquals("words repeat in a round", round.size, round.map { it.word.en }.toSet().size)
            }
        }
    }

    @Test
    fun correctAnswerIsAlwaysAnOptionAndOptionsAreDistinct() {
        for (seed in 0 until 30) {
            for (category in categories) {
                for (task in EnglishQuiz.round(category, Random(seed))) {
                    assertEquals(1, task.options.count { task.isCorrect(it) })
                    assertTrue(task.word in task.options)
                    assertEquals(task.options.size, task.options.map { it.en }.toSet().size)
                    assertEquals(task.options.size, task.options.map { it.ro }.toSet().size)
                    assertEquals(task.options.size, task.options.map { it.colorHex?.toString() ?: it.emoji }.toSet().size)
                    val expected = if (task.type == EnglishTaskType.SAY) EnglishQuiz.WORD_OPTIONS else EnglishQuiz.PICTURE_OPTIONS
                    assertEquals(expected, task.options.size)
                    assertTrue(task.options.all { it in category.words })
                }
            }
        }
    }

    @Test
    fun correctAnswerMovesAround() {
        val category = EnglishContent.category("animale")!!
        val positions = (0 until 40).map { seed ->
            val task = EnglishQuiz.task(EnglishTaskType.MEANING, category.words[0], category.words, Random(seed))
            task.options.indexOfFirst { task.isCorrect(it) }
        }
        assertTrue(positions.toSet().size > 1)
    }

    @Test
    fun roundWorksWithTinyCategories() {
        val tiny = EnglishCategory("t", "T", "T", "🙂", 0, EnglishContent.category("animale")!!.words.take(3))
        val round = EnglishQuiz.round(tiny, Random(1), size = 8)
        assertEquals(8, round.size)
        round.forEach { assertTrue(it.word in it.options) }
    }
}
