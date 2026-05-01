package com.fitlog.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fitlog.app.domain.model.Workout

@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val name: String,
    val date: String,
    val notes: String,
    val synced: Boolean = false,
    val createdAt: String
)

fun WorkoutEntity.toDomain(exercises: List<com.fitlog.app.domain.model.Exercise> = emptyList()) = Workout(
    id = id,
    userId = userId,
    name = name,
    date = date,
    notes = notes,
    exercises = exercises,
    createdAt = createdAt
)
