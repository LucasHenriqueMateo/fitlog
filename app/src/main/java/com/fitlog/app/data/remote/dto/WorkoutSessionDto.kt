package com.fitlog.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkoutSessionDto(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("template_id") val templateId: String?,
    @SerialName("template_name") val templateName: String,
    @SerialName("template_code") val templateCode: String,
    @SerialName("started_at") val startedAt: String,
    @SerialName("finished_at") val finishedAt: String?
)

@Serializable
data class SessionExerciseDto(
    val id: String,
    @SerialName("session_id") val sessionId: String,
    @SerialName("exercise_name") val exerciseName: String,
    val sets: Int?,
    val reps: Int?,
    @SerialName("weight_kg") val weightKg: Double?,
    @SerialName("recorded_at") val recordedAt: String
)
