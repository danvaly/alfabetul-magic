package com.danvaly.alfabet.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import com.danvaly.alfabet.ui.theme.OnBackgroundLight
import com.danvaly.alfabet.ui.theme.PrimaryPink

data class DrawingPath(
    val path: Path = Path(),
    val color: Color = PrimaryPink,
    val strokeWidth: Float = 16f,
    val points: List<Offset> = emptyList()
)

@Composable
fun TracingCanvas(
    guideLetter: String,
    paths: List<DrawingPath>,
    currentPath: DrawingPath?,
    onDragStart: (Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier,
    guideColor: Color = OnBackgroundLight.copy(alpha = 0.15f),
    strokeColor: Color = PrimaryPink
) {
    val density = LocalDensity.current
    val guideFontSize = with(density) { 280.sp.toPx() }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset -> onDragStart(offset) },
                    onDrag = { change, _ ->
                        change.consume()
                        onDrag(change.position)
                    },
                    onDragEnd = { onDragEnd() }
                )
            }
    ) {
        // Draw guide letter (large, semi-transparent)
        val paint = android.graphics.Paint().apply {
            color = guideColor.hashCode()
            textSize = guideFontSize
            textAlign = android.graphics.Paint.Align.CENTER
            typeface = android.graphics.Typeface.create(
                android.graphics.Typeface.SERIF,
                android.graphics.Typeface.BOLD
            )
            isAntiAlias = true
        }

        val textBounds = android.graphics.Rect()
        paint.getTextBounds(guideLetter, 0, guideLetter.length, textBounds)
        val textY = size.height / 2f + textBounds.height() / 2f

        drawContext.canvas.nativeCanvas.drawText(
            guideLetter,
            size.width / 2f,
            textY,
            paint
        )

        // Draw dashed outline of the letter
        val outlinePaint = android.graphics.Paint().apply {
            color = OnBackgroundLight.copy(alpha = 0.3f).hashCode()
            textSize = guideFontSize
            textAlign = android.graphics.Paint.Align.CENTER
            typeface = android.graphics.Typeface.create(
                android.graphics.Typeface.SERIF,
                android.graphics.Typeface.BOLD
            )
            style = android.graphics.Paint.Style.STROKE
            strokeWidth = 4f
            pathEffect = android.graphics.DashPathEffect(floatArrayOf(20f, 15f), 0f)
            isAntiAlias = true
        }

        drawContext.canvas.nativeCanvas.drawText(
            guideLetter,
            size.width / 2f,
            textY,
            outlinePaint
        )

        // Draw completed paths
        paths.forEach { drawingPath ->
            drawPath(
                path = drawingPath.path,
                color = drawingPath.color,
                style = Stroke(
                    width = drawingPath.strokeWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }

        // Draw current path
        currentPath?.let { current ->
            drawPath(
                path = current.path,
                color = strokeColor,
                style = Stroke(
                    width = current.strokeWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}

fun calculateTraceScore(paths: List<DrawingPath>, canvasWidth: Float = 0f, canvasHeight: Float = 0f): Int {
    if (paths.isEmpty()) return 0

    val totalPathLength = paths.sumOf { estimatePathLength(it).toInt() }
    val totalPoints = paths.sumOf { it.points.size }
    val allPoints = paths.flatMap { it.points }

    // Coverage score: how much of the canvas center area is covered
    val coverageScore = if (allPoints.size > 5 && canvasWidth > 0 && canvasHeight > 0) {
        val centerX = canvasWidth / 2f
        val centerY = canvasHeight / 2f
        val letterZoneRadius = minOf(canvasWidth, canvasHeight) * 0.35f

        val zonesHit = mutableSetOf<Int>()
        val zoneSize = letterZoneRadius / 4f
        allPoints.forEach { point ->
            val dx = point.x - centerX
            val dy = point.y - centerY
            if (dx * dx + dy * dy <= letterZoneRadius * letterZoneRadius) {
                val zoneX = ((point.x - centerX + letterZoneRadius) / zoneSize).toInt()
                val zoneY = ((point.y - centerY + letterZoneRadius) / zoneSize).toInt()
                zonesHit.add(zoneX * 10 + zoneY)
            }
        }
        zonesHit.size
    } else {
        0
    }

    // Combined scoring: path length + point count + zone coverage
    val lengthScore = when {
        totalPathLength > 800 -> 3
        totalPathLength > 400 -> 2
        totalPathLength > 100 -> 1
        else -> 0
    }

    val pointScore = when {
        totalPoints > 200 -> 3
        totalPoints > 80 -> 2
        totalPoints > 20 -> 1
        else -> 0
    }

    val zoneScore = when {
        coverageScore > 20 -> 3
        coverageScore > 10 -> 2
        coverageScore > 4 -> 1
        else -> 0
    }

    // Average the three scores, minimum 0, maximum 3
    val avgScore = (lengthScore + pointScore + zoneScore + 1) / 3
    return avgScore.coerceIn(0, 3)
}

private fun estimatePathLength(drawingPath: DrawingPath): Float {
    if (drawingPath.points.size < 2) {
        val bounds = drawingPath.path.getBounds()
        return bounds.width + bounds.height
    }
    var length = 0f
    for (i in 1 until drawingPath.points.size) {
        val dx = drawingPath.points[i].x - drawingPath.points[i - 1].x
        val dy = drawingPath.points[i].y - drawingPath.points[i - 1].y
        length += kotlin.math.sqrt(dx * dx + dy * dy)
    }
    return length
}
