package com.danvaly.alfabet.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Stop
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.danvaly.alfabet.audio.ReadingListener
import com.danvaly.alfabet.audio.VoiceRecorder
import com.danvaly.alfabet.data.markedOf
import com.danvaly.alfabet.data.matchRead
import com.danvaly.alfabet.ui.components.cursiveSyllables
import com.danvaly.alfabet.ui.theme.HandwritingFont
import com.danvaly.alfabet.ui.theme.HandwritingScale
import com.danvaly.alfabet.ui.theme.PrimaryPinkDark
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

/** What the microphone is doing on the current page. */
private enum class MicMode { IDLE, RECORDING, PLAYING, LISTENING }

private enum class MicAction { RECORD, LISTEN }

private val RecordColor = DuoRed
private val PlayMeColor = PrimaryPinkDark
private val ListenMeColor = StoryInk
private val HeardGreen = Color(0xFF2E7D32)

@Composable
fun StoryReaderScreen(
    storyId: String,
    onBack: () -> Unit,
    soundManager: SoundManager,
    repository: ProgressRepository
) {
    val story = remember(storyId) { ReadingContent.getStory(storyId) ?: ReadingContent.stories.first() }
    val store = LocalReadingStore.current
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    // Stickers already owned when the story was opened; anything after that is new.
    val stickersAtStart = remember { store.stickers().size }
    var newStickers by remember { mutableStateOf<List<String>>(emptyList()) }

    var phase by remember { mutableStateOf(ReaderPhase.READING) }
    var pageIndex by remember { mutableIntStateOf(0) }
    var showSyllables by remember { mutableStateOf(store.showSyllables) }
    var uppercase by remember { mutableStateOf(store.uppercase) }
    var slowVoice by remember { mutableStateOf(store.slowVoice) }
    var handwriting by remember { mutableStateOf(store.handwriting) }
    var spot by remember { mutableStateOf<ReadSpot?>(null) }
    var readingJob by remember { mutableStateOf<Job?>(null) }
    var analysedWord by remember { mutableStateOf<ReadingWord?>(null) }
    var earnedStars by remember { mutableIntStateOf(0) }

    // Microphone: "Mă înregistrez", "Ascultă-mă" and "Citesc eu".
    val recorder = remember { VoiceRecorder(context) }
    val listener = remember { ReadingListener(context) }
    var micMode by remember { mutableStateOf(MicMode.IDLE) }
    var recordingsVersion by remember { mutableIntStateOf(0) }
    var pendingMic by remember { mutableStateOf<MicAction?>(null) }
    var heard by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var missed by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var message by remember { mutableStateOf<String?>(null) }

    val page = story.pages[pageIndex]
    val pace = if (slowVoice) 0.75f else 1f
    val recordKey = "${story.id}_$pageIndex"
    val hasMyRecording = remember(recordKey, recordingsVersion) { recorder.hasRecording(recordKey) }

    fun stopMic() {
        when (micMode) {
            MicMode.RECORDING -> {
                recorder.stopRecording()
                recordingsVersion++
            }
            MicMode.PLAYING -> recorder.stopPlaying()
            MicMode.LISTENING -> listener.cancel()
            MicMode.IDLE -> Unit
        }
        micMode = MicMode.IDLE
    }

    fun stopVoice() {
        readingJob?.cancel()
        readingJob = null
        spot = null
        soundManager.stop()
    }

    fun stopReading() {
        stopVoice()
        stopMic()
    }

    fun goToPage(index: Int) {
        stopReading()
        heard = emptySet()
        missed = emptySet()
        message = null
        pageIndex = index
    }

    fun startRecording() {
        stopReading()
        heard = emptySet()
        missed = emptySet()
        val started = recorder.startRecording(recordKey, maxSeconds = 60) {
            micMode = MicMode.IDLE
            recordingsVersion++
            message = "Gata! Acum apasă pe căști 🎧 ca să te asculți."
        }
        if (started) {
            micMode = MicMode.RECORDING
            message = null
        } else {
            message = "Nu pot porni microfonul acum. Mai încearcă puțin mai târziu."
        }
    }

    fun stopRecording() {
        val saved = recorder.stopRecording()
        micMode = MicMode.IDLE
        recordingsVersion++
        message = if (saved) {
            "Bravo! Acum apasă pe căști 🎧 ca să te asculți."
        } else {
            "Nu s-a auzit nimic. Apasă pe microfon și citește cu voce tare."
        }
    }

    fun playMe() {
        if (micMode == MicMode.PLAYING) {
            stopMic()
            return
        }
        stopReading()
        if (!hasMyRecording) {
            message = "Mai întâi apasă pe microfon 🎤 și citește pagina."
            return
        }
        if (recorder.play(recordKey) { if (micMode == MicMode.PLAYING) micMode = MicMode.IDLE }) {
            micMode = MicMode.PLAYING
            message = null
        } else {
            message = "Nu pot pune înregistrarea acum."
        }
    }

    fun finishListening(problem: String?) {
        micMode = MicMode.IDLE
        val words = page.words
        if (heard.isEmpty()) {
            missed = emptySet()
            message = problem ?: "Nu te-am auzit bine. Apasă din nou și citește cu voce tare."
            return
        }
        // Words after the last one heard were probably not reached yet: they are not mistakes.
        val last = heard.max()
        missed = (0 until last).filter { it !in heard }.toSet()
        words.forEachIndexed { index, word ->
            when (index) {
                in heard -> store.recordSuccess(markedOf(word))
                in missed -> store.recordMistake(markedOf(word))
            }
        }
        val total = words.size
        val read = heard.size
        val of = if (total >= 20) "de cuvinte" else "cuvinte"
        message = when {
            read >= total -> "Ai citit bine toate cele $total $of! Bravo! 🎉"
            missed.size == 1 -> "Ai citit bine $read din $total $of. " +
                "Mai încearcă acest cuvânt: ${words[missed.first()].text.cased(uppercase)}"
            missed.isNotEmpty() -> "Ai citit bine $read din $total $of. " +
                "Mai încearcă cuvintele colorate cu portocaliu."
            else -> "Ai citit bine $read din $total $of. Continuă tot așa!"
        }
        if (read >= total) soundManager.speakEncouragement(true)
    }

    fun startListening() {
        stopReading()
        heard = emptySet()
        missed = emptySet()
        if (!listener.isAvailable) {
            message = "Ascultarea este indisponibilă pe acest dispozitiv."
            return
        }
        val expected = page.words.map { it.text }
        val started = listener.start(object : ReadingListener.Callbacks {
            override fun onReady() {
                message = "🎤 Te ascult! Citește cu voce tare."
            }

            override fun onHeard(text: String) {
                heard = heard + matchRead(expected, text)
            }

            override fun onFinished(texts: List<String>, problem: String?) {
                texts.firstOrNull()?.let { heard = heard + matchRead(expected, it) }
                finishListening(problem)
            }
        })
        if (started) {
            micMode = MicMode.LISTENING
            message = "Pregătesc urechile… 👂"
        } else {
            message = "Nu pot asculta acum. Mai încearcă puțin mai târziu."
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        val action = pendingMic
        pendingMic = null
        if (granted) {
            when (action) {
                MicAction.RECORD -> startRecording()
                MicAction.LISTEN -> startListening()
                null -> Unit
            }
        } else {
            message = "Pentru asta am nevoie de microfon. Roagă un părinte să-l permită din Setări."
        }
    }

    fun withMicrophone(action: MicAction) {
        val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
            PackageManager.PERMISSION_GRANTED
        if (granted) {
            when (action) {
                MicAction.RECORD -> startRecording()
                MicAction.LISTEN -> startListening()
            }
        } else {
            stopReading()
            pendingMic = action
            try {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            } catch (e: Exception) {
                pendingMic = null
                message = "Nu pot folosi microfonul pe acest dispozitiv."
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            soundManager.stop()
            recorder.release()
            listener.destroy()
        }
    }

    // Leaving the app stops the microphone (a finished part of a recording is kept).
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) stopReading()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
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
                            heard = heard,
                            missed = missed,
                            showSyllables = showSyllables,
                            uppercase = uppercase,
                            slowVoice = slowVoice,
                            handwriting = handwriting,
                            isFirstPage = pageIndex == 0,
                            isLastPage = pageIndex == story.pages.lastIndex,
                            micMode = micMode,
                            hasMyRecording = hasMyRecording,
                            canListen = listener.isAvailable,
                            message = message,
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
                            onToggleHandwriting = {
                                handwriting = !handwriting
                                store.handwriting = handwriting
                            },
                            onWordClick = { word ->
                                stopReading()
                                analysedWord = word
                            },
                            onListen = {
                                stopReading()
                                message = null
                                readingJob = scope.launch {
                                    soundManager.speakPaced(ReadingText.plain(page.words), 0.8f * pace)
                                }
                            },
                            onReadTogether = {
                                stopReading()
                                message = null
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
                            onRecord = {
                                if (micMode == MicMode.RECORDING) stopRecording() else withMicrophone(MicAction.RECORD)
                            },
                            onPlayMe = { playMe() },
                            onListenMe = {
                                when {
                                    micMode == MicMode.LISTENING -> listener.stop()
                                    !listener.isAvailable -> {
                                        stopReading()
                                        message = "Ascultarea este indisponibilă pe acest dispozitiv."
                                    }
                                    else -> withMicrophone(MicAction.LISTEN)
                                }
                            },
                            onPrevious = { goToPage(pageIndex - 1) },
                            onNext = {
                                if (pageIndex < story.pages.lastIndex) {
                                    goToPage(pageIndex + 1)
                                } else {
                                    stopReading()
                                    message = null
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
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
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
                                        goToPage(0)
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
            onDismiss = { analysedWord = null },
            handwriting = handwriting
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ReadingPane(
    words: List<ReadingWord>,
    spot: ReadSpot?,
    heard: Set<Int>,
    missed: Set<Int>,
    showSyllables: Boolean,
    uppercase: Boolean,
    slowVoice: Boolean,
    handwriting: Boolean,
    isFirstPage: Boolean,
    isLastPage: Boolean,
    micMode: MicMode,
    hasMyRecording: Boolean,
    canListen: Boolean,
    message: String?,
    onToggleSyllables: (Boolean) -> Unit,
    onToggleUppercase: () -> Unit,
    onToggleSlow: () -> Unit,
    onToggleHandwriting: () -> Unit,
    onWordClick: (ReadingWord) -> Unit,
    onListen: () -> Unit,
    onReadTogether: () -> Unit,
    onRecord: () -> Unit,
    onPlayMe: () -> Unit,
    onListenMe: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    val compact = LocalConfiguration.current.screenHeightDp < 400
    val textSize = if (compact) 30.sp else 38.sp
    val cursiveSize = textSize * HandwritingScale

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(6.dp))

        // How the text is shown
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TogglePill("ma-ma", "Arată silabele", showSyllables, DuoOrange) { onToggleSyllables(true) }
            TogglePill("mama", "Arată cuvintele întregi", !showSyllables, DuoOrange) { onToggleSyllables(false) }
            Spacer(modifier = Modifier.width(6.dp))
            TogglePill(if (uppercase) "ABC" else "abc", "Litere mari sau mici", uppercase, DuoPurple, onToggleUppercase)
            TogglePill("✍ de mână", "Litere de mână", handwriting, DuoBlue, onToggleHandwriting)
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
                horizontalArrangement = Arrangement.spacedBy(if (showSyllables && !handwriting) 14.dp else 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                words.forEachIndexed { index, word ->
                    val wordSpot = spot?.takeIf { it.word == index }
                    val wasHeard = index in heard
                    val background = when {
                        wordSpot != null && wordSpot.syllable == null -> DuoYellow.copy(alpha = 0.55f)
                        wasHeard -> DuoGreen.copy(alpha = 0.25f)
                        index in missed -> DuoOrange.copy(alpha = 0.2f)
                        else -> Color.Transparent
                    }
                    val plainInk = if (wasHeard) HeardGreen else OnBackground
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(background)
                            .clickable { onWordClick(word) }
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (handwriting) {
                            // One connected cursive run per word; syllables are told apart by colour only,
                            // because a "-" or separate pieces would break the joined letters.
                            val text = buildAnnotatedString {
                                if (showSyllables) {
                                    append(cursiveSyllables(word.syllables, uppercase, wordSpot?.syllable))
                                } else {
                                    withStyle(SpanStyle(color = plainInk)) { append(word.text.cased(uppercase)) }
                                }
                                withStyle(SpanStyle(color = OnBackground)) { append(word.trailing) }
                            }
                            Text(
                                text = text,
                                fontSize = cursiveSize,
                                fontFamily = HandwritingFont,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
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
                                    color = plainInk
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
        }

        // What is happening right now, or the usual hint
        val status = message ?: when (micMode) {
            MicMode.RECORDING -> "🔴 Te înregistrez… Citește cu voce tare, apoi apasă pe pătrat."
            MicMode.PLAYING -> "🎧 Așa ai citit tu!"
            MicMode.LISTENING -> "🎤 Te ascult! Citește cu voce tare."
            MicMode.IDLE -> "👆 Atinge un cuvânt ca să-l desfaci în litere și silabe"
        }
        Text(
            text = status,
            color = when {
                micMode == MicMode.RECORDING -> RecordColor
                message != null -> StoryInk
                else -> OnBackgroundLight
            },
            fontSize = 13.sp,
            fontWeight = if (message != null || micMode != MicMode.IDLE) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))

        // Controls: the app reads (orange, purple) | I read (red, pink, teal) | pages (blue, green)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RoundAction(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                description = "Pagina dinainte",
                color = DuoBlue,
                dimmed = isFirstPage,
                onClick = { if (!isFirstPage) onPrevious() }
            )
            Spacer(modifier = Modifier.weight(1f))
            ControlGroup(DuoOrange) {
                RoundAction(Icons.AutoMirrored.Filled.VolumeUp, "Ascultă", DuoOrange, onClick = onListen)
                RoundAction(Icons.Filled.TouchApp, "Citim împreună", DuoPurple, onClick = onReadTogether)
            }
            Spacer(modifier = Modifier.weight(1f))
            ControlGroup(RecordColor) {
                val recording = micMode == MicMode.RECORDING
                RoundAction(
                    icon = if (recording) Icons.Filled.Stop else Icons.Filled.Mic,
                    description = if (recording) "Oprește înregistrarea" else "Mă înregistrez",
                    color = RecordColor,
                    pulse = recording,
                    onClick = onRecord
                )
                val playing = micMode == MicMode.PLAYING
                RoundAction(
                    icon = if (playing) Icons.Filled.Stop else Icons.Filled.Headphones,
                    description = if (playing) "Oprește" else "Ascultă-mă",
                    color = PlayMeColor,
                    dimmed = !hasMyRecording && !playing,
                    onClick = onPlayMe
                )
                val listening = micMode == MicMode.LISTENING
                RoundAction(
                    icon = if (listening) Icons.Filled.Stop else Icons.Filled.RecordVoiceOver,
                    description = if (listening) "Am terminat de citit" else "Citesc eu (nou)",
                    color = ListenMeColor,
                    dimmed = !canListen,
                    pulse = listening,
                    badge = if (canListen) "nou" else null,
                    onClick = onListenMe
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            if (isLastPage) {
                RoundAction(Icons.Filled.QuestionMark, "Întrebări", DuoGreen, onClick = onNext)
            } else {
                RoundAction(Icons.AutoMirrored.Filled.ArrowForward, "Pagina următoare", DuoGreen, onClick = onNext)
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
        maxLines = 1,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (selected) color else color.copy(alpha = 0.14f))
            .clickable(onClickLabel = description, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp)
    )
}

/** A soft capsule that groups related round buttons. */
@Composable
private fun ControlGroup(color: Color, content: @Composable () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.08f))
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

/** A round, icon-only button in its own colour; [dimmed] looks faded but still answers taps. */
@Composable
private fun RoundAction(
    icon: ImageVector,
    description: String,
    color: Color,
    dimmed: Boolean = false,
    pulse: Boolean = false,
    badge: String? = null,
    onClick: () -> Unit
) {
    val scale = if (pulse) pulseScale() else 1f
    Box(modifier = Modifier.size(42.dp)) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .scale(scale)
                .clip(CircleShape)
                .background(if (dimmed) color.copy(alpha = 0.3f) else color)
                .clickable(onClickLabel = description, role = Role.Button, onClick = onClick)
                .semantics { contentDescription = description },
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
        }
        if (badge != null) {
            Text(
                text = badge,
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 6.dp, y = (-5).dp)
                    .clip(RoundedCornerShape(50))
                    .background(DuoOrange)
                    .padding(horizontal = 4.dp)
            )
        }
    }
}

@Composable
private fun pulseScale(): Float {
    val transition = rememberInfiniteTransition(label = "mic_pulse")
    val scale by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.14f,
        animationSpec = infiniteRepeatable(tween(550), RepeatMode.Reverse),
        label = "mic_pulse_scale"
    )
    return scale
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
