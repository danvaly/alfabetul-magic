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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
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

data class MatchCard(
    val id: Int,
    val letter: String,
    val isUppercase: Boolean,
    val pairId: Int,
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
    val totalPairs = 6

    val cards = remember {
        val selectedLetters = RomanianAlphabet.letters.shuffled().take(totalPairs)
        val cardList = mutableListOf<MatchCard>()
        selectedLetters.forEachIndexed { index, letter ->
            cardList.add(MatchCard(index * 2, letter.uppercase, true, index))
            cardList.add(MatchCard(index * 2 + 1, letter.lowercase, false, index))
        }
        mutableStateListOf(*cardList.shuffled().toTypedArray())
    }

    var firstFlipped by remember { mutableStateOf<Int?>(null) }
    var isChecking by remember { mutableStateOf(false) }

    fun flipCard(index: Int) {
        if (isChecking || cards[index].isMatched || cards[index].isFlipped) return

        cards[index] = cards[index].copy(isFlipped = true)
        soundManager.speakLetter(cards[index].letter)

        if (firstFlipped == null) {
            firstFlipped = index
        } else {
            moves++
            isChecking = true
            val first = firstFlipped!!
            if (cards[first].pairId == cards[index].pairId) {
                // Match!
                scope.launch {
                    delay(300)
                    cards[first] = cards[first].copy(isMatched = true)
                    cards[index] = cards[index].copy(isMatched = true)
                    matchedPairs++
                    soundManager.speakEncouragement(true)
                    firstFlipped = null
                    isChecking = false

                    if (matchedPairs == totalPairs) {
                        gameOver = true
                        val stars = when {
                            moves <= totalPairs + 2 -> 3
                            moves <= totalPairs * 2 -> 2
                            else -> 1
                        }
                        repository.onGamePlayed(stars)
                    }
                }
            } else {
                // No match - flip back
                scope.launch {
                    delay(800)
                    cards[first] = cards[first].copy(isFlipped = false)
                    cards[index] = cards[index].copy(isFlipped = false)
                    firstFlipped = null
                    isChecking = false
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFF3E5F5), Color(0xFFE1BEE7), Color(0xFFCE93D8))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
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
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Inapoi", tint = SecondaryPurple)
                }

                Text(
                    text = "Potriveste Literele",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = SecondaryPurple
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SecondaryPurple)
                    ) {
                        Text(
                            text = "Mutari: $moves",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CorrectGreen)
                    ) {
                        Text(
                            text = "Perechi: $matchedPairs/$totalPairs",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(cards.toList()) { index, card ->
                    MemoryCard(
                        card = card,
                        onClick = { flipCard(index) }
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
                elevation = CardDefaults.cardElevation(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🃏", fontSize = 56.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Felicitari!",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = SecondaryPurple
                    )
                    Text(
                        text = "Ai gasit toate perechile in $moves mutari",
                        style = MaterialTheme.typography.bodyLarge,
                        color = OnBackgroundLight,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    StarRating(
                        stars = when {
                            moves <= totalPairs + 2 -> 3
                            moves <= totalPairs * 2 -> 2
                            else -> 1
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
                                // Reset
                                val selectedLetters = RomanianAlphabet.letters.shuffled().take(totalPairs)
                                cards.clear()
                                val newCards = mutableListOf<MatchCard>()
                                selectedLetters.forEachIndexed { index, letter ->
                                    newCards.add(MatchCard(index * 2, letter.uppercase, true, index))
                                    newCards.add(MatchCard(index * 2 + 1, letter.lowercase, false, index))
                                }
                                cards.addAll(newCards.shuffled())
                                moves = 0
                                matchedPairs = 0
                                gameOver = false
                                firstFlipped = null
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
private fun MemoryCard(
    card: MatchCard,
    onClick: () -> Unit
) {
    val scale = remember { Animatable(1f) }

    LaunchedEffect(card.isFlipped, card.isMatched) {
        if (card.isFlipped || card.isMatched) {
            scale.animateTo(1.1f, spring(stiffness = Spring.StiffnessHigh))
            scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
    }

    Card(
        modifier = Modifier
            .height(100.dp)
            .scale(scale.value)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                card.isMatched -> CorrectGreenLight
                card.isFlipped -> PastelColors[5]
                else -> SecondaryPurpleLight.copy(alpha = 0.5f)
            }
        ),
        elevation = CardDefaults.cardElevation(if (card.isFlipped || card.isMatched) 8.dp else 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (card.isFlipped || card.isMatched) {
                Text(
                    text = card.letter,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (card.isMatched) CorrectGreen else SecondaryPurple
                )
            } else {
                Text(
                    text = "?",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = SecondaryPurple.copy(alpha = 0.5f)
                )
            }
        }
    }
}
