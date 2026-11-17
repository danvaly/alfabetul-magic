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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.danvaly.alfabet.ui.components.SimpleLetterCard
import com.danvaly.alfabet.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SyllableScreen(
    onBack: () -> Unit,
    soundManager: SoundManager,
    repository: ProgressRepository
) {
    var selectedConsonant by remember { mutableStateOf<String?>(null) }
    var selectedVowel by remember { mutableStateOf<String?>(null) }
    var currentSyllable by remember { mutableStateOf("") }
    val builtSyllables = remember { mutableStateListOf<String>() }
    var wordChallenge by remember { mutableStateOf<Pair<String, List<String>>?>(null) }
    val wordProgress = remember { mutableStateListOf<String>() }
    var showWordComplete by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val consonants = listOf("B", "C", "D", "F", "G", "L", "M", "N", "P", "R", "S", "T", "V", "Z")
    val vowels = listOf("A", "E", "I", "O", "U", "Ă")

    val infiniteTransition = rememberInfiniteTransition(label = "syllable_anim")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    // Form syllable when both selected
    LaunchedEffect(selectedConsonant, selectedVowel) {
        if (selectedConsonant != null && selectedVowel != null) {
            currentSyllable = "$selectedConsonant$selectedVowel"
            soundManager.speakSyllable(currentSyllable)

            wordChallenge?.let { (_, syllables) ->
                if (wordProgress.size < syllables.size &&
                    currentSyllable.equals(syllables[wordProgress.size], ignoreCase = true)
                ) {
                    wordProgress.add(currentSyllable)
                    if (wordProgress.size == syllables.size) {
                        showWordComplete = true
                        soundManager.speakEncouragement(true)
                        scope.launch {
                            repository.onGamePlayed(2)
                        }
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFF3E0), Color(0xFFFFE0B2), BackgroundCream)
                )
            )
    ) {
        // Floating decorations
        Text(
            text = "\uD83E\uDD8B",
            fontSize = 28.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-20).dp, y = (40 + floatOffset).dp)
        )
        Text(
            text = "\uD83C\uDF3A",
            fontSize = 22.sp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-15).dp, y = (-10 + floatOffset).dp)
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            // Left: Consonants + Vowels selection
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Header
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
                        text = "\uD83E\uDD8B",
                        fontSize = 24.sp,
                        modifier = Modifier.offset(y = floatOffset.dp)
                    )
                    Text(
                        text = "Formează silabe",
                        style = MaterialTheme.typography.headlineMedium,
                        color = DuoOrange,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Consonants
                Text(
                    text = "\uD83C\uDF3B Alege o consoană:",
                    style = MaterialTheme.typography.titleMedium,
                    color = OnBackground,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    consonants.forEach { c ->
                        SimpleLetterCard(
                            text = c,
                            backgroundColor = if (selectedConsonant == c) DuoBlue else DuoBlue.copy(alpha = 0.4f),
                            onClick = {
                                selectedConsonant = c
                                selectedVowel = null
                                currentSyllable = ""
                                soundManager.speakLetter(c)
                            },
                            size = 56.dp,
                            fontSize = 22
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Vowels
                Text(
                    text = "\uD83C\uDF39 Alege o vocală:",
                    style = MaterialTheme.typography.titleMedium,
                    color = OnBackground,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    vowels.forEach { v ->
                        SimpleLetterCard(
                            text = v,
                            backgroundColor = if (selectedVowel == v) VowelColor else VowelColor.copy(alpha = 0.4f),
                            onClick = {
                                selectedVowel = v
                                soundManager.speakLetter(v)
                            },
                            size = 56.dp,
                            fontSize = 22
                        )
                    }
                }
            }

            // Right: Result + Word building
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Current syllable display
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (currentSyllable.isNotEmpty()) DuoGreen else SurfaceWhite
                    ),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (currentSyllable.isNotEmpty()) currentSyllable else "_ _",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Black,
                            color = if (currentSyllable.isNotEmpty()) Color.White else OnBackgroundLight,
                            textAlign = TextAlign.Center,
                            // "_" sits on the baseline; lift the empty placeholder to the card's middle
                            modifier = Modifier.offset(y = if (currentSyllable.isEmpty()) (-12).dp else 0.dp)
                        )
                        if (currentSyllable.isNotEmpty()) {
                            IconButton(
                                onClick = { soundManager.speakSyllable(currentSyllable) }
                            ) {
                                Icon(
                                    Icons.Filled.VolumeUp,
                                    contentDescription = "Ascultă silaba",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }

                // Word challenge
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "\uD83D\uDC3B Formează cuvântul:",
                            style = MaterialTheme.typography.titleMedium,
                            color = OnBackground,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (wordChallenge != null) {
                            val (word, syllables) = wordChallenge!!
                            Text(
                                text = word,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                color = DuoOrange
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                syllables.forEachIndexed { index, syllable ->
                                    val isCompleted = index < wordProgress.size
                                    Card(
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isCompleted) DuoGreen else DuoBlue.copy(alpha = 0.15f)
                                        )
                                    ) {
                                        Text(
                                            text = if (isCompleted) wordProgress[index] else "?",
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isCompleted) Color.White else OnBackgroundLight,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }

                            AnimatedVisibility(
                                visible = showWordComplete,
                                enter = scaleIn() + fadeIn()
                            ) {
                                Text(
                                    text = "\uD83C\uDF89 Bravo!",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    color = DuoGreen,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                val words = RomanianAlphabet.simpleWords.entries.toList()
                                val entry = words.random()
                                wordChallenge = entry.key to entry.value
                                wordProgress.clear()
                                showWordComplete = false
                                selectedConsonant = null
                                selectedVowel = null
                                currentSyllable = ""
                                soundManager.speakWord(entry.key.lowercase())
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DuoOrange),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = if (wordChallenge == null) "Începe!" else "Cuvânt nou",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
