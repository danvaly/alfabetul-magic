package com.danvaly.alfabet.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danvaly.alfabet.data.RomanianAlphabet
import com.danvaly.alfabet.ui.theme.*

@Composable
fun GamesMenuScreen(
    onBalloonPop: (String) -> Unit,
    onMatchGame: () -> Unit,
    onQuizGame: () -> Unit,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BackgroundCream, BackgroundLight, Color(0xFFFFF3E0))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(PrimaryPink.copy(alpha = 0.1f))
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Inapoi",
                        tint = PrimaryPink
                    )
                }
                Text(
                    text = "Jocuri",
                    style = MaterialTheme.typography.headlineMedium,
                    color = PrimaryPink,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally)
            ) {
                // Balloon Pop
                GameCard(
                    emoji = "🎈",
                    title = "Sparge\nBaloanele",
                    description = "Atinge baloanele cu litera corecta",
                    backgroundColor = BalloonRed.copy(alpha = 0.2f),
                    accentColor = BalloonRed,
                    onClick = {
                        val randomLetter = RomanianAlphabet.letters.random().uppercase
                        onBalloonPop(randomLetter)
                    },
                    modifier = Modifier.weight(1f)
                )

                // Match Game
                GameCard(
                    emoji = "🃏",
                    title = "Potriveste\nLiterele",
                    description = "Gaseste perechile majuscula-minuscula",
                    backgroundColor = SecondaryPurpleLight.copy(alpha = 0.2f),
                    accentColor = SecondaryPurple,
                    onClick = onMatchGame,
                    modifier = Modifier.weight(1f)
                )

                // Quiz
                GameCard(
                    emoji = "🎵",
                    title = "Ce Litera\nAuzi?",
                    description = "Asculta si alege litera corecta",
                    backgroundColor = CorrectGreenLight,
                    accentColor = CorrectGreen,
                    onClick = onQuizGame,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun GameCard(
    emoji: String,
    title: String,
    description: String,
    backgroundColor: Color,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(280.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = emoji, fontSize = 56.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = accentColor,
                textAlign = TextAlign.Center,
                lineHeight = 28.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = OnBackgroundLight,
                textAlign = TextAlign.Center
            )
        }
    }
}
