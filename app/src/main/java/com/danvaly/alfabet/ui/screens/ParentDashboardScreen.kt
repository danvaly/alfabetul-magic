package com.danvaly.alfabet.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danvaly.alfabet.data.DifficultyLevel
import com.danvaly.alfabet.data.DifficultyManager
import com.danvaly.alfabet.data.RomanianAlphabet
import com.danvaly.alfabet.data.repository.ProgressRepository
import com.danvaly.alfabet.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ParentDashboardScreen(
    onBack: () -> Unit,
    repository: ProgressRepository
) {
    val scope = rememberCoroutineScope()
    var isUnlocked by remember { mutableStateOf(false) }
    var gateAnswer by remember { mutableStateOf("") }
    var gateError by remember { mutableStateOf(false) }

    // Random math problem for parent gate
    val num1 = remember { (10..30).random() }
    val num2 = remember { (10..30).random() }
    val correctAnswer = num1 + num2

    val learnedCount by repository.learnedCount.collectAsState(initial = 0)
    val totalStars by repository.totalStars.collectAsState(initial = 0)
    val appStats by repository.appStats.collectAsState(initial = null)
    val allProgress by repository.allProgress.collectAsState(initial = emptyList())

    val totalLetters = RomanianAlphabet.letters.size
    val currentDifficulty = DifficultyManager.fromInt(appStats?.difficulty ?: 1)

    val infiniteTransition = rememberInfiniteTransition(label = "parent_anim")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
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
                    colors = listOf(Color(0xFFF5F5F5), Color(0xFFEEEEEE), Color(0xFFF5F5F5))
                )
            )
    ) {
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
                        .background(DuoGreen.copy(alpha = 0.15f))
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Inapoi", tint = DuoGreen)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC67 Panou Parinti",
                    style = MaterialTheme.typography.headlineMedium,
                    color = OnBackground,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            if (!isUnlocked) {
                // Parent gate
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        elevation = CardDefaults.cardElevation(8.dp),
                        modifier = Modifier.padding(48.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "\uD83D\uDD12",
                                fontSize = 48.sp,
                                modifier = Modifier.offset(y = floatOffset.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Verificare Parinte",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = OnBackground
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Rezolva problema pentru a accesa panoul:",
                                style = MaterialTheme.typography.bodyLarge,
                                color = OnBackgroundLight,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "$num1 + $num2 = ?",
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Black,
                                color = DuoGreen
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(
                                value = gateAnswer,
                                onValueChange = {
                                    gateAnswer = it
                                    gateError = false
                                },
                                label = { Text("Raspuns") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                isError = gateError,
                                modifier = Modifier.width(200.dp)
                            )
                            if (gateError) {
                                Text(
                                    text = "Raspuns gresit!",
                                    color = DuoRed,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    if (gateAnswer.toIntOrNull() == correctAnswer) {
                                        isUnlocked = true
                                    } else {
                                        gateError = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DuoGreen),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("Verifica", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                // Dashboard content
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                ) {
                    // Left: Stats overview
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Statistici",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = OnBackground
                        )

                        // Progress
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Litere Invatate",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = OnBackground
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$learnedCount / $totalLetters",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Black,
                                    color = DuoGreen
                                )
                                LinearProgressIndicator(
                                    progress = { if (totalLetters > 0) learnedCount.toFloat() / totalLetters else 0f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = DuoGreen,
                                    trackColor = DuoGreen.copy(alpha = 0.15f)
                                )
                            }
                        }

                        // Stats grid
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ParentStatCard(
                                emoji = "\u2B50",
                                value = "$totalStars",
                                label = "Stele Totale",
                                modifier = Modifier.weight(1f)
                            )
                            ParentStatCard(
                                emoji = "\uD83C\uDFAE",
                                value = "${appStats?.gamesPlayed ?: 0}",
                                label = "Jocuri",
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ParentStatCard(
                                emoji = "\uD83D\uDD25",
                                value = "${appStats?.currentStreak ?: 0}",
                                label = "Serie Zile",
                                modifier = Modifier.weight(1f)
                            )
                            ParentStatCard(
                                emoji = "\u23F0",
                                value = "${appStats?.totalPlayTimeMinutes ?: 0} min",
                                label = "Timp Total",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Right: Difficulty settings
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Nivel Dificultate",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = OnBackground
                        )

                        DifficultyLevel.entries.forEach { level ->
                            val isSelected = level == currentDifficulty
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        scope.launch {
                                            repository.setDifficulty(DifficultyManager.toInt(level))
                                        }
                                    },
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) DuoGreen.copy(alpha = 0.15f) else SurfaceWhite
                                ),
                                elevation = CardDefaults.cardElevation(if (isSelected) 4.dp else 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(text = level.emoji, fontSize = 28.sp)
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = level.label,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (isSelected) DuoGreen else OnBackground
                                        )
                                        Text(
                                            text = when (level) {
                                                DifficultyLevel.BEGINNER -> "3 optiuni, viteza lenta, indicii activate"
                                                DifficultyLevel.INTERMEDIATE -> "4 optiuni, viteza normala"
                                                DifficultyLevel.ADVANCED -> "5 optiuni, viteza rapida"
                                            },
                                            style = MaterialTheme.typography.bodySmall,
                                            color = OnBackgroundLight
                                        )
                                    }
                                    if (isSelected) {
                                        Text(text = "\u2705", fontSize = 24.sp)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Sfaturi",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = OnBackground
                        )
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = DuoYellow.copy(alpha = 0.1f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "\uD83D\uDCA1 Sesiunile scurte de 10-15 minute sunt cele mai eficiente pentru copii.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = OnBackground
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "\uD83C\uDF1F Incurajati copilul sa exploreze mai multe moduri de invatare.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = OnBackground
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
private fun ParentStatCard(
    emoji: String,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = emoji, fontSize = 24.sp)
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = DuoGreen
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = OnBackgroundLight,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
