package com.danvaly.alfabet.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import com.danvaly.alfabet.data.Actor
import com.danvaly.alfabet.data.Backdrop
import com.danvaly.alfabet.data.Motion
import com.danvaly.alfabet.data.Scene
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private const val TAU = (2 * PI).toFloat()

/** A small animated picture: a painted backdrop with emoji characters moving on it. */
@Composable
fun StoryIllustration(scene: Scene, modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "scene")
    // One looping clock for the whole picture. Every movement uses a whole number
    // of cycles per loop, so nothing jumps when the clock restarts.
    val phase = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing), RepeatMode.Restart),
        label = "phase"
    )

    BoxWithConstraints(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .clearAndSetSemantics { }
    ) {
        val boxWidth = maxWidth
        val boxHeight = maxHeight

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawBackdrop(scene.backdrop, phase.value)
        }

        scene.actors.forEachIndexed { index, actor ->
            ActorView(actor, index, phase, boxWidth, boxHeight)
        }
    }
}

@Composable
private fun ActorView(
    actor: Actor,
    index: Int,
    phase: State<Float>,
    boxWidth: androidx.compose.ui.unit.Dp,
    boxHeight: androidx.compose.ui.unit.Dp
) {
    val glyph = boxHeight * actor.size
    val frame = glyph * 1.5f
    val fontSize = with(LocalDensity.current) { glyph.toSp() }

    Box(
        modifier = Modifier
            .offset(x = boxWidth * actor.x - frame / 2, y = boxHeight * actor.y - frame / 2)
            .size(frame)
            .graphicsLayer {
                val t = phase.value * TAU
                val s = glyph.toPx()
                val shift = index * 0.9f
                when (actor.motion) {
                    Motion.NONE -> Unit
                    Motion.BOUNCE -> translationY = -abs(sin(t * 4 + shift)) * s * 0.22f
                    Motion.FLOAT -> {
                        translationY = sin(t * 2 + shift) * s * 0.16f
                        translationX = cos(t + shift) * s * 0.12f
                    }
                    Motion.SWAY -> rotationZ = sin(t * 3 + shift) * 9f
                    Motion.PULSE -> {
                        val k = 1f + 0.07f * sin(t * 4 + shift)
                        scaleX = k
                        scaleY = k
                    }
                    Motion.RUN -> {
                        translationX = sin(t * 2 + shift) * s * 0.45f
                        translationY = -abs(sin(t * 8)) * s * 0.07f
                    }
                }
                if (actor.flip) scaleX = -scaleX
            },
        contentAlignment = Alignment.Center
    ) {
        Text(text = actor.emoji, fontSize = fontSize, maxLines = 1, softWrap = false)
    }
}

private fun DrawScope.drawBackdrop(backdrop: Backdrop, phase: Float) {
    when (backdrop) {
        Backdrop.MEADOW -> {
            sky(Color(0xFF7FD0FF), Color(0xFFD9F3FF))
            sun(0.84f, 0.2f, phase)
            cloud(0.26f, 0.2f, 1f, phase)
            cloud(0.6f, 0.12f, 0.7f, -phase)
            hill(0.2f, 0.62f, 0.9f, Color(0xFF8FD46A))
            hill(0.85f, 0.66f, 0.8f, Color(0xFF7AC74F))
            ground(0.8f, Color(0xFF6BBF45))
            flowers(11)
        }
        Backdrop.FARM -> {
            sky(Color(0xFF8AD4FF), Color(0xFFE3F6FF))
            sun(0.14f, 0.18f, phase)
            cloud(0.55f, 0.14f, 0.9f, phase)
            hill(0.5f, 0.58f, 1.3f, Color(0xFF9BD67A))
            ground(0.7f, Color(0xFF7AC74F))
            fence(0.66f)
            flowers(23)
        }
        Backdrop.GARDEN -> {
            sky(Color(0xFF9ADBFF), Color(0xFFE8F8FF))
            sun(0.86f, 0.18f, phase)
            cloud(0.3f, 0.16f, 0.8f, phase)
            fence(0.42f)
            ground(0.46f, Color(0xFF7AC74F))
            ground(0.68f, Color(0xFF9C6B3F))
            // furrows in the soil
            for (i in 0..2) {
                val y = size.height * (0.76f + i * 0.08f)
                drawLine(Color(0xFF7D5230), Offset(0f, y), Offset(size.width, y), size.height * 0.012f)
            }
        }
        Backdrop.RAIN -> {
            sky(Color(0xFF7C8FA3), Color(0xFFB9C7D4))
            cloud(0.22f, 0.14f, 1.3f, phase, Color(0xFF5E6E7E))
            cloud(0.62f, 0.1f, 1.5f, -phase, Color(0xFF6B7B8C))
            cloud(0.9f, 0.18f, 1.1f, phase, Color(0xFF5E6E7E))
            ground(0.78f, Color(0xFF5C9C4A))
            drawOval(
                Color(0xFF8FB8D8),
                topLeft = Offset(size.width * 0.42f, size.height * 0.84f),
                size = Size(size.width * 0.5f, size.height * 0.1f)
            )
            rain(phase)
        }
        Backdrop.RAINBOW -> {
            sky(Color(0xFF8AD4FF), Color(0xFFE8F8FF))
            rainbow()
            sun(0.86f, 0.16f, phase)
            cloud(0.16f, 0.52f, 1f, phase)
            cloud(0.86f, 0.54f, 0.9f, -phase)
            hill(0.3f, 0.68f, 1f, Color(0xFF8FD46A))
            ground(0.8f, Color(0xFF6BBF45))
            flowers(5)
        }
        Backdrop.FOREST -> {
            sky(Color(0xFFBFE8C9), Color(0xFFE9F8DD))
            val trees = listOf(0.08f to 0.9f, 0.3f to 0.7f, 0.5f to 1f, 0.72f to 0.75f, 0.93f to 0.95f)
            trees.forEach { (x, scale) -> tree(x, 0.82f, scale * 0.8f, Color(0xFF3F9B57)) }
            listOf(0.18f to 0.9f, 0.62f to 0.85f, 0.84f to 1f).forEach { (x, scale) ->
                tree(x, 0.86f, scale, Color(0xFF2E7D46))
            }
            ground(0.82f, Color(0xFF5FA84A))
            flowers(41)
        }
        Backdrop.EVENING -> {
            drawRect(
                Brush.verticalGradient(
                    listOf(Color(0xFF6A4C93), Color(0xFFFF8E72), Color(0xFFFFD08A))
                )
            )
            drawCircle(
                Color(0xFFFFE9A8),
                radius = size.height * 0.16f,
                center = Offset(size.width * 0.78f, size.height * 0.7f)
            )
            hill(0.25f, 0.66f, 1f, Color(0xFF5B7F4A))
            hill(0.8f, 0.72f, 0.9f, Color(0xFF4C6E3F))
            ground(0.82f, Color(0xFF446338))
        }
        Backdrop.NIGHT -> {
            sky(Color(0xFF14204A), Color(0xFF3A4C8C))
            stars(phase)
            val moon = Offset(size.width * 0.82f, size.height * 0.2f)
            drawCircle(Color(0x33FFF4C2), size.height * 0.16f, moon)
            drawCircle(Color(0xFFFFF4C2), size.height * 0.11f, moon)
            drawCircle(
                Color(0xFFEFDFA0),
                size.height * 0.025f,
                moon + Offset(size.height * 0.03f, size.height * 0.02f)
            )
            hill(0.2f, 0.7f, 1f, Color(0xFF233266))
            ground(0.84f, Color(0xFF1B2854))
        }
        Backdrop.BEACH -> {
            sky(Color(0xFF6FCBFF), Color(0xFFD6F2FF))
            sun(0.86f, 0.16f, phase)
            cloud(0.24f, 0.14f, 0.8f, phase)
            drawRect(
                Brush.verticalGradient(
                    listOf(Color(0xFF2FA8E0), Color(0xFF7FD8F5)),
                    startY = size.height * 0.36f,
                    endY = size.height * 0.64f
                ),
                topLeft = Offset(0f, size.height * 0.36f),
                size = Size(size.width, size.height * 0.3f)
            )
            ground(0.62f, Color(0xFFF6DFA4))
            waves(0.44f, phase, Color(0x66FFFFFF))
            waves(0.52f, -phase, Color(0x88FFFFFF))
            waves(0.62f, phase, Color(0xFFFFFFFF))
        }
        Backdrop.ROOM -> {
            drawRect(Color(0xFFFFE9C7))
            // wallpaper stripes
            val stripe = size.width / 14f
            for (i in 0 until 14 step 2) {
                drawRect(Color(0xFFFFDFB0), Offset(i * stripe, 0f), Size(stripe, size.height))
            }
            // window with sky
            val win = Offset(size.width * 0.08f, size.height * 0.1f)
            val winSize = Size(size.width * 0.26f, size.height * 0.34f)
            drawRoundRect(Color(0xFFFFFFFF), win, winSize, CornerRadius(size.height * 0.03f))
            val pad = size.height * 0.02f
            drawRoundRect(
                Color(0xFF9ADBFF),
                win + Offset(pad, pad),
                Size(winSize.width - 2 * pad, winSize.height - 2 * pad),
                CornerRadius(size.height * 0.02f)
            )
            drawLine(
                Color.White,
                win + Offset(winSize.width / 2, 0f),
                win + Offset(winSize.width / 2, winSize.height),
                pad
            )
            drawLine(
                Color.White,
                win + Offset(0f, winSize.height / 2),
                win + Offset(winSize.width, winSize.height / 2),
                pad
            )
            ground(0.78f, Color(0xFFC98F5A))
            drawLine(
                Color(0xFFA9713F),
                Offset(0f, size.height * 0.78f),
                Offset(size.width, size.height * 0.78f),
                size.height * 0.015f
            )
            drawOval(
                Color(0xFFEF8FA5),
                topLeft = Offset(size.width * 0.2f, size.height * 0.84f),
                size = Size(size.width * 0.6f, size.height * 0.13f)
            )
            drawOval(
                Color(0xFFF7B6C4),
                topLeft = Offset(size.width * 0.26f, size.height * 0.865f),
                size = Size(size.width * 0.48f, size.height * 0.08f)
            )
        }
    }
}

private fun DrawScope.sky(top: Color, bottom: Color) {
    drawRect(Brush.verticalGradient(listOf(top, bottom)))
}

private fun DrawScope.ground(fromY: Float, color: Color) {
    drawRect(color, Offset(0f, size.height * fromY), Size(size.width, size.height * (1f - fromY)))
}

private fun DrawScope.hill(centerX: Float, topY: Float, widthScale: Float, color: Color) {
    val w = size.width * widthScale
    val h = size.height * (1f - topY) * 2f
    drawOval(color, Offset(size.width * centerX - w / 2, size.height * topY), Size(w, h))
}

private fun DrawScope.sun(x: Float, y: Float, phase: Float) {
    val center = Offset(size.width * x, size.height * y)
    val r = size.height * 0.09f
    val spin = phase * TAU
    for (i in 0 until 12) {
        val a = spin + i * TAU / 12
        val dir = Offset(cos(a), sin(a))
        drawLine(
            Color(0xFFFFD54F),
            center + dir * (r * 1.3f),
            center + dir * (r * 1.8f),
            strokeWidth = r * 0.18f,
            cap = StrokeCap.Round
        )
    }
    drawCircle(Color(0xFFFFE066), r * 1.1f, center)
    drawCircle(Color(0xFFFFC928), r * 0.9f, center)
}

private fun DrawScope.cloud(x: Float, y: Float, scale: Float, phase: Float, color: Color = Color.White) {
    val drift = sin(phase * TAU) * size.width * 0.03f
    val c = Offset(size.width * x + drift, size.height * y)
    val r = size.height * 0.07f * scale
    drawCircle(color, r, c)
    drawCircle(color, r * 0.8f, c + Offset(-r * 1.1f, r * 0.25f))
    drawCircle(color, r * 0.85f, c + Offset(r * 1.1f, r * 0.2f))
    drawRoundRect(
        color,
        topLeft = c + Offset(-r * 1.8f, r * 0.1f),
        size = Size(r * 3.6f, r * 0.9f),
        cornerRadius = CornerRadius(r * 0.45f)
    )
}

private fun DrawScope.flowers(seed: Int) {
    val random = Random(seed)
    val colors = listOf(Color(0xFFFF86D0), Color(0xFFFFD900), Color.White, Color(0xFFFF6B6B))
    repeat(9) {
        val center = Offset(
            size.width * random.nextFloat(),
            size.height * (0.86f + random.nextFloat() * 0.12f)
        )
        drawCircle(colors[it % colors.size], size.height * 0.014f, center)
        drawCircle(Color(0xFFFFB300), size.height * 0.006f, center)
    }
}

private fun DrawScope.fence(y: Float) {
    val wood = Color(0xFFE8C79A)
    val top = size.height * y
    val postHeight = size.height * 0.12f
    val count = 12
    val gap = size.width / count
    for (i in 0..count) {
        drawRoundRect(
            wood,
            topLeft = Offset(i * gap - gap * 0.12f, top - postHeight),
            size = Size(gap * 0.24f, postHeight),
            cornerRadius = CornerRadius(gap * 0.1f)
        )
    }
    for (rail in listOf(0.3f, 0.7f)) {
        val railY = top - postHeight * rail
        drawLine(wood, Offset(0f, railY), Offset(size.width, railY), postHeight * 0.14f)
    }
}

private fun DrawScope.tree(x: Float, baseY: Float, scale: Float, leaf: Color) {
    val base = Offset(size.width * x, size.height * baseY)
    val h = size.height * 0.62f * scale
    val w = h * 0.5f
    drawRect(
        Color(0xFF8D5B36),
        topLeft = Offset(base.x - w * 0.09f, base.y - h * 0.3f),
        size = Size(w * 0.18f, h * 0.3f)
    )
    for (layer in 0 until 3) {
        val bottom = base.y - h * (0.22f + layer * 0.22f)
        val half = w * (0.5f - layer * 0.1f)
        val path = Path().apply {
            moveTo(base.x - half, bottom)
            lineTo(base.x + half, bottom)
            lineTo(base.x, bottom - h * 0.36f)
            close()
        }
        drawPath(path, leaf)
    }
}

private fun DrawScope.rain(phase: Float) {
    val random = Random(7)
    val length = size.height * 0.06f
    repeat(46) {
        val x = size.width * random.nextFloat()
        val start = random.nextFloat()
        val speed = 2 + random.nextInt(2)
        val y = size.height * (((start + phase * speed) % 1f) * 0.9f)
        drawLine(
            Color(0xCCE3F2FF),
            Offset(x, y),
            Offset(x - length * 0.25f, y + length),
            strokeWidth = size.height * 0.008f,
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.rainbow() {
    val colors = listOf(
        Color(0xFFFF4B4B), Color(0xFFFF9600), Color(0xFFFFD900),
        Color(0xFF58CC02), Color(0xFF1CB0F6), Color(0xFFCE82FF)
    )
    val band = size.height * 0.035f
    val center = Offset(size.width * 0.5f, size.height * 0.78f)
    colors.forEachIndexed { i, color ->
        val r = size.width * 0.42f - i * band
        drawArc(
            color = color,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(center.x - r, center.y - r),
            size = Size(r * 2, r * 2),
            style = Stroke(width = band)
        )
    }
}

private fun DrawScope.stars(phase: Float) {
    val random = Random(3)
    repeat(34) {
        val center = Offset(size.width * random.nextFloat(), size.height * random.nextFloat() * 0.6f)
        val twinkle = 0.55f + 0.45f * sin(phase * TAU * (1 + it % 3) + it)
        drawCircle(
            Color.White.copy(alpha = twinkle.coerceIn(0.1f, 1f)),
            size.height * (0.005f + random.nextFloat() * 0.007f),
            center
        )
    }
}

private fun DrawScope.waves(y: Float, phase: Float, color: Color) {
    val baseY = size.height * y
    val amplitude = size.height * 0.012f
    val path = Path()
    val steps = 40
    for (i in 0..steps) {
        val x = size.width * i / steps
        val waveY = baseY + sin(i * 0.9f + phase * TAU * 2) * amplitude
        if (i == 0) path.moveTo(x, waveY) else path.lineTo(x, waveY)
    }
    drawPath(path, color, style = Stroke(width = size.height * 0.012f, cap = StrokeCap.Round))
}
