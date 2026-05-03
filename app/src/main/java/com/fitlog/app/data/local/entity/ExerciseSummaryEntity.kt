package com.fitlog.app.data.local.entity

data class ExerciseSummaryEntity(
    val exerciseName: String,
    val recordWeight: Double,
    val lastWeight: Double,
    val sessionCount: Int,
    val lastSessionDate: String
)
