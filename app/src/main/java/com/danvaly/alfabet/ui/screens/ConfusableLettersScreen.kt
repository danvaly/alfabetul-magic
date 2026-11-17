package com.danvaly.alfabet.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danvaly.alfabet.audio.SoundManager
import com.danvaly.alfabet.data.ConfusableSet
import com.danvaly.alfabet.data.ConfusableTask
import com.danvaly.alfabet.data.ReadingContent
import com.danvaly.alfabet.data.ReadingGames
import com.danvaly.alfabet.data.markedOf
import com.danvaly.alfabet.data.repository.ProgressRepository
import com.danvaly.alfabet.ui.LocalReadingStore
import com.danvaly.alfabet.ui.components.ConfettiAnimation
import com.danvaly.alfabet.ui.theme.BackgroundCream
import com.danvaly.alfabet.ui.theme.DuoBlue
import com.danvaly.alfabet.ui.theme.DuoGreen
import com.danvaly.alfabet.ui.theme.DuoOrange
import com.danvaly.alfabet.ui.theme.DuoPink
import com.danvaly.alfabet.ui.theme.DuoPurple
import com.danvaly.alfabet.ui.theme.DuoRed
import com.danvaly.alfabet.ui.theme.OnBackground
import com.danvaly.alfabet.ui.theme.OnBackgroundLight
import com.danvaly.alfabet.ui.theme.SurfaceWhite
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val ConfusableInk = Color(0xFFC2185B)
private val SetColors = listOf(DuoBlue, DuoOrange, DuoGreen, DuoPurple, DuoPink)

/**
 * "Litere care seamănă": b/d, m/n, s/ș ... The child fills in the missing letter of a
 * picture word and finds every copy of one letter in a grid of look-alikes.
 */
@Composable
fun ConfusableLettersScreen(
    onBack: () -> Unit,
    soundManager: SoundManager,
    repository: ProgressRepository
) {
    val store = LocalReadingStore.current
    val scope = rememberCoroutineScope()

    var set by remember { mutableStateOf<ConfusableSet?>(null) }
    var round by remember { mutableStateOf<List<ConfusableTask>>(emptyList()) }
    var taskIndex by remember { mutableIntStateOf(0) }
    var firstTryCorrect by remember { mutableIntStateOf(0) }
    var resultStars by remember { mutableStateOf<Int?>(null) }

    fun start(selected: ConfusableSet) {
        set = selected
        round = ReadingGames.confusableRound(selected)
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
            .background(Brush.verticalGradient(listOf(Color(0xFFFCE4EC), Color(0xFFF8BBD0), BackgroundCream)))
            .systemBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 10.dp)) {
            val current = set
            val stars = resultStars

            ReadingGameTopBar(
                title = if (current == null) "🔍 Litere care seamănă" else "🔍 Literele ${current.title}",
                ink = ConfusableInk,
                total = if (current != null && stars == null) round.size else 0,
                current = taskIndex,
                onBack = { if (current == null) onBack() else set = null }
            )

            Spacer(modifier = Modifier.height(8.dp))

            when {
                current == null -> SetPicker(onSelect = { start(it) })

                stars != null -> ReadingGameResult(
                    message = "Bravo! Nu te mai încurci: ${current.title}!",
                    stars = stars,
                    onReplay = { start(current) },
                    onOtherLevel = { set = null },
                    otherLabel = "Alte litere"
                )

                else -> {
                    val task = round[taskIndex]
                    val onSolved: (Boolean) -> Unit = { firstTry ->
                        if (firstTry) firstTryCorrect++
                        if (taskIndex < round.lastIndex) {
                            taskIndex++
                        } else {
                            val earned = ReadingContent.starsFor(firstTryCorrect, round.size)
                            store.saveGameStars("confusable_${current.id}", earned)
                            scope.launch { repository.onGamePlayed(earned) }
                            resultStars = earned
                        }
                    }
                    key(taskIndex, task) {
                        when (task) {
                            is ConfusableTask.Complete -> CompleteWordView(task, soundManager, onSolved)
                            is ConfusableTask.Find -> FindLetterView(task, soundManager, onSolved)
                        }
                    }
                }
            }
        }

        ConfettiAnimation(isActive = resultStars != null, modifier = Modifier.fillMaxSize())
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SetPicker(onSelect: (ConfusableSet) -> Unit) {
    val store = LocalReadingStore.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Unele litere seamănă foarte mult. Alege o pereche și învață să le deosebești!",
            style = MaterialTheme.typography.bodyLarge,
            color = OnBackgroundLight
        )
        Spacer(modifier = Modifier.height(10.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ReadingGames.confusableSets.forEachIndexed { index, item ->
                val stars = store.gameStars("confusable_${item.id}")
                Card(
                    modifier = Modifier
                        .width(118.dp)
                        .height(104.dp)
                        .clickable { onSelect(item) },
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = SetColors[index % SetColors.size])
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "${item.first}  ${item.second}",
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = if (stars > 0) "⭐".repeat(stars) else "☆☆☆",
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

/** "Completează cuvântul": picture + word with a gap + two big letter buttons. */
@Composable
private fun CompleteWordView(
    task: ConfusableTask.Complete,
    soundManager: SoundManager,
    onSolved: (firstTry: Boolean) -> Unit
) {
    val scope = rememberCoroutineScope()
    val store = LocalReadingStore.current
    val word = task.word
    val wrongChoices = remember { mutableStateListOf<Char>() }
    var solved by remember { mutableStateOf(false) }

    LaunchedEffect(word) {
        delay(400)
        soundManager.speakWord(word.text)
    }

    Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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
                Text(text = word.emoji, fontSize = 72.sp)
                ReadingGameSpeakButton(label = "Ascultă", onClick = { soundManager.speakWord(word.text) })
            }
        }

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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (solved) "Bravo! ${word.text}" else "Completează cuvântul",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (solved) DuoGreen else ConfusableInk,
                    fontWeight = FontWeight.ExtraBold
                )

                // The word with its missing letter
                Row(verticalAlignment = Alignment.CenterVertically) {
                    word.text.forEachIndexed { i, c ->
                        if (i == word.index) {
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 3.dp)
                                    .defaultMinSize(minWidth = 44.dp)
                                    .height(64.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (solved) DuoGreen else DuoOrange.copy(alpha = 0.18f))
                                    .border(
                                        BorderStroke(3.dp, if (solved) DuoGreen else DuoOrange),
                                        RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (solved) c.toString() else "?",
                                    fontSize = 44.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (solved) Color.White else DuoOrange
                                )
                            }
                        } else {
                            Text(
                                text = c.toString(),
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Black,
                                color = OnBackground
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    task.choices.forEach { letter ->
                        val isAnswer = letter == word.answer
                        val wrong = letter in wrongChoices
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(RoundedCornerShape(22.dp))
                                .background(
                                    when {
                                        solved && isAnswer -> DuoGreen
                                        wrong -> DuoRed.copy(alpha = 0.3f)
                                        else -> DuoBlue
                                    }
                                )
                                .clickable(enabled = !solved && !wrong, onClickLabel = "Alege litera") {
                                    if (isAnswer) {
                                        solved = true
                                        val firstTry = wrongChoices.isEmpty()
                                        if (firstTry) store.recordSuccess(markedOf(word.word))
                                        scope.launch {
                                            soundManager.speakStep(word.text)
                                            soundManager.speakEncouragement(true)
                                            delay(1500)
                                            onSolved(firstTry)
                                        }
                                    } else {
                                        wrongChoices.add(letter)
                                        store.recordMistake(markedOf(word.word))
                                        soundManager.speakEncouragement(false)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = letter.toString(),
                                fontSize = 56.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

/** "Găsește litera": tap every copy of the target letter. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FindLetterView(
    task: ConfusableTask.Find,
    soundManager: SoundManager,
    onSolved: (firstTry: Boolean) -> Unit
) {
    val scope = rememberCoroutineScope()
    val found = remember { mutableStateListOf<Int>() }
    val flashing = remember { mutableStateListOf<Int>() }
    var hadMistake by remember { mutableStateOf(false) }
    var solved by remember { mutableStateOf(false) }
    val target = task.target

    fun sayTarget() {
        soundManager.speakPhonetic(soundManager.soundOf(target.toString()))
    }

    LaunchedEffect(task) {
        delay(400)
        sayTarget()
    }

    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (solved) "Bravo! Le-ai găsit pe toate!" else "Atinge toate literele",
                    style = MaterialTheme.typography.titleLarge,
                    color = if (solved) DuoGreen else ConfusableInk,
                    fontWeight = FontWeight.ExtraBold
                )
                if (!solved) {
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(DuoOrange)
                            .clickable { sayTarget() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = target.toString(), fontSize = 34.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "${found.size} / ${task.targetCount}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnBackgroundLight
                )
            }

            FlowRow(
                maxItemsInEachRow = 6,
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                task.grid.forEachIndexed { i, letter ->
                    val isFound = i in found
                    val isFlashing = i in flashing
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                when {
                                    isFound -> DuoGreen
                                    isFlashing -> DuoRed
                                    else -> DuoBlue.copy(alpha = 0.14f)
                                }
                            )
                            .clickable(enabled = !solved && !isFound) {
                                if (letter == target) {
                                    found.add(i)
                                    sayTarget()
                                    if (found.size == task.targetCount) {
                                        solved = true
                                        scope.launch {
                                            delay(300)
                                            soundManager.speakEncouragement(true)
                                            delay(1400)
                                            onSolved(!hadMistake)
                                        }
                                    }
                                } else {
                                    hadMistake = true
                                    if (i !in flashing) flashing.add(i)
                                    soundManager.speakEncouragement(false)
                                    scope.launch {
                                        delay(700)
                                        flashing.remove(i)
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = letter.toString(),
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isFound || isFlashing) Color.White else OnBackground,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
