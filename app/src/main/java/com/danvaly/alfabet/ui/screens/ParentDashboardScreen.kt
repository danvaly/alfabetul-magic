package com.danvaly.alfabet.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danvaly.alfabet.data.DifficultyLevel
import com.danvaly.alfabet.data.DifficultyManager
import com.danvaly.alfabet.data.Profile
import com.danvaly.alfabet.data.ProfileManager
import com.danvaly.alfabet.data.ReadingContent
import com.danvaly.alfabet.data.RomanianAlphabet
import com.danvaly.alfabet.data.repository.ProgressRepository
import com.danvaly.alfabet.ui.LocalProfileManager
import com.danvaly.alfabet.ui.LocalReadingStore
import com.danvaly.alfabet.ui.theme.*
import kotlinx.coroutines.launch
import java.time.LocalDate

private val GoalOptions = listOf(5, 10, 15, 20)

@Composable
fun ParentDashboardScreen(
    onBack: () -> Unit,
    repository: ProgressRepository,
    onProfileSelected: (String) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val store = LocalReadingStore.current
    val profileManager = LocalProfileManager.current

    var isUnlocked by remember { mutableStateOf(false) }
    var gateAnswer by remember { mutableStateOf("") }
    var gateError by remember { mutableStateOf(false) }

    // Random math problem for parent gate
    val num1 = remember { (10..30).random() }
    val num2 = remember { (10..30).random() }
    val correctAnswer = num1 + num2

    val learnedCount by repository.learnedCount.collectAsState(initial = 0)
    val totalStars by repository.totalStars.collectAsState(initial = 0)
    val appStats by repository.appStats.collectAsState(initial = null)

    val totalLetters = RomanianAlphabet.letters.size
    val currentDifficulty = DifficultyManager.fromInt(appStats?.difficulty ?: 1)

    val today = remember { LocalDate.now().toString() }
    var dailyGoal by remember(store) { mutableIntStateOf(store.dailyGoalMinutes) }
    val minutesToday = remember(store) { store.minutesOn(today) }
    val storiesRead = remember(store) { store.storiesRead() }
    val readingStars = remember(store) { store.totalReadingStars() }
    val hardWords = remember(store) { store.hardWords(10) }

    // Profile dialogs
    var editing by remember { mutableStateOf<Profile?>(null) }
    var adding by remember { mutableStateOf(false) }
    var removing by remember { mutableStateOf<Profile?>(null) }

    val infiniteTransition = rememberInfiniteTransition(label = "parent_anim")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFF5F5F5), Color(0xFFEEEEEE), Color(0xFFF5F5F5))
                )
            )
            .systemBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(DuoGreen.copy(alpha = 0.15f))
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Înapoi", tint = DuoGreen)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "👨‍👩‍👧 Panou Părinți",
                    style = MaterialTheme.typography.headlineMedium,
                    color = OnBackground,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            if (!isUnlocked) {
                // Parent gate
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        elevation = CardDefaults.cardElevation(8.dp),
                        modifier = Modifier.padding(horizontal = 48.dp, vertical = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🔒",
                                fontSize = 36.sp,
                                modifier = Modifier.offset(y = floatOffset.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Verificare părinte",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = OnBackground
                            )
                            Text(
                                text = "Rezolvă problema pentru a accesa panoul:",
                                style = MaterialTheme.typography.bodyLarge,
                                color = OnBackgroundLight,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "$num1 + $num2 = ?",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Black,
                                color = DuoGreen
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(
                                    value = gateAnswer,
                                    onValueChange = {
                                        gateAnswer = it
                                        gateError = false
                                    },
                                    label = { Text("Răspuns") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    isError = gateError,
                                    modifier = Modifier.width(180.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Button(
                                    onClick = {
                                        if (gateAnswer.trim().toIntOrNull() == correctAnswer) {
                                            isUnlocked = true
                                        } else {
                                            gateError = true
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = DuoGreen),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Text("Verifică", fontWeight = FontWeight.Bold)
                                }
                            }
                            if (gateError) {
                                Text(
                                    text = "Răspuns greșit!",
                                    color = DuoRed,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            } else {
                // Dashboard content
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                ) {
                    // Left: stats, reading, hard words
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(end = 16.dp, bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SectionTitle("Statistici — ${profileManager.active.avatar} ${profileManager.active.name}")

                        // Progress
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Litere învățate",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = OnBackground
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$learnedCount / $totalLetters",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Black,
                                    color = DuoGreen
                                )
                                LinearProgressIndicator(
                                    progress = { if (totalLetters > 0) learnedCount.toFloat() / totalLetters else 0f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = DuoGreen,
                                    trackColor = DuoGreen.copy(alpha = 0.15f)
                                )
                            }
                        }

                        // Stats grid
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ParentStatCard(
                                emoji = "⭐",
                                value = "${totalStars ?: 0}",
                                label = "Stele totale",
                                modifier = Modifier.weight(1f)
                            )
                            ParentStatCard(
                                emoji = "🎮",
                                value = "${appStats?.gamesPlayed ?: 0}",
                                label = "Jocuri",
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ParentStatCard(
                                emoji = "🔥",
                                value = "${appStats?.currentStreak ?: 0}",
                                label = "Serie zile",
                                modifier = Modifier.weight(1f)
                            )
                            ParentStatCard(
                                emoji = "⏰",
                                value = "${appStats?.totalPlayTimeMinutes ?: 0} min",
                                label = "Timp total",
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ParentStatCard(
                                emoji = "📚",
                                value = "$storiesRead / ${ReadingContent.stories.size}",
                                label = "Povești citite",
                                modifier = Modifier.weight(1f)
                            )
                            ParentStatCard(
                                emoji = "📖",
                                value = "$readingStars",
                                label = "Stele la citit",
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ParentStatCard(
                                emoji = "⏱",
                                value = "$minutesToday / $dailyGoal min",
                                label = "Azi",
                                modifier = Modifier.weight(1f)
                            )
                            ParentStatCard(
                                emoji = "💪",
                                value = "${hardWords.size}",
                                label = "Cuvinte grele",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        SectionTitle("Cuvinte grele")
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (hardWords.isEmpty()) {
                                    Text(
                                        text = "Niciun cuvânt greu deocamdată. Bravo!",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = OnBackgroundLight
                                    )
                                } else {
                                    Text(
                                        text = "Cuvintele pe care copilul le-a deschis des sau le-a greșit:",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = OnBackgroundLight
                                    )
                                    hardWords.forEach { stat ->
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = stat.marked.replace("-", ""),
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = OnBackground
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "(${stat.marked})",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = OnBackgroundLight,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Text(
                                                text = "❌ ${stat.mistakes}  🔍 ${stat.lookups}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = OnBackgroundLight
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Right: settings and profiles
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SectionTitle("Nivel de dificultate")

                        DifficultyLevel.entries.forEach { level ->
                            val isSelected = level == currentDifficulty
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        scope.launch {
                                            repository.setDifficulty(DifficultyManager.toInt(level))
                                        }
                                    },
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) DuoGreen.copy(alpha = 0.15f) else SurfaceWhite
                                ),
                                elevation = CardDefaults.cardElevation(if (isSelected) 4.dp else 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(text = level.emoji, fontSize = 28.sp)
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = level.label,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (isSelected) DuoGreen else OnBackground
                                        )
                                        Text(
                                            text = when (level) {
                                                DifficultyLevel.BEGINNER -> "3 opțiuni, viteză lentă, indicii activate"
                                                DifficultyLevel.INTERMEDIATE -> "4 opțiuni, viteză normală"
                                                DifficultyLevel.ADVANCED -> "5 opțiuni, viteză rapidă"
                                            },
                                            style = MaterialTheme.typography.bodySmall,
                                            color = OnBackgroundLight
                                        )
                                    }
                                    if (isSelected) {
                                        Text(text = "✅", fontSize = 24.sp)
                                    }
                                }
                            }
                        }

                        SectionTitle("Obiectiv zilnic")
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            GoalOptions.forEach { minutes ->
                                val selected = minutes == dailyGoal
                                Text(
                                    text = "$minutes min",
                                    color = if (selected) Color.White else DuoOrange,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(50))
                                        .background(if (selected) DuoOrange else DuoOrange.copy(alpha = 0.14f))
                                        .clickable {
                                            store.dailyGoalMinutes = minutes
                                            dailyGoal = minutes
                                        }
                                        .padding(vertical = 10.dp)
                                )
                            }
                        }

                        SectionTitle("Copii")
                        profileManager.profiles.forEach { profile ->
                            val isActive = profile.id == profileManager.activeId
                            ProfileRow(
                                profile = profile,
                                isActive = isActive,
                                canRemove = !isActive && profile.id != ProfileManager.DEFAULT_ID,
                                onSelect = { if (!isActive) onProfileSelected(profile.id) },
                                onEdit = { editing = profile },
                                onRemove = { removing = profile }
                            )
                        }
                        Button(
                            onClick = { adding = true },
                            colors = ButtonDefaults.buttonColors(containerColor = DuoBlue),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Adaugă un copil", fontWeight = FontWeight.Bold)
                        }
                        Text(
                            text = "Fiecare copil are progresul lui. Profilul folosit acum nu poate fi șters; alege mai întâi alt copil.",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnBackgroundLight
                        )

                        SectionTitle("Sfaturi")
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = DuoYellow.copy(alpha = 0.1f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "💡 Sesiunile scurte de 10-15 minute sunt cele mai eficiente pentru copii.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = OnBackground
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "🌟 Încurajați copilul să exploreze mai multe moduri de învățare.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = OnBackground
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (adding) {
        ProfileEditDialog(
            title = "Copil nou",
            initialName = "",
            initialAvatar = ProfileManager.AVATARS[profileManager.profiles.size % ProfileManager.AVATARS.size],
            onConfirm = { name, avatar ->
                profileManager.add(name, avatar)
                adding = false
            },
            onDismiss = { adding = false }
        )
    }

    editing?.let { profile ->
        ProfileEditDialog(
            title = "Schimbă numele",
            initialName = profile.name,
            initialAvatar = profile.avatar,
            onConfirm = { name, avatar ->
                profileManager.rename(profile.id, name, avatar)
                editing = null
            },
            onDismiss = { editing = null }
        )
    }

    removing?.let { profile ->
        AlertDialog(
            onDismissRequest = { removing = null },
            title = { Text("Ștergi profilul ${profile.name}?", fontWeight = FontWeight.ExtraBold) },
            text = { Text("Tot progresul acestui copil (litere, stele, povești, abțibilduri) va fi șters definitiv.") },
            confirmButton = {
                TextButton(onClick = {
                    profileManager.remove(profile.id)
                    removing = null
                }) {
                    Text("Șterge", color = DuoRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { removing = null }) { Text("Renunță") }
            }
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.ExtraBold,
        color = OnBackground
    )
}

@Composable
private fun ProfileRow(
    profile: Profile,
    isActive: Boolean,
    canRemove: Boolean,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) DuoGreen.copy(alpha = 0.15f) else SurfaceWhite
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = profile.avatar, fontSize = 28.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = profile.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = OnBackground
                )
                Text(
                    text = if (isActive) "Se joacă acum" else "Atinge ca să-l alegi",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isActive) DuoGreen else OnBackgroundLight
                )
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Filled.Edit, contentDescription = "Schimbă numele", tint = DuoBlue)
            }
            if (canRemove) {
                IconButton(onClick = onRemove) {
                    Icon(Icons.Filled.Delete, contentDescription = "Șterge profilul", tint = DuoRed)
                }
            }
        }
    }
}

@Composable
private fun ProfileEditDialog(
    title: String,
    initialName: String,
    initialAvatar: String,
    onConfirm: (name: String, avatar: String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var avatar by remember { mutableStateOf(initialAvatar) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.ExtraBold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it.take(20) },
                    label = { Text("Numele copilului") },
                    singleLine = true
                )
                ProfileManager.AVATARS.chunked(4).forEach { rowAvatars ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        rowAvatars.forEach { option ->
                            val selected = option == avatar
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (selected) DuoGreen.copy(alpha = 0.25f) else Color.Transparent)
                                    .border(
                                        width = if (selected) 2.dp else 0.dp,
                                        color = if (selected) DuoGreen else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { avatar = option },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = option, fontSize = 22.sp)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name, avatar) },
                enabled = name.isNotBlank()
            ) {
                Text("Salvează", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Renunță") }
        }
    )
}

@Composable
private fun ParentStatCard(
    emoji: String,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = emoji, fontSize = 24.sp)
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = DuoGreen,
                textAlign = TextAlign.Center
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = OnBackgroundLight,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}
