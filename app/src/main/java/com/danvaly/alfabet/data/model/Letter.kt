package com.danvaly.alfabet.data.model

data class Letter(
    val uppercase: String,
    val lowercase: String,
    val phoneticSound: String,
    val words: List<ExampleWord>,
    val isVowel: Boolean,
    val isDiacritic: Boolean = false,
    val colorIndex: Int = 0
)

data class ExampleWord(
    val word: String,
    val emoji: String,
    val syllables: List<String> = emptyList()
)
