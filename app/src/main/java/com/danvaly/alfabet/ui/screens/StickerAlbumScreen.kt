package com.danvaly.alfabet.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danvaly.alfabet.data.StickerCatalog
import com.danvaly.alfabet.ui.LocalReadingStore
import com.danvaly.alfabet.ui.theme.BackgroundCream
import com.danvaly.alfabet.ui.theme.DuoGreen
import com.danvaly.alfabet.ui.theme.DuoOrange
import com.danvaly.alfabet.ui.theme.DuoYellow
import com.danvaly.alfabet.ui.theme.OnBackground
import com.danvaly.alfabet.ui.theme.OnBackgroundLight
import com.danvaly.alfabet.ui.theme.SurfaceWhite
import kotlinx.coroutines.delay
import java.time.LocalDate

private val AlbumInk = Color(0xFF6A1B9A)
private val SlotColors = listOf(
    Color(0xFFFFF3E0), Color(0xFFE3F2FD), Color(0xFFF1F8E9), Color(0xFFFCE4EC), Color(0xFFEDE7F6)
)

/** "3 abțibilduri", "21 de abțibilduri" — Romanian numerals from 20 up take "de". */
private fun stickerCount(n: Int): String {
    val rest = n % 100
    return when {
        n == 0 -> "Încă nu ai abțibilduri"
        n == 1 -> "Ai un abțibild"
        rest == 0 || rest >= 20 -> "Ai $n de abțibilduri"
        else -> "Ai $n abțibilduri"
    }
}

/** "Albumul cu abțibilduri": every sticker the child has earned, plus how to earn more. */
@Composable
fun StickerAlbumScreen(onBack: () -> Unit) {
    val store = LocalReadingStore.current
    val stickers = remember(store) { store.stickers() }
    val today = remember { LocalDate.now().toString() }
    val minutesToday = remember(store) { store.minutesOn(today) }
    val goal = remember(store) { store.dailyGoalMinutes.coerceAtLeast(1) }
    val slots = maxOf(StickerCatalog.all.size, stickers.size)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFF3E5F5), Color(0xFFE1BEE7), BackgroundCream)))
            .systemBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RoundBackButton(onBack = onBack, tint = AlbumInk)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "🦋 Albumul cu abțibilduri",
                        style = MaterialTheme.typography.headlineSmall,
                        color = AlbumInk,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = stickerCount(stickers.size),
                        style = MaterialTheme.typography.titleMedium,
                        color = DuoOrange,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // The album page
                Card(
                    modifier = Modifier
                        .weight(0.64f)
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 64.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(slots) { index ->
                            val sticker = stickers.getOrNull(index)
                            if (sticker != null) {
                                EarnedSlot(sticker, index)
                            } else {
                                EmptySlot()
                            }
                        }
                    }
                }

                // How to earn stickers
                Card(
                    modifier = Modifier
                        .weight(0.36f)
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Cum primești abțibilduri?",
                            style = MaterialTheme.typography.titleMedium,
                            color = AlbumInk,
                            fontWeight = FontWeight.ExtraBold
                        )
                        HowToEarn("⏰", "Citește $goal minute în fiecare zi.")
                        DailyProgress(minutes = minutesToday, goal = goal)
                        HowToEarn("📚", "Termină o poveste.")
                        HowToEarn("💪", "Exersează cuvintele grele.")
                    }
                }
            }
        }
    }
}

@Composable
private fun EarnedSlot(sticker: String, index: Int) {
    val pop = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(minOf(index, 20) * 60L)
        pop.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .scale(pop.value)
            .clip(CircleShape)
            .background(SlotColors[index % SlotColors.size]),
        contentAlignment = Alignment.Center
    ) {
        Text(text = sticker, fontSize = 34.sp)
    }
}

@Composable
private fun EmptySlot() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(4.dp)) {
            val stroke = 2.dp.toPx()
            drawCircle(
                color = OnBackgroundLight.copy(alpha = 0.45f),
                radius = size.minDimension / 2 - stroke,
                style = Stroke(
                    width = stroke,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8.dp.toPx(), 6.dp.toPx()))
                )
            )
        }
        Text(
            text = "?",
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = OnBackgroundLight.copy(alpha = 0.55f)
        )
    }
}

@Composable
private fun HowToEarn(emoji: String, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = emoji, fontSize = 24.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, color = OnBackground, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}

@Composable
private fun DailyProgress(minutes: Int, goal: Int) {
    val fraction = (minutes.toFloat() / goal).coerceIn(0f, 1f)
    val done = minutes >= goal
    Column(modifier = Modifier.padding(start = 32.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(RoundedCornerShape(50))
                .background(OnBackgroundLight.copy(alpha = 0.15f))
        ) {
            if (fraction > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(50))
                        .background(if (done) DuoGreen else DuoYellow)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (done) "Azi ai reușit! ✅" else "Azi: $minutes din $goal minute",
            color = if (done) DuoGreen else OnBackgroundLight,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}
