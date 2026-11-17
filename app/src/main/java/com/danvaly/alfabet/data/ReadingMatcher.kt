package com.danvaly.alfabet.data

import java.text.Normalizer

/**
 * Compares the words of a sentence with what a speech recogniser heard, so the
 * story reader can colour the words the child has read. It is a friendly helper,
 * not a judge: it forgives missing diacritics, case, punctuation and recognisers
 * that glue words together or split one word into pieces.
 */
object ReadingMatcher {

    /** Lowercase, no diacritics, letters and digits only: "Pisică," -> "pisica", "s-a" -> "sa". */
    fun normalize(text: String): String {
        val mapped = buildString(text.length) {
            for (c in text.lowercase()) {
                append(
                    when (c) {
                        'ă', 'â', 'á', 'à' -> 'a'
                        'î', 'í', 'ì' -> 'i'
                        'ș', 'ş' -> 's'
                        'ț', 'ţ' -> 't'
                        else -> c
                    }
                )
            }
        }
        // Remove any other accents (é, ö, ...) left over.
        val decomposed = Normalizer.normalize(mapped, Normalizer.Form.NFD)
        return decomposed.filter { it.isLetterOrDigit() && Character.getType(it) != Character.NON_SPACING_MARK.toInt() }
    }

    /** The recogniser's text as normalised tokens, e.g. "Ana are, o pisică!" -> [ana, are, o, pisica]. */
    fun tokens(heard: String): List<String> =
        heard.split(Regex("[\\s]+")).map { normalize(it) }.filter { it.isNotEmpty() }

    /**
     * Indices of [expected] words that count as read in [heard].
     * Each heard token marks at most one expected word (or, when the recogniser glued
     * words together, the run of consecutive words it spells). Matching does not depend on
     * order, but when a word appears twice the next occurrence after the last match wins.
     */
    fun matchRead(expected: List<String>, heard: String): Set<Int> {
        val want = expected.map { normalize(it) }
        val got = tokens(heard)
        val matched = mutableSetOf<Int>()
        var cursor = 0

        fun pick(predicate: (Int) -> Boolean): Int? {
            val free = want.indices.filter { it !in matched && want[it].isNotEmpty() && predicate(it) }
            return free.firstOrNull { it >= cursor } ?: free.firstOrNull()
        }

        fun mark(from: Int, count: Int = 1) {
            for (k in from until from + count) matched += k
            cursor = from + count
        }

        var i = 0
        while (i < got.size) {
            val token = got[i]

            // 1. The same word.
            val same = pick { want[it] == token }
            if (same != null) {
                mark(same)
                i++
                continue
            }

            // 2. One word heard in pieces: "pi si ca" -> "pisica".
            var used = 0
            for (k in 3 downTo 2) {
                if (i + k > got.size) continue
                val joined = got.subList(i, i + k).joinToString("")
                val j = pick { want[it] == joined }
                if (j != null) {
                    mark(j)
                    used = k
                    break
                }
            }
            if (used > 0) {
                i += used
                continue
            }

            // 3. Several words glued together: "sejoaca" -> "se", "joaca".
            val glued = gluedRun(want, matched, token, cursor)
            if (glued != null) {
                mark(glued.first, glued.second)
                i++
                continue
            }

            // 4. Almost the same longer word ("bunicii" for "bunici").
            pick { similar(want[it], token) }?.let { mark(it) }
            i++
        }
        return matched
    }

    /** Finds 2-3 consecutive unread words that spell [token]; returns (start, count). */
    private fun gluedRun(want: List<String>, matched: Set<Int>, token: String, cursor: Int): Pair<Int, Int>? {
        val runs = mutableListOf<Pair<Int, Int>>()
        for (start in want.indices) {
            for (count in 2..3) {
                if (start + count > want.size) break
                val range = start until start + count
                if (range.any { it in matched || want[it].isEmpty() }) break
                if (range.joinToString("") { want[it] } == token) runs += start to count
            }
        }
        return runs.firstOrNull { it.first >= cursor } ?: runs.firstOrNull()
    }

    /** Words of four or more letters that differ by a single letter. */
    private fun similar(a: String, b: String): Boolean {
        if (a.length < 4 || b.length < 4) return false
        if (kotlin.math.abs(a.length - b.length) > 1) return false
        return editDistance(a, b) <= 1
    }

    private fun editDistance(a: String, b: String): Int {
        var previous = IntArray(b.length + 1) { it }
        for (i in 1..a.length) {
            val current = IntArray(b.length + 1)
            current[0] = i
            for (j in 1..b.length) {
                val cost = if (a[i - 1] == b[j - 1]) 0 else 1
                current[j] = minOf(previous[j] + 1, current[j - 1] + 1, previous[j - 1] + cost)
            }
            previous = current
        }
        return previous[b.length]
    }
}

/** Shorthand used by the story reader. */
fun matchRead(expected: List<String>, heard: String): Set<Int> = ReadingMatcher.matchRead(expected, heard)
