package com.danvaly.alfabet.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danvaly.alfabet.audio.SoundManager
import com.danvaly.alfabet.ui.theme.*

@Composable
fun HomeScreen(
    onNavigateToLearn: () -> Unit,
    onNavigateToTrace: () -> Unit,
    onNavigateToSyllable: () -> Unit,
    onNavigateToGames: () -> Unit,
    onNavigateToProgress: () -> Unit,
    soundManager: SoundManager
) {
    val titleScale = remember { Animatable(0.8f) }

    LaunchedEffect(Unit) {
        titleScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                text = "Alfabetul Magic",
                style = MaterialTheme.typography.displaySmall,
                color = PrimaryPink,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.scale(titleScale.value)
            )
            Text(
                text = "Invata alfabetul romanesc prin joaca!",
                style = MaterialTheme.typography.titleMedium,
                color = OnBackgroundLight,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            // Game mode cards in a 2x3 grid-like layout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
            ) {
                GameModeCard(
                    title = "Invata\nLiterele",
                    emoji = "📚",
                    icon = Icons.Filled.AutoStories,
                    backgroundColor = PastelColors[0],
                    accentColor = VowelColor,
                    onClick = onNavigateToLearn,
                    modifier = Modifier.weight(1f)
                )
                GameModeCard(
                    title = "Scrie\nLiterele",
                    emoji = "✍️",
                    icon = Icons.Filled.Draw,
                    backgroundColor = PastelColors[1],
                    accentColor = ConsonantColor,
                    onClick = onNavigateToTrace,
                    modifier = Modifier.weight(1f)
                )
                GameModeCard(
                    title = "Formeaza\nSilabe",
                    emoji = "🧩",
                    icon = Icons.Filled.Extension,
                    backgroundColor = PastelColors[3],
                    accentColor = CorrectGreen,
                    onClick = onNavigateToSyllable,
                    modifier = Modifier.weight(1f)
                )
                GameModeCard(
                    title = "Jocuri",
                    emoji = "🎮",
                    icon = Icons.Filled.SportsEsports,
                    backgroundColor = PastelColors[4],
                    accentColor = SecondaryPurple,
                    onClick = onNavigateToGames,
                    modifier = Modifier.weight(1f)
                )
                GameModeCard(
                    title = "Progresul\nMeu",
                    emoji = "🏆",
                    icon = Icons.Filled.EmojiEvents,
                    backgroundColor = PastelColors[2],
                    accentColor = StarGoldDark,
                    onClick = onNavigateToProgress,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun GameModeCard(
    title: String,
    emoji: String,
    icon: ImageVector,
    backgroundColor: Color,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = remember { Animatable(1f) }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            scale.animateTo(0.92f, spring(stiffness = Spring.StiffnessHigh))
        } else {
            scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
    }

    Card(
        modifier = modifier
            .height(220.dp)
            .scale(scale.value)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp, pressedElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = emoji,
                fontSize = 48.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = accentColor,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = OnBackground,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}
