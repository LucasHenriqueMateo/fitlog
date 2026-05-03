package com.fitlog.app.navigation

import android.net.Uri

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Auth : Screen("auth")
    data object Home : Screen("home")
    data object AiSuggest : Screen("ai_suggest")
    data object ManageWorkouts : Screen("manage_workouts")
    data object StartSession : Screen("start_session")
    data object History : Screen("history")

    data object ExerciseHistory : Screen("exercise_history/{exerciseName}") {
        fun createRoute(name: String) = "exercise_history/${Uri.encode(name)}"
    }

    data object CreateTemplate : Screen("create_template") {
        const val ARG_ID = "id"
        val routeWithArgs = "create_template?$ARG_ID={$ARG_ID}"
        fun createRoute(id: String? = null) =
            if (id != null) "create_template?$ARG_ID=$id" else "create_template"
    }

    data object ActiveSession : Screen("active_session/{workoutId}") {
        fun createRoute(id: String) = "active_session/$id"
    }

    data object WorkoutDetail : Screen("workout_detail/{workoutId}") {
        fun createRoute(id: String) = "workout_detail/$id"
    }
}
