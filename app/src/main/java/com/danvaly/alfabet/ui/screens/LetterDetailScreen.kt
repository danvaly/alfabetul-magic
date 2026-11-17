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
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.MenuBook
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
    repository: ProgressRepository,
    onStory: () -> Unit = {}
) {
    val letter = remember { RomanianAlphabet.getLetterByUppercase(letterChar) }
    val scope = rememberCoroutineScope()
    var isVisible by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "detail_anim")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )
    val floatOffset2 by infiniteTransition.animateFloat(
        initialValue = 4f,
        targetValue = -4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float2"
    )

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
                    colors = listOf(Color(0xFFE8F5E9), BackgroundCream, BackgroundLight)
                )
            )
    ) {
        // Floating nature decorations
        Text(
            text = "\uD83E\uDD8B",
            fontSize = 28.sp,
            modifier = Modifier
                .offset(x = 20.dp, y = (30 + floatOffset).dp)
        )
        Text(
            text = "\uD83C\uDF3A",
            fontSize = 24.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-15).dp, y = (25 + floatOffset2).dp)
        )
        Text(
            text = "\uD83D\uDC1E",
            fontSize = 22.sp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 20.dp, y = (-15 + floatOffset).dp)
        )
        Text(
            text = "\uD83C\uDF37",
            fontSize = 22.sp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-20).dp, y = (-10 + floatOffset2).dp)
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
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(DuoGreen.copy(alpha = 0.15f))
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Înapoi",
                        tint = DuoGreen
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "\uD83D\uDC3B",
                    fontSize = 28.sp,
                    modifier = Modifier.offset(y = floatOffset.dp)
                )
                Text(
                    text = "Litera ${letter.uppercase}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = DuoGreen,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(start = 8.dp)
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
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = 16.dp)
                    ) {
                        BouncingLetter(
                            letter = letter,
                            letterColor = letterColor,
                            onClick = {
                                soundManager.speakLetter(letter.uppercase)
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = letterColor.copy(alpha = 0.15f))
                        ) {
                            Text(
                                text = "${letter.uppercase} face \"${letter.phoneticSound}\"",
                                style = MaterialTheme.typography.titleMedium,
                                color = OnBackground,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Hear button - Duolingo 3D style
                            Button(
                                onClick = { soundManager.speakPhonetic(letter.phoneticSound) },
                                colors = ButtonDefaults.buttonColors(containerColor = DuoGreen),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Icon(Icons.Filled.VolumeUp, contentDescription = "Ascultă")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Ascultă", fontWeight = FontWeight.Bold)
                            }
                            // Trace button
                            Button(
                                onClick = onTrace,
                                colors = ButtonDefaults.buttonColors(containerColor = DuoBlue),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Icon(Icons.Filled.Draw, contentDescription = "Scrie")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Scrie", fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        // Story button
                        Button(
                            onClick = onStory,
                            colors = ButtonDefaults.buttonColors(containerColor = DuoOrange),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Icon(Icons.Filled.MenuBook, contentDescription = "Povestea literei")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Povestea literei", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Right: Example words
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Cuvinte cu litera ${letter.uppercase}:",
                            style = MaterialTheme.typography.titleLarge,
                            color = OnBackground,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "\uD83D\uDC1D",
                            fontSize = 20.sp,
                            modifier = Modifier.offset(y = floatOffset2.dp)
                        )
                    }
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
    val infiniteTransition = rememberInfiniteTransition(label = "bounce_letter")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    LaunchedEffect(letter) {
        scale.animateTo(
            1f,
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        )
    }

    // Duolingo-style 3D card
    Box {
        // Shadow
        Card(
            modifier = Modifier
                .size(200.dp)
                .scale(scale.value * pulse)
                .offset(y = 6.dp),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = letterColor.copy(alpha = 0.6f)),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {}

        Card(
            modifier = Modifier
                .size(200.dp)
                .scale(scale.value * pulse)
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = letterColor),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = letter.uppercase,
                        fontSize = 96.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif,
                        color = Color.White
                    )
                    Text(
                        text = letter.lowercase,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
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
        shape = RoundedCornerShape(20.dp),
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
                fontWeight = FontWeight.ExtraBold,
                color = OnBackground
            )
            Spacer(modifier = Modifier.weight(1f))
            Card(
                shape = CircleShape,
                colors = CardDefaults.cardColors(containerColor = DuoGreen.copy(alpha = 0.15f))
            ) {
                Icon(
                    Icons.Filled.VolumeUp,
                    contentDescription = "Ascultă $word",
                    tint = DuoGreen,
                    modifier = Modifier
                        .size(36.dp)
                        .padding(6.dp)
                )
            }
        }
    }
}
