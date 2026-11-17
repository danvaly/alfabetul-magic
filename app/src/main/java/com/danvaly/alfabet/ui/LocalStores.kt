package com.danvaly.alfabet.ui

import androidx.compose.runtime.staticCompositionLocalOf
import com.danvaly.alfabet.data.ProfileManager
import com.danvaly.alfabet.data.ReadingProgressStore

/** The active profile's reading store; provided in MainActivity. */
val LocalReadingStore = staticCompositionLocalOf<ReadingProgressStore> {
    error("LocalReadingStore is not provided")
}

/** The app-wide profile manager; provided in MainActivity. */
val LocalProfileManager = staticCompositionLocalOf<ProfileManager> {
    error("LocalProfileManager is not provided")
}
