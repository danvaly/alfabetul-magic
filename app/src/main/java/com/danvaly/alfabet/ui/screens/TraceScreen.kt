package com.danvaly.alfabet.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danvaly.alfabet.audio.SoundManager
import com.danvaly.alfabet.data.RomanianAlphabet
import com.danvaly.alfabet.data.repository.ProgressRepository
import com.danvaly.alfabet.ui.components.DrawingPath
import com.danvaly.alfabet.ui.components.LetterCard
import com.danvaly.alfabet.ui.components.StarRating
import com.danvaly.alfabet.ui.components.TracingCanvas
import com.danvaly.alfabet.ui.components.calculateTraceScore
import com.danvaly.alfabet.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun TraceScreen(
    letterChar: String,
    onBack: () -> Unit,
    soundManager: SoundManager,
    repository: ProgressRepository
) {
    val letter = remember { RomanianAlphabet.getLetterByUppercase(letterChar) }
    val scope = rememberCoroutineScope()
    val paths = remember { mutableStateListOf<DrawingPath>() }
    var currentPath by remember { mutableStateOf<DrawingPath?>(null) }
    var isUppercase by remember { mutableStateOf(true) }
    var showResult by remember { mutableStateOf(false) }
    var stars by remember { mutableIntStateOf(0) }

    val displayLetter = if (isUppercase) letterChar else (letter?.lowercase ?: letterChar.lowercase())

    val infiniteTransition = rememberInfiniteTransition(label = "trace_anim")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    LaunchedEffect(letterChar) {
        soundManager.speakSentence("Scrie litera $letterChar cu degetul")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFE3F2FD), Color(0xFFBBDEFB), BackgroundCream)
                )
            )
            .systemBarsPadding()
    ) {
        // Decorations
        Text(
            text = "\uD83D\uDC1D",
            fontSize = 26.sp,
            modifier = Modifier
                .offset(x = 10.dp, y = (20 + floatOffset).dp)
        )
        Text(
            text = "\uD83C\uDF3A",
            fontSize = 22.sp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 10.dp, y = (-10 + floatOffset).dp)
        )

        Row(modifier = Modifier.fillMaxSize()) {
            // Left panel: controls
            Column(
                modifier = Modifier
                    .width(200.dp)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(DuoBlue.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Înapoi",
                            tint = DuoBlue
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "\u270D\uFE0F",
                            fontSize = 22.sp,
                            modifier = Modifier.offset(y = floatOffset.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Scrie litera",
                            style = MaterialTheme.typography.titleLarge,
                            color = DuoBlue,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Text(
                        text = "Urmărește forma literei cu degetul",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnBackgroundLight
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            isUppercase = !isUppercase
                            paths.clear()
                            currentPath = null
                            showResult = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DuoPurple),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            if (isUppercase) "literă mică" else "LITERĂ MARE",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = {
                            paths.clear()
                            currentPath = null
                            showResult = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DuoRed),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = "Șterge")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Șterge", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            stars = calculateTraceScore(paths)
                            showResult = true
                            soundManager.speakEncouragement(stars >= 2)
                            scope.launch {
                                repository.updateTraceStars(letterChar, stars)
                                repository.onGamePlayed(stars)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DuoGreen),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.Done, contentDescription = "Gata")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Gata!", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            val letters = RomanianAlphabet.letters
                            val currentIndex = letters.indexOfFirst { it.uppercase == letterChar }
                            val nextIndex = (currentIndex + 1) % letters.size
                            paths.clear()
                            currentPath = null
                            showResult = false
                            soundManager.speakLetter(letters[nextIndex].uppercase)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DuoOrange),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Următoarea", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Următoarea")
                    }
                }
            }

            // Right panel: drawing canvas
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    TracingCanvas(
                        guideLetter = displayLetter,
                        paths = paths.toList(),
                        currentPath = currentPath,
                        onDragStart = { offset ->
                            val newPath = DrawingPath(
                                path = Path().apply { moveTo(offset.x, offset.y) },
                                color = DuoBlue,
                                strokeWidth = 14f
                            )
                            currentPath = newPath
                        },
                        onDrag = { offset ->
                            currentPath?.let { current ->
                                val updatedPath = Path().apply {
                                    addPath(current.path)
                                    lineTo(offset.x, offset.y)
                                }
                                currentPath = current.copy(path = updatedPath)
                            }
                        },
                        onDragEnd = {
                            currentPath?.let { paths.add(it) }
                            currentPath = null
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Result overlay
                if (showResult) {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (stars >= 2) DuoGreen else DuoOrange
                        ),
                        elevation = CardDefaults.cardElevation(8.dp),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = when (stars) {
                                    3 -> "\uD83C\uDF89 Excelent!"
                                    2 -> "\u2B50 Foarte bine!"
                                    1 -> "\uD83D\uDC4D Bun început!"
                                    else -> "\uD83D\uDCAA Mai încearcă!"
                                },
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            StarRating(stars = stars)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TraceSelectorScreen(
    onLetterSelect: (String) -> Unit,
    onBack: () -> Unit,
    soundManager: SoundManager? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "trace_sel_anim")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
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
                    colors = listOf(Color(0xFFE3F2FD), Color(0xFFBBDEFB), BackgroundCream)
                )
            )
            .systemBarsPadding()
    ) {
        Text(
            text = "\uD83D\uDC1D",
            fontSize = 26.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-25).dp, y = (40 + floatOffset).dp)
        )

        Column(modifier = Modifier.fillMaxSize()) {
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
                        .background(DuoBlue.copy(alpha = 0.15f))
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Înapoi",
                        tint = DuoBlue
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "\u270D\uFE0F",
                    fontSize = 24.sp,
                    modifier = Modifier.offset(y = floatOffset.dp)
                )
                Text(
                    text = "Alege o literă pentru a scrie",
                    style = MaterialTheme.typography.headlineMedium,
                    color = DuoBlue,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 90.dp),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(RomanianAlphabet.letters.size) { index ->
                    val letter = RomanianAlphabet.letters[index]
                    LetterCard(
                        letter = letter,
                        onClick = {
                            soundManager?.speakLetter(letter.uppercase)
                            onLetterSelect(letter.uppercase)
                        },
                        isHighlighted = true,
                        size = 90.dp
                    )
                }
            }
        }
    }
}
