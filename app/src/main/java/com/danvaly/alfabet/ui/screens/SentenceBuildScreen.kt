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
fun SentenceBuildScreen(
    onBack: () -> Unit,
    soundManager: SoundManager,
    repository: ProgressRepository
) {
    val scope = rememberCoroutineScope()
    val allSentences = remember { RomanianAlphabet.sentences.shuffled() }
    var currentIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var gameOver by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }
    val totalRounds = allSentences.size.coerceAtMost(8)

    val currentSentence = allSentences.getOrNull(currentIndex)
    val selectedWords = remember(currentIndex) { mutableStateListOf<String>() }
    val availableWords = remember(currentIndex) {
        mutableStateListOf<String>().apply {
            addAll(currentSentence?.words?.shuffled() ?: emptyList())
        }
    }
    var showFeedback by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "sentence_anim")
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
                    colors = listOf(Color(0xFFE8F5E9), Color(0xFFC8E6C9), Color(0xFFE0F2F1))
                )
            )
            .systemBarsPadding()
    ) {
        Text(
            text = "\uD83D\uDC26",
            fontSize = 28.sp,
            modifier = Modifier.offset(x = 20.dp, y = (30 + floatOffset).dp)
        )
        Text(
            text = "\uD83C\uDF3C",
            fontSize = 24.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-25).dp, y = (40 + floatOffset).dp)
        )
        Text(
            text = "\uD83D\uDC1B",
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
                        .background(DuoGreen.copy(alpha = 0.15f))
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Înapoi", tint = DuoGreen)
                }
                Text(
                    text = "\uD83D\uDCDD Construiește propoziția",
                    style = MaterialTheme.typography.headlineMedium,
                    color = DuoGreen,
                    fontWeight = FontWeight.ExtraBold
                )
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DuoGreen)
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
                color = DuoGreen,
                trackColor = DuoGreen.copy(alpha = 0.2f)
            )

            if (currentSentence != null && !gameOver) {
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Emoji hint and sentence area
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = currentSentence.emoji,
                            fontSize = 64.sp,
                            modifier = Modifier.offset(y = floatOffset.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Pune cuvintele în ordine:",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = OnBackground,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Selected words area
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
                                if (selectedWords.isEmpty()) {
                                    Text(
                                        text = "Atinge cuvintele de mai jos...",
                                        color = OnBackgroundLight,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                } else {
                                    selectedWords.forEachIndexed { index, word ->
                                        Card(
                                            modifier = Modifier
                                                .padding(horizontal = 4.dp)
                                                .clickable {
                                                    if (!showFeedback) {
                                                        availableWords.add(word)
                                                        selectedWords.removeAt(index)
                                                    }
                                                },
                                            shape = RoundedCornerShape(12.dp),
                                            colors = CardDefaults.cardColors(containerColor = DuoGreen)
                                        ) {
                                            Text(
                                                text = word,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Right: Available words + check
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Cuvinte disponibile:",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = OnBackground
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            availableWords.forEachIndexed { index, word ->
                                Box {
                                    Card(
                                        modifier = Modifier.offset(y = 3.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0))
                                    ) {
                                        Text(
                                            text = word,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                            color = Color.Transparent
                                        )
                                    }
                                    Card(
                                        modifier = Modifier.clickable {
                                            if (!showFeedback) {
                                                selectedWords.add(word)
                                                availableWords.removeAt(index)
                                            }
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                                    ) {
                                        Text(
                                            text = word,
                                            fontWeight = FontWeight.Bold,
                                            color = OnBackground,
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (!showFeedback && selectedWords.size == currentSentence.words.size) {
                            Button(
                                onClick = {
                                    showFeedback = true
                                    isCorrect = selectedWords.toList() == currentSentence.words
                                    if (isCorrect) {
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
                                                score >= totalRounds - 1 -> 3
                                                score >= totalRounds / 2 -> 2
                                                score >= 1 -> 1
                                                else -> 0
                                            }
                                            scope.launch { repository.onGamePlayed(stars) }
                                        } else {
                                            currentIndex++
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

                        if (showFeedback) {
                            Text(
                                text = if (isCorrect) "\u2705 Corect!" else "\u274C Greșit! Mai încearcă!",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (isCorrect) DuoGreen else DuoRed
                            )
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
                        text = "$score/$totalRounds corecte!",
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
