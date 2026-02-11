package com.danvaly.alfabet.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
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
import androidx.compose.material3.LinearProgressIndicator
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
    val scope = rememberCoroutineScope()
    var currentQuestion by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var gameOver by remember { mutableStateOf(false) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var showFeedback by remember { mutableStateOf(false) }
    val totalQuestions = 10

    val targetLetter = remember(currentQuestion) {
        RomanianAlphabet.letters.random()
    }
    val options = remember(currentQuestion) {
        val others = RomanianAlphabet.getRandomLetters(3, targetLetter.uppercase)
        (others + targetLetter).shuffled()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "quiz_anim")
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
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    LaunchedEffect(currentQuestion) {
        if (!gameOver) {
            delay(500)
            soundManager.speakPhonetic(targetLetter.phoneticSound)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFE8F5E9), Color(0xFFC8E6C9), Color(0xFFE0F2F1))
                )
            )
    ) {
        // Floating decorations
        Text(
            text = "\uD83E\uDD89",
            fontSize = 32.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-25).dp, y = (50 + floatOffset).dp)
        )
        Text(
            text = "\uD83C\uDF3A",
            fontSize = 24.sp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 20.dp, y = (-15 + floatOffset).dp)
        )
        Text(
            text = "\uD83C\uDF38",
            fontSize = 22.sp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-20).dp, y = (-10 + floatOffset).dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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
                        .background(DuoGreen.copy(alpha = 0.15f))
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Inapoi", tint = DuoGreen)
                }

                Text(
                    text = "\uD83C\uDFB5 Ce Litera Auzi?",
                    style = MaterialTheme.typography.headlineMedium,
                    color = DuoGreen,
                    fontWeight = FontWeight.ExtraBold
                )

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DuoGreen)
                ) {
                    Text(
                        text = "${currentQuestion + 1}/$totalQuestions",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }

            // Progress bar
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { (currentQuestion + 1).toFloat() / totalQuestions },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = DuoGreen,
                trackColor = DuoGreen.copy(alpha = 0.2f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Listen button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Asculta sunetul:",
                        style = MaterialTheme.typography.titleLarge,
                        color = OnBackground,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Big listen button - 3D style
                    Box {
                        Card(
                            modifier = Modifier
                                .size(120.dp)
                                .scale(pulseScale)
                                .offset(y = 5.dp),
                            shape = CircleShape,
                            colors = CardDefaults.cardColors(containerColor = DuoGreenDark),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {}
                        Card(
                            modifier = Modifier
                                .size(120.dp)
                                .scale(pulseScale)
                                .clickable {
                                    soundManager.speakPhonetic(targetLetter.phoneticSound)
                                },
                            shape = CircleShape,
                            colors = CardDefaults.cardColors(containerColor = DuoGreen),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Filled.VolumeUp,
                                    contentDescription = "Asculta",
                                    tint = Color.White,
                                    modifier = Modifier.size(56.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\uD83D\uDC3B",
                        fontSize = 32.sp,
                        modifier = Modifier.offset(y = floatOffset.dp)
                    )
                }

                // Right: Answer options
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Alege litera corecta:",
                        style = MaterialTheme.typography.titleLarge,
                        color = OnBackground,
                        fontWeight = FontWeight.Bold
                    )
                    options.forEach { letter ->
                        AnswerCard3D(
                            letter = letter,
                            isSelected = selectedAnswer == letter.uppercase,
                            isCorrect = showFeedback && letter.uppercase == targetLetter.uppercase,
                            isWrong = showFeedback && selectedAnswer == letter.uppercase && letter.uppercase != targetLetter.uppercase,
                            onClick = {
                                if (showFeedback) return@AnswerCard3D
                                selectedAnswer = letter.uppercase
                                showFeedback = true
                                val isCorrect = letter.uppercase == targetLetter.uppercase
                                if (isCorrect) {
                                    score++
                                    soundManager.speakEncouragement(true)
                                } else {
                                    soundManager.speakEncouragement(false)
                                }

                                scope.launch {
                                    delay(1500)
                                    if (currentQuestion + 1 >= totalQuestions) {
                                        gameOver = true
                                        val stars = when {
                                            score >= 9 -> 3
                                            score >= 6 -> 2
                                            score >= 3 -> 1
                                            else -> 0
                                        }
                                        scope.launch {
                                            repository.onGamePlayed(stars)
                                        }
                                    } else {
                                        currentQuestion++
                                        selectedAnswer = null
                                        showFeedback = false
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }

        // Game over overlay
        AnimatedVisibility(
            visible = gameOver,
            enter = scaleIn() + fadeIn(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Card(
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(12.dp),
                modifier = Modifier.padding(32.dp)
            ) {
                Column(
                    modifier = Modifier.padding(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("\uD83C\uDF89", fontSize = 56.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Bravo!",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        color = DuoGreen
                    )
                    Text(
                        text = "$score/$totalQuestions corecte!",
                        style = MaterialTheme.typography.headlineMedium,
                        color = OnBackground
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
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onBack,
                        colors = ButtonDefaults.buttonColors(containerColor = DuoGreen),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("Gata!", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun AnswerCard3D(
    letter: Letter,
    isSelected: Boolean,
    isCorrect: Boolean,
    isWrong: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = remember { Animatable(1f) }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            scale.animateTo(0.95f, spring(stiffness = Spring.StiffnessHigh))
        } else {
            scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
    }

    val bgColor = when {
        isCorrect -> DuoGreen
        isWrong -> DuoRed
        isSelected -> DuoBlue
        else -> SurfaceWhite
    }
    val shadowColor = when {
        isCorrect -> DuoGreenDark
        isWrong -> Color(0xFFCC3333)
        isSelected -> DuoBlueDark
        else -> Color(0xFFE0E0E0)
    }
    val textColor = when {
        isCorrect || isWrong || isSelected -> Color.White
        else -> OnBackground
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .scale(scale.value)
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = 4.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = shadowColor),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {}

        Card(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = if (isPressed) 3.dp else 0.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                ),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = bgColor),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = letter.words.firstOrNull()?.emoji ?: "",
                    fontSize = 24.sp
                )
                Text(
                    text = "${letter.uppercase} ${letter.lowercase}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = textColor
                )
                if (isCorrect) {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = "\u2705", fontSize = 22.sp)
                } else if (isWrong) {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = "\u274C", fontSize = 22.sp)
                }
            }
        }
    }
}
