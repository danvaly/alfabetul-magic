package com.danvaly.alfabet.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.danvaly.alfabet.data.EnglishContent
import com.danvaly.alfabet.ui.theme.DuoGreen
import com.danvaly.alfabet.ui.theme.DuoOrange
import com.danvaly.alfabet.ui.theme.DuoPurple
import com.danvaly.alfabet.ui.theme.OnBackground
import com.danvaly.alfabet.ui.theme.OnBackgroundLight
import com.danvaly.alfabet.ui.theme.SurfaceWhite
import kotlinx.coroutines.delay

/** Flash cards: picture, the English word (spoken), and its Romanian meaning. */
@Composable
fun EnglishLearnScreen(
    categoryId: String,
    onBack: () -> Unit,
    onQuiz: () -> Unit,
    soundManager: SoundManager
) {
    val category = EnglishContent.category(categoryId)
    var index by rememberSaveable(categoryId) { mutableIntStateOf(0) }

    DisposableEffect(Unit) {
        onDispose { soundManager.stop() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EnglishBackground)
            .systemBarsPadding()
    ) {
        if (category == null) {
            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 10.dp)) {
                ReadingGameTopBar(title = "🌍 Engleză", ink = EnglishInk, total = 0, current = 0, onBack = onBack)
            }
        } else Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 10.dp)) {
            val words = category.words
            val word = words[index.coerceIn(0, words.lastIndex)]
            val isLast = index >= words.lastIndex

            LaunchedEffect(categoryId, index) {
                delay(350)
                soundManager.speakEnglish(word.en)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                RoundBackButton(onBack = onBack, tint = EnglishInk)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "${category.emoji} ${category.titleRo}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = EnglishInk,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = category.titleEn,
                    color = OnBackgroundLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${index + 1} / ${words.size}",
                    color = OnBackground,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    onClick = onQuiz,
                    modifier = Modifier.height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DuoGreen),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text("🎮 Joacă", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ArrowButton(
                    forward = false,
                    enabled = index > 0,
                    onClick = { if (index > 0) index-- }
                )

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { soundManager.speakEnglish(word.en) },
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            EnglishPicture(word = word, size = 104.dp)
                            Text(
                                text = word.en,
                                fontSize = 46.sp,
                                lineHeight = 52.sp,
                                fontWeight = FontWeight.Black,
                                color = EnglishInk,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = word.ro,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnBackground,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Atinge cartonașul ca să asculți.",
                                fontSize = 13.sp,
                                color = OnBackgroundLight,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .width(156.dp)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
                ) {
                    Button(
                        onClick = { soundManager.speakEnglish(word.en) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DuoOrange),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ascultă", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    }
                    Button(
                        onClick = { soundManager.speakEnglish(word.en, slow = true) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DuoPurple),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("🐢 Încet", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    }
                    if (isLast) {
                        Button(
                            onClick = onQuiz,
                            modifier = Modifier.fillMaxWidth().height(64.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DuoGreen),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text("🎮 Joacă!", fontWeight = FontWeight.Black, fontSize = 19.sp)
                        }
                    } else {
                        Button(
                            onClick = { index++ },
                            modifier = Modifier.fillMaxWidth().height(64.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EnglishInk),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text("Înainte", fontWeight = FontWeight.Black, fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ArrowButton(forward: Boolean, enabled: Boolean, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .size(60.dp)
            .clip(CircleShape)
            .background(if (enabled) EnglishInk.copy(alpha = 0.15f) else Color.Transparent),
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = EnglishInk,
            disabledContentColor = EnglishInk.copy(alpha = 0.25f)
        )
    ) {
        Icon(
            imageVector = if (forward) Icons.AutoMirrored.Filled.ArrowForward else Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = if (forward) "Cuvântul următor" else "Cuvântul anterior",
            modifier = Modifier.size(34.dp)
        )
    }
}
