package com.fitlog.app.navigation

import android.net.Uri

sealed class Screen(val route: String) {
    data object Auth : Screen("auth")
    data object Home : Screen("home")
    data object AiSuggest : Screen("ai_suggest")

    data object CreateWorkout : Screen("create_workout") {
        const val ARG_WORKOUT_ID = "workoutId"
        const val ARG_AI_SUGGESTION = "aiSuggestion"
        val routeWithArgs = "create_workout?$ARG_WORKOUT_ID={$ARG_WORKOUT_ID}&$ARG_AI_SUGGESTION={$ARG_AI_SUGGESTION}"

        fun createRoute(workoutId: String? = null, aiSuggestion: String? = null): String {
            val params = buildList {
                workoutId?.let { add("$ARG_WORKOUT_ID=${Uri.encode(it)}") }
                aiSuggestion?.let { add("$ARG_AI_SUGGESTION=${Uri.encode(it)}") }
            }
            return if (params.isEmpty()) route else "$route?${params.joinToString("&")}"
        }
    }

    data object WorkoutDetail : Screen("workout_detail/{workoutId}") {
        fun createRoute(id: String) = "workout_detail/$id"
    }
}
