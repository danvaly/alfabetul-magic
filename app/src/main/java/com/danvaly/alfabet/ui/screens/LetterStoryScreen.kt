package com.danvaly.alfabet.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.danvaly.alfabet.ui.theme.*

@Composable
fun LetterStoryScreen(
    letterChar: String,
    onBack: () -> Unit,
    soundManager: SoundManager
) {
    val letter = remember { RomanianAlphabet.getLetterByUppercase(letterChar) }
    var isVisible by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "story_anim")
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
        if (letter != null && letter.story.isNotEmpty()) {
            soundManager.speakWord(letter.story)
        }
    }

    if (letter == null) return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFF3E0), Color(0xFFFFE0B2), Color(0xFFFFF8E1))
                )
            )
            .systemBarsPadding()
    ) {
        Text(
            text = "\uD83C\uDF3B",
            fontSize = 24.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-25).dp, y = (35 + floatOffset2).dp)
        )
        Text(
            text = "\uD83D\uDC1E",
            fontSize = 22.sp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 15.dp, y = (-15 + floatOffset).dp)
        )
        Text(
            text = "\uD83D\uDC23",
            fontSize = 26.sp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-20).dp, y = (-15 + floatOffset2).dp)
        )

        Column(modifier = Modifier.fillMaxSize()) {
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
                        .background(DuoOrange.copy(alpha = 0.15f))
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Înapoi", tint = DuoOrange)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "\uD83D\uDCD6",
                    fontSize = 28.sp,
                    modifier = Modifier.offset(y = floatOffset.dp)
                )
                Text(
                    text = "Povestea literei ${letter.uppercase}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = DuoOrange,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Left: Big letter with animation
                AnimatedVisibility(
                    visible = isVisible,
                    enter = scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Card(
                            modifier = Modifier.size(180.dp),
                            shape = RoundedCornerShape(32.dp),
                            colors = CardDefaults.cardColors(containerColor = DuoOrange),
                            elevation = CardDefaults.cardElevation(8.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = letter.uppercase,
                                        fontSize = 80.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                    Text(
                                        text = letter.lowercase,
                                        fontSize = 40.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                if (letter.story.isNotEmpty()) {
                                    soundManager.speakWord(letter.story)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DuoOrange),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Icon(Icons.Filled.VolumeUp, contentDescription = "Ascultă povestea")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Ascultă povestea", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Right: Story and words
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "\uD83D\uDCD6", fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Povestea",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = OnBackground
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = letter.story,
                                style = MaterialTheme.typography.bodyLarge,
                                color = OnBackground,
                                lineHeight = 26.sp
                            )
                        }
                    }

                    Text(
                        text = "\uD83C\uDF1F Cuvinte cu ${letter.uppercase}:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = OnBackground
                    )

                    letter.words.forEach { word ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { soundManager.speakWord(word.word) },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = DuoOrange.copy(alpha = 0.1f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(text = word.emoji, fontSize = 32.sp)
                                Text(
                                    text = word.word,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = OnBackground
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
