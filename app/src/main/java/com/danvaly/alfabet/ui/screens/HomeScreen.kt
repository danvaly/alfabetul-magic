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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
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
    onNavigateToDailyChallenge: () -> Unit,
    onNavigateToParentDashboard: () -> Unit,
    soundManager: SoundManager
) {
    val titleScale = remember { Animatable(0.3f) }
    val infiniteTransition = rememberInfiniteTransition(label = "home_anim")

    LaunchedEffect(Unit) {
        titleScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    val floatOffset2 by infiniteTransition.animateFloat(
        initialValue = 6f,
        targetValue = -6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float2"
    )

    val floatOffset3 by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float3"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE8F5E9),
                        Color(0xFFC8E6C9),
                        BackgroundCream
                    )
                )
            )
    ) {
        // Floating animals and flowers
        Text(
            text = "\uD83E\uDD8B",
            fontSize = 36.sp,
            modifier = Modifier.offset(x = 30.dp, y = (20 + floatOffset).dp)
        )
        Text(
            text = "\uD83C\uDF3B",
            fontSize = 32.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-40).dp, y = (25 + floatOffset2).dp)
        )
        Text(
            text = "\uD83D\uDC30",
            fontSize = 34.sp,
            modifier = Modifier.offset(x = 15.dp, y = (140 + floatOffset3).dp)
        )
        Text(
            text = "\uD83C\uDF3A",
            fontSize = 28.sp,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = (-20).dp, y = (-60 + floatOffset).dp)
        )
        Text(
            text = "\uD83D\uDC1E",
            fontSize = 28.sp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 25.dp, y = (-30 + floatOffset2).dp)
        )
        Text(
            text = "\uD83D\uDC22",
            fontSize = 30.sp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-35).dp, y = (-25 + floatOffset3).dp)
        )
        Text(
            text = "\uD83C\uDF37",
            fontSize = 26.sp,
            modifier = Modifier.offset(x = 50.dp, y = (260 + floatOffset).dp)
        )
        Text(
            text = "\uD83D\uDC26",
            fontSize = 28.sp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(x = 60.dp, y = (15 + floatOffset2).dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title with mascot owls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.scale(titleScale.value)
            ) {
                Text(
                    text = "\uD83E\uDD89",
                    fontSize = 44.sp,
                    modifier = Modifier.offset(y = floatOffset.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Alfabetul Magic",
                        style = MaterialTheme.typography.displaySmall,
                        color = DuoGreen,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Invata alfabetul romanesc prin joaca!",
                        style = MaterialTheme.typography.titleMedium,
                        color = OnBackgroundLight,
                        textAlign = TextAlign.Center,
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "\uD83E\uDD89",
                    fontSize = 44.sp,
                    modifier = Modifier.offset(y = floatOffset2.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main game mode cards - top row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterHorizontally)
            ) {
                GameModeCard(
                    title = "Invata\nLiterele",
                    emoji = "\uD83D\uDC3B",
                    icon = Icons.Filled.AutoStories,
                    backgroundColor = DuoGreen,
                    shadowColor = DuoGreenDark,
                    onClick = onNavigateToLearn,
                    modifier = Modifier.weight(1f)
                )
                GameModeCard(
                    title = "Scrie\nLiterele",
                    emoji = "\uD83D\uDC1D",
                    icon = Icons.Filled.Draw,
                    backgroundColor = DuoBlue,
                    shadowColor = DuoBlueDark,
                    onClick = onNavigateToTrace,
                    modifier = Modifier.weight(1f)
                )
                GameModeCard(
                    title = "Formeaza\nSilabe",
                    emoji = "\uD83E\uDD8B",
                    icon = Icons.Filled.Extension,
                    backgroundColor = DuoOrange,
                    shadowColor = Color(0xFFCC7800),
                    onClick = onNavigateToSyllable,
                    modifier = Modifier.weight(1f)
                )
                GameModeCard(
                    title = "Jocuri",
                    emoji = "\uD83D\uDC3E",
                    icon = Icons.Filled.SportsEsports,
                    backgroundColor = DuoPurple,
                    shadowColor = Color(0xFFA45ED6),
                    onClick = onNavigateToGames,
                    modifier = Modifier.weight(1f)
                )
                GameModeCard(
                    title = "Progresul\nMeu",
                    emoji = "\uD83C\uDF3B",
                    icon = Icons.Filled.EmojiEvents,
                    backgroundColor = DuoYellow,
                    shadowColor = Color(0xFFCCAD00),
                    onClick = onNavigateToProgress,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom row: Daily Challenge + Parent Dashboard
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterHorizontally)
            ) {
                GameModeCard(
                    title = "Provocarea\nZilei",
                    emoji = "\uD83C\uDF1F",
                    icon = Icons.Filled.Star,
                    backgroundColor = Color(0xFFFF6B6B),
                    shadowColor = Color(0xFFCC4444),
                    onClick = onNavigateToDailyChallenge,
                    modifier = Modifier.weight(1f),
                    cardHeight = 140
                )
                GameModeCard(
                    title = "Panou\nParinti",
                    emoji = "\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC67",
                    icon = Icons.Filled.Settings,
                    backgroundColor = Color(0xFF78909C),
                    shadowColor = Color(0xFF546E7A),
                    onClick = onNavigateToParentDashboard,
                    modifier = Modifier.weight(1f),
                    cardHeight = 140
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom row of decorative flowers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("\uD83C\uDF3A", "\uD83C\uDF38", "\uD83C\uDF3C", "\uD83C\uDF3B", "\uD83C\uDF37", "\uD83C\uDF39", "\uD83C\uDF3A").forEachIndexed { index, flower ->
                    Text(
                        text = flower,
                        fontSize = 22.sp,
                        modifier = Modifier.offset(
                            y = if (index % 2 == 0) floatOffset.dp else floatOffset2.dp
                        )
                    )
                }
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
    shadowColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cardHeight: Int = 190
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = remember { Animatable(1f) }
    val infiniteTransition = rememberInfiniteTransition(label = "card_$title")

    val wobble by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wobble_$title"
    )

    LaunchedEffect(isPressed) {
        if (isPressed) {
            scale.animateTo(0.88f, spring(stiffness = Spring.StiffnessHigh))
        } else {
            scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
    }

    Box(
        modifier = modifier
            .height(cardHeight.dp)
            .scale(scale.value)
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = 6.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = shadowColor),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {}

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
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = emoji,
                    fontSize = if (cardHeight > 150) 44.sp else 32.sp,
                    modifier = Modifier.offset(y = wobble.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
