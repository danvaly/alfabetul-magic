package com.danvaly.alfabet.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danvaly.alfabet.audio.SoundManager
import com.danvaly.alfabet.data.RomanianAlphabet
import com.danvaly.alfabet.ui.components.LetterCard
import com.danvaly.alfabet.ui.theme.*

@Composable
fun LearnScreen(
    onLetterClick: (String) -> Unit,
    onBack: () -> Unit,
    soundManager: SoundManager
) {
    val infiniteTransition = rememberInfiniteTransition(label = "learn_anim")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFE8F5E9), BackgroundCream, BackgroundLight)
                )
            )
            .systemBarsPadding()
    ) {
        // Decorative floating animals
        Text(
            text = "\uD83D\uDC1D",
            fontSize = 28.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-20).dp, y = (50 + floatOffset).dp)
        )
        Text(
            text = "\uD83E\uDD8B",
            fontSize = 24.sp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 15.dp, y = (-15 + floatOffset).dp)
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header with Duolingo-style
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(DuoGreen.copy(alpha = 0.15f))
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Înapoi",
                        tint = DuoGreen
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "\uD83D\uDC3B",
                    fontSize = 28.sp,
                    modifier = Modifier.offset(y = floatOffset.dp)
                )
                Text(
                    text = "Alfabetul românesc",
                    style = MaterialTheme.typography.headlineMedium,
                    color = DuoGreen,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Legend with Duolingo colors
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                LegendItem(color = VowelColor, label = "Vocale", emoji = "\uD83C\uDF39")
                LegendItem(color = ConsonantColor, label = "Consoane", emoji = "\uD83C\uDF3B")
                LegendItem(color = DiacriticColor, label = "Diacritice", emoji = "\uD83C\uDF38")
            }

            // Alphabet grid
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 90.dp),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(RomanianAlphabet.letters) { letter ->
                    LetterCard(
                        letter = letter,
                        onClick = {
                            soundManager.speakLetter(letter.uppercase)
                            onLetterClick(letter.uppercase)
                        },
                        isHighlighted = true,
                        size = 90.dp
                    )
                }
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String, emoji: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(text = emoji, fontSize = 14.sp)
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(color)
                    .padding(6.dp)
            )
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = OnBackground
            )
        }
    }
}
