package com.fitlog.app.domain.model

data class WorkoutSession(
    val id: String,
    val templateName: String,
    val templateCode: String,
    val startedAt: String,
    val exercises: List<SessionExercise>
)

data class SessionExercise(
    val exerciseName: String,
    val sets: Int?,
    val reps: Int?,
    val weightKg: Double?,
    val isPr: Boolean
)
