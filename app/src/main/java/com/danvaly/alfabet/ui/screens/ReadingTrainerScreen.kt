package com.danvaly.alfabet.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danvaly.alfabet.audio.SoundManager
import com.danvaly.alfabet.data.ReadingContent
import com.danvaly.alfabet.data.ReadingProgressStore
import com.danvaly.alfabet.data.TrainerLevel
import com.danvaly.alfabet.data.TrainerWord
import com.danvaly.alfabet.data.repository.ProgressRepository
import com.danvaly.alfabet.ui.components.ConfettiAnimation
import com.danvaly.alfabet.ui.components.LetterTile
import com.danvaly.alfabet.ui.components.StarRating
import com.danvaly.alfabet.ui.components.StepLabel
import com.danvaly.alfabet.ui.components.SyllableChip
import com.danvaly.alfabet.ui.components.SyllableInkB
import com.danvaly.alfabet.ui.theme.BackgroundCream
import com.danvaly.alfabet.ui.theme.DuoBlue
import com.danvaly.alfabet.ui.theme.DuoGreen
import com.danvaly.alfabet.ui.theme.DuoOrange
import com.danvaly.alfabet.ui.theme.DuoPurple
import com.danvaly.alfabet.ui.theme.DuoRed
import com.danvaly.alfabet.ui.theme.OnBackground
import com.danvaly.alfabet.ui.theme.OnBackgroundLight
import com.danvaly.alfabet.ui.theme.SurfaceWhite
import com.danvaly.alfabet.ui.theme.VowelColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val WORDS_PER_ROUND = 6
private val TrainerInk = Color(0xFF3949AB)
private val TrainerLevelColors = listOf(DuoGreen, DuoBlue, DuoOrange, DuoPurple)

/** One word of a round together with the three pictures the child chooses from. */
private data class TrainerTask(val target: TrainerWord, val pictures: List<TrainerWord>)

private fun buildRound(level: TrainerLevel): List<TrainerTask> =
    level.words.shuffled().take(WORDS_PER_ROUND).map { target ->
        val others = level.words.filter { it != target }.shuffled().take(2)
        TrainerTask(target, (others + target).shuffled())
    }

/**
 * "Citesc pas cu pas": the child sounds out the letters, joins them into
 * syllables, reads the whole word alone, then proves it by picking the picture.
 */
@Composable
fun ReadingTrainerScreen(
    onBack: () -> Unit,
    soundManager: SoundManager,
    repository: ProgressRepository
) {
    val context = LocalContext.current
    val store = remember { ReadingProgressStore(context) }
    val scope = rememberCoroutineScope()

    var level by remember { mutableStateOf<TrainerLevel?>(null) }
    var round by remember { mutableStateOf<List<TrainerTask>>(emptyList()) }
    var taskIndex by remember { mutableIntStateOf(0) }
    var firstTryCorrect by remember { mutableIntStateOf(0) }
    var resultStars by remember { mutableStateOf<Int?>(null) }

    fun start(selected: TrainerLevel) {
        level = selected
        round = buildRound(selected)
        taskIndex = 0
        firstTryCorrect = 0
        resultStars = null
    }

    DisposableEffect(Unit) {
        onDispose { soundManager.stop() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFE8EAF6), Color(0xFFC5CAE9), BackgroundCream)))
            .systemBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 12.dp)) {
            val current = level
            val stars = resultStars

            Row(verticalAlignment = Alignment.CenterVertically) {
                RoundBackButton(
                    onBack = { if (current == null) onBack() else level = null },
                    tint = TrainerInk
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (current == null) "👣 Citesc pas cu pas" else "${current.emoji} ${current.title}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TrainerInk,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.weight(1f)
                )
                if (current != null && stars == null) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        round.indices.forEach { i ->
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            i < taskIndex -> DuoGreen
                                            i == taskIndex -> DuoOrange
                                            else -> OnBackgroundLight.copy(alpha = 0.25f)
                                        }
                                    )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            when {
                current == null -> LevelPicker(store = store, onSelect = { start(it) })

                stars != null -> Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Bravo! Ai citit ${round.size} cuvinte!",
                        style = MaterialTheme.typography.headlineMedium,
                        color = DuoGreen,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    StarRating(stars = stars)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = { start(current) },
                            colors = ButtonDefaults.buttonColors(containerColor = DuoBlue),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Icon(Icons.Filled.Replay, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Încă o dată", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Button(
                            onClick = { level = null },
                            colors = ButtonDefaults.buttonColors(containerColor = DuoGreen),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text("Alt nivel", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }

                else -> {
                    val task = round[taskIndex]
                    // key() gives every word its own fresh state
                    androidx.compose.runtime.key(taskIndex, task.target) {
                        TrainerTaskView(
                            task = task,
                            soundManager = soundManager,
                            onSolved = { firstTry ->
                                if (firstTry) firstTryCorrect++
                                if (taskIndex < round.lastIndex) {
                                    taskIndex++
                                } else {
                                    val earned = ReadingContent.starsFor(firstTryCorrect, round.size)
                                    store.saveLevelStars(current.id, earned)
                                    scope.launch { repository.onGamePlayed(earned) }
                                    resultStars = earned
                                }
                            }
                        )
                    }
                }
            }
        }

        ConfettiAnimation(isActive = resultStars != null, modifier = Modifier.fillMaxSize())
    }
}

@Composable
private fun LevelPicker(store: ReadingProgressStore, onSelect: (TrainerLevel) -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Citește literele, apoi silabele, apoi cuvântul întreg. La final alege imaginea potrivită!",
            style = MaterialTheme.typography.bodyLarge,
            color = OnBackgroundLight
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ReadingContent.trainerLevels.forEachIndexed { index, level ->
                val color = TrainerLevelColors[index % TrainerLevelColors.size]
                val stars = store.levelStars(level.id)
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onSelect(level) },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = color)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = level.emoji, fontSize = 44.sp)
                        Text(
                            text = "Nivel ${level.id}",
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = level.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = level.description,
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = if (stars > 0) "⭐".repeat(stars) else "☆☆☆", fontSize = 16.sp, color = Color.White)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TrainerTaskView(
    task: TrainerTask,
    soundManager: SoundManager,
    onSolved: (firstTry: Boolean) -> Unit
) {
    val scope = rememberCoroutineScope()
    val word = task.target.word
    val units = remember(word) { word.units }

    val heardLetters = remember { mutableStateListOf<Int>() }
    val heardSyllables = remember { mutableStateListOf<Int>() }
    val wrongPictures = remember { mutableStateListOf<TrainerWord>() }
    var solved by remember { mutableStateOf(false) }

    val lettersDone = heardLetters.size == units.size
    val syllablesDone = lettersDone && heardSyllables.size == word.syllables.size

    val instruction = when {
        solved -> "Bravo! Ai citit: ${word.text}"
        !lettersDone -> "Atinge fiecare literă și spune sunetul ei."
        !syllablesDone -> "Acum leagă literele: citește silabele."
        else -> "Citește singur cuvântul, apoi alege imaginea."
    }

    Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        // Left: the three reading steps
        Card(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = instruction,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (solved) DuoGreen else TrainerInk,
                    fontWeight = FontWeight.ExtraBold
                )

                StepLabel(1, "Litere", VowelColor)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    units.forEachIndexed { index, unit ->
                        LetterTile(
                            unit = unit,
                            uppercase = false,
                            done = index in heardLetters,
                            height = 52.dp,
                            onClick = {
                                if (index !in heardLetters) heardLetters.add(index)
                                soundManager.speakPhonetic(soundManager.soundOf(unit.text))
                            }
                        )
                    }
                }

                StepLabel(2, "Silabe", SyllableInkB)
                if (lettersDone) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        word.syllables.forEachIndexed { index, syllable ->
                            SyllableChip(
                                text = syllable,
                                index = index,
                                done = index in heardSyllables,
                                height = 52.dp,
                                onClick = {
                                    if (index !in heardSyllables) heardSyllables.add(index)
                                    soundManager.speakSyllable(syllable)
                                }
                            )
                        }
                    }
                } else {
                    LockedStep("Mai întâi citește toate literele")
                }

                StepLabel(3, "Cuvânt", DuoGreen)
                if (syllablesDone) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = word.text,
                            fontSize = 44.sp,
                            fontWeight = FontWeight.Black,
                            color = OnBackground
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        TextButton(onClick = { soundManager.speakWord(word.text) }) {
                            Icon(
                                Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = null,
                                tint = DuoOrange
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ajutor", color = DuoOrange, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                } else {
                    LockedStep("Apoi citește silabele")
                }
            }
        }

        // Right: which picture matches the word?
        Card(
            modifier = Modifier
                .weight(0.4f)
                .fillMaxHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Ce ai citit?",
                    style = MaterialTheme.typography.titleMedium,
                    color = TrainerInk,
                    fontWeight = FontWeight.ExtraBold
                )
                task.pictures.forEach { picture ->
                    val isAnswer = picture == task.target
                    val container = when {
                        !syllablesDone -> OnBackgroundLight.copy(alpha = 0.1f)
                        solved && isAnswer -> DuoGreen
                        picture in wrongPictures -> DuoRed.copy(alpha = 0.25f)
                        else -> DuoBlue.copy(alpha = 0.14f)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(container)
                            .clickable(
                                enabled = syllablesDone && !solved && picture !in wrongPictures,
                                onClickLabel = "Alege imaginea"
                            ) {
                                if (isAnswer) {
                                    solved = true
                                    val firstTry = wrongPictures.isEmpty()
                                    scope.launch {
                                        soundManager.speakStep(word.text)
                                        soundManager.speakEncouragement(true)
                                        delay(1600)
                                        onSolved(firstTry)
                                    }
                                } else {
                                    wrongPictures.add(picture)
                                    soundManager.speakEncouragement(false)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        // Pictures stay hidden until the word has been read, so it cannot be guessed.
                        Text(text = if (syllablesDone) picture.emoji else "❔", fontSize = 40.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun LockedStep(hint: String) {
    Text(
        text = "🔒 $hint",
        color = OnBackgroundLight,
        fontSize = 15.sp,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(OnBackgroundLight.copy(alpha = 0.1f))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    )
}
