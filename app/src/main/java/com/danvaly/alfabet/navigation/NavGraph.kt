package com.danvaly.alfabet.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.danvaly.alfabet.audio.SoundManager
import com.danvaly.alfabet.data.repository.ProgressRepository
import com.danvaly.alfabet.ui.screens.*

@Composable
fun AlfabetNavGraph(
    navController: NavHostController,
    soundManager: SoundManager,
    repository: ProgressRepository
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToLearn = { navController.navigate(Screen.Learn.route) },
                onNavigateToTrace = { navController.navigate(Screen.TraceSelector.route) },
                onNavigateToSyllable = { navController.navigate(Screen.Syllable.route) },
                onNavigateToGames = { navController.navigate(Screen.Games.route) },
                onNavigateToProgress = { navController.navigate(Screen.Progress.route) },
                onNavigateToDailyChallenge = { navController.navigate(Screen.DailyChallenge.route) },
                onNavigateToParentDashboard = { navController.navigate(Screen.ParentDashboard.route) },
                onNavigateToStories = { navController.navigate(Screen.Stories.route) },
                onNavigateToReadingTrainer = { navController.navigate(Screen.ReadingTrainer.route) },
                soundManager = soundManager
            )
        }

        composable(Screen.Learn.route) {
            LearnScreen(
                onLetterClick = { letter ->
                    navController.navigate(Screen.LetterDetail.createRoute(letter))
                },
                onBack = { navController.popBackStack() },
                soundManager = soundManager
            )
        }

        composable(
            route = Screen.LetterDetail.route,
            arguments = listOf(navArgument("letter") { type = NavType.StringType })
        ) { backStackEntry ->
            val letter = backStackEntry.arguments?.getString("letter") ?: "A"
            LetterDetailScreen(
                letterChar = letter,
                onBack = { navController.popBackStack() },
                onTrace = { navController.navigate(Screen.Trace.createRoute(letter)) },
                soundManager = soundManager,
                repository = repository
            )
        }

        composable(Screen.TraceSelector.route) {
            TraceSelectorScreen(
                onLetterSelect = { letter ->
                    navController.navigate(Screen.Trace.createRoute(letter))
                },
                onBack = { navController.popBackStack() },
                soundManager = soundManager
            )
        }

        composable(
            route = Screen.Trace.route,
            arguments = listOf(navArgument("letter") { type = NavType.StringType })
        ) { backStackEntry ->
            val letter = backStackEntry.arguments?.getString("letter") ?: "A"
            TraceScreen(
                letterChar = letter,
                onBack = { navController.popBackStack() },
                soundManager = soundManager,
                repository = repository
            )
        }

        composable(Screen.Syllable.route) {
            SyllableScreen(
                onBack = { navController.popBackStack() },
                soundManager = soundManager,
                repository = repository
            )
        }

        composable(Screen.Games.route) {
            GamesMenuScreen(
                onBalloonPop = { letter ->
                    navController.navigate(Screen.BalloonPop.createRoute(letter))
                },
                onMatchGame = { navController.navigate(Screen.MatchGame.route) },
                onQuizGame = { navController.navigate(Screen.QuizGame.route) },
                onWordMatch = { navController.navigate(Screen.WordMatch.route) },
                onDiacritics = { navController.navigate(Screen.Diacritics.route) },
                onSentenceBuild = { navController.navigate(Screen.SentenceBuild.route) },
                onRhymingGame = { navController.navigate(Screen.RhymingGame.route) },
                onAlphabetOrder = { navController.navigate(Screen.AlphabetOrder.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.BalloonPop.route,
            arguments = listOf(navArgument("letter") { type = NavType.StringType })
        ) { backStackEntry ->
            val letter = backStackEntry.arguments?.getString("letter") ?: "A"
            BalloonPopScreen(
                targetLetter = letter,
                onBack = { navController.popBackStack() },
                soundManager = soundManager,
                repository = repository
            )
        }

        composable(Screen.MatchGame.route) {
            MatchGameScreen(
                onBack = { navController.popBackStack() },
                soundManager = soundManager,
                repository = repository
            )
        }

        composable(Screen.QuizGame.route) {
            QuizGameScreen(
                onBack = { navController.popBackStack() },
                soundManager = soundManager,
                repository = repository
            )
        }

        composable(Screen.Progress.route) {
            ProgressScreen(
                onBack = { navController.popBackStack() },
                repository = repository
            )
        }

        composable(Screen.WordMatch.route) {
            WordMatchScreen(
                onBack = { navController.popBackStack() },
                soundManager = soundManager,
                repository = repository
            )
        }

        composable(
            route = Screen.LetterStory.route,
            arguments = listOf(navArgument("letter") { type = NavType.StringType })
        ) { backStackEntry ->
            val letter = backStackEntry.arguments?.getString("letter") ?: "A"
            LetterStoryScreen(
                letterChar = letter,
                onBack = { navController.popBackStack() },
                soundManager = soundManager
            )
        }

        composable(Screen.Diacritics.route) {
            DiacriticsScreen(
                onBack = { navController.popBackStack() },
                soundManager = soundManager,
                repository = repository
            )
        }

        composable(Screen.DailyChallenge.route) {
            DailyChallengeScreen(
                onBack = { navController.popBackStack() },
                soundManager = soundManager,
                repository = repository
            )
        }

        composable(Screen.SentenceBuild.route) {
            SentenceBuildScreen(
                onBack = { navController.popBackStack() },
                soundManager = soundManager,
                repository = repository
            )
        }

        composable(Screen.RhymingGame.route) {
            RhymingGameScreen(
                onBack = { navController.popBackStack() },
                soundManager = soundManager,
                repository = repository
            )
        }

        composable(Screen.AlphabetOrder.route) {
            AlphabetOrderScreen(
                onBack = { navController.popBackStack() },
                soundManager = soundManager,
                repository = repository
            )
        }

        composable(Screen.Stories.route) {
            StoriesScreen(
                onStoryClick = { storyId ->
                    navController.navigate(Screen.StoryReader.createRoute(storyId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.StoryReader.route,
            arguments = listOf(navArgument("storyId") { type = NavType.StringType })
        ) { backStackEntry ->
            StoryReaderScreen(
                storyId = backStackEntry.arguments?.getString("storyId").orEmpty(),
                onBack = { navController.popBackStack() },
                soundManager = soundManager,
                repository = repository
            )
        }

        composable(Screen.ReadingTrainer.route) {
            ReadingTrainerScreen(
                onBack = { navController.popBackStack() },
                soundManager = soundManager,
                repository = repository
            )
        }

        composable(Screen.ParentDashboard.route) {
            ParentDashboardScreen(
                onBack = { navController.popBackStack() },
                repository = repository
            )
        }
    }
}
