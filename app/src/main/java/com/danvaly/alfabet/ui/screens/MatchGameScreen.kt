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
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danvaly.alfabet.audio.SoundManager
import com.danvaly.alfabet.data.DifficultyLevel
import com.danvaly.alfabet.data.DifficultyManager
import com.danvaly.alfabet.data.RomanianAlphabet
import com.danvaly.alfabet.data.repository.ProgressRepository
import com.danvaly.alfabet.ui.components.StarRating
import com.danvaly.alfabet.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class MatchCard(
    val id: Int,
    val text: String,
    val matchId: String,
    var isFlipped: Boolean = false,
    var isMatched: Boolean = false
)

@Composable
fun MatchGameScreen(
    onBack: () -> Unit,
    soundManager: SoundManager,
    repository: ProgressRepository
) {
    val scope = rememberCoroutineScope()
    var moves by remember { mutableIntStateOf(0) }
    var matchedPairs by remember { mutableIntStateOf(0) }
    var gameOver by remember { mutableStateOf(false) }
    val firstSelection = remember { mutableStateOf<Int?>(null) }
    val secondSelection = remember { mutableStateOf<Int?>(null) }
    var isChecking by remember { mutableStateOf(false) }

    val stats by repository.appStats.collectAsState(initial = null)
    val level = DifficultyManager.fromInt(stats?.difficulty ?: 1)
    val pairCount = when (level) {
        DifficultyLevel.BEGINNER -> 4
        DifficultyLevel.INTERMEDIATE -> 6
        DifficultyLevel.ADVANCED -> 8
    }

    val cards = remember(pairCount) {
        val selected = RomanianAlphabet.getRandomLetters(pairCount, "")
        val cardList = mutableStateListOf<MatchCard>()
        var id = 0
        selected.forEach { letter ->
            cardList.add(MatchCard(id = id++, text = letter.uppercase, matchId = letter.uppercase))
            cardList.add(MatchCard(id = id++, text = letter.lowercase, matchId = letter.uppercase))
        }
        cardList.shuffle()
        cardList
    }

    val infiniteTransition = rememberInfiniteTransition(label = "match_anim")
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
                    colors = listOf(Color(0xFFFFF3E0), Color(0xFFFFE0B2), Color(0xFFFFF8E1))
                )
            )
    ) {
        // Floating decorations
        Text(
            text = "\uD83D\uDC30",
            fontSize = 28.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-20).dp, y = (50 + floatOffset).dp)
        )
        Text(
            text = "\uD83C\uDF3B",
            fontSize = 24.sp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 15.dp, y = (-15 + floatOffset).dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(DuoOrange.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Înapoi",
                            tint = DuoOrange
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "\uD83C\uDCCF",
                        fontSize = 26.sp,
                        modifier = Modifier.offset(y = floatOffset.dp)
                    )
                    Text(
                        text = "Potrivește literele",
                        style = MaterialTheme.typography.headlineMedium,
                        color = DuoOrange,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DuoOrange)
                ) {
                    Text(
                        text = "Mutări: $moves",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }

            // Card grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(horizontal = 40.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(cards.size) { index ->
                    val card = cards[index]
                    MemoryCard(
                        card = card,
                        onClick = {
                            if (isChecking || card.isFlipped || card.isMatched) return@MemoryCard

                            cards[index] = card.copy(isFlipped = true)
                            soundManager.speakLetter(card.text)

                            if (firstSelection.value == null) {
                                firstSelection.value = index
                            } else {
                                secondSelection.value = index
                                moves++
                                isChecking = true

                                val first = cards[firstSelection.value!!]
                                val second = cards[index]

                                scope.launch {
                                    delay(800)
                                    if (first.matchId == second.matchId) {
                                        cards[firstSelection.value!!] = first.copy(isMatched = true)
                                        cards[index] = second.copy(isMatched = true)
                                        matchedPairs++
                                        soundManager.speakEncouragement(true)
                                        if (matchedPairs == pairCount) {
                                            gameOver = true
                                            val stars = when {
                                                moves <= pairCount + 2 -> 3
                                                moves <= pairCount * 2 -> 2
                                                else -> 1
                                            }
                                            scope.launch {
                                                repository.onGamePlayed(stars)
                                            }
                                        }
                                    } else {
                                        cards[firstSelection.value!!] = first.copy(isFlipped = false)
                                        cards[index] = second.copy(isFlipped = false)
                                    }
                                    firstSelection.value = null
                                    secondSelection.value = null
                                    isChecking = false
                                }
                            }
                        }
                    )
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
                        text = "Felicitări!",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        color = DuoGreen
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Ai terminat în $moves mutări!",
                        style = MaterialTheme.typography.titleLarge,
                        color = OnBackground
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    StarRating(
                        stars = when {
                            moves <= pairCount + 2 -> 3
                            moves <= pairCount * 2 -> 2
                            else -> 1
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
private fun MemoryCard(
    card: MatchCard,
    onClick: () -> Unit
) {
    val scale = remember { Animatable(1f) }

    LaunchedEffect(card.isFlipped) {
        if (card.isFlipped) {
            scale.animateTo(1.08f, spring(stiffness = Spring.StiffnessHigh))
            scale.animateTo(1f, spring(stiffness = Spring.StiffnessMedium))
        }
    }

    val bgColor = when {
        card.isMatched -> DuoGreen
        card.isFlipped -> DuoBlue
        else -> Color(0xFFE0E0E0)
    }
    val shadowColor = when {
        card.isMatched -> DuoGreenDark
        card.isFlipped -> DuoBlueDark
        else -> Color(0xFFBDBDBD)
    }

    Box(
        modifier = Modifier
            .size(100.dp)
            .scale(scale.value)
    ) {
        // 3D shadow
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
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                ),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = bgColor),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (card.isFlipped || card.isMatched) {
                    Text(
                        text = card.text,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                } else {
                    Text(
                        text = "?",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
