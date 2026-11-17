package com.danvaly.alfabet.shots

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.danvaly.alfabet.audio.SoundManager
import com.danvaly.alfabet.data.InMemoryStore
import com.danvaly.alfabet.data.ProfileManager
import com.danvaly.alfabet.data.ReadingProgressStore
import com.danvaly.alfabet.data.local.AppDatabase
import com.danvaly.alfabet.data.repository.ProgressRepository
import com.danvaly.alfabet.ui.LocalProfileManager
import com.danvaly.alfabet.ui.LocalReadingStore
import com.danvaly.alfabet.ui.screens.ConfusableLettersScreen
import com.danvaly.alfabet.ui.screens.HardWordsScreen
import com.danvaly.alfabet.ui.screens.HomeScreen
import com.danvaly.alfabet.ui.screens.ParentDashboardScreen
import com.danvaly.alfabet.ui.screens.ProgressScreen
import com.danvaly.alfabet.ui.screens.ReadingHubScreen
import com.danvaly.alfabet.ui.screens.ReadingTrainerScreen
import com.danvaly.alfabet.ui.screens.SpellWordScreen
import com.danvaly.alfabet.ui.screens.StickerAlbumScreen
import com.danvaly.alfabet.ui.screens.StoriesScreen
import com.danvaly.alfabet.ui.screens.StoryReaderScreen
import com.danvaly.alfabet.ui.screens.SyllableScreen
import com.danvaly.alfabet.ui.screens.SyllableSplitScreen
import com.danvaly.alfabet.ui.screens.TraceScreen
import com.danvaly.alfabet.ui.theme.AlfabetulMagicTheme
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.time.LocalDate

/**
 * Renders the main screens on a small landscape phone (740 x 360 dp) and saves
 * them to app/build/shots/ so layouts can be checked without a device.
 * Run: gradle testDebugUnitTest --tests '*ScreenshotTest*'
 */
@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w740dp-h360dp-land-xhdpi")
class ScreenshotTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private val context get() = ApplicationProvider.getApplicationContext<android.content.Context>()

    private val store = ReadingProgressStore(InMemoryStore()).apply {
        saveStoryStars("ana_pisica", 3)
        recordMistake("pi-si-că")
        recordLookup("um-bre-lă")
        recordMistake("ghioz-dan")
        repeat(4) { addMinute(LocalDate.now().toString()) }
        awardSticker("a")
        awardSticker("b")
        awardSticker("c")
    }

    private fun shot(
        name: String,
        clicks: List<String> = emptyList(),
        content: @Composable (SoundManager, ProgressRepository) -> Unit
    ) {
        rule.mainClock.autoAdvance = false
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val repository = ProgressRepository(db.progressDao())
        val sound = SoundManager(context)
        val profiles = ProfileManager(context)
        rule.setContent {
            AlfabetulMagicTheme {
                CompositionLocalProvider(
                    LocalReadingStore provides store,
                    LocalProfileManager provides profiles
                ) {
                    content(sound, repository)
                }
            }
        }
        rule.mainClock.advanceTimeBy(1500)
        clicks.forEach { text ->
            rule.onAllNodesWithText(text, substring = true)[0].performClick()
            rule.mainClock.advanceTimeBy(1500)
        }
        rule.onRoot().captureRoboImage("build/shots/$name.png")
    }

    @Test fun home() = shot("01_home") { s, _ -> HomeScreen({}, {}, {}, {}, {}, {}, {}, {}, {}, {}, s) }
    @Test fun hub() = shot("02_citesc_hub") { _, _ -> ReadingHubScreen({}, {}, {}, {}, {}, {}, {}) }
    @Test fun stories() = shot("03_povesti") { _, _ -> StoriesScreen({}, {}) }
    @Test fun reader() = shot("04_poveste") { s, r -> StoryReaderScreen("ana_pisica", {}, s, r) }
    @Test fun readerLong() = shot("05_poveste_lunga") { s, r -> StoryReaderScreen("iepurasul", {}, s, r) }
    @Test fun readerCursive() =
        shot("06_poveste_de_mana", listOf("de mână")) { s, r -> StoryReaderScreen("prima_zi_scoala", {}, s, r) }
    @Test fun trainerLevels() = shot("07_pas_cu_pas_niveluri") { s, r -> ReadingTrainerScreen({}, s, r) }
    @Test fun trainer() = shot("08_pas_cu_pas", listOf("Cuvinte lungi")) { s, r -> ReadingTrainerScreen({}, s, r) }
    @Test fun spell() = shot("09_scrie_cuvantul", listOf("Cuvinte lungi")) { s, r -> SpellWordScreen({}, s, r) }
    @Test fun split() = shot("10_desparte_in_silabe", listOf("Cuvinte lungi")) { s, r -> SyllableSplitScreen({}, s, r) }
    @Test fun confusable() = shot("11_litere_care_seamana") { s, r -> ConfusableLettersScreen({}, s, r) }
    @Test fun hardWords() = shot("12_cuvinte_grele") { s, r -> HardWordsScreen({}, s, r) }
    @Test fun stickers() = shot("13_abtibilduri") { _, _ -> StickerAlbumScreen({}) }
    @Test fun trace() = shot("14_scrie_litera") { s, r -> TraceScreen("A", {}, s, r) }
    @Test fun progress() = shot("15_progres") { _, r -> ProgressScreen({}, r) }
    @Test fun parent() = shot("16_panou_parinti") { _, r -> ParentDashboardScreen({}, r) }
    @Test fun syllable() = shot("17_formeaza_silabe") { s, r -> SyllableScreen({}, s, r) }
}
