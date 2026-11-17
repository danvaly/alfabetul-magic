package com.danvaly.alfabet.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danvaly.alfabet.audio.SoundManager
import com.danvaly.alfabet.data.CutCheck
import com.danvaly.alfabet.data.ReadingContent
import com.danvaly.alfabet.data.ReadingGames
import com.danvaly.alfabet.data.TrainerLevel
import com.danvaly.alfabet.data.TrainerWord
import com.danvaly.alfabet.data.markedOf
import com.danvaly.alfabet.data.repository.ProgressRepository
import com.danvaly.alfabet.ui.LocalReadingStore
import com.danvaly.alfabet.ui.components.ConfettiAnimation
import com.danvaly.alfabet.ui.components.SyllableChip
import com.danvaly.alfabet.ui.components.speakPaced
import com.danvaly.alfabet.ui.components.syllableInk
import com.danvaly.alfabet.ui.theme.BackgroundCream
import com.danvaly.alfabet.ui.theme.DuoBlue
import com.danvaly.alfabet.ui.theme.DuoGreen
import com.danvaly.alfabet.ui.theme.DuoOrange
import com.danvaly.alfabet.ui.theme.DuoRed
import com.danvaly.alfabet.ui.theme.OnBackground
import com.danvaly.alfabet.ui.theme.OnBackgroundLight
import com.danvaly.alfabet.ui.theme.SurfaceWhite
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val SplitInk = Color(0xFF6A1B9A)

private fun syllableCount(n: Int): String = if (n == 1) "1 silabă" else "$n silabe"

/**
 * "Desparte în silabe": the child cuts the word between letters, then checks.
 * A right answer turns into coloured syllables that are clapped one by one.
 */
@Composable
fun SyllableSplitScreen(
    onBack: () -> Unit,
    soundManager: SoundManager,
    repository: ProgressRepository
) {
    val store = LocalReadingStore.current
    val scope = rememberCoroutineScope()

    var level by remember { mutableStateOf<TrainerLevel?>(null) }
    var round by remember { mutableStateOf<List<TrainerWord>>(emptyList()) }
    var taskIndex by remember { mutableIntStateOf(0) }
    var firstTryCorrect by remember { mutableIntStateOf(0) }
    var resultStars by remember { mutableStateOf<Int?>(null) }

    fun start(selected: TrainerLevel) {
        level = selected
        round = ReadingGames.syllableRound(selected)
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
            .background(Brush.verticalGradient(listOf(Color(0xFFF3E5F5), Color(0xFFE1BEE7), BackgroundCream)))
            .systemBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 10.dp)) {
            val current = level
            val stars = resultStars

            ReadingGameTopBar(
                title = if (current == null) "✂️ Desparte în silabe" else "✂️ ${current.title}",
                ink = SplitInk,
                total = if (current != null && stars == null) round.size else 0,
                current = taskIndex,
                onBack = { if (current == null) onBack() else level = null }
            )

            Spacer(modifier = Modifier.height(8.dp))

            when {
                current == null -> ReadingGameLevelPicker(
                    intro = "Atinge spațiul dintre litere ca să tai cuvântul în silabe. Unele cuvinte nu se despart!",
                    gameKey = "syllable_split",
                    onSelect = { start(it) }
                )

                stars != null -> ReadingGameResult(
                    message = "Bravo! Ai despărțit ${round.size} cuvinte!",
                    stars = stars,
                    onReplay = { start(current) },
                    onOtherLevel = { level = null }
                )

                else -> {
                    val task = round[taskIndex]
                    key(taskIndex, task) {
                        SplitTaskView(
                            task = task,
                            soundManager = soundManager,
                            onSolved = { firstTry ->
                                if (firstTry) firstTryCorrect++
                                if (taskIndex < round.lastIndex) {
                                    taskIndex++
                                } else {
                                    val earned = ReadingContent.starsFor(firstTryCorrect, round.size)
                                    store.saveGameStars("syllable_split_${current.id}", earned)
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
private fun SplitTaskView(
    task: TrainerWord,
    soundManager: SoundManager,
    onSolved: (firstTry: Boolean) -> Unit
) {
    val scope = rememberCoroutineScope()
    val store = LocalReadingStore.current
    val word = task.word
    val letters = remember(word) { word.text.toList() }

    var cuts by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var check by remember { mutableStateOf<CutCheck?>(null) }
    var failedChecks by remember { mutableIntStateOf(0) }
    var solved by remember { mutableStateOf(false) }
    var clapped by remember { mutableIntStateOf(0) }
    var spoken by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(word) {
        delay(400)
        soundManager.speakWord(word.text)
    }

    fun verify() {
        val result = ReadingGames.checkCuts(word, cuts)
        check = result
        if (result.isCorrect) {
            solved = true
            val firstTry = failedChecks == 0
            if (firstTry) store.recordSuccess(markedOf(word))
            scope.launch {
                delay(300)
                word.syllables.forEachIndexed { index, syllable ->
                    spoken = index
                    clapped = index + 1
                    soundManager.speakPaced(syllable.lowercase(), 0.6f)
                    delay(150)
                }
                spoken = null
                soundManager.speakPaced(word.text, 0.75f)
                soundManager.speakEncouragement(true)
                delay(1600)
                onSolved(firstTry)
            }
        } else {
            if (failedChecks == 0) store.recordMistake(markedOf(word))
            failedChecks++
            soundManager.speakEncouragement(false)
        }
    }

    val feedback = check
    val instruction = when {
        solved -> "Bravo! ${word.text} are ${syllableCount(word.syllables.size)}."
        feedback != null && feedback.wrong.isNotEmpty() && feedback.missing.isNotEmpty() ->
            "Tăieturile roșii nu sunt bune și mai lipsesc tăieturi. Mai încearcă!"
        feedback != null && feedback.wrong.isNotEmpty() -> "Tăieturile roșii nu sunt la locul lor. Mai încearcă!"
        feedback != null && feedback.missing.isNotEmpty() ->
            if (feedback.missing.size == 1) "Mai lipsește o tăietură. Mai încearcă!"
            else "Mai lipsesc ${feedback.missing.size} tăieturi. Mai încearcă!"
        else -> "Atinge între litere ca să tai cuvântul în silabe."
    }
    // Missing places are shown only after the second wrong try, so the child thinks first.
    val showMissing = feedback != null && failedChecks >= 2

    Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            modifier = Modifier
                .weight(0.26f)
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
                Text(text = task.emoji, fontSize = 64.sp)
                ReadingGameSpeakButton(label = "Ascultă", onClick = { soundManager.speakWord(word.text) })
            }
        }

        Card(
            modifier = Modifier
                .weight(0.74f)
                .fillMaxHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = instruction,
                    style = MaterialTheme.typography.titleMedium,
                    color = when {
                        solved -> DuoGreen
                        feedback != null -> DuoRed
                        else -> SplitInk
                    },
                    fontWeight = FontWeight.ExtraBold
                )

                // The word, with a tappable gap between every two letters
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    letters.forEachIndexed { i, letter ->
                        if (i > 0) {
                            val gapState = when {
                                i in cuts && feedback != null && i in feedback.wrong -> GapState.WRONG
                                i in cuts && solved -> GapState.RIGHT
                                i in cuts -> GapState.CUT
                                showMissing && feedback != null && i in feedback.missing -> GapState.MISSING
                                else -> GapState.EMPTY
                            }
                            CutGap(
                                state = gapState,
                                enabled = !solved,
                                onClick = {
                                    cuts = if (i in cuts) cuts - i else cuts + i
                                    check = null
                                }
                            )
                        }
                        Text(
                            text = letter.toString(),
                            fontSize = 46.sp,
                            fontWeight = FontWeight.Black,
                            color = if (solved) syllableInk(ReadingGames.syllableOfLetter(word, i)) else OnBackground
                        )
                    }
                }

                if (solved) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        word.syllables.forEachIndexed { index, syllable ->
                            SyllableChip(
                                text = syllable,
                                index = index,
                                highlighted = spoken == index,
                                height = 52.dp,
                                onClick = { soundManager.speakSyllable(syllable) }
                            )
                        }
                    }
                    Text(
                        text = "👏".repeat(clapped) + "  " + syllableCount(word.syllables.size),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SplitInk
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Button(
                            onClick = { verify() },
                            modifier = Modifier.height(52.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DuoGreen),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text("✔️ Verifică", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (cuts.isEmpty()) "Nicio tăietură = o singură silabă"
                            else "Tăieturi: ${cuts.size}",
                            color = OnBackgroundLight,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

private enum class GapState { EMPTY, CUT, WRONG, MISSING, RIGHT }

/** The space between two letters; a cut shows as a thick vertical bar with scissors. */
@Composable
private fun CutGap(state: GapState, enabled: Boolean, onClick: () -> Unit) {
    val barColor = when (state) {
        GapState.CUT -> DuoBlue
        GapState.WRONG -> DuoRed
        GapState.MISSING -> DuoOrange.copy(alpha = 0.6f)
        GapState.RIGHT -> DuoGreen
        GapState.EMPTY -> Color.Transparent
    }
    Box(
        modifier = Modifier
            .width(30.dp)
            .height(84.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                when (state) {
                    GapState.WRONG -> DuoRed.copy(alpha = 0.12f)
                    GapState.MISSING -> DuoOrange.copy(alpha = 0.15f)
                    else -> Color.Transparent
                }
            )
            .clickable(enabled = enabled, onClickLabel = "Taie aici", onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (state == GapState.EMPTY) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(OnBackgroundLight.copy(alpha = 0.3f))
            )
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (state == GapState.MISSING) "?" else "✂️",
                    fontSize = 13.sp,
                    color = DuoOrange,
                    fontWeight = FontWeight.Black
                )
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .height(56.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(barColor)
                )
            }
        }
    }
}
