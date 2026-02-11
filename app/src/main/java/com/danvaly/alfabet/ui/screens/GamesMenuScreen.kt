package com.danvaly.alfabet.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
    val infiniteTransition = rememberInfiniteTransition(label = "games_anim")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )
    val floatOffset2 by infiniteTransition.animateFloat(
        initialValue = 5f,
        targetValue = -5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float2"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFF3E5F5), Color(0xFFE1BEE7), Color(0xFFF8F0FF))
                )
            )
    ) {
        // Floating decorations
        Text(
            text = "\uD83D\uDC3E",
            fontSize = 30.sp,
            modifier = Modifier
                .offset(x = 20.dp, y = (30 + floatOffset).dp)
        )
        Text(
            text = "\uD83C\uDF3A",
            fontSize = 26.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-25).dp, y = (35 + floatOffset2).dp)
        )
        Text(
            text = "\uD83D\uDC1D",
            fontSize = 28.sp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 30.dp, y = (-20 + floatOffset).dp)
        )
        Text(
            text = "\uD83C\uDF38",
            fontSize = 24.sp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-20).dp, y = (-15 + floatOffset2).dp)
        )

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
                        .background(DuoPurple.copy(alpha = 0.15f))
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Inapoi",
                        tint = DuoPurple
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "\uD83C\uDFAE",
                    fontSize = 28.sp,
                    modifier = Modifier.offset(y = floatOffset.dp)
                )
                Text(
                    text = "Jocuri",
                    style = MaterialTheme.typography.headlineMedium,
                    color = DuoPurple,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally)
            ) {
                // Balloon Pop
                GameCard3D(
                    emoji = "\uD83C\uDF88",
                    animal = "\uD83D\uDC3B",
                    title = "Sparge\nBaloanele",
                    description = "Atinge baloanele cu litera corecta",
                    backgroundColor = DuoRed,
                    shadowColor = Color(0xFFCC3333),
                    onClick = {
                        val randomLetter = RomanianAlphabet.letters.random().uppercase
                        onBalloonPop(randomLetter)
                    },
                    modifier = Modifier.weight(1f)
                )

                // Match Game
                GameCard3D(
                    emoji = "\uD83C\uDCCF",
                    animal = "\uD83D\uDC30",
                    title = "Potriveste\nLiterele",
                    description = "Gaseste perechile majuscula-minuscula",
                    backgroundColor = DuoPurple,
                    shadowColor = Color(0xFFA45ED6),
                    onClick = onMatchGame,
                    modifier = Modifier.weight(1f)
                )

                // Quiz
                GameCard3D(
                    emoji = "\uD83C\uDFB5",
                    animal = "\uD83E\uDD89",
                    title = "Ce Litera\nAuzi?",
                    description = "Asculta si alege litera corecta",
                    backgroundColor = DuoGreen,
                    shadowColor = DuoGreenDark,
                    onClick = onQuizGame,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun GameCard3D(
    emoji: String,
    animal: String,
    title: String,
    description: String,
    backgroundColor: Color,
    shadowColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = remember { Animatable(1f) }
    val infiniteTransition = rememberInfiniteTransition(label = "game_card_$title")
    val bounce by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce_$title"
    )

    LaunchedEffect(isPressed) {
        if (isPressed) {
            scale.animateTo(0.92f, spring(stiffness = Spring.StiffnessHigh))
        } else {
            scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
    }

    Box(
        modifier = modifier
            .height(280.dp)
            .scale(scale.value)
    ) {
        // 3D shadow
        Card(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = 6.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = shadowColor),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {}

        // Main card
        Card(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = if (isPressed) 4.dp else 0.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                ),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = backgroundColor),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = emoji,
                    fontSize = 56.sp,
                    modifier = Modifier.offset(y = bounce.dp)
                )
                Text(
                    text = animal,
                    fontSize = 24.sp,
                    modifier = Modifier.offset(x = 30.dp, y = (-10).dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 28.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
