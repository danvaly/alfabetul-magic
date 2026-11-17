package com.danvaly.alfabet.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.danvaly.alfabet.audio.SoundManager
import com.danvaly.alfabet.data.RomanianAlphabet
import com.danvaly.alfabet.data.repository.ProgressRepository
import com.danvaly.alfabet.ui.components.ConfettiAnimation
import com.danvaly.alfabet.ui.theme.*
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun DailyChallengeScreen(
    onBack: () -> Unit,
    soundManager: SoundManager,
    repository: ProgressRepository
) {
    val scope = rememberCoroutineScope()
    val dayOfYear = LocalDate.now().dayOfYear
    val dailyWord = remember { RomanianAlphabet.getDailyWord(dayOfYear) }
    var selectedLetter by remember { mutableStateOf<String?>(null) }
    var showResult by remember { mutableStateOf(false) }
    var isCompleted by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    val letterOptions = remember {
        val correctLetter = dailyWord.letter
        val others = RomanianAlphabet.letters
            .map { it.uppercase }
            .filter { it != correctLetter }
            .shuffled()
            .take(3)
        (others + correctLetter).shuffled()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "daily_anim")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    LaunchedEffect(Unit) {
        soundManager.speakWord(dailyWord.word)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFF8E1), Color(0xFFFFECB3), Color(0xFFFFF3E0))
                )
            )
            .systemBarsPadding()
    ) {
        Text(
            text = "\u2B50",
            fontSize = 28.sp,
            modifier = Modifier.offset(x = 20.dp, y = (30 + floatOffset).dp)
        )
        Text(
            text = "\uD83C\uDF1F",
            fontSize = 24.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-25).dp, y = (40 + floatOffset).dp)
        )
        Text(
            text = "\uD83D\uDC3B",
            fontSize = 30.sp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 20.dp, y = (-15 + floatOffset).dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(DuoYellow.copy(alpha = 0.3f))
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Înapoi", tint = Color(0xFFCCAD00))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "\uD83C\uDF1F",
                    fontSize = 28.sp,
                    modifier = Modifier.offset(y = floatOffset.dp)
                )
                Text(
                    text = "Provocarea zilei",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFFCCAD00),
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Daily word display
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Cuvântul de azi:",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = OnBackground
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier
                            .size(160.dp)
                            .scale(pulseScale),
                        shape = RoundedCornerShape(32.dp),
                        colors = CardDefaults.cardColors(containerColor = DuoYellow),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = dailyWord.emoji, fontSize = 72.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = dailyWord.word,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        color = OnBackground
                    )
                    Text(
                        text = dailyWord.meaning,
                        style = MaterialTheme.typography.bodyLarge,
                        color = OnBackgroundLight,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { soundManager.speakWord(dailyWord.word) },
                        colors = ButtonDefaults.buttonColors(containerColor = DuoYellow),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(Icons.Filled.VolumeUp, contentDescription = "Ascultă", tint = Color(0xFF5D4E00))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ascultă", fontWeight = FontWeight.Bold, color = Color(0xFF5D4E00))
                    }
                }

                // Right: Letter quiz
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Cu ce literă începe\n\"${dailyWord.word}\"?",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = OnBackground,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        letterOptions.forEach { letter ->
                            val isCorrect = showResult && letter == dailyWord.letter
                            val isWrong = showResult && selectedLetter == letter && letter != dailyWord.letter
                            val bgColor = when {
                                isCorrect -> DuoGreen
                                isWrong -> DuoRed
                                selectedLetter == letter -> DuoYellow
                                else -> SurfaceWhite
                            }
                            val shadowColor = when {
                                isCorrect -> DuoGreenDark
                                isWrong -> Color(0xFFCC3333)
                                else -> Color(0xFFE0E0E0)
                            }

                            Box(modifier = Modifier.size(72.dp)) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .offset(y = 4.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = shadowColor)
                                ) {}
                                Card(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clickable {
                                            if (showResult) return@clickable
                                            selectedLetter = letter
                                            showResult = true
                                            if (letter == dailyWord.letter) {
                                                soundManager.speakEncouragement(true)
                                                showConfetti = true
                                                isCompleted = true
                                                scope.launch { repository.completeDailyChallenge() }
                                            } else {
                                                soundManager.speakEncouragement(false)
                                            }
                                        },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = bgColor)
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = letter,
                                            fontSize = 32.sp,
                                            fontWeight = FontWeight.Black,
                                            color = if (isCorrect || isWrong) Color.White else OnBackground
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (isCompleted) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = DuoGreen.copy(alpha = 0.15f))
                        ) {
                            Text(
                                text = "\u2705 Provocare completă! Revino mâine!",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = DuoGreen,
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    if (showResult && selectedLetter != dailyWord.letter) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                selectedLetter = null
                                showResult = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DuoOrange),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text("Încearcă din nou", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        ConfettiAnimation(isActive = showConfetti, modifier = Modifier.fillMaxSize())
    }
}
