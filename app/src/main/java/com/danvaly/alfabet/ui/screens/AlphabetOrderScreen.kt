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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import com.danvaly.alfabet.ui.components.StarRating
import com.danvaly.alfabet.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AlphabetOrderScreen(
    onBack: () -> Unit,
    soundManager: SoundManager,
    repository: ProgressRepository
) {
    val scope = rememberCoroutineScope()
    var currentRound by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var gameOver by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }
    val totalRounds = 5
    val lettersPerRound = 5

    val allLetters = RomanianAlphabet.letters.map { it.uppercase }

    // Generate sorted subset for current round
    val sortedSubset = remember(currentRound) {
        val startIndex = (currentRound * lettersPerRound) % (allLetters.size - lettersPerRound)
        allLetters.subList(startIndex, startIndex + lettersPerRound)
    }
    val shuffledLetters = remember(currentRound) {
        mutableStateListOf<String>().apply { addAll(sortedSubset.shuffled()) }
    }
    val selectedOrder = remember(currentRound) { mutableStateListOf<String>() }
    var showFeedback by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "order_anim")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
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
                    colors = listOf(Color(0xFFE0F7FA), Color(0xFFB2EBF2), Color(0xFFE0F2F1))
                )
            )
            .systemBarsPadding()
    ) {
        Text(
            text = "\uD83D\uDC22",
            fontSize = 28.sp,
            modifier = Modifier.offset(x = 20.dp, y = (30 + floatOffset).dp)
        )
        Text(
            text = "\uD83C\uDF3B",
            fontSize = 24.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-25).dp, y = (40 + floatOffset).dp)
        )
        Text(
            text = "\uD83D\uDC1D",
            fontSize = 22.sp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-20).dp, y = (-15 + floatOffset).dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
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
                        .background(DuoBlue.copy(alpha = 0.15f))
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Înapoi", tint = DuoBlue)
                }
                Text(
                    text = "\uD83D\uDD24 Ordonează alfabetul",
                    style = MaterialTheme.typography.headlineMedium,
                    color = DuoBlue,
                    fontWeight = FontWeight.ExtraBold
                )
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DuoBlue)
                ) {
                    Text(
                        text = "${currentRound + 1}/$totalRounds",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { (currentRound + 1).toFloat() / totalRounds },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = DuoBlue,
                trackColor = DuoBlue.copy(alpha = 0.2f)
            )

            if (!gameOver) {
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Selected order
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Pune literele în ordine:",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = OnBackground,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (showFeedback && isCorrect) DuoGreen.copy(alpha = 0.15f)
                                else if (showFeedback) DuoRed.copy(alpha = 0.15f)
                                else SurfaceWhite
                            ),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (selectedOrder.isEmpty()) {
                                    Text(
                                        text = "Atinge literele în ordine alfabetică...",
                                        color = OnBackgroundLight,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                } else {
                                    selectedOrder.forEachIndexed { index, letter ->
                                        Card(
                                            modifier = Modifier
                                                .padding(horizontal = 4.dp)
                                                .clickable {
                                                    if (!showFeedback) {
                                                        shuffledLetters.add(letter)
                                                        selectedOrder.removeAt(index)
                                                    }
                                                },
                                            shape = RoundedCornerShape(12.dp),
                                            colors = CardDefaults.cardColors(containerColor = DuoBlue)
                                        ) {
                                            Text(
                                                text = letter,
                                                color = Color.White,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 20.sp,
                                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (showFeedback) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isCorrect) "\u2705 Corect!" else "\u274C Ordinea corectă: ${sortedSubset.joinToString(" ")}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isCorrect) DuoGreen else DuoRed,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Right: Available letters + check
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Litere disponibile:",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = OnBackground
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            shuffledLetters.forEachIndexed { index, letter ->
                                Box(modifier = Modifier.size(64.dp)) {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .offset(y = 3.dp),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0))
                                    ) {}
                                    Card(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clickable {
                                                if (!showFeedback) {
                                                    selectedOrder.add(letter)
                                                    shuffledLetters.removeAt(index)
                                                    soundManager.speakLetter(letter)
                                                }
                                            },
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = letter,
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.Black,
                                                color = OnBackground
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (!showFeedback && selectedOrder.size == lettersPerRound) {
                            Button(
                                onClick = {
                                    showFeedback = true
                                    isCorrect = selectedOrder.toList() == sortedSubset
                                    if (isCorrect) {
                                        score++
                                        soundManager.speakEncouragement(true)
                                    } else {
                                        soundManager.speakEncouragement(false)
                                    }
                                    scope.launch {
                                        delay(2000)
                                        if (currentRound + 1 >= totalRounds) {
                                            gameOver = true
                                            showConfetti = true
                                            val stars = when {
                                                score >= totalRounds - 1 -> 3
                                                score >= totalRounds / 2 -> 2
                                                score >= 1 -> 1
                                                else -> 0
                                            }
                                            scope.launch { repository.onGamePlayed(stars) }
                                        } else {
                                            currentRound++
                                            showFeedback = false
                                            isCorrect = false
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DuoGreen),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text("Verifică!", fontWeight = FontWeight.Bold, fontSize = 18.sp)
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
                        text = "Felicitări!",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        color = DuoGreen
                    )
                    Text(
                        text = "$score/$totalRounds ordonate corect!",
                        style = MaterialTheme.typography.headlineMedium,
                        color = OnBackground
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    StarRating(
                        stars = when {
                            score >= totalRounds - 1 -> 3
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
