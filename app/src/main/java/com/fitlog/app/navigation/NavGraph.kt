package com.fitlog.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.fitlog.app.ui.ai.AiSuggestScreen
import com.fitlog.app.ui.auth.AuthScreen
import com.fitlog.app.ui.history.ExerciseHistoryScreen
import com.fitlog.app.ui.history.HistoryScreen
import com.fitlog.app.ui.home.HomeScreen
import com.fitlog.app.ui.session.ActiveSessionScreen
import com.fitlog.app.ui.session.StartSessionScreen
import com.fitlog.app.ui.splash.SplashScreen
import com.fitlog.app.ui.workout.detail.WorkoutDetailScreen
import com.fitlog.app.ui.workout.manage.ManageWorkoutsScreen
import com.fitlog.app.ui.workout.template.CreateTemplateScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Splash.route) {

        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToAuth = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

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
                onStartWorkout = { navController.navigate(Screen.StartSession.route) },
                onManageWorkouts = { navController.navigate(Screen.ManageWorkouts.route) },
                onHistory = { navController.navigate(Screen.History.route) },
                onAiSuggest = { navController.navigate(Screen.AiSuggest.route) },
                onLogout = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ManageWorkouts.route) {
            ManageWorkoutsScreen(
                onBack = { navController.popBackStack() },
                onCreateTemplate = { navController.navigate(Screen.CreateTemplate.createRoute()) },
                onEditTemplate = { id -> navController.navigate(Screen.CreateTemplate.createRoute(id)) }
            )
        }

        composable(
            route = Screen.CreateTemplate.routeWithArgs,
            arguments = listOf(
                navArgument(Screen.CreateTemplate.ARG_ID) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val templateId = backStackEntry.arguments?.getString(Screen.CreateTemplate.ARG_ID)
            CreateTemplateScreen(
                templateId = templateId,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        composable(Screen.StartSession.route) {
            StartSessionScreen(
                onBack = { navController.popBackStack() },
                onStartSession = { workoutId ->
                    navController.navigate(Screen.ActiveSession.createRoute(workoutId))
                }
            )
        }

        composable(
            route = Screen.ActiveSession.route,
            arguments = listOf(
                navArgument("workoutId") { type = NavType.StringType }
            )
        ) {
            ActiveSessionScreen(
                onFinished = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
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
                onEdit = { id -> navController.navigate(Screen.CreateTemplate.createRoute(id)) },
                onDeleted = { navController.popBackStack() }
            )
        }

        composable(Screen.AiSuggest.route) {
            AiSuggestScreen(
                onBack = { navController.popBackStack() },
                onUseWorkout = { _ ->
                    navController.navigate(Screen.CreateTemplate.createRoute())
                }
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                onBack = { navController.popBackStack() },
                onExerciseClick = { name ->
                    navController.navigate(Screen.ExerciseHistory.createRoute(name))
                }
            )
        }

        composable(
            route = Screen.ExerciseHistory.route,
            arguments = listOf(
                navArgument("exerciseName") { type = NavType.StringType }
            )
        ) {
            ExerciseHistoryScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
