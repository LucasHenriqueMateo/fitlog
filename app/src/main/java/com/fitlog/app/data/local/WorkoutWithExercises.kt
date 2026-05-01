package com.fitlog.app.data.local

import androidx.room.Embedded
import androidx.room.Relation
import com.fitlog.app.data.local.entity.ExerciseEntity
import com.fitlog.app.data.local.entity.WorkoutEntity
import com.fitlog.app.data.local.entity.toDomain
import com.fitlog.app.domain.model.Workout

data class WorkoutWithExercises(
    @Embedded val workout: WorkoutEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "workoutId"
    )
    val exercises: List<ExerciseEntity>
)

fun WorkoutWithExercises.toDomain(): Workout = workout.toDomain(
    exercises = exercises.sortedBy { it.order }.map { it.toDomain() }
)
