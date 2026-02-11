package com.danvaly.alfabet.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.sin
import kotlin.random.Random

data class ConfettiParticle(
    val x: Float,
    val startY: Float,
    val color: Color,
    val size: Float,
    val speed: Float,
    val angle: Float,
    val wobbleSpeed: Float,
    val wobbleAmplitude: Float
)

@Composable
fun ConfettiAnimation(
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    if (!isActive) return

    val progress = remember { Animatable(0f) }

    val confettiColors = listOf(
        Color(0xFF58CC02), Color(0xFF1CB0F6), Color(0xFFCE82FF),
        Color(0xFFFF9600), Color(0xFFFF4B4B), Color(0xFFFFD900),
        Color(0xFFFF86D0), Color(0xFF00CD9C)
    )

    val particles = remember {
        List(60) {
            ConfettiParticle(
                x = Random.nextFloat(),
                startY = Random.nextFloat() * -0.3f,
                color = confettiColors.random(),
                size = Random.nextFloat() * 12f + 6f,
                speed = Random.nextFloat() * 0.4f + 0.6f,
                angle = Random.nextFloat() * 360f,
                wobbleSpeed = Random.nextFloat() * 4f + 2f,
                wobbleAmplitude = Random.nextFloat() * 30f + 10f
            )
        }
    }

    LaunchedEffect(isActive) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 3000, easing = LinearEasing)
        )
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        particles.forEach { particle ->
            val p = progress.value
            val yProgress = p * particle.speed
            val currentY = particle.startY * h + yProgress * h * 1.5f
            val wobble = sin(p * particle.wobbleSpeed * Math.PI.toFloat() * 2f) * particle.wobbleAmplitude
            val currentX = particle.x * w + wobble
            val alpha = if (p > 0.7f) (1f - p) / 0.3f else 1f

            if (currentY < h && alpha > 0f) {
                rotate(
                    degrees = particle.angle + p * 360f * particle.speed,
                    pivot = Offset(currentX, currentY)
                ) {
                    drawRect(
                        color = particle.color.copy(alpha = alpha.coerceIn(0f, 1f)),
                        topLeft = Offset(currentX - particle.size / 2, currentY - particle.size / 2),
                        size = Size(particle.size, particle.size * 0.6f)
                    )
                }
            }
        }
    }
}
