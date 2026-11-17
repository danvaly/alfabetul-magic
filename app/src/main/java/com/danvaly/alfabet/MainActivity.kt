package com.danvaly.alfabet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.rememberNavController
import com.danvaly.alfabet.audio.SoundManager
import com.danvaly.alfabet.data.ProfileManager
import com.danvaly.alfabet.data.ReadingProgressStore
import com.danvaly.alfabet.data.local.AppDatabase
import com.danvaly.alfabet.data.repository.ProgressRepository
import com.danvaly.alfabet.navigation.AlfabetNavGraph
import com.danvaly.alfabet.ui.LocalProfileManager
import com.danvaly.alfabet.ui.LocalReadingStore
import com.danvaly.alfabet.ui.theme.AlfabetulMagicTheme
import com.danvaly.alfabet.ui.theme.BackgroundCream
import kotlinx.coroutines.delay
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val appContext = applicationContext
            val soundManager = remember { SoundManager(this) }
            val profileManager = remember { ProfileManager(appContext) }
            val lifecycleOwner = LocalLifecycleOwner.current

            DisposableEffect(Unit) {
                onDispose {
                    soundManager.shutdown()
                }
            }

            // Snapshot state inside ProfileManager: changing the profile recomposes from here.
            val activeId = profileManager.activeId

            AlfabetulMagicTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BackgroundCream
                ) {
                    // Everything below belongs to one child: switching profile rebuilds it all.
                    key(activeId) {
                        val database = remember {
                            AppDatabase.getInstance(appContext, ProfileManager.dbNameFor(activeId))
                        }
                        val repository = remember { ProgressRepository(database.progressDao()) }
                        val store = remember {
                            ReadingProgressStore(appContext, ProfileManager.prefsNameFor(activeId))
                        }
                        val navController = rememberNavController()

                        LaunchedEffect(Unit) {
                            repository.onAppOpened()
                        }

                        // Count play time while the app is in front of the child.
                        LaunchedEffect(lifecycleOwner) {
                            lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                                while (true) {
                                    delay(60_000L)
                                    val today = LocalDate.now().toString()
                                    repository.addPlayTime(1)
                                    store.addMinute(today)
                                    if (store.goalReached(today)) {
                                        // Returns null when today's sticker was already given.
                                        store.awardSticker("day_$today")
                                    }
                                }
                            }
                        }

                        CompositionLocalProvider(
                            LocalReadingStore provides store,
                            LocalProfileManager provides profileManager
                        ) {
                            AlfabetNavGraph(
                                navController = navController,
                                soundManager = soundManager,
                                repository = repository,
                                onProfileSelected = { id -> profileManager.setActive(id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
