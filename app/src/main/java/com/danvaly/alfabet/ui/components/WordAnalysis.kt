package com.danvaly.alfabet.ui.components

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.danvaly.alfabet.audio.SoundManager
import com.danvaly.alfabet.data.LetterUnit
import com.danvaly.alfabet.data.ReadingWord
import com.danvaly.alfabet.data.SoundKind
import com.danvaly.alfabet.data.markedOf
import com.danvaly.alfabet.ui.LocalReadingStore
import com.danvaly.alfabet.ui.theme.ConsonantColor
import com.danvaly.alfabet.ui.theme.DiacriticColor
import com.danvaly.alfabet.ui.theme.DuoGreen
import com.danvaly.alfabet.ui.theme.DuoYellow
import com.danvaly.alfabet.ui.theme.OnBackground
import com.danvaly.alfabet.ui.theme.OnBackgroundLight
import com.danvaly.alfabet.ui.theme.SurfaceWhite
import com.danvaly.alfabet.ui.theme.VowelColor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Alternating ink colours that make syllable borders visible inside a word. */
val SyllableInkA = Color(0xFF1565C0)
val SyllableInkB = Color(0xFFE65100)

fun syllableInk(index: Int): Color = if (index % 2 == 0) SyllableInkA else SyllableInkB

fun String.cased(uppercase: Boolean): String = if (uppercase) uppercase() else this

fun soundKindColor(kind: SoundKind): Color = when (kind) {
    SoundKind.VOWEL -> VowelColor
    SoundKind.CONSONANT -> ConsonantColor
    SoundKind.GROUP -> DiacriticColor
}

/**
 * Speaks one step and waits for it. When no voice is available the step still
 * takes a moment, so the highlight keeps moving and the child can read alone.
 */
suspend fun SoundManager.speakPaced(text: String, rate: Float = 0.7f) {
    if (speakStep(text, rate)) delay(140) else delay(650)
}

@Composable
fun LetterTile(
    unit: LetterUnit,
    uppercase: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    highlighted: Boolean = false,
    done: Boolean = false,
    height: Dp = 48.dp
) {
    val scale by animateFloatAsState(if (highlighted) 1.18f else 1f, label = "tile_scale")
    val color = soundKindColor(unit.kind)
    Box(
        modifier = modifier
            .scale(scale)
            .height(height)
            .defaultMinSize(minWidth = height * 0.9f)
            .clip(RoundedCornerShape(12.dp))
            .background(if (done) color.copy(alpha = 0.55f) else color)
            .border(
                BorderStroke(3.dp, if (highlighted) DuoYellow else Color.Transparent),
                RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = unit.text.cased(uppercase),
            fontSize = (height.value * 0.54f).sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
    }
}

@Composable
fun SyllableChip(
    text: String,
    index: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    highlighted: Boolean = false,
    done: Boolean = false,
    height: Dp = 48.dp
) {
    val scale by animateFloatAsState(if (highlighted) 1.15f else 1f, label = "chip_scale")
    val ink = syllableInk(index)
    Box(
        modifier = modifier
            .scale(scale)
            .height(height)
            .clip(RoundedCornerShape(50))
            .background(if (highlighted) DuoYellow.copy(alpha = 0.45f) else ink.copy(alpha = 0.1f))
            .border(BorderStroke(3.dp, if (done) ink.copy(alpha = 0.45f) else ink), RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = (height.value * 0.52f).sp,
            fontWeight = FontWeight.Black,
            color = ink
        )
    }
}

/** A round "1 Litere" style label that names a reading step. */
@Composable
fun StepLabel(number: Int, title: String, color: Color, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "$number", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = title, color = OnBackground, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
    }
}

private sealed interface Spot {
    data class Letter(val index: Int) : Spot
    data class Syllable(val index: Int) : Spot
    data object Whole : Spot
}

/**
 * Takes one word apart the way it is done in first grade:
 * letters (vowels and consonants), then syllables, then the whole word.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WordAnalysisDialog(
    word: ReadingWord,
    uppercase: Boolean,
    soundManager: SoundManager,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var spot by remember { mutableStateOf<Spot?>(null) }
    var job by remember { mutableStateOf<Job?>(null) }
    val units = remember(word) { word.units }
    val store = LocalReadingStore.current

    // Opening the analysis means the word was hard to read alone.
    LaunchedEffect(word) {
        store.recordLookup(markedOf(word))
    }

    fun play(block: suspend () -> Unit) {
        job?.cancel()
        job = scope.launch {
            block()
            spot = null
        }
    }

    DisposableEffect(Unit) {
        onDispose { soundManager.stop() }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 680.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(start = 20.dp, end = 12.dp, top = 8.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🔍 Analizăm cuvântul",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = OnBackground,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Închide", tint = OnBackgroundLight)
                    }
                }

                // 1. Letters
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StepLabel(1, "Litere", VowelColor, Modifier.width(108.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        units.forEachIndexed { index, unit ->
                            LetterTile(
                                unit = unit,
                                uppercase = uppercase,
                                highlighted = spot == Spot.Letter(index),
                                onClick = {
                                    play {
                                        spot = Spot.Letter(index)
                                        soundManager.speakPaced(soundManager.soundOf(unit.text), 0.6f)
                                    }
                                }
                            )
                        }
                    }
                }

                // 2. Syllables
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StepLabel(2, "Silabe", SyllableInkB, Modifier.width(108.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        word.syllables.forEachIndexed { index, syllable ->
                            SyllableChip(
                                text = syllable.cased(uppercase),
                                index = index,
                                highlighted = spot == Spot.Syllable(index),
                                onClick = {
                                    play {
                                        spot = Spot.Syllable(index)
                                        soundManager.speakPaced(syllable.lowercase(), 0.6f)
                                    }
                                }
                            )
                        }
                    }
                }

                // 3. Whole word
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StepLabel(3, "Cuvânt", DuoGreen, Modifier.width(108.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (spot == Spot.Whole) DuoYellow.copy(alpha = 0.45f)
                                else DuoGreen.copy(alpha = 0.12f)
                            )
                            .clickable {
                                play {
                                    spot = Spot.Whole
                                    soundManager.speakPaced(word.text, 0.75f)
                                }
                            }
                            .padding(horizontal = 18.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = word.text.cased(uppercase),
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Black,
                            color = OnBackground
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    FlowRow(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FactPill(count(word.letterCount, "literă", "litere"), OnBackgroundLight)
                        FactPill(count(word.syllables.size, "silabă", "silabe"), SyllableInkB)
                        FactPill(count(word.vowelCount, "vocală", "vocale"), VowelColor)
                        FactPill(count(word.consonantCount, "consoană", "consoane"), ConsonantColor)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            play {
                                units.forEachIndexed { index, unit ->
                                    spot = Spot.Letter(index)
                                    soundManager.speakPaced(soundManager.soundOf(unit.text), 0.6f)
                                }
                                word.syllables.forEachIndexed { index, syllable ->
                                    spot = Spot.Syllable(index)
                                    soundManager.speakPaced(syllable.lowercase(), 0.6f)
                                }
                                spot = Spot.Whole
                                soundManager.speakPaced(word.text, 0.75f)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DuoGreen),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Pas cu pas", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

private fun count(n: Int, one: String, many: String): String =
    if (n == 1) "o $one" else "$n $many"

@Composable
private fun FactPill(text: String, color: Color) {
    Text(
        text = text,
        color = color,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}
