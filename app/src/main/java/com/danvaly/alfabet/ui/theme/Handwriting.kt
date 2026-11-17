package com.danvaly.alfabet.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.danvaly.alfabet.R

/** Romanian school cursive ("litere de mână"): Playwrite RO, SIL Open Font License. */
val HandwritingFont = FontFamily(Font(R.font.playwrite_ro))

/**
 * The cursive font has tall loops above and below the letters, so it is drawn a bit
 * smaller than the print font to keep the same line height.
 */
const val HandwritingScale = 0.8f
