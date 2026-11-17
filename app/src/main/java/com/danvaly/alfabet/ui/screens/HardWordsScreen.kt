package com.danvaly.alfabet.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.danvaly.alfabet.audio.SoundManager
import com.danvaly.alfabet.data.repository.ProgressRepository
import com.danvaly.alfabet.ui.theme.BackgroundCream
import com.danvaly.alfabet.ui.theme.OnBackground

/** Placeholder until the real "Cuvintele mele grele" screen lands. */
@Composable
fun HardWordsScreen(
    onBack: () -> Unit,
    soundManager: SoundManager,
    repository: ProgressRepository
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundCream)
            .systemBarsPadding()
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            RoundBackButton(onBack = onBack, tint = Color(0xFF3949AB))
        }
        Text(
            text = "💪 În curând",
            style = MaterialTheme.typography.headlineMedium,
            color = OnBackground,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
