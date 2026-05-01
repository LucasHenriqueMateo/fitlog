package com.fitlog.app.data.remote.dto

import com.fitlog.app.data.local.entity.ExerciseEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExerciseDto(
    val id: String = "",
    @SerialName("workout_id") val workoutId: String = "",
    val name: String = "",
    val sets: Int = 0,
    val reps: Int = 0,
    @SerialName("weight_kg") val weightKg: Double? = null,
    val notes: String = "",
    @SerialName("order") val order: Int = 0
)

fun ExerciseDto.toEntity() = ExerciseEntity(
    id = id,
    workoutId = workoutId,
    name = name,
    sets = sets,
    reps = reps,
    weightKg = weightKg,
    notes = notes,
    order = order
)
