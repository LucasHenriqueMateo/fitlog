package com.fitlog.app.data.local.entity

data class ExerciseHistoryEntry(
    val id: String,
    val sessionId: String,
    val exerciseName: String,
    val sets: Int?,
    val reps: Int?,
    val weightKg: Double?,
    val recordedAt: String,
    val templateName: String,
    val sessionDate: String
)
