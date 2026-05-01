package com.fitlog.app.domain.model

data class Exercise(
    val id: String = "",
    val workoutId: String = "",
    val name: String,
    val sets: Int,
    val reps: Int,
    val weightKg: Double? = null,
    val notes: String = "",
    val order: Int = 0
)
