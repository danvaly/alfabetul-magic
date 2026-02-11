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
    }
}
