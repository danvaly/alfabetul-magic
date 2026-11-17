package com.danvaly.alfabet.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danvaly.alfabet.audio.SoundManager
import com.danvaly.alfabet.data.Actor
import com.danvaly.alfabet.data.Backdrop
import com.danvaly.alfabet.data.Motion
import com.danvaly.alfabet.data.ReadingContent
import com.danvaly.alfabet.data.ReadingText
import com.danvaly.alfabet.data.ReadingWord
import com.danvaly.alfabet.data.Scene
import com.danvaly.alfabet.data.Story
import com.danvaly.alfabet.data.StoryChoice
import com.danvaly.alfabet.data.repository.ProgressRepository
import com.danvaly.alfabet.ui.LocalReadingStore
import com.danvaly.alfabet.ui.components.ConfettiAnimation
import com.danvaly.alfabet.ui.components.StarRating
import com.danvaly.alfabet.ui.components.StoryIllustration
import com.danvaly.alfabet.ui.components.WordAnalysisDialog
import com.danvaly.alfabet.ui.components.cased
import com.danvaly.alfabet.ui.components.speakPaced
import com.danvaly.alfabet.ui.components.syllableInk
import com.danvaly.alfabet.ui.theme.BackgroundCream
import com.danvaly.alfabet.ui.theme.DuoBlue
import com.danvaly.alfabet.ui.theme.DuoGreen
import com.danvaly.alfabet.ui.theme.DuoOrange
import com.danvaly.alfabet.ui.theme.DuoPurple
import com.danvaly.alfabet.ui.theme.DuoRed
import com.danvaly.alfabet.ui.theme.DuoYellow
import com.danvaly.alfabet.ui.theme.OnBackground
import com.danvaly.alfabet.ui.theme.OnBackgroundLight
import com.danvaly.alfabet.ui.theme.SurfaceWhite
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val StoryInk = Color(0xFF00838F)
private val LevelColors = listOf(DuoGreen, DuoBlue, DuoPurple)

@Composable
internal fun RoundBackButton(onBack: () -> Unit, tint: Color) {
    IconButton(
        onClick = onBack,
        modifier = Modifier
            .clip(CircleShape)
            .background(tint.copy(alpha = 0.15f))
    ) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Înapoi", tint = tint)
    }
}

// ---------------------------------------------------------------------------
// Story library
// ---------------------------------------------------------------------------

@Composable
fun StoriesScreen(
    onStoryClick: (String) -> Unit,
    onBack: () -> Unit
) {
    val store = LocalReadingStore.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFE0F7FA), Color(0xFFB2EBF2), BackgroundCream)))
            .systemBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RoundBackButton(onBack, StoryInk)
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "📚", fontSize = 28.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Povești de citit",
                        style = MaterialTheme.typography.headlineMedium,
                        color = StoryInk,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Alege o poveste. Atinge orice cuvânt ca să-l citești pe litere și silabe.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnBackgroundLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 168.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(ReadingContent.stories, key = { it.id }) { story ->
                    StoryCard(story, store.storyStars(story.id)) { onStoryClick(story.id) }
                }
            }
        }
    }
}

@Composable
private fun StoryCard(story: Story, stars: Int, onClick: () -> Unit) {
    val levelColor = LevelColors[(story.level - 1).coerceIn(0, LevelColors.lastIndex)]
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
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = story.cover, fontSize = 48.sp)
            Text(
                text = story.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = OnBackground,
                textAlign = TextAlign.Center,
                maxLines = 2,
                minLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Nivel ${story.level}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(levelColor)
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (stars > 0) "⭐".repeat(stars) else "Nouă",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = DuoOrange
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Story reader
// ---------------------------------------------------------------------------

private enum class ReaderPhase { READING, QUESTIONS, DONE }

/** Which word (and, when reading by syllables, which syllable) the voice is on. */
private data class ReadSpot(val word: Int, val syllable: Int? = null)

@Composable
fun StoryReaderScreen(
    storyId: String,
    onBack: () -> Unit,
    soundManager: SoundManager,
    repository: ProgressRepository
) {
    val story = remember(storyId) { ReadingContent.getStory(storyId) ?: ReadingContent.stories.first() }
    val store = LocalReadingStore.current
    val scope = rememberCoroutineScope()
    // Stickers already owned when the story was opened; anything after that is new.
    val stickersAtStart = remember { store.stickers().size }
    var newStickers by remember { mutableStateOf<List<String>>(emptyList()) }

    var phase by remember { mutableStateOf(ReaderPhase.READING) }
    var pageIndex by remember { mutableIntStateOf(0) }
    var showSyllables by remember { mutableStateOf(store.showSyllables) }
    var uppercase by remember { mutableStateOf(store.uppercase) }
    var slowVoice by remember { mutableStateOf(store.slowVoice) }
    var spot by remember { mutableStateOf<ReadSpot?>(null) }
    var readingJob by remember { mutableStateOf<Job?>(null) }
    var analysedWord by remember { mutableStateOf<ReadingWord?>(null) }
    var earnedStars by remember { mutableIntStateOf(0) }

    val page = story.pages[pageIndex]
    val pace = if (slowVoice) 0.75f else 1f

    fun stopReading() {
        readingJob?.cancel()
        readingJob = null
        spot = null
        soundManager.stop()
    }

    fun goToPage(index: Int) {
        stopReading()
        pageIndex = index
    }

    DisposableEffect(Unit) {
        onDispose { soundManager.stop() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFE0F7FA), BackgroundCream)))
            .systemBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Left: the picture
            val scene = when (phase) {
                ReaderPhase.READING -> page.scene
                ReaderPhase.QUESTIONS -> Scene(
                    Backdrop.MEADOW,
                    listOf(
                        Actor(story.cover, 0.42f, 0.62f, 0.36f, Motion.SWAY),
                        Actor("❓", 0.72f, 0.36f, 0.2f, Motion.FLOAT)
                    )
                )
                ReaderPhase.DONE -> Scene(
                    Backdrop.RAINBOW,
                    listOf(
                        Actor(story.cover, 0.5f, 0.66f, 0.36f, Motion.BOUNCE),
                        Actor("🎉", 0.22f, 0.4f, 0.16f, Motion.FLOAT),
                        Actor("🎉", 0.8f, 0.4f, 0.16f, Motion.FLOAT, flip = true)
                    )
                )
            }
            StoryIllustration(
                scene = scene,
                modifier = Modifier
                    .weight(0.42f)
                    .fillMaxHeight()
            )

            // Right: the text
            Card(
                modifier = Modifier
                    .weight(0.58f)
                    .fillMaxHeight(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RoundBackButton(
                            onBack = {
                                stopReading()
                                onBack()
                            },
                            tint = StoryInk
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = story.title,
                            style = MaterialTheme.typography.titleLarge,
                            color = StoryInk,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        if (phase == ReaderPhase.READING) {
                            Text(
                                text = "${pageIndex + 1} / ${story.pages.size}",
                                color = OnBackgroundLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    when (phase) {
                        ReaderPhase.READING -> ReadingPane(
                            words = page.words,
                            spot = spot,
                            showSyllables = showSyllables,
                            uppercase = uppercase,
                            slowVoice = slowVoice,
                            isFirstPage = pageIndex == 0,
                            isLastPage = pageIndex == story.pages.lastIndex,
                            onToggleSyllables = {
                                showSyllables = it
                                store.showSyllables = it
                            },
                            onToggleUppercase = {
                                uppercase = !uppercase
                                store.uppercase = uppercase
                            },
                            onToggleSlow = {
                                slowVoice = !slowVoice
                                store.slowVoice = slowVoice
                            },
                            onWordClick = { word ->
                                stopReading()
                                analysedWord = word
                            },
                            onListen = {
                                stopReading()
                                readingJob = scope.launch {
                                    soundManager.speakPaced(ReadingText.plain(page.words), 0.8f * pace)
                                }
                            },
                            onReadTogether = {
                                stopReading()
                                val words = page.words
                                val bySyllables = showSyllables
                                readingJob = scope.launch {
                                    words.forEachIndexed { w, word ->
                                        if (bySyllables && word.syllables.size > 1) {
                                            word.syllables.forEachIndexed { s, syllable ->
                                                spot = ReadSpot(w, s)
                                                soundManager.speakPaced(syllable.lowercase(), 0.6f * pace)
                                            }
                                        }
                                        spot = ReadSpot(w)
                                        soundManager.speakPaced(word.text, 0.72f * pace)
                                        delay(if (slowVoice) 350 else 150)
                                    }
                                    spot = null
                                }
                            },
                            onPrevious = { goToPage(pageIndex - 1) },
                            onNext = {
                                if (pageIndex < story.pages.lastIndex) {
                                    goToPage(pageIndex + 1)
                                } else {
                                    stopReading()
                                    phase = ReaderPhase.QUESTIONS
                                }
                            }
                        )

                        ReaderPhase.QUESTIONS -> QuestionsPane(
                            story = story,
                            soundManager = soundManager,
                            onFinished = { firstTryCorrect ->
                                val stars = ReadingContent.starsFor(firstTryCorrect, story.questions.size)
                                earnedStars = stars
                                store.saveStoryStars(story.id, stars)
                                store.awardSticker("story_${story.id}")
                                newStickers = store.stickers().drop(stickersAtStart)
                                scope.launch { repository.onGamePlayed(stars) }
                                phase = ReaderPhase.DONE
                            }
                        )

                        ReaderPhase.DONE -> Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Bravo! Ai citit povestea!",
                                style = MaterialTheme.typography.headlineMedium,
                                color = DuoGreen,
                                fontWeight = FontWeight.Black,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            StarRating(stars = earnedStars)
                            if (newStickers.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (newStickers.size == 1) {
                                        "Ai primit un abțibild: ${newStickers.first()}"
                                    } else {
                                        "Ai primit abțibilduri: ${newStickers.joinToString(" ")}"
                                    },
                                    style = MaterialTheme.typography.titleLarge,
                                    color = DuoOrange,
                                    fontWeight = FontWeight.ExtraBold,
                                    textAlign = TextAlign.Center
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Button(
                                    onClick = {
                                        pageIndex = 0
                                        newStickers = emptyList()
                                        phase = ReaderPhase.READING
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = DuoBlue),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Icon(Icons.Filled.Replay, contentDescription = null)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Citesc din nou", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                                Button(
                                    onClick = onBack,
                                    colors = ButtonDefaults.buttonColors(containerColor = DuoGreen),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Text("Alte povești", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        ConfettiAnimation(isActive = phase == ReaderPhase.DONE, modifier = Modifier.fillMaxSize())
    }

    analysedWord?.let { word ->
        WordAnalysisDialog(
            word = word,
            uppercase = uppercase,
            soundManager = soundManager,
            onDismiss = { analysedWord = null }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ReadingPane(
    words: List<ReadingWord>,
    spot: ReadSpot?,
    showSyllables: Boolean,
    uppercase: Boolean,
    slowVoice: Boolean,
    isFirstPage: Boolean,
    isLastPage: Boolean,
    onToggleSyllables: (Boolean) -> Unit,
    onToggleUppercase: () -> Unit,
    onToggleSlow: () -> Unit,
    onWordClick: (ReadingWord) -> Unit,
    onListen: () -> Unit,
    onReadTogether: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    val compact = LocalConfiguration.current.screenHeightDp < 400
    val textSize = if (compact) 30.sp else 38.sp

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(6.dp))

        // How the text is shown
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TogglePill("ma-ma", "Arată silabele", showSyllables, DuoOrange) { onToggleSyllables(true) }
            TogglePill("mama", "Arată cuvintele întregi", !showSyllables, DuoOrange) { onToggleSyllables(false) }
            Spacer(modifier = Modifier.weight(1f))
            TogglePill(if (uppercase) "ABC" else "abc", "Litere mari sau mici", uppercase, DuoPurple, onToggleUppercase)
            TogglePill("🐢", "Voce mai lentă", slowVoice, DuoGreen, onToggleSlow)
        }

        // The sentence
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.Center
        ) {
            FlowRow(
                modifier = Modifier.padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(if (showSyllables) 14.dp else 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                words.forEachIndexed { index, word ->
                    val wordSpot = spot?.takeIf { it.word == index }
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (wordSpot != null && wordSpot.syllable == null) DuoYellow.copy(alpha = 0.55f)
                                else Color.Transparent
                            )
                            .clickable { onWordClick(word) }
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (showSyllables) {
                            word.syllables.forEachIndexed { s, syllable ->
                                if (s > 0) {
                                    Text(
                                        text = "-",
                                        fontSize = textSize,
                                        fontWeight = FontWeight.Bold,
                                        color = OnBackgroundLight.copy(alpha = 0.5f)
                                    )
                                }
                                Text(
                                    text = syllable.cased(uppercase),
                                    fontSize = textSize,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = syllableInk(s),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (wordSpot?.syllable == s) DuoYellow.copy(alpha = 0.55f)
                                            else Color.Transparent
                                        )
                                )
                            }
                        } else {
                            Text(
                                text = word.text.cased(uppercase),
                                fontSize = textSize,
                                fontWeight = FontWeight.ExtraBold,
                                color = OnBackground
                            )
                        }
                        if (word.trailing.isNotEmpty()) {
                            Text(
                                text = word.trailing,
                                fontSize = textSize,
                                fontWeight = FontWeight.ExtraBold,
                                color = OnBackground
                            )
                        }
                    }
                }
            }
        }

        Text(
            text = "👆 Atinge un cuvânt ca să-l desfaci în litere și silabe",
            color = OnBackgroundLight,
            fontSize = 13.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(6.dp))

        // Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NavButton(Icons.AutoMirrored.Filled.ArrowBack, "Pagina dinainte", !isFirstPage, DuoBlue, onPrevious)
            Spacer(modifier = Modifier.weight(1f))
            ActionButton(Icons.AutoMirrored.Filled.VolumeUp, "Ascultă", DuoOrange, onListen)
            ActionButton(Icons.Filled.TouchApp, "Citim împreună", DuoPurple, onReadTogether)
            Spacer(modifier = Modifier.weight(1f))
            if (isLastPage) {
                Button(
                    onClick = onNext,
                    colors = ButtonDefaults.buttonColors(containerColor = DuoGreen),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text("Întrebări ❓", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            } else {
                NavButton(Icons.AutoMirrored.Filled.ArrowForward, "Pagina următoare", true, DuoGreen, onNext)
            }
        }
    }
}

@Composable
private fun TogglePill(
    label: String,
    description: String,
    selected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Text(
        text = label,
        color = if (selected) Color.White else color,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 15.sp,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (selected) color else color.copy(alpha = 0.14f))
            .clickable(onClickLabel = description, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp)
    )
}

@Composable
private fun NavButton(
    icon: ImageVector,
    description: String,
    enabled: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(if (enabled) color else OnBackgroundLight.copy(alpha = 0.15f))
    ) {
        Icon(icon, contentDescription = description, tint = Color.White)
    }
}

@Composable
private fun ActionButton(icon: ImageVector, label: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(20.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1)
    }
}

/** Comprehension check: reports how many questions were answered right on the first try. */
@Composable
private fun QuestionsPane(
    story: Story,
    soundManager: SoundManager,
    onFinished: (firstTryCorrect: Int) -> Unit
) {
    val scope = rememberCoroutineScope()
    var index by remember { mutableIntStateOf(0) }
    var firstTryCorrect by remember { mutableIntStateOf(0) }
    val wrong = remember { mutableStateListOf<StoryChoice>() }
    var solved by remember { mutableStateOf(false) }

    val question = story.questions[index]
    val choices = remember(index) { question.choices.shuffled() }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Întrebarea ${index + 1} din ${story.questions.size}",
            color = OnBackgroundLight,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = question.question,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = OnBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f, fill = false)
            )
            IconButton(onClick = { soundManager.speakSentence(question.question) }) {
                Icon(
                    Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Ascultă întrebarea",
                    tint = DuoOrange
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            choices.forEach { choice ->
                val isAnswer = choice == question.answer
                val container = when {
                    solved && isAnswer -> DuoGreen
                    choice in wrong -> DuoRed.copy(alpha = 0.25f)
                    else -> DuoBlue.copy(alpha = 0.12f)
                }
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(enabled = !solved && choice !in wrong) {
                            if (isAnswer) {
                                solved = true
                                if (wrong.isEmpty()) firstTryCorrect++
                                soundManager.speakEncouragement(true)
                                scope.launch {
                                    delay(1400)
                                    if (index < story.questions.lastIndex) {
                                        wrong.clear()
                                        solved = false
                                        index++
                                    } else {
                                        onFinished(firstTryCorrect)
                                    }
                                }
                            } else {
                                wrong.add(choice)
                                soundManager.speakEncouragement(false)
                            }
                        },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = container)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = choice.emoji, fontSize = 40.sp)
                        Text(
                            text = choice.label,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (solved && isAnswer) Color.White else OnBackground,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
