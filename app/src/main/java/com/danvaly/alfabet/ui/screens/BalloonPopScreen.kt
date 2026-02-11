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
    val speed: Float
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
    val poppedIds = remember { mutableStateListOf<Int>() }
    var balloonIdCounter by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()
    val screenWidth = LocalConfiguration.current.screenWidthDp

    val infiniteTransition = rememberInfiniteTransition(label = "bg_anim")
    val cloudOffset by infiniteTransition.animateFloat(
        initialValue = -20f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "clouds"
    )

    fun spawnBalloon() {
        val allLetters = RomanianAlphabet.letters.map { it.uppercase }
        val isTarget = (0..2).random() == 0
        val letter = if (isTarget) targetLetter else allLetters.filter { it != targetLetter }.random()
        balloons.add(
            Balloon(
                id = balloonIdCounter++,
                letter = letter,
                isTarget = letter == targetLetter,
                color = BalloonColors.random(),
                x = (50..(screenWidth - 100)).random().toFloat(),
                // SLOWER speed: was 3-6, now 1.5-3 for gentler floating
                speed = (15..30).random().toFloat() / 10f
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

    // Spawn balloons - slower spawn rate
    LaunchedEffect(gameOver) {
        if (!gameOver) {
            while (!gameOver) {
                spawnBalloon()
                delay((1200L..2200L).random())
            }
        }
    }

    // Cleanup popped balloons periodically
    LaunchedEffect(Unit) {
        while (true) {
            delay(2000)
            balloons.removeAll { it.id in poppedIds }
            poppedIds.clear()
            if (balloons.size > 20) {
                val toRemove = balloons.size - 15
                repeat(toRemove) { balloons.removeFirstOrNull() }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF87CEEB), Color(0xFFB8E6FF), Color(0xFFE0F4FF))
                )
            )
    ) {
        // Floating clouds in background
        Text(
            text = "\u2601\uFE0F",
            fontSize = 48.sp,
            modifier = Modifier.offset(x = (40 + cloudOffset).dp, y = 80.dp)
        )
        Text(
            text = "\u2601\uFE0F",
            fontSize = 36.sp,
            modifier = Modifier.offset(x = (screenWidth - 120 + cloudOffset).dp, y = 120.dp)
        )
        // Flowers at bottom
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("\uD83C\uDF3B", "\uD83C\uDF37", "\uD83C\uDF3A", "\uD83C\uDF3C", "\uD83C\uDF38", "\uD83C\uDF3B", "\uD83C\uDF37").forEach { flower ->
                Text(text = flower, fontSize = 20.sp)
            }
        }

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
                    .background(Color.White.copy(alpha = 0.8f))
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Inapoi", tint = DuoGreen)
            }

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DuoGreen)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "\uD83C\uDF88", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Gaseste: $targetLetter",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp
                    )
                }
            }

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DuoOrange)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "\u2B50", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$score",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp
                    )
                }
            }

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (timeLeft <= 10) DuoRed else DuoBlue
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "\u23F0", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${timeLeft}s",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp
                    )
                }
            }
        }

        // Balloons
        if (!gameOver) {
            balloons.filter { it.id !in poppedIds }.forEach { balloon ->
                FloatingBalloon(
                    balloon = balloon,
                    onPop = {
                        poppedIds.add(balloon.id)
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
                    Text("\uD83C\uDF89", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Bravo!",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        color = DuoGreen
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "\u2B50", fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Scor: $score",
                            style = MaterialTheme.typography.headlineMedium,
                            color = OnBackground
                        )
                    }
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
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text("Inapoi", fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = {
                                score = 0
                                timeLeft = 45
                                gameOver = false
                                balloons.clear()
                                poppedIds.clear()
                                balloonIdCounter = 0
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DuoGreen),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Icon(Icons.Filled.Refresh, "Joaca din nou")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Joaca din nou", fontWeight = FontWeight.Bold)
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

    // MUCH SLOWER vertical movement: was 6000/speed, now 12000/speed
    val yOffset by infiniteTransition.animateFloat(
        initialValue = 600f,
        targetValue = -200f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (12000 / balloon.speed).toInt(),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "y_${balloon.id}"
    )

    // Gentler wobble
    val xWobble by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wobble_${balloon.id}"
    )

    val scale = remember { Animatable(1f) }

    // Balloon with gradient effect
    Box(
        modifier = Modifier
            .offset(x = (balloon.x + xWobble).dp, y = yOffset.dp)
            .scale(scale.value)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onPop
            ),
        contentAlignment = Alignment.Center
    ) {
        // Balloon body - oval shape
        Box(
            modifier = Modifier
                .size(width = 72.dp, height = 88.dp)
                .clip(RoundedCornerShape(50))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            balloon.color.copy(alpha = 0.7f),
                            balloon.color,
                            balloon.color.copy(alpha = 0.9f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = balloon.letter,
                fontSize = 30.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}
