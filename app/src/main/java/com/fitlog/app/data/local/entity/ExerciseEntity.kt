package com.fitlog.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.fitlog.app.domain.model.Exercise

@Entity(
    tableName = "exercises",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutEntity::class,
            parentColumns = ["id"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ExerciseEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(index = true) val workoutId: String,
    val name: String,
    val sets: Int,
    val reps: Int,
    val weightKg: Double?,
    val notes: String,
    val order: Int
)

fun ExerciseEntity.toDomain() = Exercise(
    id = id,
    workoutId = workoutId,
    name = name,
    sets = sets,
    reps = reps,
    weightKg = weightKg,
    notes = notes,
    order = order
)
