package com.danvaly.alfabet.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danvaly.alfabet.data.model.Letter
import com.danvaly.alfabet.ui.theme.*

@Composable
fun LetterCard(
    letter: Letter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isHighlighted: Boolean = false,
    size: Dp = 90.dp,
    showLowercase: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = remember { Animatable(1f) }

    val letterColor = getLetterTypeColor(letter)
    val shadowColor = letterColor.copy(alpha = 0.6f)

    LaunchedEffect(isPressed) {
        if (isPressed) {
            scale.animateTo(0.9f, spring(stiffness = Spring.StiffnessHigh))
        } else {
            scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
    }

    // Duolingo-style 3D card
    Box(
        modifier = modifier
            .size(size)
            .scale(scale.value)
    ) {
        // Shadow card
        Card(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = 4.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = shadowColor),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {}

        // Main card
        Card(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = if (isPressed) 3.dp else 0.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                ),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = letterColor),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = letter.uppercase,
                    fontSize = (size.value * 0.38f).sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                if (showLowercase) {
                    Text(
                        text = letter.lowercase,
                        fontSize = (size.value * 0.22f).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun SimpleLetterCard(
    text: String,
    backgroundColor: Color = DuoBlue,
    onClick: () -> Unit = {},
    size: Dp = 60.dp,
    fontSize: Int = 24
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = remember { Animatable(1f) }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            scale.animateTo(0.9f, spring(stiffness = Spring.StiffnessHigh))
        } else {
            scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
    }

    Box(
        modifier = Modifier
            .size(size)
            .scale(scale.value)
    ) {
        // Shadow
        Card(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = 3.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = backgroundColor.copy(alpha = 0.5f)),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {}

        Card(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = if (isPressed) 2.dp else 0.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                ),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = backgroundColor),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    fontSize = fontSize.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

fun getLetterTypeColor(letter: Letter): Color {
    return when {
        letter.isDiacritic -> DiacriticColor
        letter.isVowel -> VowelColor
        else -> ConsonantColor
    }
}
