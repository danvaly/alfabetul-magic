package com.danvaly.alfabet.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.danvaly.alfabet.ui.theme.ConsonantColor
import com.danvaly.alfabet.ui.theme.DiacriticColor
import com.danvaly.alfabet.ui.theme.PastelColors
import com.danvaly.alfabet.ui.theme.VowelColor

@Composable
fun LetterCard(
    letter: Letter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 90.dp,
    showLowercase: Boolean = true,
    isHighlighted: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "scale"
    )

    val backgroundColor = if (isHighlighted) {
        when {
            letter.isDiacritic -> DiacriticColor
            letter.isVowel -> VowelColor
            else -> ConsonantColor
        }
    } else {
        PastelColors[letter.colorIndex % PastelColors.size]
    }

    val animatedColor by animateColorAsState(
        targetValue = backgroundColor,
        label = "color"
    )

    Card(
        modifier = modifier
            .size(size)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = animatedColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp, pressedElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(4.dp).size(size - 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = letter.uppercase,
                fontSize = if (size > 80.dp) 36.sp else 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            if (showLowercase) {
                Text(
                    text = letter.lowercase,
                    fontSize = if (size > 80.dp) 22.sp else 18.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun SimpleLetterCard(
    text: String,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    fontSize: Int = 32
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "scale"
    )

    Card(
        modifier = modifier
            .size(size)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(4.dp).size(size - 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                fontSize = fontSize.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun getLetterTypeColor(letter: Letter): Color = when {
    letter.isDiacritic -> DiacriticColor
    letter.isVowel -> VowelColor
    else -> ConsonantColor
}
