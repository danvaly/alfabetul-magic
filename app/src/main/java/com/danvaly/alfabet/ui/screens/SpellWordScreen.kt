package com.danvaly.alfabet.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danvaly.alfabet.audio.SoundManager
import com.danvaly.alfabet.data.DifficultyLevel
import com.danvaly.alfabet.data.DifficultyManager
import com.danvaly.alfabet.data.ReadingContent
import com.danvaly.alfabet.data.ReadingGames
import com.danvaly.alfabet.data.SpellBoard
import com.danvaly.alfabet.data.SpellTile
import com.danvaly.alfabet.data.TrainerLevel
import com.danvaly.alfabet.data.TrainerWord
import com.danvaly.alfabet.data.markedOf
import com.danvaly.alfabet.data.repository.ProgressRepository
import com.danvaly.alfabet.ui.LocalReadingStore
import com.danvaly.alfabet.ui.components.ConfettiAnimation
import com.danvaly.alfabet.ui.components.LetterTile
import com.danvaly.alfabet.ui.components.StarRating
import com.danvaly.alfabet.ui.components.soundKindColor
import com.danvaly.alfabet.ui.components.syllableInk
import com.danvaly.alfabet.ui.theme.BackgroundCream
import com.danvaly.alfabet.ui.theme.DuoBlue
import com.danvaly.alfabet.ui.theme.DuoGreen
import com.danvaly.alfabet.ui.theme.DuoOrange
import com.danvaly.alfabet.ui.theme.DuoPurple
import com.danvaly.alfabet.ui.theme.DuoRed
import com.danvaly.alfabet.ui.theme.DuoYellow
import com.danvaly.alfabet.ui.theme.OnBackgroundLight
import com.danvaly.alfabet.ui.theme.SurfaceWhite
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val SpellInk = Color(0xFF00897B)

private data class SpellTask(val word: TrainerWord, val distractors: Int)

/**
 * "Scrie cuvântul": the child sees a picture, hears the word and builds it
 * from letter tiles, syllable by syllable.
 */
@Composable
fun SpellWordScreen(
    onBack: () -> Unit,
    soundManager: SoundManager,
    repository: ProgressRepository
) {
    val store = LocalReadingStore.current
    val scope = rememberCoroutineScope()
    val stats by repository.appStats.collectAsState(initial = null)

    var level by remember { mutableStateOf<TrainerLevel?>(null) }
    var round by remember { mutableStateOf<List<SpellTask>>(emptyList()) }
    var taskIndex by remember { mutableIntStateOf(0) }
    var firstTryCorrect by remember { mutableIntStateOf(0) }
    var resultStars by remember { mutableStateOf<Int?>(null) }

    fun start(selected: TrainerLevel) {
        val difficulty = DifficultyManager.fromInt(stats?.difficulty ?: 1)
        val distractors = if (difficulty == DifficultyLevel.BEGINNER) 0 else 2
        level = selected
        round = selected.words.shuffled().take(ReadingGames.ROUND_SIZE).map { SpellTask(it, distractors) }
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
            .background(Brush.verticalGradient(listOf(Color(0xFFE0F2F1), Color(0xFFB2DFDB), BackgroundCream)))
            .systemBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 10.dp)) {
            val current = level
            val stars = resultStars

            ReadingGameTopBar(
                title = if (current == null) "✏️ Scrie cuvântul" else "✏️ ${current.title}",
                ink = SpellInk,
                total = if (current != null && stars == null) round.size else 0,
                current = taskIndex,
                onBack = { if (current == null) onBack() else level = null }
            )

            Spacer(modifier = Modifier.height(8.dp))

            when {
                current == null -> ReadingGameLevelPicker(
                    intro = "Privește imaginea, ascultă cuvântul și scrie-l cu literele de jos!",
                    gameKey = "spell_word",
                    onSelect = { start(it) }
                )

                stars != null -> ReadingGameResult(
                    message = "Bravo! Ai scris ${round.size} cuvinte!",
                    stars = stars,
                    onReplay = { start(current) },
                    onOtherLevel = { level = null }
                )

                else -> {
                    val task = round[taskIndex]
                    key(taskIndex, task.word) {
                        SpellTaskView(
                            task = task,
                            soundManager = soundManager,
                            onSolved = { firstTry ->
                                if (firstTry) firstTryCorrect++
                                if (taskIndex < round.lastIndex) {
                                    taskIndex++
                                } else {
                                    val earned = ReadingContent.starsFor(firstTryCorrect, round.size)
                                    store.saveGameStars("spell_word_${current.id}", earned)
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SpellTaskView(
    task: SpellTask,
    soundManager: SoundManager,
    onSolved: (firstTry: Boolean) -> Unit
) {
    val scope = rememberCoroutineScope()
    val store = LocalReadingStore.current
    val word = task.word.word
    val groups = remember(word) { ReadingGames.slotGroups(word) }

    var board by remember { mutableStateOf(ReadingGames.spellBoard(word, task.distractors)) }
    var flashWrong by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var hadMistake by remember { mutableStateOf(false) }
    var solved by remember { mutableStateOf(false) }
    val shake = remember { Animatable(0f) }
    val busy = solved || flashWrong.isNotEmpty()

    LaunchedEffect(word) {
        delay(400)
        soundManager.speakWord(word.text)
    }

    fun markMistake() {
        if (!hadMistake) {
            hadMistake = true
            store.recordMistake(markedOf(word))
        }
    }

    fun update(next: SpellBoard) {
        board = next
        if (!next.isFull) return
        if (next.isSolved) {
            solved = true
            val firstTry = !hadMistake
            if (firstTry) store.recordSuccess(markedOf(word))
            scope.launch {
                delay(250)
                soundManager.speakStep(word.text)
                soundManager.speakEncouragement(true)
                delay(1500)
                onSolved(firstTry)
            }
        } else {
            markMistake()
            flashWrong = next.wrongSlots
            soundManager.speakEncouragement(false)
            scope.launch {
                for (x in listOf(16f, -16f, 12f, -12f, 6f, -6f, 0f)) {
                    shake.animateTo(x, tween(45))
                }
                delay(450)
                board = board.clearWrong()
                flashWrong = emptySet()
            }
        }
    }

    Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        // Left: picture, listen, hint
        Card(
            modifier = Modifier
                .weight(0.3f)
                .fillMaxHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
            ) {
                Text(text = task.word.emoji, fontSize = 72.sp)
                ReadingGameSpeakButton(label = "Ascultă", onClick = { soundManager.speakWord(word.text) })
                Button(
                    onClick = {
                        if (busy) return@Button
                        val slot = board.nextHintSlot() ?: return@Button
                        markMistake()
                        soundManager.speakPhonetic(soundManager.soundOf(board.target[slot]))
                        update(board.hint())
                    },
                    enabled = !busy,
                    modifier = Modifier.height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DuoYellow, contentColor = Color(0xFF5D4037)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("💡 Indiciu", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }

        // Right: slots and tiles
        Card(
            modifier = Modifier
                .weight(0.7f)
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
                    text = when {
                        solved -> "Bravo! Ai scris: ${word.text}"
                        flashWrong.isNotEmpty() -> "Ups! Literele roșii nu sunt la locul lor."
                        else -> "Atinge literele în ordine ca să scrii cuvântul."
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = when {
                        solved -> DuoGreen
                        flashWrong.isNotEmpty() -> DuoRed
                        else -> SpellInk
                    },
                    fontWeight = FontWeight.ExtraBold
                )

                // Slots, grouped by syllable
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    var offset = 0
                    groups.forEachIndexed { syllableIndex, count ->
                        val start = offset
                        offset += count
                        val ink = syllableInk(syllableIndex)
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(ink.copy(alpha = 0.08f))
                                .border(BorderStroke(2.dp, ink.copy(alpha = 0.5f)), RoundedCornerShape(16.dp))
                                .padding(5.dp),
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            for (slot in start until start + count) {
                                val wrong = slot in flashWrong
                                SpellSlot(
                                    tile = board.tileAt(slot),
                                    wrong = wrong,
                                    locked = slot in board.locked,
                                    solved = solved,
                                    modifier = Modifier.offset {
                                        IntOffset(if (wrong) shake.value.roundToInt() else 0, 0)
                                    },
                                    onClick = {
                                        if (!busy) board = board.remove(slot)
                                    }
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "Literele:",
                    color = OnBackgroundLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    board.tray.forEach { tile ->
                        LetterTile(
                            unit = tile.unit,
                            uppercase = false,
                            height = 56.dp,
                            onClick = {
                                if (!busy && !board.isFull) {
                                    soundManager.speakPhonetic(soundManager.soundOf(tile.text))
                                    update(board.place(tile.id))
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SpellSlot(
    tile: SpellTile?,
    wrong: Boolean,
    locked: Boolean,
    solved: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val background = when {
        tile == null -> OnBackgroundLight.copy(alpha = 0.12f)
        wrong -> DuoRed
        solved -> DuoGreen
        else -> soundKindColor(tile.unit.kind)
    }
    Box(
        modifier = modifier
            .height(56.dp)
            .defaultMinSize(minWidth = 50.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .border(
                BorderStroke(
                    3.dp,
                    when {
                        locked && !solved -> DuoYellow
                        tile == null -> OnBackgroundLight.copy(alpha = 0.35f)
                        else -> Color.Transparent
                    }
                ),
                RoundedCornerShape(12.dp)
            )
            .clickable(enabled = tile != null && !locked, onClick = onClick)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = tile?.text ?: "",
            fontSize = 30.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
    }
}

// ---------------------------------------------------------------------------
// Pieces shared by the reading games (Scrie cuvântul, Desparte în silabe,
// Litere care seamănă).
// ---------------------------------------------------------------------------

private val ReadingGameLevelColors = listOf(DuoGreen, DuoBlue, DuoOrange, DuoPurple)

/** Back button, title and the round's progress dots ([total] = 0 hides the dots). */
@Composable
internal fun ReadingGameTopBar(
    title: String,
    ink: Color,
    total: Int,
    current: Int,
    onBack: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RoundBackButton(onBack = onBack, tint = ink)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = ink,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            modifier = Modifier.weight(1f)
        )
        if (total > 0) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                for (i in 0 until total) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    i < current -> DuoGreen
                                    i == current -> DuoOrange
                                    else -> OnBackgroundLight.copy(alpha = 0.25f)
                                }
                            )
                    )
                }
            }
        }
    }
}

/** The four trainer levels as big cards, with the stars won in game [gameKey]. */
@Composable
internal fun ReadingGameLevelPicker(
    intro: String,
    gameKey: String,
    onSelect: (TrainerLevel) -> Unit
) {
    val store = LocalReadingStore.current
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = intro,
            style = MaterialTheme.typography.bodyLarge,
            color = OnBackgroundLight
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ReadingContent.trainerLevels.forEachIndexed { index, level ->
                val color = ReadingGameLevelColors[index % ReadingGameLevelColors.size]
                val stars = store.gameStars("${gameKey}_${level.id}")
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onSelect(level) },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = color)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = level.emoji, fontSize = 40.sp)
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
}

/** End of a round: message, stars and the "again" / "other level" buttons. */
@Composable
internal fun ReadingGameResult(
    message: String,
    stars: Int,
    onReplay: () -> Unit,
    onOtherLevel: () -> Unit,
    otherLabel: String = "Alt nivel"
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message,
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
                    onClick = onReplay,
                    modifier = Modifier.height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DuoBlue),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Icon(Icons.Filled.Replay, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Încă o dată", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Button(
                    onClick = onOtherLevel,
                    modifier = Modifier.height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DuoGreen),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(otherLabel, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

/** A big orange "listen" button. */
@Composable
internal fun ReadingGameSpeakButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = DuoOrange),
        shape = RoundedCornerShape(20.dp)
    ) {
        Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null)
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}
