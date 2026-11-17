package com.danvaly.alfabet.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ReadingProgressStoreTest {

    private lateinit var kv: InMemoryStore
    private lateinit var store: ReadingProgressStore

    @Before
    fun setUp() {
        kv = InMemoryStore()
        store = ReadingProgressStore(kv)
    }

    // --- Stars -----------------------------------------------------------------

    @Test
    fun `story and level stars keep the best result`() {
        store.saveStoryStars("ana", 2)
        store.saveStoryStars("ana", 1)
        assertEquals(2, store.storyStars("ana"))
        store.saveStoryStars("ana", 3)
        assertEquals(3, store.storyStars("ana"))

        store.saveLevelStars(1, 3)
        store.saveLevelStars(1, 0)
        assertEquals(3, store.levelStars(1))
        assertEquals(0, store.levelStars(2))
    }

    @Test
    fun `game stars keep the best result`() {
        assertEquals(0, store.gameStars("spell_word"))
        store.saveGameStars("spell_word", 2)
        store.saveGameStars("spell_word", 1)
        assertEquals(2, store.gameStars("spell_word"))
        store.saveGameStars("spell_word", 3)
        assertEquals(3, store.gameStars("spell_word"))
    }

    @Test
    fun `totals count stories, trainer levels and games`() {
        assertEquals(0, store.storiesRead())
        assertEquals(0, store.totalReadingStars())

        val story = ReadingContent.stories.first()
        val level = ReadingContent.trainerLevels.first()
        store.saveStoryStars(story.id, 2)
        store.saveLevelStars(level.id, 3)
        store.saveGameStars("syllable_split", 1)
        store.saveGameStars("syllable_split", 2)
        store.saveGameStars("spell_word", 1)

        assertEquals(1, store.storiesRead())
        assertEquals(2 + 3 + 2 + 1, store.totalReadingStars())
    }

    @Test
    fun `stars survive a new store on the same storage`() {
        store.saveStoryStars("ana", 2)
        store.saveGameStars("hard", 1)
        val again = ReadingProgressStore(kv)
        assertEquals(2, again.storyStars("ana"))
        assertEquals(1, again.gameStars("hard"))
    }

    // --- Word stats ------------------------------------------------------------

    @Test
    fun `markedOf joins syllables in lowercase`() {
        assertEquals("pi-si-că", markedOf(ReadingText.parseWord("Pi-si-că")))
        assertEquals("ana", markedOf(ReadingText.parseWord("A-na").copy(syllables = listOf("Ana"))))
        assertEquals("a-na", markedOf(ReadingText.parseSentence("A-na are.").first()))
    }

    @Test
    fun `word stats are counted per lowercase word`() {
        store.recordLookup("Pi-si-că")
        store.recordLookup("pi-si-că")
        store.recordMistake("pi-si-că")
        store.recordSuccess("pi-si-că")

        val stat = store.wordStat("pi-si-că")
        assertNotNull(stat)
        assertEquals(WordStat("pi-si-că", lookups = 2, mistakes = 1, successes = 1), stat)
        assertNull(store.wordStat("ca-să"))
    }

    @Test
    fun `hard words are sorted hardest first`() {
        store.recordLookup("ca-să")                 // score 1
        repeat(3) { store.recordMistake("ur-sul") } // score 6
        store.recordMistake("mă-ră")                // score 2
        store.recordLookup("mă-ră")                 // score 3

        val hard = store.hardWords()
        assertEquals(listOf("ur-sul", "mă-ră", "ca-să"), hard.map { it.marked })
        assertEquals(listOf("ur-sul"), store.hardWords(limit = 1).map { it.marked })
    }

    @Test
    fun `a word drops out of the hard list after enough successes`() {
        store.recordMistake("ur-sul")
        store.recordLookup("ur-sul") // score 3
        assertEquals(listOf("ur-sul"), store.hardWords().map { it.marked })

        store.recordSuccess("ur-sul") // score 1
        assertEquals(1, store.hardWords().size)

        store.recordSuccess("ur-sul") // score -1
        assertTrue(store.hardWords().isEmpty())
        // The stats are still kept
        assertEquals(2, store.wordStat("ur-sul")?.successes)
    }

    @Test
    fun `words read right only are never hard`() {
        store.recordSuccess("ca-să")
        assertTrue(store.hardWords().isEmpty())
    }

    @Test
    fun `word stats encode and decode`() {
        val stats = listOf(WordStat("pi-si-că", 1, 2, 3), WordStat("ur-sul", 0, 4, 0))
        assertEquals(stats, ReadingProgressStore.decodeWordStats(ReadingProgressStore.encodeWordStats(stats)))
        assertTrue(ReadingProgressStore.decodeWordStats("").isEmpty())
        assertEquals(
            listOf(WordStat("ok", 1, 1, 1)),
            ReadingProgressStore.decodeWordStats("broken\nok|1|1|1\nbad|x|1|1")
        )
    }

    // --- Daily goal ------------------------------------------------------------

    @Test
    fun `daily goal defaults to ten minutes`() {
        assertEquals(10, store.dailyGoalMinutes)
        store.dailyGoalMinutes = 15
        assertEquals(15, store.dailyGoalMinutes)
    }

    @Test
    fun `goal is reached when enough minutes are played that day`() {
        store.dailyGoalMinutes = 5
        val day = "2026-11-17"
        repeat(4) { store.addMinute(day) }
        assertEquals(4, store.minutesOn(day))
        assertFalse(store.goalReached(day))

        store.addMinute(day)
        assertTrue(store.goalReached(day))

        // Another day starts from zero
        assertEquals(0, store.minutesOn("2026-11-18"))
        assertFalse(store.goalReached("2026-11-18"))
    }

    // --- Stickers --------------------------------------------------------------

    @Test
    fun `a sticker is awarded only once per reason`() {
        val first = store.awardSticker("day_2026-11-17")
        assertEquals(StickerCatalog.all[0], first)
        assertNull(store.awardSticker("day_2026-11-17"))

        val second = store.awardSticker("story_ana")
        assertEquals(StickerCatalog.all[1], second)
        assertNull(store.awardSticker("story_ana"))

        assertEquals(listOf(StickerCatalog.all[0], StickerCatalog.all[1]), store.stickers())
    }

    @Test
    fun `stickers survive a new store on the same storage`() {
        store.awardSticker("story_ana")
        val again = ReadingProgressStore(kv)
        assertEquals(1, again.stickers().size)
        assertNull(again.awardSticker("story_ana"))
    }

    @Test
    fun `sticker catalog is large and cycles when exhausted`() {
        val size = StickerCatalog.all.size
        assertTrue(size >= 48)
        repeat(size) { store.awardSticker("reason_$it") }
        assertEquals(StickerCatalog.all, store.stickers())

        val next = store.awardSticker("one_more")
        assertEquals(StickerCatalog.all[0], next)
        assertEquals(size + 1, store.stickers().size)
        assertEquals(StickerCatalog.all[1], StickerCatalog.forIndex(size + 1))
    }

    @Test
    fun `reasons with separators do not break the sticker list`() {
        assertNotNull(store.awardSticker("weird|reason\nhere"))
        assertNull(store.awardSticker("weird|reason\nhere"))
        assertEquals(1, store.stickers().size)
    }

    // --- Preferences -----------------------------------------------------------

    @Test
    fun `reading preferences keep their defaults and values`() {
        assertTrue(store.showSyllables)
        assertFalse(store.uppercase)
        assertFalse(store.slowVoice)
        store.showSyllables = false
        store.uppercase = true
        store.slowVoice = true
        assertFalse(store.showSyllables)
        assertTrue(store.uppercase)
        assertTrue(store.slowVoice)
    }
}
