package com.danvaly.alfabet.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danvaly.alfabet.audio.SoundManager
import com.danvaly.alfabet.audio.VoiceState
import com.danvaly.alfabet.data.Profile
import com.danvaly.alfabet.ui.LocalProfileManager
import com.danvaly.alfabet.ui.LocalReadingStore
import kotlinx.coroutines.delay
import java.time.LocalDate
import com.danvaly.alfabet.ui.theme.*

@Composable
fun HomeScreen(
    onNavigateToLearn: () -> Unit,
    onNavigateToTrace: () -> Unit,
    onNavigateToSyllable: () -> Unit,
    onNavigateToGames: () -> Unit,
    onNavigateToProgress: () -> Unit,
    onNavigateToDailyChallenge: () -> Unit,
    onNavigateToParentDashboard: () -> Unit,
    onNavigateToReading: () -> Unit,
    onNavigateToStickers: () -> Unit,
    onProfileSelected: (String) -> Unit,
    soundManager: SoundManager
) {
    val context = LocalContext.current
    val store = LocalReadingStore.current
    val profileManager = LocalProfileManager.current
    val voiceState by soundManager.voiceState.collectAsState()

    val titleScale = remember { Animatable(0.3f) }
    val infiniteTransition = rememberInfiniteTransition(label = "home_anim")

    // Daily goal: MainActivity adds a minute every 60 s, so refresh the indicator now and then.
    val today = remember { LocalDate.now().toString() }
    val goal = remember(store) { store.dailyGoalMinutes }
    var minutesToday by remember { mutableIntStateOf(store.minutesOn(today)) }
    var showProfiles by remember { mutableStateOf(false) }

    LaunchedEffect(store) {
        while (true) {
            minutesToday = store.minutesOn(today)
            delay(5_000L)
        }
    }

    LaunchedEffect(Unit) {
        titleScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    val floatOffset2 by infiniteTransition.animateFloat(
        initialValue = 6f,
        targetValue = -6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float2"
    )

    val floatOffset3 by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float3"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE8F5E9),
                        Color(0xFFC8E6C9),
                        BackgroundCream
                    )
                )
            )
    ) {
        // Floating animals and flowers (behind the scrolling content)
        Text(
            text = "🌻",
            fontSize = 32.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-40).dp, y = (90 + floatOffset2).dp)
        )
        Text(
            text = "🐰",
            fontSize = 34.sp,
            modifier = Modifier.offset(x = 15.dp, y = (140 + floatOffset3).dp)
        )
        Text(
            text = "🐞",
            fontSize = 28.sp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 25.dp, y = (-30 + floatOffset2).dp)
        )
        Text(
            text = "🐢",
            fontSize = 30.sp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-35).dp, y = (-25 + floatOffset3).dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top bar: who is playing, title, today's goal
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfileChip(
                    profile = profileManager.active,
                    onClick = { showProfiles = true }
                )
                Spacer(modifier = Modifier.width(12.dp))
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .scale(titleScale.value),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🦉",
                        fontSize = 36.sp,
                        modifier = Modifier.offset(y = floatOffset.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Alfabetul Magic",
                            style = MaterialTheme.typography.headlineMedium,
                            color = DuoGreen,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Învață alfabetul românesc prin joacă!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnBackgroundLight,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                DailyGoalIndicator(minutes = minutesToday, goal = goal)
            }

            if (voiceState == VoiceState.MISSING) {
                Spacer(modifier = Modifier.height(10.dp))
                VoiceMissingBanner(onInstall = { soundManager.openVoiceSettings(context) })
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main game mode cards - top row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterHorizontally)
            ) {
                GameModeCard(
                    title = "Învață\nLiterele",
                    emoji = "🐻",
                    icon = Icons.Filled.AutoStories,
                    backgroundColor = DuoGreen,
                    shadowColor = DuoGreenDark,
                    onClick = onNavigateToLearn,
                    modifier = Modifier.weight(1f),
                    cardHeight = 140
                )
                GameModeCard(
                    title = "Scrie\nLiterele",
                    emoji = "🐝",
                    icon = Icons.Filled.Draw,
                    backgroundColor = DuoBlue,
                    shadowColor = DuoBlueDark,
                    onClick = onNavigateToTrace,
                    modifier = Modifier.weight(1f),
                    cardHeight = 140
                )
                GameModeCard(
                    title = "Formează\nSilabe",
                    emoji = "🦋",
                    icon = Icons.Filled.Extension,
                    backgroundColor = DuoOrange,
                    shadowColor = Color(0xFFCC7800),
                    onClick = onNavigateToSyllable,
                    modifier = Modifier.weight(1f),
                    cardHeight = 140
                )
                GameModeCard(
                    title = "Jocuri",
                    emoji = "🐾",
                    icon = Icons.Filled.SportsEsports,
                    backgroundColor = DuoPurple,
                    shadowColor = Color(0xFFA45ED6),
                    onClick = onNavigateToGames,
                    modifier = Modifier.weight(1f),
                    cardHeight = 140
                )
                GameModeCard(
                    title = "Progresul\nMeu",
                    emoji = "🌻",
                    icon = Icons.Filled.EmojiEvents,
                    backgroundColor = DuoYellow,
                    shadowColor = Color(0xFFCCAD00),
                    onClick = onNavigateToProgress,
                    modifier = Modifier.weight(1f),
                    cardHeight = 140
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom row: one big reading card + daily challenge + stickers + parents
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterHorizontally)
            ) {
                GameModeCard(
                    title = "Citesc\nPovești, cuvinte, silabe",
                    emoji = "📖",
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    backgroundColor = Color(0xFF5C6BC0),
                    shadowColor = Color(0xFF3949AB),
                    onClick = onNavigateToReading,
                    modifier = Modifier.weight(2f),
                    cardHeight = 160
                )
                GameModeCard(
                    title = "Provocarea\nZilei",
                    emoji = "🌟",
                    icon = Icons.Filled.Star,
                    backgroundColor = Color(0xFFFF6B6B),
                    shadowColor = Color(0xFFCC4444),
                    onClick = onNavigateToDailyChallenge,
                    modifier = Modifier.weight(1f),
                    cardHeight = 160
                )
                GameModeCard(
                    title = "Abțibilduri",
                    emoji = "🦄",
                    icon = Icons.Filled.Favorite,
                    backgroundColor = Color(0xFFEC407A),
                    shadowColor = Color(0xFFC2185B),
                    onClick = onNavigateToStickers,
                    modifier = Modifier.weight(1f),
                    cardHeight = 160
                )
                GameModeCard(
                    title = "Panou\nPărinți",
                    emoji = "👨‍👩‍👧",
                    icon = Icons.Filled.Settings,
                    backgroundColor = Color(0xFF78909C),
                    shadowColor = Color(0xFF546E7A),
                    onClick = onNavigateToParentDashboard,
                    modifier = Modifier.weight(1f),
                    cardHeight = 160
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bottom row of decorative flowers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("🌺", "🌸", "🌼", "🌻", "🌷", "🌹", "🌺").forEachIndexed { index, flower ->
                    Text(
                        text = flower,
                        fontSize = 22.sp,
                        modifier = Modifier.offset(
                            y = if (index % 2 == 0) floatOffset.dp else floatOffset2.dp
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }

    if (showProfiles) {
        ProfilePickerDialog(
            profiles = profileManager.profiles,
            activeId = profileManager.activeId,
            onSelect = { id ->
                showProfiles = false
                if (id != profileManager.activeId) onProfileSelected(id)
            },
            onDismiss = { showProfiles = false }
        )
    }
}

@Composable
private fun ProfileChip(profile: Profile, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(SurfaceWhite)
            .clickable(onClickLabel = "Schimbă copilul", onClick = onClick)
            .padding(start = 6.dp, end = 14.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(DuoGreen.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = profile.avatar, fontSize = 22.sp)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = profile.name,
            color = OnBackground,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 16.sp,
            maxLines = 1
        )
    }
}

@Composable
private fun DailyGoalIndicator(minutes: Int, goal: Int) {
    val reached = minutes >= goal
    Column(
        modifier = Modifier
            .width(130.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceWhite)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (reached) "⏱ $minutes / $goal min ⭐" else "⏱ $minutes / $goal min",
            color = if (reached) DuoGreen else OnBackground,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 14.sp,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { if (goal > 0) (minutes.toFloat() / goal).coerceIn(0f, 1f) else 0f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = if (reached) DuoGreen else DuoOrange,
            trackColor = DuoOrange.copy(alpha = 0.15f)
        )
    }
}

@Composable
private fun VoiceMissingBanner(onInstall: () -> Unit) {
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
                text = "Vocea în limba română nu este instalată",
                color = OnBackground,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp
            )
            Text(
                text = "Fără ea aplicația nu poate citi cu voce tare. Cereți ajutorul unui părinte.",
                color = OnBackgroundLight,
                fontSize = 13.sp
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Button(
            onClick = onInstall,
            colors = ButtonDefaults.buttonColors(containerColor = DuoRed),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Instalează vocea", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ProfilePickerDialog(
    profiles: List<Profile>,
    activeId: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cine se joacă?", fontWeight = FontWeight.ExtraBold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                profiles.forEach { profile ->
                    val selected = profile.id == activeId
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (selected) DuoGreen.copy(alpha = 0.18f) else DuoBlue.copy(alpha = 0.08f))
                            .clickable { onSelect(profile.id) }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = profile.avatar, fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = profile.name,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = OnBackground,
                            modifier = Modifier.weight(1f)
                        )
                        if (selected) Text(text = "✅", fontSize = 20.sp)
                    }
                }
                if (profiles.size < 2) {
                    Text(
                        text = "Alți copii se pot adăuga din Panou Părinți.",
                        color = OnBackgroundLight,
                        fontSize = 13.sp
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Închide") }
        }
    )
}

@Composable
private fun GameModeCard(
    title: String,
    emoji: String,
    icon: ImageVector,
    backgroundColor: Color,
    shadowColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cardHeight: Int = 190
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = remember { Animatable(1f) }
    val infiniteTransition = rememberInfiniteTransition(label = "card_$title")

    val wobble by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wobble_$title"
    )

    LaunchedEffect(isPressed) {
        if (isPressed) {
            scale.animateTo(0.88f, spring(stiffness = Spring.StiffnessHigh))
        } else {
            scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
    }

    Box(
        modifier = modifier
            .height(cardHeight.dp)
            .scale(scale.value)
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = 6.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = shadowColor),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {}

        Card(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = if (isPressed) 4.dp else 0.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                ),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = backgroundColor),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = emoji,
                    fontSize = if (cardHeight > 150) 44.sp else 32.sp,
                    modifier = Modifier.offset(y = wobble.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
