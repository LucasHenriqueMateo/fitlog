package com.fitlog.app.data.remote.dto

import com.fitlog.app.data.local.entity.WorkoutEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkoutDto(
    val id: String = "",
    @SerialName("user_id") val userId: String = "",
    val name: String = "",
    val date: String = "",
    val notes: String = "",
    @SerialName("created_at") val createdAt: String? = null
)

fun WorkoutDto.toEntity() = WorkoutEntity(
    id = id,
    userId = userId,
    name = name,
    date = date,
    notes = notes,
    synced = true,
    createdAt = createdAt ?: ""
)
