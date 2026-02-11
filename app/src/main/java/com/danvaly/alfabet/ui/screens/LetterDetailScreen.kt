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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Draw
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danvaly.alfabet.audio.SoundManager
import com.danvaly.alfabet.data.RomanianAlphabet
import com.danvaly.alfabet.data.model.Letter
import com.danvaly.alfabet.data.repository.ProgressRepository
import com.danvaly.alfabet.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun LetterDetailScreen(
    letterChar: String,
    onBack: () -> Unit,
    onTrace: () -> Unit,
    soundManager: SoundManager,
    repository: ProgressRepository
) {
    val letter = remember { RomanianAlphabet.getLetterByUppercase(letterChar) }
    val scope = rememberCoroutineScope()
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(letterChar) {
        isVisible = true
        soundManager.speakLetterIntro(letterChar, letter?.phoneticSound ?: "")
        scope.launch {
            repository.markLetterLearned(letterChar)
        }
    }

    if (letter == null) return

    val letterColor = when {
        letter.isDiacritic -> DiacriticColor
        letter.isVowel -> VowelColor
        else -> ConsonantColor
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
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                    text = "Litera ${letter.uppercase}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = PrimaryPink,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }

            // Main content - landscape layout
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Left: Big animated letter
                AnimatedVisibility(
                    visible = isVisible,
                    enter = scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        BouncingLetter(
                            letter = letter,
                            letterColor = letterColor,
                            onClick = {
                                soundManager.speakLetter(letter.uppercase)
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${letter.uppercase} face \"${letter.phoneticSound}\"",
                            style = MaterialTheme.typography.titleMedium,
                            color = OnBackgroundLight,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Hear button
                            Button(
                                onClick = { soundManager.speakPhonetic(letter.phoneticSound) },
                                colors = ButtonDefaults.buttonColors(containerColor = letterColor),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(Icons.Filled.VolumeUp, contentDescription = "Asculta")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Asculta")
                            }
                            // Trace button
                            Button(
                                onClick = onTrace,
                                colors = ButtonDefaults.buttonColors(containerColor = SecondaryPurple),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(Icons.Filled.Draw, contentDescription = "Scrie")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Scrie")
                            }
                        }
                    }
                }

                // Right: Example words
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Cuvinte cu litera ${letter.uppercase}:",
                        style = MaterialTheme.typography.titleLarge,
                        color = OnBackground,
                        fontWeight = FontWeight.Bold
                    )
                    letter.words.forEach { word ->
                        WordCard(
                            emoji = word.emoji,
                            word = word.word,
                            highlightLetter = letter.uppercase,
                            onClick = { soundManager.speakWord(word.word) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BouncingLetter(
    letter: Letter,
    letterColor: Color,
    onClick: () -> Unit
) {
    val scale = remember { Animatable(0.5f) }

    LaunchedEffect(letter) {
        scale.animateTo(
            1f,
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        )
    }

    Card(
        modifier = Modifier
            .size(200.dp)
            .scale(scale.value)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = letterColor),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = letter.uppercase,
                    fontSize = 96.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = Color.White
                )
                Text(
                    text = letter.lowercase,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = FontFamily.Serif,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun WordCard(
    emoji: String,
    word: String,
    highlightLetter: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = emoji, fontSize = 40.sp)
            Text(
                text = word,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = OnBackground
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                Icons.Filled.VolumeUp,
                contentDescription = "Asculta $word",
                tint = PrimaryPink,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
