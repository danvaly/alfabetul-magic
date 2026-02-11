package com.danvaly.alfabet.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danvaly.alfabet.data.RomanianAlphabet
import com.danvaly.alfabet.data.repository.ProgressRepository
import com.danvaly.alfabet.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProgressScreen(
    onBack: () -> Unit,
    repository: ProgressRepository
) {
    val learnedCount by repository.learnedCount.collectAsState(initial = 0)
    val totalStars by repository.totalStars.collectAsState(initial = 0)
    val appStats by repository.appStats.collectAsState(initial = null)
    val allProgress by repository.allProgress.collectAsState(initial = emptyList())

    val totalLetters = RomanianAlphabet.letters.size
    val progressPercent = if (totalLetters > 0) learnedCount.toFloat() / totalLetters else 0f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BackgroundCream, BackgroundLight, Color(0xFFFFF8E1))
                )
            )
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Left: Stats
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
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
                        text = "Progresul Meu",
                        style = MaterialTheme.typography.headlineMedium,
                        color = PrimaryPink,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Overall progress
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Litere invatate",
                            style = MaterialTheme.typography.titleMedium,
                            color = OnBackgroundLight
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = "$learnedCount",
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryPink
                            )
                            Text(
                                text = " / $totalLetters",
                                fontSize = 24.sp,
                                color = OnBackgroundLight
                            )
                        }
                        LinearProgressIndicator(
                            progress = { progressPercent },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            color = PrimaryPink,
                            trackColor = PrimaryPink.copy(alpha = 0.15f),
                            strokeCap = StrokeCap.Round
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stats row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        emoji = "⭐",
                        value = "${totalStars ?: 0}",
                        label = "Stele",
                        color = StarGoldDark,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        emoji = "🎮",
                        value = "${appStats?.gamesPlayed ?: 0}",
                        label = "Jocuri",
                        color = SecondaryPurple,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        emoji = "🔥",
                        value = "${appStats?.currentStreak ?: 0}",
                        label = "Zile",
                        color = BalloonOrange,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Achievements
                Text(
                    text = "Realizari",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = OnBackground
                )
                Spacer(modifier = Modifier.height(12.dp))
                AchievementCard(
                    emoji = "🔍",
                    title = "Explorator de Litere",
                    description = "Invata 10 litere",
                    isUnlocked = learnedCount >= 10
                )
                AchievementCard(
                    emoji = "✍️",
                    title = "Scriitor Incepator",
                    description = "Scrie 15 litere",
                    isUnlocked = allProgress.count { it.traceStars > 0 } >= 15
                )
                AchievementCard(
                    emoji = "👑",
                    title = "Maestru al Alfabetului",
                    description = "Invata toate literele",
                    isUnlocked = learnedCount >= totalLetters
                )
            }

            // Right: Sticker album
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Albumul de Abtibilduri",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = OnBackground
                )
                Text(
                    text = "Fiecare litera invatata deblocheaza un abtibild!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnBackgroundLight
                )

                Spacer(modifier = Modifier.height(16.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RomanianAlphabet.letters.forEach { letter ->
                        val isLearned = allProgress.any {
                            it.letter == letter.uppercase && it.learnCompleted
                        }
                        val emoji = letter.words.firstOrNull()?.emoji ?: "❓"

                        Card(
                            modifier = Modifier.size(72.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isLearned) {
                                    PastelColors[letter.colorIndex % PastelColors.size]
                                } else {
                                    Color.Gray.copy(alpha = 0.2f)
                                }
                            ),
                            elevation = CardDefaults.cardElevation(if (isLearned) 4.dp else 0.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = if (isLearned) emoji else "🔒",
                                    fontSize = 24.sp
                                )
                                Text(
                                    text = letter.uppercase,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isLearned) OnBackground else OnBackgroundLight
                                )
                            }
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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = emoji, fontSize = 28.sp)
            Text(
                text = value,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = OnBackgroundLight
            )
        }
    }
}

@Composable
private fun AchievementCard(
    emoji: String,
    title: String,
    description: String,
    isUnlocked: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) CorrectGreenLight else Color.Gray.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(if (isUnlocked) 4.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (isUnlocked) emoji else "🔒",
                fontSize = 32.sp
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isUnlocked) OnBackground else OnBackgroundLight
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnBackgroundLight
                )
            }
            if (isUnlocked) {
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    Icons.Filled.EmojiEvents,
                    contentDescription = "Deblocat",
                    tint = StarGoldDark,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}
