package com.danvaly.alfabet.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun StarRating(
    stars: Int,
    maxStars: Int = 3,
    starSize: Dp = 48.dp
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(maxStars) { index ->
            val isFilled = index < stars
            val scale = remember { Animatable(0f) }

            LaunchedEffect(stars) {
                delay(index * 200L)
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }

            Box(
                modifier = Modifier
                    .size(starSize)
                    .scale(scale.value),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isFilled) "\u2B50" else "\u2606",
                    fontSize = (starSize.value * 0.7f).sp
                )
            }
        }
    }
}
