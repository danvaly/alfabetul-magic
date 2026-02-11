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
    val strokeWidth: Float = 16f
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

fun calculateTraceScore(paths: List<DrawingPath>): Int {
    if (paths.isEmpty()) return 0
    val totalPathLength = paths.sumOf { estimatePathLength(it.path).toInt() }
    return when {
        totalPathLength > 800 -> 3
        totalPathLength > 400 -> 2
        totalPathLength > 100 -> 1
        else -> 0
    }
}

private fun estimatePathLength(path: Path): Float {
    val bounds = path.getBounds()
    return bounds.width + bounds.height
}
