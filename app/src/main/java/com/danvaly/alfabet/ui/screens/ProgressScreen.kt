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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.danvaly.alfabet.data.repository.ProgressRepository
import com.danvaly.alfabet.ui.theme.*

@Composable
fun ProgressScreen(
    onBack: () -> Unit,
    repository: ProgressRepository
) {
    val learnedCount by repository.learnedCount.collectAsState(initial = 0)
    val totalStars by repository.totalStars.collectAsState(initial = 0)
    val appStats by repository.appStats.collectAsState(initial = null)
    val allProgress by repository.allProgress.collectAsState(initial = emptyList())

    val infiniteTransition = rememberInfiniteTransition(label = "progress_anim")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )
    val floatOffset2 by infiniteTransition.animateFloat(
        initialValue = 4f,
        targetValue = -4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float2"
    )

    val learnedLetters = allProgress.filter { it.learnCompleted }.map { it.letter }.toSet()
    val totalLetters = RomanianAlphabet.letters.size
    val progress = if (totalLetters > 0) learnedCount.toFloat() / totalLetters else 0f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFF8E1), Color(0xFFFFECB3), Color(0xFFFFF3E0))
                )
            )
    ) {
        // Floating decorations
        Text(
            text = "\uD83C\uDF3B",
            fontSize = 28.sp,
            modifier = Modifier
                .offset(x = 20.dp, y = (30 + floatOffset).dp)
        )
        Text(
            text = "\uD83D\uDC1E",
            fontSize = 24.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-25).dp, y = (35 + floatOffset2).dp)
        )
        Text(
            text = "\uD83C\uDF37",
            fontSize = 22.sp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 15.dp, y = (-10 + floatOffset).dp)
        )
        Text(
            text = "\uD83D\uDC22",
            fontSize = 26.sp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-20).dp, y = (-15 + floatOffset2).dp)
        )

        Column(modifier = Modifier.fillMaxSize()) {
            // Header
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
                        .background(DuoYellow.copy(alpha = 0.3f))
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Inapoi",
                        tint = Color(0xFFCCAD00)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "\uD83C\uDFC6",
                    fontSize = 28.sp,
                    modifier = Modifier.offset(y = floatOffset.dp)
                )
                Text(
                    text = "Progresul Meu",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFFCCAD00),
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                // Left: Stats
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Progress card
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "\uD83D\uDCDA", fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Litere invatate",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = OnBackground
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "$learnedCount / $totalLetters",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Black,
                                color = DuoGreen
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp)),
                                color = DuoGreen,
                                trackColor = DuoGreen.copy(alpha = 0.15f)
                            )
                        }
                    }

                    // Stats row
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        StatCard(
                            emoji = "\u2B50",
                            value = "$totalStars",
                            label = "Stele",
                            color = DuoYellow,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            emoji = "\uD83C\uDFAE",
                            value = "${appStats?.gamesPlayed ?: 0}",
                            label = "Jocuri",
                            color = DuoPurple,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            emoji = "\uD83D\uDD25",
                            value = "${appStats?.currentStreak ?: 0}",
                            label = "Serie",
                            color = DuoOrange,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Achievements
                    Text(
                        text = "\uD83C\uDFC5 Realizari",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = OnBackground
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AchievementBadge(
                            emoji = "\uD83D\uDD0D",
                            label = "Explorator",
                            unlocked = learnedCount >= 10,
                            modifier = Modifier.weight(1f)
                        )
                        AchievementBadge(
                            emoji = "\u270D\uFE0F",
                            label = "Scriitor",
                            unlocked = allProgress.count { it.traceStars > 0 } >= 15,
                            modifier = Modifier.weight(1f)
                        )
                        AchievementBadge(
                            emoji = "\uD83D\uDC51",
                            label = "Maestru",
                            unlocked = learnedCount >= totalLetters,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Right: Sticker album
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "\uD83D\uDC3E Album de Stickere",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = OnBackground
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "\uD83D\uDC3B",
                            fontSize = 24.sp,
                            modifier = Modifier.offset(y = floatOffset2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 64.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(RomanianAlphabet.letters.size) { index ->
                            val letter = RomanianAlphabet.letters[index]
                            val isLearned = letter.uppercase in learnedLetters
                            StickerCard(
                                letter = letter.uppercase,
                                emoji = letter.words.firstOrNull()?.emoji ?: "",
                                isLearned = isLearned,
                                colorIndex = index
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    emoji: String,
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = emoji, fontSize = 24.sp)
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = OnBackgroundLight,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun AchievementBadge(
    emoji: String,
    label: String,
    unlocked: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (unlocked) DuoGreen.copy(alpha = 0.15f) else Color(0xFFE0E0E0)
        )
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (unlocked) emoji else "\uD83D\uDD12",
                fontSize = 28.sp
            )
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (unlocked) DuoGreen else OnBackgroundLight,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun StickerCard(
    letter: String,
    emoji: String,
    isLearned: Boolean,
    colorIndex: Int
) {
    val bgColor = if (isLearned) {
        PastelColors[colorIndex % PastelColors.size]
    } else {
        Color(0xFFE0E0E0)
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        modifier = Modifier.size(64.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isLearned) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = emoji, fontSize = 20.sp)
                    Text(
                        text = letter,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            } else {
                Text(
                    text = "\uD83D\uDD12",
                    fontSize = 20.sp
                )
            }
        }
    }
}
