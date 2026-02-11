package com.danvaly.alfabet.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Learn : Screen("learn")
    data object LetterDetail : Screen("letter_detail/{letter}") {
        fun createRoute(letter: String) = "letter_detail/$letter"
    }
    data object Trace : Screen("trace/{letter}") {
        fun createRoute(letter: String) = "trace/$letter"
    }
    data object TraceSelector : Screen("trace_selector")
    data object Syllable : Screen("syllable")
    data object Games : Screen("games")
    data object BalloonPop : Screen("balloon_pop/{letter}") {
        fun createRoute(letter: String) = "balloon_pop/$letter"
    }
    data object MatchGame : Screen("match_game")
    data object QuizGame : Screen("quiz_game")
    data object Progress : Screen("progress")
    data object WordMatch : Screen("word_match")
    data object LetterStory : Screen("letter_story/{letter}") {
        fun createRoute(letter: String) = "letter_story/$letter"
    }
    data object Diacritics : Screen("diacritics")
    data object DailyChallenge : Screen("daily_challenge")
    data object SentenceBuild : Screen("sentence_build")
    data object RhymingGame : Screen("rhyming_game")
    data object AlphabetOrder : Screen("alphabet_order")
    data object ParentDashboard : Screen("parent_dashboard")
}
