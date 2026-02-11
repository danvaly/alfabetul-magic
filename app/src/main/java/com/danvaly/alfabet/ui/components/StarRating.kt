package com.danvaly.alfabet.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.danvaly.alfabet.ui.theme.OnBackgroundLight
import com.danvaly.alfabet.ui.theme.StarGold
import kotlinx.coroutines.delay

@Composable
fun StarRating(
    stars: Int,
    maxStars: Int = 3,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    animated: Boolean = true
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(maxStars) { index ->
            val isFilled = index < stars
            val scale = remember { Animatable(if (animated) 0f else 1f) }

            LaunchedEffect(stars) {
                if (animated && isFilled) {
                    delay(index * 200L)
                    scale.animateTo(
                        targetValue = 1.3f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    )
                    scale.animateTo(
                        targetValue = 1f,
                        animationSpec = spring(stiffness = Spring.StiffnessMedium)
                    )
                }
            }

            Icon(
                imageVector = if (isFilled) Icons.Filled.Star else Icons.Filled.StarBorder,
                contentDescription = if (isFilled) "Stea câștigată" else "Stea necâștigată",
                tint = if (isFilled) StarGold else OnBackgroundLight,
                modifier = Modifier
                    .size(size)
                    .scale(scale.value)
            )
        }
    }
}
