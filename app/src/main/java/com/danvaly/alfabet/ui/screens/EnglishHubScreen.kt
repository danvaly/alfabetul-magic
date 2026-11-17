package com.danvaly.alfabet.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danvaly.alfabet.audio.SoundManager
import com.danvaly.alfabet.audio.VoiceState
import com.danvaly.alfabet.data.EnglishCategory
import com.danvaly.alfabet.data.EnglishContent
import com.danvaly.alfabet.data.EnglishWord
import com.danvaly.alfabet.ui.LocalReadingStore
import com.danvaly.alfabet.ui.theme.BackgroundCream
import com.danvaly.alfabet.ui.theme.DuoRed
import com.danvaly.alfabet.ui.theme.OnBackground
import com.danvaly.alfabet.ui.theme.OnBackgroundLight
import com.danvaly.alfabet.ui.theme.SurfaceWhite

/** Ink and background shared by the "Engleză" screens. */
internal val EnglishInk = Color(0xFF0277BD)
internal val EnglishBackground = Brush.verticalGradient(listOf(Color(0xFFE1F5FE), Color(0xFFB3E5FC), BackgroundCream))

/** "Engleză": the English word categories, with the best stars won in each quiz. */
@Composable
fun EnglishHubScreen(
    onBack: () -> Unit,
    onCategory: (String) -> Unit,
    soundManager: SoundManager
) {
    val context = LocalContext.current
    val store = LocalReadingStore.current
    val voiceState by soundManager.englishVoiceState.collectAsState()
    val categories = EnglishContent.categories
    val totalStars = categories.sumOf { store.gameStars(it.gameKey) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EnglishBackground)
            .systemBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RoundBackButton(onBack = onBack, tint = EnglishInk)
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "🌍", fontSize = 28.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Engleză · English",
                        style = MaterialTheme.typography.headlineMedium,
                        color = EnglishInk,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1
                    )
                    Text(
                        text = "Învață primele cuvinte în limba engleză!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnBackgroundLight,
                        maxLines = 1
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "⭐ $totalStars / ${EnglishContent.maxStars}",
                    color = OnBackground,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceWhite)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 200.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                if (voiceState == VoiceState.MISSING) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        EnglishVoiceBanner(onInstall = { soundManager.openVoiceSettings(context) })
                    }
                }
                items(categories, key = { it.id }) { category ->
                    EnglishCategoryCard(
                        category = category,
                        stars = store.gameStars(category.gameKey),
                        onClick = { onCategory(category.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EnglishCategoryCard(category: EnglishCategory, stars: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(104.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(category.color)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = category.emoji, fontSize = 38.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.titleRo,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 19.sp,
                    maxLines = 2
                )
                Text(
                    text = category.titleEn,
                    color = Color.White.copy(alpha = 0.92f),
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1
                )
                Text(
                    text = if (stars > 0) "⭐".repeat(stars) + "☆".repeat(3 - stars) else "☆☆☆",
                    color = Color.White,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
private fun EnglishVoiceBanner(onInstall: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DuoRed.copy(alpha = 0.12f))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "🔇", fontSize = 24.sp)
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Vocea în limba engleză nu este instalată",
                color = OnBackground,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp
            )
            Text(
                text = "Fără ea nu poți auzi cuvintele în engleză. Cereți ajutorul unui părinte.",
                color = OnBackgroundLight,
                fontSize = 13.sp
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Button(
            onClick = onInstall,
            modifier = Modifier.height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DuoRed),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Instalează vocea", fontWeight = FontWeight.Bold)
        }
    }
}

/** The word's picture: its emoji, or a round colour swatch for colours without a good emoji. */
@Composable
internal fun EnglishPicture(word: EnglishWord, size: Dp, modifier: Modifier = Modifier) {
    val hex = word.colorHex
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        if (hex != null) {
            Box(
                modifier = Modifier
                    .size(size * 0.86f)
                    .clip(CircleShape)
                    .background(Color(hex))
                    .border(3.dp, OnBackground.copy(alpha = 0.25f), CircleShape)
            )
        } else {
            Text(
                text = word.emoji,
                fontSize = (size.value * 0.68f).sp,
                lineHeight = (size.value * 0.9f).sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

/** "Ce înseamnă?" style label for the current activity. */
@Composable
internal fun EnglishStepLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = EnglishInk,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 18.sp,
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(EnglishInk.copy(alpha = 0.1f))
            .padding(horizontal = 12.dp, vertical = 5.dp)
    )
}
