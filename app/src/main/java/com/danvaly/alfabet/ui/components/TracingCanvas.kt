package com.danvaly.alfabet.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.core.content.res.ResourcesCompat
import com.danvaly.alfabet.R
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

/**
 * Where the guide letter sits on the canvas, plus a coarse mask of its ink used for scoring.
 * [mask] has [cols] x [rows] cells of [cell] pixels each (row-major); true = the letter is there.
 */
class TraceGuide(val cols: Int, val rows: Int, val cell: Float, val mask: BooleanArray) {
    fun inkCells(): Int = mask.count { it }
}

private class GuideLayout(
    val textSize: Float,
    val x: Float,
    val baseline: Float,
    val thickening: Float,
    val guide: TraceGuide
)

/**
 * Draws a big guide letter the child traces with a finger. With [handwriting] the guide is the
 * school cursive glyph (Playwrite RO), drawn as a soft wide band with the real letter line inside.
 * [onGuideReady] reports the letter's ink mask whenever the letter, font or canvas size changes,
 * so the trace can be scored against it (see [scoreTrace]).
 */
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
    strokeColor: Color = PrimaryPink,
    handwriting: Boolean = false,
    onGuideReady: (TraceGuide) -> Unit = {}
) {
    val density = LocalDensity.current
    val context = LocalContext.current
    val maxTextSize = with(density) { 280.sp.toPx() }
    val typeface = remember(handwriting) {
        val cursive = if (handwriting) {
            try {
                ResourcesCompat.getFont(context, R.font.playwrite_ro)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
        cursive ?: android.graphics.Typeface.create(android.graphics.Typeface.SERIF, android.graphics.Typeface.BOLD)
    }
    val cursive = handwriting
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    val layout = remember(guideLetter, typeface, cursive, canvasSize) {
        if (canvasSize.width > 0 && canvasSize.height > 0 && guideLetter.isNotEmpty()) {
            try {
                layoutGuide(guideLetter, typeface, cursive, canvasSize.width, canvasSize.height, maxTextSize)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }
    val currentOnGuideReady by rememberUpdatedState(onGuideReady)
    LaunchedEffect(layout) {
        layout?.let { currentOnGuideReady(it.guide) }
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { canvasSize = it }
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
        if (layout != null) {
            val native = drawContext.canvas.nativeCanvas
            if (cursive) {
                // A wide soft band to stay inside, and the letter's own line to follow.
                val band = guidePaint(typeface, layout.textSize).apply {
                    color = guideColor.copy(alpha = 0.22f).toArgb()
                    style = android.graphics.Paint.Style.FILL_AND_STROKE
                    strokeWidth = layout.thickening
                    strokeJoin = android.graphics.Paint.Join.ROUND
                    strokeCap = android.graphics.Paint.Cap.ROUND
                }
                native.drawText(guideLetter, layout.x, layout.baseline, band)
                val line = guidePaint(typeface, layout.textSize).apply {
                    color = OnBackgroundLight.copy(alpha = 0.45f).toArgb()
                }
                native.drawText(guideLetter, layout.x, layout.baseline, line)
            } else {
                // Draw guide letter (large, semi-transparent)
                val fill = guidePaint(typeface, layout.textSize).apply { color = guideColor.toArgb() }
                native.drawText(guideLetter, layout.x, layout.baseline, fill)

                // Draw dashed outline of the letter
                val outline = guidePaint(typeface, layout.textSize).apply {
                    color = OnBackgroundLight.copy(alpha = 0.3f).toArgb()
                    style = android.graphics.Paint.Style.STROKE
                    strokeWidth = 4f
                    pathEffect = android.graphics.DashPathEffect(floatArrayOf(20f, 15f), 0f)
                }
                native.drawText(guideLetter, layout.x, layout.baseline, outline)
            }
        }

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

private fun guidePaint(typeface: android.graphics.Typeface, size: Float) =
    android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        this.typeface = typeface
        textSize = size
        textAlign = android.graphics.Paint.Align.LEFT
    }

/** Rows of the scoring mask; cells are square, so the column count follows the canvas shape. */
private const val MASK_ROWS = 40

/** Fits the letter in the middle of the canvas and renders its ink mask for scoring. */
private fun layoutGuide(
    letter: String,
    typeface: android.graphics.Typeface,
    cursive: Boolean,
    width: Int,
    height: Int,
    maxTextSize: Float
): GuideLayout {
    val probe = guidePaint(typeface, maxTextSize)
    val bounds = android.graphics.Rect()
    probe.getTextBounds(letter, 0, letter.length, bounds)
    val boundsWidth = bounds.width().coerceAtLeast(1)
    val boundsHeight = bounds.height().coerceAtLeast(1)
    // Shrink (never grow past the print size) so the whole glyph fits, loops included.
    val fit = minOf(1f, width * 0.8f / boundsWidth, height * 0.8f / boundsHeight)
    val textSize = maxTextSize * fit
    val x = width / 2f - (bounds.left + bounds.right) / 2f * fit
    val baseline = height / 2f - (bounds.top + bounds.bottom) / 2f * fit
    val thickening = if (cursive) textSize * 0.07f else 0f

    // Render the guide's ink at low resolution: one pixel per scoring cell.
    val cell = height / MASK_ROWS.toFloat()
    val cols = kotlin.math.ceil(width / cell).toInt().coerceAtLeast(1)
    val bitmap = android.graphics.Bitmap.createBitmap(cols, MASK_ROWS, android.graphics.Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    canvas.scale(1f / cell, 1f / cell)
    val ink = guidePaint(typeface, textSize).apply {
        color = android.graphics.Color.BLACK
        if (cursive) {
            // Score against the band the child sees, not just the thin letter line.
            style = android.graphics.Paint.Style.FILL_AND_STROKE
            strokeWidth = thickening
            strokeJoin = android.graphics.Paint.Join.ROUND
            strokeCap = android.graphics.Paint.Cap.ROUND
        }
    }
    canvas.drawText(letter, x, baseline, ink)
    val pixels = IntArray(cols * MASK_ROWS)
    bitmap.getPixels(pixels, 0, cols, 0, 0, cols, MASK_ROWS)
    bitmap.recycle()
    val mask = BooleanArray(pixels.size) { (pixels[it] ushr 24) > 60 }
    return GuideLayout(textSize, x, baseline, thickening, TraceGuide(cols, MASK_ROWS, cell, mask))
}

/**
 * Stars (0..3) for a trace, measured against the guide letter's ink mask. Works the same for
 * print and cursive letters because it only looks at where the letter is drawn:
 * - coverage: how much of the letter the finger went over (with one cell of tolerance);
 * - precision: how much of the finger's line stayed on or near the letter (two cells of tolerance).
 * [strokes] are the finger lines in canvas pixels.
 */
fun scoreTrace(guide: TraceGuide, strokes: List<List<Offset>>): Int {
    val cols = guide.cols
    val rows = guide.rows
    if (cols <= 0 || rows <= 0 || guide.mask.size != cols * rows || guide.cell <= 0f) return 0
    val touched = BooleanArray(cols * rows)

    fun touch(x: Float, y: Float) {
        val cx = (x / guide.cell).toInt()
        val cy = (y / guide.cell).toInt()
        if (cx in 0 until cols && cy in 0 until rows) touched[cy * cols + cx] = true
    }

    strokes.forEach { points ->
        points.firstOrNull()?.let { touch(it.x, it.y) }
        for (i in 1 until points.size) {
            val a = points[i - 1]
            val b = points[i]
            val distance = kotlin.math.hypot((b.x - a.x).toDouble(), (b.y - a.y).toDouble()).toFloat()
            val steps = kotlin.math.ceil(distance / (guide.cell / 2f)).toInt().coerceAtLeast(1)
            for (step in 1..steps) {
                val t = step / steps.toFloat()
                touch(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t)
            }
        }
    }

    val ink = guide.mask.count { it }
    val drawn = touched.count { it }
    if (ink == 0 || drawn == 0) return 0

    val touchedNear = dilate(touched, cols, rows, 1)
    val inkNear = dilate(guide.mask, cols, rows, 2)
    val covered = guide.mask.indices.count { guide.mask[it] && touchedNear[it] }
    val onLetter = touched.indices.count { touched[it] && inkNear[it] }
    val coverage = covered.toFloat() / ink
    val precision = onLetter.toFloat() / drawn

    return when {
        coverage >= 0.65f && precision >= 0.75f -> 3
        coverage >= 0.45f && precision >= 0.6f -> 2
        coverage >= 0.2f && precision >= 0.4f -> 1
        else -> 0
    }
}

private fun dilate(cells: BooleanArray, cols: Int, rows: Int, radius: Int): BooleanArray {
    val out = BooleanArray(cells.size)
    for (y in 0 until rows) {
        for (x in 0 until cols) {
            if (!cells[y * cols + x]) continue
            for (dy in -radius..radius) {
                val ny = y + dy
                if (ny !in 0 until rows) continue
                for (dx in -radius..radius) {
                    val nx = x + dx
                    if (nx in 0 until cols) out[ny * cols + nx] = true
                }
            }
        }
    }
    return out
}

/** Older, shape-blind score; used only when the guide mask is not available yet. */
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
