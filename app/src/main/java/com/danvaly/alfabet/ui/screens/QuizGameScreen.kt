package com.danvaly.alfabet.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.runtime.mutableIntStateOf
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
import com.danvaly.alfabet.data.model.Letter
import com.danvaly.alfabet.data.repository.ProgressRepository
import com.danvaly.alfabet.ui.components.StarRating
import com.danvaly.alfabet.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun QuizGameScreen(
    onBack: () -> Unit,
    soundManager: SoundManager,
    repository: ProgressRepository
) {
    val totalQuestions = 10
    var currentQuestion by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var gameOver by remember { mutableStateOf(false) }

    var targetLetter by remember { mutableStateOf(RomanianAlphabet.letters.random()) }
    var options by remember { mutableStateOf(generateOptions(targetLetter)) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var showFeedback by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun nextQuestion() {
        if (currentQuestion >= totalQuestions - 1) {
            gameOver = true
            val stars = when {
                score >= 9 -> 3
                score >= 6 -> 2
                score >= 3 -> 1
                else -> 0
            }
            scope.launch { repository.onGamePlayed(stars) }
            return
        }
        currentQuestion++
        targetLetter = RomanianAlphabet.letters.random()
        options = generateOptions(targetLetter)
        selectedAnswer = null
        showFeedback = false
    }

    // Speak the letter when question changes
    LaunchedEffect(currentQuestion) {
        delay(500)
        soundManager.speakPhonetic(targetLetter.phoneticSound)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFE8F5E9), Color(0xFFC8E6C9), Color(0xFFA5D6A7))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.7f))
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Inapoi", tint = CorrectGreen)
                }

                Text(
                    text = "Ce Litera Auzi?",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = CorrectGreen
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CorrectGreen)
                    ) {
                        Text(
                            text = "${currentQuestion + 1}/$totalQuestions",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = StarGoldDark)
                    ) {
                        Text(
                            text = "Scor: $score",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            if (!gameOver) {
                Spacer(modifier = Modifier.height(32.dp))

                // Play sound button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = { soundManager.speakPhonetic(targetLetter.phoneticSound) },
                        modifier = Modifier.size(120.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = CorrectGreen)
                    ) {
                        Icon(
                            Icons.Filled.VolumeUp,
                            contentDescription = "Asculta",
                            modifier = Modifier.size(56.dp),
                            tint = Color.White
                        )
                    }
                }

                Text(
                    text = "Atinge pentru a asculta din nou",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnBackgroundLight,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Answer options
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
                ) {
                    options.forEach { option ->
                        AnswerCard(
                            letter = option.uppercase,
                            isSelected = selectedAnswer == option.uppercase,
                            isCorrect = if (showFeedback) option.uppercase == targetLetter.uppercase else null,
                            onClick = {
                                if (!showFeedback) {
                                    selectedAnswer = option.uppercase
                                    showFeedback = true
                                    val isCorrect = option.uppercase == targetLetter.uppercase
                                    if (isCorrect) {
                                        score++
                                        soundManager.speakEncouragement(true)
                                    } else {
                                        soundManager.speakEncouragement(false)
                                    }
                                    scope.launch {
                                        delay(1500)
                                        nextQuestion()
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Feedback
                AnimatedVisibility(
                    visible = showFeedback,
                    enter = scaleIn() + fadeIn(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    val isCorrect = selectedAnswer == targetLetter.uppercase
                    Text(
                        text = if (isCorrect) "Corect! Este litera ${targetLetter.uppercase}" else "Era litera ${targetLetter.uppercase}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isCorrect) CorrectGreen else WrongRed,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Game over
        AnimatedVisibility(
            visible = gameOver,
            enter = scaleIn() + fadeIn(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Card(
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🎵", fontSize = 56.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Bravo!",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = CorrectGreen
                    )
                    Text(
                        "$score din $totalQuestions corecte",
                        style = MaterialTheme.typography.bodyLarge,
                        color = OnBackgroundLight
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    StarRating(
                        stars = when {
                            score >= 9 -> 3
                            score >= 6 -> 2
                            score >= 3 -> 1
                            else -> 0
                        }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = onBack,
                            colors = ButtonDefaults.buttonColors(containerColor = OnBackgroundLight),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Inapoi")
                        }
                        Button(
                            onClick = {
                                currentQuestion = 0
                                score = 0
                                gameOver = false
                                targetLetter = RomanianAlphabet.letters.random()
                                options = generateOptions(targetLetter)
                                selectedAnswer = null
                                showFeedback = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CorrectGreen),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Filled.Refresh, "Joaca din nou")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Joaca din nou")
                        }
                    }
                }
            }
        }
    }
}

private fun generateOptions(target: Letter): List<Letter> {
    val others = RomanianAlphabet.getRandomLetters(3, target.uppercase)
    return (others + target).shuffled()
}

@Composable
private fun AnswerCard(
    letter: String,
    isSelected: Boolean,
    isCorrect: Boolean?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(isSelected) {
        if (isSelected) {
            scale.animateTo(1.1f, spring(stiffness = Spring.StiffnessHigh))
            scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
    }

    val backgroundColor = when {
        isCorrect == true -> CorrectGreen
        isCorrect == false && isSelected -> WrongRed
        isSelected -> PrimaryPink
        else -> SurfaceWhite
    }

    Card(
        modifier = modifier
            .height(120.dp)
            .scale(scale.value)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = letter,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected || isCorrect != null) Color.White else OnBackground
            )
        }
    }
}
