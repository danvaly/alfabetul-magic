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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.danvaly.alfabet.data.DifficultyManager
import com.danvaly.alfabet.data.RomanianAlphabet
import com.danvaly.alfabet.data.repository.ProgressRepository
import com.danvaly.alfabet.ui.components.ConfettiAnimation
import com.danvaly.alfabet.ui.components.StarRating
import com.danvaly.alfabet.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RhymingGameScreen(
    onBack: () -> Unit,
    soundManager: SoundManager,
    repository: ProgressRepository
) {
    val scope = rememberCoroutineScope()
    val pairs = remember { RomanianAlphabet.rhymingPairs.shuffled() }
    var currentIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var gameOver by remember { mutableStateOf(false) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var showFeedback by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }
    val totalRounds = pairs.size.coerceAtMost(10)
    val stats by repository.appStats.collectAsState(initial = null)
    val level = DifficultyManager.fromInt(stats?.difficulty ?: 1)

    val currentPair = pairs.getOrNull(currentIndex)
    val options = remember(currentIndex, level) {
        if (currentPair == null) emptyList()
        else {
            // Never offer a distractor that also rhymes (e.g. "sare" for "soare").
            val ending = currentPair.first.takeLast(2)
            val distractors = pairs
                .filter { it != currentPair && !it.second.endsWith(ending) }
                .shuffled()
                .take(level.optionCount - 1)
                .map { it.second }
            (distractors + currentPair.second).shuffled()
        }
    }

    // Beginners hear the word to rhyme with at the start of every round.
    LaunchedEffect(currentIndex, level) {
        val pair = pairs.getOrNull(currentIndex)
        if (level.hintEnabled && pair != null && !gameOver) {
            soundManager.speakWord(pair.first)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "rhyme_anim")
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
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFCE4EC), Color(0xFFF8BBD0), Color(0xFFFFF0F5))
                )
            )
    ) {
        Text(
            text = "\uD83C\uDFB5",
            fontSize = 28.sp,
            modifier = Modifier.offset(x = 20.dp, y = (30 + floatOffset).dp)
        )
        Text(
            text = "\uD83C\uDF3A",
            fontSize = 24.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-25).dp, y = (40 + floatOffset).dp)
        )
        Text(
            text = "\uD83D\uDC23",
            fontSize = 26.sp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 20.dp, y = (-15 + floatOffset).dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(DuoPink.copy(alpha = 0.15f))
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Înapoi", tint = DuoPink)
                }
                Text(
                    text = "\uD83C\uDFB5 Jocul rimelor",
                    style = MaterialTheme.typography.headlineMedium,
                    color = DuoPink,
                    fontWeight = FontWeight.ExtraBold
                )
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DuoPink)
                ) {
                    Text(
                        text = "${currentIndex + 1}/$totalRounds",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { (currentIndex + 1).toFloat() / totalRounds },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = DuoPink,
                trackColor = DuoPink.copy(alpha = 0.2f)
            )

            if (currentPair != null && !gameOver) {
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Target word
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Ce rimează cu:",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = OnBackground,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier
                                .scale(pulseScale)
                                .clickable { soundManager.speakWord(currentPair.first) },
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = DuoPink),
                            elevation = CardDefaults.cardElevation(8.dp)
                        ) {
                            Text(
                                text = currentPair.first,
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 32.dp, vertical = 20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "\uD83D\uDC25",
                            fontSize = 36.sp,
                            modifier = Modifier.offset(y = floatOffset.dp)
                        )
                    }

                    // Right: Options
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Alege rima:",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = OnBackground
                        )

                        options.forEach { word ->
                            val isCorrectOpt = showFeedback && word == currentPair.second
                            val isWrong = showFeedback && selectedAnswer == word && word != currentPair.second
                            val bgColor = when {
                                isCorrectOpt -> DuoGreen
                                isWrong -> DuoRed
                                selectedAnswer == word -> DuoPink
                                else -> SurfaceWhite
                            }
                            val shadowColor = when {
                                isCorrectOpt -> DuoGreenDark
                                isWrong -> Color(0xFFCC3333)
                                selectedAnswer == word -> Color(0xFFE060A0)
                                else -> Color(0xFFE0E0E0)
                            }
                            val textColor = when {
                                isCorrectOpt || isWrong || selectedAnswer == word -> Color.White
                                else -> OnBackground
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp)
                            ) {
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
                                            if (showFeedback) return@clickable
                                            selectedAnswer = word
                                            showFeedback = true
                                            if (word == currentPair.second) {
                                                score++
                                                soundManager.speakEncouragement(true)
                                            } else {
                                                soundManager.speakEncouragement(false)
                                            }
                                            scope.launch {
                                                delay(1500)
                                                if (currentIndex + 1 >= totalRounds) {
                                                    gameOver = true
                                                    showConfetti = true
                                                    val stars = when {
                                                        score >= totalRounds - 2 -> 3
                                                        score >= totalRounds / 2 -> 2
                                                        score >= 1 -> 1
                                                        else -> 0
                                                    }
                                                    scope.launch { repository.onGamePlayed(stars) }
                                                } else {
                                                    currentIndex++
                                                    selectedAnswer = null
                                                    showFeedback = false
                                                }
                                            }
                                        },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = bgColor)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 20.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = word,
                                            style = MaterialTheme.typography.headlineSmall,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = textColor
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        ConfettiAnimation(isActive = showConfetti, modifier = Modifier.fillMaxSize())

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
                        text = "$score/$totalRounds rime corecte!",
                        style = MaterialTheme.typography.headlineMedium,
                        color = OnBackground
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    StarRating(
                        stars = when {
                            score >= totalRounds - 2 -> 3
                            score >= totalRounds / 2 -> 2
                            score >= 1 -> 1
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
