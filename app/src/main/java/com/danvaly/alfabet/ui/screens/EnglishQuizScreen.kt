package com.danvaly.alfabet.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danvaly.alfabet.audio.SoundManager
import com.danvaly.alfabet.data.EnglishCategory
import com.danvaly.alfabet.data.EnglishContent
import com.danvaly.alfabet.data.EnglishQuiz
import com.danvaly.alfabet.data.EnglishTask
import com.danvaly.alfabet.data.EnglishTaskType
import com.danvaly.alfabet.data.EnglishWord
import com.danvaly.alfabet.data.ReadingContent
import com.danvaly.alfabet.data.repository.ProgressRepository
import com.danvaly.alfabet.ui.LocalReadingStore
import com.danvaly.alfabet.ui.components.ConfettiAnimation
import com.danvaly.alfabet.ui.components.StarRating
import com.danvaly.alfabet.ui.theme.DuoBlue
import com.danvaly.alfabet.ui.theme.DuoGreen
import com.danvaly.alfabet.ui.theme.DuoOrange
import com.danvaly.alfabet.ui.theme.DuoPurple
import com.danvaly.alfabet.ui.theme.DuoRed
import com.danvaly.alfabet.ui.theme.OnBackground
import com.danvaly.alfabet.ui.theme.OnBackgroundLight
import com.danvaly.alfabet.ui.theme.SurfaceWhite
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * The English quiz of one category: 8 tasks mixing "Ascultă și alege",
 * "Ce înseamnă?" and "Cum se spune în engleză?".
 * [seed] makes the round repeatable (screenshots); null = a new random round.
 */
@Composable
fun EnglishQuizScreen(
    categoryId: String,
    onBack: () -> Unit,
    onCategories: () -> Unit,
    soundManager: SoundManager,
    repository: ProgressRepository,
    seed: Int? = null
) {
    val category = EnglishContent.category(categoryId)
    val store = LocalReadingStore.current
    val scope = rememberCoroutineScope()

    var roundNumber by remember { mutableIntStateOf(0) }
    val round = remember(categoryId, roundNumber) {
        if (category == null) emptyList()
        else EnglishQuiz.round(category, if (seed != null) Random(seed + roundNumber) else Random.Default)
    }
    var taskIndex by remember(round) { mutableIntStateOf(0) }
    var firstTryCorrect by remember(round) { mutableIntStateOf(0) }
    var resultStars by remember(round) { mutableStateOf<Int?>(null) }
    var newSticker by remember(round) { mutableStateOf<String?>(null) }

    DisposableEffect(Unit) {
        onDispose { soundManager.stop() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EnglishBackground)
            .systemBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 10.dp)) {
            val stars = resultStars
            ReadingGameTopBar(
                title = if (category == null) "🌍 Engleză" else "🎮 ${category.titleRo} · ${category.titleEn}",
                ink = EnglishInk,
                total = if (stars == null) round.size else 0,
                current = taskIndex,
                onBack = onBack
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (category != null && round.isNotEmpty()) {
                if (stars != null) {
                    EnglishQuizResult(
                        category = category,
                        stars = stars,
                        correct = firstTryCorrect,
                        total = round.size,
                        sticker = newSticker,
                        onReplay = { roundNumber++ },
                        onLearn = onBack,
                        onCategories = onCategories
                    )
                } else {
                    val task = round[taskIndex]
                    key(round, taskIndex) {
                        EnglishTaskView(
                            task = task,
                            soundManager = soundManager,
                            onSolved = { firstTry ->
                                if (firstTry) firstTryCorrect++
                                if (taskIndex < round.lastIndex) {
                                    taskIndex++
                                } else {
                                    val earned = ReadingContent.starsFor(firstTryCorrect, round.size)
                                    store.saveGameStars(category.gameKey, earned)
                                    if (earned == 3) newSticker = store.awardSticker(category.gameKey)
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

private fun typeLabel(type: EnglishTaskType): String = when (type) {
    EnglishTaskType.LISTEN -> "👂 Ascultă și alege"
    EnglishTaskType.MEANING -> "🤔 Ce înseamnă?"
    EnglishTaskType.SAY -> "🗣️ Cum se spune în engleză?"
}

@Composable
private fun EnglishTaskView(
    task: EnglishTask,
    soundManager: SoundManager,
    onSolved: (firstTry: Boolean) -> Unit
) {
    val scope = rememberCoroutineScope()
    val word = task.word
    var wrongPick by remember { mutableStateOf<EnglishWord?>(null) }
    var solved by remember { mutableStateOf(false) }
    var speech by remember { mutableStateOf<Job?>(null) }

    fun say(block: suspend () -> Unit) {
        speech?.cancel()
        speech = scope.launch { block() }
    }

    LaunchedEffect(task) {
        say {
            delay(400)
            when (task.type) {
                EnglishTaskType.LISTEN -> {
                    soundManager.speakStep("Ascultă și alege!")
                    soundManager.speakEnglishStep(word.en)
                }
                EnglishTaskType.MEANING -> {
                    soundManager.speakStep("Ce înseamnă?")
                    soundManager.speakEnglishStep(word.en)
                }
                EnglishTaskType.SAY -> soundManager.speakStep("Cum se spune în engleză: ${word.ro}?")
            }
        }
    }

    fun choose(option: EnglishWord) {
        if (solved) return
        if (task.isCorrect(option)) {
            solved = true
            val firstTry = wrongPick == null
            say {
                soundManager.speakEnglishStep(word.en)
                soundManager.speakEncouragement(true)
                delay(1500)
                onSolved(firstTry)
            }
        } else if (wrongPick == null) {
            wrongPick = option
            say {
                soundManager.speakStep("Nu-i nimic! ${word.ro}, în engleză, se spune:")
                delay(150)
                soundManager.speakEnglishStep(word.en, slow = true)
            }
        }
    }

    val wrong = wrongPick
    val feedback = when {
        solved && wrong == null -> "Bravo! „${word.en}” înseamnă „${word.ro}”."
        solved -> "Bravo! Acum știi: „${word.en}” = „${word.ro}”."
        wrong != null && task.type == EnglishTaskType.SAY ->
            "„${wrong.en}” înseamnă „${wrong.ro}”. „${word.ro}” se spune „${word.en}”. Atinge răspunsul verde."
        wrong != null ->
            "Ai ales „${wrong.ro}”. „${word.en}” înseamnă „${word.ro}”. Atinge răspunsul verde."
        else -> null
    }

    Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        // Left: what to do and the prompt
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
                    .verticalScroll(rememberScrollState())
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
            ) {
                EnglishStepLabel(typeLabel(task.type))
                when (task.type) {
                    EnglishTaskType.LISTEN -> {
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(DuoOrange)
                                .clickable { soundManager.speakEnglish(word.en) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Ascultă din nou",
                                tint = Color.White,
                                modifier = Modifier.size(52.dp)
                            )
                        }
                        SlowButton { soundManager.speakEnglish(word.en, slow = true) }
                    }
                    EnglishTaskType.MEANING -> {
                        Text(
                            text = word.en,
                            fontSize = 40.sp,
                            lineHeight = 44.sp,
                            fontWeight = FontWeight.Black,
                            color = EnglishInk,
                            textAlign = TextAlign.Center
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ReadingGameSpeakButton(label = "Ascultă", onClick = { soundManager.speakEnglish(word.en) })
                            SlowButton { soundManager.speakEnglish(word.en, slow = true) }
                        }
                    }
                    EnglishTaskType.SAY -> {
                        EnglishPicture(word = word, size = if (feedback == null) 84.dp else 56.dp)
                        Text(
                            text = word.ro,
                            fontSize = if (feedback == null) 28.sp else 24.sp,
                            lineHeight = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = OnBackground,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                if (feedback != null) {
                    Text(
                        text = feedback,
                        color = if (solved) DuoGreen else DuoOrange,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        lineHeight = 18.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Right: the answers
        Column(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val reveal = wrong != null || solved
            if (task.type == EnglishTaskType.SAY) {
                task.options.forEach { option ->
                    AnswerBox(
                        state = answerState(option, task, wrong, reveal),
                        onClick = { choose(option) },
                        modifier = Modifier.weight(1f).fillMaxWidth()
                    ) {
                        Text(
                            text = option.en,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Black,
                            color = EnglishInk,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                    }
                }
            } else {
                task.options.chunked(2).forEach { pair ->
                    Row(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        pair.forEach { option ->
                            AnswerBox(
                                state = answerState(option, task, wrong, reveal),
                                onClick = { choose(option) },
                                modifier = Modifier.weight(1f).fillMaxHeight()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    EnglishPicture(word = option, size = 60.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = option.ro,
                                        fontSize = 20.sp,
                                        lineHeight = 22.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = OnBackground,
                                        textAlign = TextAlign.Center,
                                        maxLines = 2
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private enum class AnswerState { NORMAL, WRONG, RIGHT, DIMMED }

private fun answerState(option: EnglishWord, task: EnglishTask, wrong: EnglishWord?, reveal: Boolean): AnswerState = when {
    task.isCorrect(option) && reveal -> AnswerState.RIGHT
    wrong != null && option.en == wrong.en -> AnswerState.WRONG
    reveal -> AnswerState.DIMMED
    else -> AnswerState.NORMAL
}

@Composable
private fun AnswerBox(
    state: AnswerState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val background = when (state) {
        AnswerState.RIGHT -> DuoGreen.copy(alpha = 0.22f)
        AnswerState.WRONG -> DuoRed.copy(alpha = 0.18f)
        AnswerState.DIMMED -> SurfaceWhite.copy(alpha = 0.55f)
        AnswerState.NORMAL -> SurfaceWhite
    }
    val border = when (state) {
        AnswerState.RIGHT -> DuoGreen
        AnswerState.WRONG -> DuoRed
        else -> EnglishInk.copy(alpha = 0.25f)
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(background)
            .border(BorderStroke(if (state == AnswerState.NORMAL) 2.dp else 4.dp, border), RoundedCornerShape(22.dp))
            .clickable(enabled = state == AnswerState.NORMAL || state == AnswerState.RIGHT, onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
private fun SlowButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = DuoPurple),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text("🐢 Încet", fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

@Composable
private fun EnglishQuizResult(
    category: EnglishCategory,
    stars: Int,
    correct: Int,
    total: Int,
    sticker: String?,
    onReplay: () -> Unit,
    onLearn: () -> Unit,
    onCategories: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = when (stars) {
                    3 -> "Perfect! Știi toate cuvintele!"
                    2 -> "Foarte bine! Încă puțin și le știi pe toate!"
                    else -> "Bravo că ai încercat! Mai exersează puțin."
                },
                style = MaterialTheme.typography.headlineSmall,
                color = DuoGreen,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
            Text(
                text = "${category.emoji} ${category.titleRo}: $correct din $total corecte din prima",
                color = OnBackgroundLight,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            StarRating(stars = stars)
            if (sticker != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(DuoOrange.copy(alpha = 0.15f))
                        .padding(horizontal = 14.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = sticker, fontSize = 36.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Ai primit un abțibild nou!",
                        color = OnBackground,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
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
                    onClick = onLearn,
                    modifier = Modifier.height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DuoPurple),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("📖 Cartonașe", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Button(
                    onClick = onCategories,
                    modifier = Modifier.height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DuoGreen),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Alte cuvinte", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}
