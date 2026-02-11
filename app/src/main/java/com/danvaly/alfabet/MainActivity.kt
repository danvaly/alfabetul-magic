package com.danvaly.alfabet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.danvaly.alfabet.audio.SoundManager
import com.danvaly.alfabet.data.local.AppDatabase
import com.danvaly.alfabet.data.repository.ProgressRepository
import com.danvaly.alfabet.navigation.AlfabetNavGraph
import com.danvaly.alfabet.ui.theme.AlfabetulMagicTheme
import com.danvaly.alfabet.ui.theme.BackgroundCream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val soundManager = remember { SoundManager(this) }
            val database = remember { AppDatabase.getInstance(this) }
            val repository = remember { ProgressRepository(database.progressDao()) }
            val navController = rememberNavController()

            DisposableEffect(Unit) {
                onDispose {
                    soundManager.shutdown()
                }
            }

            AlfabetulMagicTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BackgroundCream
                ) {
                    AlfabetNavGraph(
                        navController = navController,
                        soundManager = soundManager,
                        repository = repository
                    )
                }
            }
        }
    }
}
