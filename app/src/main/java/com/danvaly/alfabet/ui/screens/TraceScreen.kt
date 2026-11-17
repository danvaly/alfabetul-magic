package com.danvaly.alfabet.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.clickable
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danvaly.alfabet.audio.SoundManager
import com.danvaly.alfabet.data.RomanianAlphabet
import com.danvaly.alfabet.data.repository.ProgressRepository
import com.danvaly.alfabet.ui.components.DrawingPath
import com.danvaly.alfabet.ui.components.LetterCard
import com.danvaly.alfabet.ui.components.StarRating
import com.danvaly.alfabet.ui.components.TraceGuide
import com.danvaly.alfabet.ui.components.TracingCanvas
import com.danvaly.alfabet.ui.components.scoreTrace
import com.danvaly.alfabet.ui.LocalReadingStore
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
    val store = LocalReadingStore.current
    // "Următoarea" moves on inside this screen, so the current letter is local state.
    var currentLetter by remember { mutableStateOf(letterChar) }
    val letter = remember(currentLetter) { RomanianAlphabet.getLetterByUppercase(currentLetter) }
    val scope = rememberCoroutineScope()
    val paths = remember { mutableStateListOf<DrawingPath>() }
    var currentPath by remember { mutableStateOf<DrawingPath?>(null) }
    var isUppercase by remember { mutableStateOf(true) }
    var cursive by remember { mutableStateOf(store.traceHandwriting) }
    var guide by remember { mutableStateOf<TraceGuide?>(null) }
    var showResult by remember { mutableStateOf(false) }
    var stars by remember { mutableIntStateOf(0) }

    val displayLetter = if (isUppercase) currentLetter else (letter?.lowercase ?: currentLetter.lowercase())

    fun clearDrawing() {
        paths.clear()
        currentPath = null
        showResult = false
    }

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

    LaunchedEffect(currentLetter) {
        soundManager.speakSentence("Scrie litera $currentLetter cu degetul")
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
        Row(modifier = Modifier.fillMaxSize()) {
            // Left panel: controls. Scrolls if the screen is too short; otherwise the
            // buttons sit at the bottom (the inner column is at least as tall as the panel).
            BoxWithConstraints(
                modifier = Modifier
                    .width(200.dp)
                    .fillMaxHeight()
            ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .heightIn(min = maxHeight)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
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
                        Spacer(modifier = Modifier.width(16.dp))
                        // Decoration, beside (not on top of) the back button
                        Text(
                            text = "\uD83D\uDC1D",
                            fontSize = 24.sp,
                            modifier = Modifier.offset(y = floatOffset.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
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
                        style = MaterialTheme.typography.bodySmall,
                        color = OnBackgroundLight
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Button(
                        onClick = {
                            isUppercase = !isUppercase
                            clearDrawing()
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
                        onClick = { clearDrawing() },
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
                            val shape = guide
                            stars = if (shape != null) {
                                scoreTrace(shape, paths.map { it.points })
                            } else {
                                calculateTraceScore(paths)
                            }
                            showResult = true
                            soundManager.speakEncouragement(stars >= 2)
                            val traced = currentLetter
                            scope.launch {
                                repository.updateTraceStars(traced, stars)
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
                            val currentIndex = letters.indexOfFirst { it.uppercase == currentLetter }
                            val nextIndex = (currentIndex + 1) % letters.size
                            clearDrawing()
                            // Changing the letter also announces it ("Scrie litera ...").
                            currentLetter = letters[nextIndex].uppercase
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
                                strokeWidth = 14f,
                                points = listOf(offset)
                            )
                            currentPath = newPath
                        },
                        onDrag = { offset ->
                            currentPath?.let { current ->
                                val updatedPath = Path().apply {
                                    addPath(current.path)
                                    lineTo(offset.x, offset.y)
                                }
                                currentPath = current.copy(path = updatedPath, points = current.points + offset)
                            }
                        },
                        onDragEnd = {
                            currentPath?.let { paths.add(it) }
                            currentPath = null
                        },
                        handwriting = cursive,
                        onGuideReady = { guide = it },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Print or school cursive letters
                ScriptSwitch(
                    cursive = cursive,
                    onChange = {
                        cursive = it
                        store.traceHandwriting = it
                        guide = null
                        clearDrawing()
                    },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                )

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
    val store = LocalReadingStore.current
    var cursive by remember { mutableStateOf(store.traceHandwriting) }
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
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                ScriptSwitch(
                    cursive = cursive,
                    onChange = {
                        cursive = it
                        store.traceHandwriting = it
                    }
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

/** "Tipar / De mână": trace print letters or school cursive letters. */
@Composable
private fun ScriptSwitch(
    cursive: Boolean,
    onChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(DuoBlue.copy(alpha = 0.12f))
            .padding(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ScriptOption("Tipar", selected = !cursive, fontFamily = null) { onChange(false) }
        ScriptOption("De mână", selected = cursive, fontFamily = HandwritingFont) { onChange(true) }
    }
}

@Composable
private fun ScriptOption(label: String, selected: Boolean, fontFamily: FontFamily?, onClick: () -> Unit) {
    Text(
        text = label,
        color = if (selected) Color.White else DuoBlue,
        fontWeight = FontWeight.Bold,
        fontFamily = fontFamily,
        fontSize = if (fontFamily != null) 13.sp else 15.sp,
        maxLines = 1,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (selected) DuoBlue else Color.Transparent)
            .clickable(role = Role.Button, onClickLabel = label, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 5.dp)
    )
}
