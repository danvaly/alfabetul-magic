package com.danvaly.alfabet.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danvaly.alfabet.ui.LocalReadingStore
import com.danvaly.alfabet.ui.theme.BackgroundCream
import com.danvaly.alfabet.ui.theme.DuoBlue
import com.danvaly.alfabet.ui.theme.DuoGreen
import com.danvaly.alfabet.ui.theme.DuoOrange
import com.danvaly.alfabet.ui.theme.DuoPurple
import com.danvaly.alfabet.ui.theme.DuoRed
import com.danvaly.alfabet.ui.theme.OnBackgroundLight

private val HubInk = Color(0xFF3949AB)

private data class HubItem(
    val title: String,
    val subtitle: String,
    val emoji: String,
    val color: Color,
    val badge: Int,
    val onClick: () -> Unit
)

/** "Citesc": one place for every reading activity. */
@Composable
fun ReadingHubScreen(
    onBack: () -> Unit,
    onReadingTrainer: () -> Unit,
    onStories: () -> Unit,
    onSpellWord: () -> Unit,
    onSyllableSplit: () -> Unit,
    onConfusableLetters: () -> Unit,
    onHardWords: () -> Unit
) {
    val store = LocalReadingStore.current
    val hardCount = remember(store) { store.hardWords().size }

    val hubItems = listOf(
        HubItem("Citesc pas cu pas", "Litere, silabe, cuvânt", "👣", Color(0xFF5C6BC0), 0, onReadingTrainer),
        HubItem("Povești de citit", "Citește și răspunde", "📚", Color(0xFF00ACC1), 0, onStories),
        HubItem("Scrie cuvântul", "Pune literele în ordine", "✏️", DuoOrange, 0, onSpellWord),
        HubItem("Desparte în silabe", "Bate din palme", "👏", DuoPurple, 0, onSyllableSplit),
        HubItem("Litere care seamănă", "b sau d? m sau n?", "🔍", DuoBlue, 0, onConfusableLetters),
        HubItem("Cuvintele mele grele", "Exersează-le din nou", "💪", DuoRed, hardCount, onHardWords)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFE8EAF6), Color(0xFFC5CAE9), BackgroundCream)))
            .systemBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RoundBackButton(onBack = onBack, tint = HubInk)
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "📖", fontSize = 28.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Citesc",
                        style = MaterialTheme.typography.headlineMedium,
                        color = HubInk,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Alege ce vrei să citești azi.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnBackgroundLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 190.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(hubItems, key = { it.title }) { item ->
                    HubCard(item)
                }
            }
        }
    }
}

@Composable
private fun HubCard(item: HubItem) {
    Box {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(118.dp)
                .clickable(onClick = item.onClick),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = item.color),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = item.emoji, fontSize = 40.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 20.sp
                    )
                    Text(
                        text = item.subtitle,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 13.sp
                    )
                }
            }
        }
        if (item.badge > 0) {
            Text(
                text = "${item.badge}",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(DuoGreen)
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            )
        }
    }
}
