package com.fitlog.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "session_exercises")
data class SessionExerciseEntity(
    @PrimaryKey val id: String,
    val sessionId: String,
    val exerciseName: String,
    val sets: Int?,
    val reps: Int?,
    val weightKg: Double?,
    val recordedAt: String
)
