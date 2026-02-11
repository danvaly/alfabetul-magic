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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
    var wordProgress = remember { mutableStateListOf<String>() }
    var showWordComplete by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val consonants = listOf("B", "C", "D", "F", "G", "L", "M", "N", "P", "R", "S", "T", "V", "Z")
    val vowels = listOf("A", "E", "I", "O", "U", "Ă")

    // Form syllable when both selected
    LaunchedEffect(selectedConsonant, selectedVowel) {
        if (selectedConsonant != null && selectedVowel != null) {
            currentSyllable = "$selectedConsonant$selectedVowel"
            soundManager.speakSyllable(currentSyllable)

            // Check if this syllable matches word challenge
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
                    colors = listOf(BackgroundCream, BackgroundLight)
                )
            )
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Left: Consonants + Vowels selection
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(PrimaryPink.copy(alpha = 0.1f))
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Inapoi",
                            tint = PrimaryPink
                        )
                    }
                    Text(
                        text = "Formeaza Silabe",
                        style = MaterialTheme.typography.headlineMedium,
                        color = PrimaryPink,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Consonants
                Text(
                    text = "Alege o consoana:",
                    style = MaterialTheme.typography.titleMedium,
                    color = OnBackground,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    consonants.forEach { c ->
                        SimpleLetterCard(
                            text = c,
                            backgroundColor = if (selectedConsonant == c) ConsonantColor else ConsonantColor.copy(alpha = 0.4f),
                            onClick = {
                                selectedConsonant = c
                                selectedVowel = null
                                currentSyllable = ""
                                soundManager.speakLetter(c)
                            },
                            size = 64.dp,
                            fontSize = 24
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Vowels
                Text(
                    text = "Alege o vocala:",
                    style = MaterialTheme.typography.titleMedium,
                    color = OnBackground,
                    fontWeight = FontWeight.Bold
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
                            size = 64.dp,
                            fontSize = 24
                        )
                    }
                }
            }

            // Right: Result + Word building
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Current syllable display
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (currentSyllable.isNotEmpty()) CorrectGreenLight else SurfaceWhite
                    ),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (currentSyllable.isNotEmpty()) currentSyllable else "_ _",
                            fontSize = 64.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (currentSyllable.isNotEmpty()) CorrectGreen else OnBackgroundLight,
                            textAlign = TextAlign.Center
                        )
                        if (currentSyllable.isNotEmpty()) {
                            IconButton(
                                onClick = { soundManager.speakSyllable(currentSyllable) }
                            ) {
                                Icon(
                                    Icons.Filled.VolumeUp,
                                    contentDescription = "Asculta silaba",
                                    tint = CorrectGreen,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }

                // Word challenge
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Formeaza cuvantul:",
                            style = MaterialTheme.typography.titleMedium,
                            color = OnBackground,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (wordChallenge != null) {
                            val (word, syllables) = wordChallenge!!
                            Text(
                                text = word,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryPink
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                syllables.forEachIndexed { index, syllable ->
                                    val isCompleted = index < wordProgress.size
                                    Card(
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isCompleted) CorrectGreenLight else PastelColors[4]
                                        )
                                    ) {
                                        Text(
                                            text = if (isCompleted) wordProgress[index] else "?",
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isCompleted) CorrectGreen else OnBackgroundLight,
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                        )
                                    }
                                }
                            }

                            // Word complete celebration
                            AnimatedVisibility(
                                visible = showWordComplete,
                                enter = scaleIn() + fadeIn()
                            ) {
                                Text(
                                    text = "Bravo!",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StarGoldDark,
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
                                soundManager.speakWord(entry.key)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryPurple),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(if (wordChallenge == null) "Incepe!" else "Cuvant nou")
                        }
                    }
                }
            }
        }
    }
}
