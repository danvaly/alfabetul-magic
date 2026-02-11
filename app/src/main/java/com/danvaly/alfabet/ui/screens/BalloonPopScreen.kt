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
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danvaly.alfabet.audio.SoundManager
import com.danvaly.alfabet.data.RomanianAlphabet
import com.danvaly.alfabet.data.repository.ProgressRepository
import com.danvaly.alfabet.ui.components.StarRating
import com.danvaly.alfabet.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class Balloon(
    val id: Int,
    val letter: String,
    val isTarget: Boolean,
    val color: Color,
    val x: Float,
    val speed: Float,
    var popped: Boolean = false
)

@Composable
fun BalloonPopScreen(
    targetLetter: String,
    onBack: () -> Unit,
    soundManager: SoundManager,
    repository: ProgressRepository
) {
    var score by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(45) }
    var gameOver by remember { mutableStateOf(false) }
    val balloons = remember { mutableStateListOf<Balloon>() }
    var balloonIdCounter by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()
    val screenWidth = LocalConfiguration.current.screenWidthDp

    fun spawnBalloon() {
        val allLetters = RomanianAlphabet.letters.map { it.uppercase }
        val isTarget = (0..2).random() == 0 // 33% chance of target
        val letter = if (isTarget) targetLetter else allLetters.filter { it != targetLetter }.random()
        balloons.add(
            Balloon(
                id = balloonIdCounter++,
                letter = letter,
                isTarget = letter == targetLetter,
                color = BalloonColors.random(),
                x = (50..(screenWidth - 100)).random().toFloat(),
                speed = (3..6).random().toFloat()
            )
        )
    }

    // Timer
    LaunchedEffect(gameOver) {
        if (!gameOver) {
            soundManager.speakSentence("Atinge baloanele cu litera $targetLetter")
            while (timeLeft > 0 && !gameOver) {
                delay(1000)
                timeLeft--
            }
            gameOver = true
            val stars = when {
                score >= 15 -> 3
                score >= 8 -> 2
                score >= 3 -> 1
                else -> 0
            }
            soundManager.speakEncouragement(stars >= 2)
            scope.launch {
                repository.onGamePlayed(stars)
            }
        }
    }

    // Spawn balloons
    LaunchedEffect(gameOver) {
        if (!gameOver) {
            while (!gameOver) {
                spawnBalloon()
                delay((800L..1500L).random())
            }
        }
    }

    // Cleanup old balloons
    LaunchedEffect(Unit) {
        while (true) {
            delay(2000)
            balloons.removeAll { it.popped }
            if (balloons.size > 20) {
                balloons.removeRange(0, balloons.size - 15)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFE3F2FD), Color(0xFFBBDEFB), Color(0xFF90CAF9))
                )
            )
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.7f))
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Inapoi", tint = PrimaryPink)
            }

            // Target letter indicator
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryPink)
            ) {
                Text(
                    text = "Gaseste: $targetLetter",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                )
            }

            // Score
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CorrectGreen)
            ) {
                Text(
                    text = "Scor: $score",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                )
            }

            // Timer
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (timeLeft <= 10) WrongRed else SecondaryPurple
                )
            ) {
                Text(
                    text = "⏱ ${timeLeft}s",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                )
            }
        }

        // Balloons
        if (!gameOver) {
            balloons.filter { !it.popped }.forEach { balloon ->
                FloatingBalloon(
                    balloon = balloon,
                    onPop = {
                        balloon.popped = true
                        if (balloon.isTarget) {
                            score++
                            soundManager.speakEncouragement(true)
                        } else {
                            soundManager.speakEncouragement(false)
                            if (score > 0) score--
                        }
                    }
                )
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
                    Text("🎈", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Bravo!",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryPink
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Scor: $score",
                        style = MaterialTheme.typography.headlineMedium,
                        color = OnBackground
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    StarRating(
                        stars = when {
                            score >= 15 -> 3
                            score >= 8 -> 2
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
                                score = 0
                                timeLeft = 45
                                gameOver = false
                                balloons.clear()
                                balloonIdCounter = 0
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

@Composable
private fun FloatingBalloon(
    balloon: Balloon,
    onPop: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "balloon_${balloon.id}")

    val yOffset by infiniteTransition.animateFloat(
        initialValue = 600f,
        targetValue = -200f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (6000 / balloon.speed).toInt(),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "y_${balloon.id}"
    )

    val xWobble by infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wobble_${balloon.id}"
    )

    val scale = remember { Animatable(1f) }

    Box(
        modifier = Modifier
            .offset(x = (balloon.x + xWobble).dp, y = yOffset.dp)
            .size(80.dp)
            .scale(scale.value)
            .clip(CircleShape)
            .background(balloon.color)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onPop
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = balloon.letter,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}
