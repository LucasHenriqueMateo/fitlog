package com.fitlog.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_sessions")
data class WorkoutSessionEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val templateId: String?,
    val templateName: String,
    val templateCode: String,
    val startedAt: String,
    val finishedAt: String?
)
