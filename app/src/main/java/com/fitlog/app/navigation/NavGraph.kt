package com.fitlog.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.fitlog.app.ui.ai.AiSuggestScreen
import com.fitlog.app.ui.auth.AuthScreen
import com.fitlog.app.ui.home.HomeScreen
import com.fitlog.app.ui.workout.create.CreateWorkoutScreen
import com.fitlog.app.ui.workout.detail.WorkoutDetailScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Auth.route) {
            AuthScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNewWorkout = { navController.navigate(Screen.CreateWorkout.createRoute()) },
                onWorkoutClick = { id -> navController.navigate(Screen.WorkoutDetail.createRoute(id)) },
                onAiSuggest = { navController.navigate(Screen.AiSuggest.route) },
                onLogout = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.CreateWorkout.routeWithArgs,
            arguments = listOf(
                navArgument(Screen.CreateWorkout.ARG_WORKOUT_ID) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument(Screen.CreateWorkout.ARG_AI_SUGGESTION) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getString(Screen.CreateWorkout.ARG_WORKOUT_ID)
            val aiSuggestion = backStackEntry.arguments?.getString(Screen.CreateWorkout.ARG_AI_SUGGESTION)
            CreateWorkoutScreen(
                workoutId = workoutId,
                aiSuggestion = aiSuggestion,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.WorkoutDetail.route,
            arguments = listOf(
                navArgument("workoutId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getString("workoutId") ?: return@composable
            WorkoutDetailScreen(
                workoutId = workoutId,
                onBack = { navController.popBackStack() },
                onEdit = { id -> navController.navigate(Screen.CreateWorkout.createRoute(workoutId = id)) },
                onDeleted = { navController.popBackStack() }
            )
        }

        composable(Screen.AiSuggest.route) {
            AiSuggestScreen(
                onBack = { navController.popBackStack() },
                onUseWorkout = { aiText ->
                    navController.navigate(Screen.CreateWorkout.createRoute(aiSuggestion = aiText))
                }
            )
        }
    }
}
