package com.danvaly.alfabet.data

import androidx.compose.ui.geometry.Offset
import com.danvaly.alfabet.ui.components.TraceGuide
import com.danvaly.alfabet.ui.components.scoreTrace
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** The mask-based trace score used for both print and cursive letters. */
class TraceScoreTest {

    private val cell = 10f
    private val cols = 40
    private val rows = 40

    /** An "L": a vertical bar at columns 10..12, rows 5..34, and a foot at rows 32..34, columns 10..29. */
    private val guide: TraceGuide = run {
        val mask = BooleanArray(cols * rows)
        for (y in 5..34) for (x in 10..12) mask[y * cols + x] = true
        for (y in 32..34) for (x in 10..29) mask[y * cols + x] = true
        TraceGuide(cols, rows, cell, mask)
    }

    private fun line(x1: Float, y1: Float, x2: Float, y2: Float): List<Offset> =
        (0..20).map { t -> Offset(x1 + (x2 - x1) * t / 20f, y1 + (y2 - y1) * t / 20f) }

    private fun c(n: Int) = n * cell + cell / 2f

    @Test
    fun `no trace gives no stars`() {
        assertEquals(0, scoreTrace(guide, emptyList()))
        assertEquals(0, scoreTrace(guide, listOf(emptyList())))
    }

    @Test
    fun `tracing the whole letter gives three stars`() {
        val strokes = listOf(
            line(c(11), c(5), c(11), c(33)),
            line(c(11), c(33), c(29), c(33))
        )
        assertEquals(3, scoreTrace(guide, strokes))
    }

    @Test
    fun `tracing only part of the letter gives fewer stars`() {
        val bar = listOf(line(c(11), c(5), c(11), c(33)))
        val stars = scoreTrace(guide, bar)
        assertTrue("got $stars", stars in 1..2)
    }

    @Test
    fun `scribbling away from the letter gives no stars`() {
        val strokes = listOf(
            line(c(25), c(2), c(38), c(20)),
            line(c(38), c(2), c(25), c(20))
        )
        assertEquals(0, scoreTrace(guide, strokes))
    }

    @Test
    fun `small wobbles next to the line still count`() {
        val strokes = listOf(
            line(c(13), c(5), c(9), c(33)),
            line(c(11), c(31), c(29), c(35))
        )
        assertEquals(3, scoreTrace(guide, strokes))
    }

    @Test
    fun `points outside the canvas are ignored`() {
        val strokes = listOf(listOf(Offset(-50f, -50f), Offset(5000f, 5000f)))
        assertTrue(scoreTrace(guide, strokes) <= 1)
        assertEquals(0, scoreTrace(TraceGuide(0, 0, cell, BooleanArray(0)), strokes))
    }
}
