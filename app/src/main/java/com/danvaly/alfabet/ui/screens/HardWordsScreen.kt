package com.danvaly.alfabet.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danvaly.alfabet.audio.SoundManager
import com.danvaly.alfabet.data.ReadingContent
import com.danvaly.alfabet.data.ReadingText
import com.danvaly.alfabet.data.ReadingWord
import com.danvaly.alfabet.data.WordStat
import com.danvaly.alfabet.data.repository.ProgressRepository
import com.danvaly.alfabet.ui.LocalReadingStore
import com.danvaly.alfabet.ui.components.ConfettiAnimation
import com.danvaly.alfabet.ui.components.StarRating
import com.danvaly.alfabet.ui.components.WordAnalysisDialog
import com.danvaly.alfabet.ui.components.speakPaced
import com.danvaly.alfabet.ui.components.syllableInk
import com.danvaly.alfabet.ui.theme.BackgroundCream
import com.danvaly.alfabet.ui.theme.DuoBlue
import com.danvaly.alfabet.ui.theme.DuoGreen
import com.danvaly.alfabet.ui.theme.DuoOrange
import com.danvaly.alfabet.ui.theme.DuoPurple
import com.danvaly.alfabet.ui.theme.DuoYellow
import com.danvaly.alfabet.ui.theme.OnBackground
import com.danvaly.alfabet.ui.theme.OnBackgroundLight
import com.danvaly.alfabet.ui.theme.SurfaceWhite
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDate

private val HardInk = Color(0xFFAD1457)
private const val WORDS_PER_PRACTICE = 10
private const val MIN_WORDS_FOR_STICKER = 3

private enum class HardWordsMode { LIST, PRACTICE, DONE }

/** The word as the child reads it: "pi-si-că" -> "pisică". */
private fun plainWord(marked: String): String = marked.replace("-", "")

/** Romanian numerals from 20 up take "de": "3 cuvinte", "21 de cuvinte". */
private fun countWords(n: Int): String {
    val rest = n % 100
    return when {
        n == 1 -> "un cuvânt"
        rest == 0 || rest >= 20 -> "$n de cuvinte"
        else -> "$n cuvinte"
    }
}

/**
 * "Cuvintele mele grele": the words the child looked up or read wrong,
 * plus a flash-card practice that helps them become easy words.
 */
@Composable
fun HardWordsScreen(
    onBack: () -> Unit,
    soundManager: SoundManager,
    repository: ProgressRepository
) {
    val store = LocalReadingStore.current
    val scope = rememberCoroutineScope()

    var refresh by remember { mutableIntStateOf(0) }
    val hardWords = remember(store, refresh) { store.hardWords() }

    var mode by remember { mutableStateOf(HardWordsMode.LIST) }
    var analysed by remember { mutableStateOf<ReadingWord?>(null) }

    // Practice state
    var deck by remember { mutableStateOf<List<WordStat>>(emptyList()) }
    var cardIndex by remember { mutableIntStateOf(0) }
    var mastered by remember { mutableIntStateOf(0) }
    var resultStars by remember { mutableIntStateOf(0) }
    var newSticker by remember { mutableStateOf<String?>(null) }

    fun startPractice() {
        val words = store.hardWords().take(WORDS_PER_PRACTICE)
        if (words.isEmpty()) return
        deck = words
        cardIndex = 0
        mastered = 0
        newSticker = null
        mode = HardWordsMode.PRACTICE
    }

    fun finishPractice() {
        val stars = ReadingContent.starsFor(mastered, deck.size).coerceAtLeast(1)
        resultStars = stars
        store.saveGameStars("hard_words", stars)
        if (deck.size >= MIN_WORDS_FOR_STICKER) {
            newSticker = store.awardSticker("hardwords_${LocalDate.now()}")
        }
        scope.launch { repository.onGamePlayed(stars) }
        refresh++
        mode = HardWordsMode.DONE
    }

    fun nextCard(readWell: Boolean) {
        val current = deck.getOrNull(cardIndex) ?: return
        if (readWell) {
            store.recordSuccess(current.marked)
            mastered++
        }
        if (cardIndex < deck.lastIndex) cardIndex++ else finishPractice()
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
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RoundBackButton(
                    onBack = {
                        if (mode == HardWordsMode.LIST) {
                            onBack()
                        } else {
                            soundManager.stop()
                            refresh++
                            mode = HardWordsMode.LIST
                        }
                    },
                    tint = HardInk
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "💪 Cuvintele mele grele",
                        style = MaterialTheme.typography.headlineSmall,
                        color = HardInk,
                        fontWeight = FontWeight.ExtraBold
                    )
                    if (mode == HardWordsMode.LIST && hardWords.isNotEmpty()) {
                        Text(
                            text = "Atinge un cuvânt ca să-l citești pe litere și silabe.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnBackgroundLight
                        )
                    }
                }
                if (mode == HardWordsMode.LIST && hardWords.isNotEmpty()) {
                    Button(
                        onClick = { startPractice() },
                        colors = ButtonDefaults.buttonColors(containerColor = DuoGreen),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Exersează", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
                if (mode == HardWordsMode.PRACTICE) {
                    Text(
                        text = "${cardIndex + 1} / ${deck.size}",
                        color = HardInk,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            when (mode) {
                HardWordsMode.LIST -> {
                    if (hardWords.isEmpty()) {
                        EmptyHardWords()
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 160.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(hardWords, key = { it.marked }) { stat ->
                                HardWordCard(stat) { analysed = ReadingText.parseWord(stat.marked) }
                            }
                        }
                    }
                }

                HardWordsMode.PRACTICE -> {
                    val card = deck.getOrNull(cardIndex)
                    if (card != null) {
                        androidx.compose.runtime.key(cardIndex, card.marked) {
                            FlashCard(
                                stat = card,
                                soundManager = soundManager,
                                onReadWell = { nextCard(readWell = true) },
                                onPractiseMore = { nextCard(readWell = false) }
                            )
                        }
                    }
                }

                HardWordsMode.DONE -> PracticeResult(
                    mastered = mastered,
                    total = deck.size,
                    stars = resultStars,
                    sticker = newSticker,
                    canPlayAgain = hardWords.isNotEmpty(),
                    onAgain = { startPractice() },
                    onList = { mode = HardWordsMode.LIST }
                )
            }
        }

        ConfettiAnimation(isActive = mode == HardWordsMode.DONE, modifier = Modifier.fillMaxSize())
    }

    analysed?.let { word ->
        WordAnalysisDialog(
            word = word,
            uppercase = store.uppercase,
            soundManager = soundManager,
            onDismiss = {
                analysed = null
                refresh++
            }
        )
    }
}

@Composable
private fun EmptyHardWords() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "🌟", fontSize = 56.sp)
        Text(
            text = "Super! Nu ai niciun cuvânt greu acum.",
            style = MaterialTheme.typography.headlineSmall,
            color = DuoGreen,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Când atingi un cuvânt într-o poveste sau greșești un cuvânt, " +
                "el apare aici ca să-l exersăm împreună.",
            style = MaterialTheme.typography.bodyLarge,
            color = OnBackgroundLight,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Composable
private fun HardWordCard(stat: WordStat, onClick: () -> Unit) {
    val syllables = stat.marked.split("-").filter { it.isNotEmpty() }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = plainWord(stat.marked),
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = OnBackground,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                syllables.forEachIndexed { index, syllable ->
                    Text(
                        text = syllable,
                        color = syllableInk(index),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(syllableInk(index).copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun FlashCard(
    stat: WordStat,
    soundManager: SoundManager,
    onReadWell: () -> Unit,
    onPractiseMore: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val word = remember(stat.marked) { ReadingText.parseWord(stat.marked) }
    var spot by remember { mutableStateOf<Int?>(null) }
    var job by remember { mutableStateOf<Job?>(null) }

    fun play(block: suspend () -> Unit) {
        job?.cancel()
        job = scope.launch {
            block()
            spot = null
        }
    }

    DisposableEffect(Unit) {
        onDispose { job?.cancel() }
    }

    Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        // The word, big, with every syllable in its own colour
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
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Citește cuvântul:",
                    style = MaterialTheme.typography.titleMedium,
                    color = OnBackgroundLight,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    word.syllables.forEachIndexed { index, syllable ->
                        Text(
                            text = syllable,
                            fontSize = 60.sp,
                            fontWeight = FontWeight.Black,
                            color = if (spot == null || spot == index) syllableInk(index)
                            else syllableInk(index).copy(alpha = 0.35f),
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (spot == index) DuoYellow.copy(alpha = 0.45f) else Color.Transparent)
                                .padding(horizontal = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = word.syllables.joinToString(" - "),
                    fontSize = 18.sp,
                    color = OnBackgroundLight,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Help and self-assessment
        Column(
            modifier = Modifier
                .weight(0.4f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)
        ) {
            PracticeButton(text = "Ascultă", emoji = null, color = DuoBlue, icon = true) {
                play {
                    spot = null
                    soundManager.speakWord(word.text)
                }
            }
            PracticeButton(text = "Pe silabe", emoji = "👏", color = DuoPurple) {
                play {
                    word.syllables.forEachIndexed { index, syllable ->
                        spot = index
                        soundManager.speakPaced(syllable.lowercase(), 0.6f)
                    }
                    spot = null
                    soundManager.speakPaced(word.text, 0.75f)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            PracticeButton(text = "Am citit bine", emoji = "👍", color = DuoGreen) {
                job?.cancel()
                soundManager.speakEncouragement(true)
                onReadWell()
            }
            PracticeButton(text = "Mai exersez", emoji = "🔁", color = DuoOrange) {
                job?.cancel()
                onPractiseMore()
            }
        }
    }
}

@Composable
private fun PracticeButton(
    text: String,
    emoji: String?,
    color: Color,
    icon: Boolean = false,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(20.dp)
    ) {
        if (icon) {
            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(
            text = if (emoji != null) "$text $emoji" else text,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
    }
}

@Composable
private fun PracticeResult(
    mastered: Int,
    total: Int,
    stars: Int,
    sticker: String?,
    canPlayAgain: Boolean,
    onAgain: () -> Unit,
    onList: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (mastered > 0) {
                "Bravo! Ai citit bine ${countWords(mastered)} din $total!"
            } else {
                "Ai exersat ${countWords(total)}. Data viitoare va fi mai ușor!"
            },
            style = MaterialTheme.typography.headlineMedium,
            color = DuoGreen,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        StarRating(stars = stars)
        if (sticker != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(DuoYellow.copy(alpha = 0.3f))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(SurfaceWhite),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = sticker, fontSize = 28.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Abțibild nou în album!",
                    color = OnBackground,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (canPlayAgain) {
                Button(
                    onClick = onAgain,
                    colors = ButtonDefaults.buttonColors(containerColor = DuoBlue),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Icon(Icons.Filled.Replay, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Încă o dată", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
            Button(
                onClick = onList,
                colors = ButtonDefaults.buttonColors(containerColor = DuoGreen),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Lista mea", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}
