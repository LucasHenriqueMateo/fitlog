package com.fitlog.app.data.repository

import com.fitlog.app.domain.model.Workout
import com.fitlog.app.util.Result
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    fun getWorkouts(): Flow<List<Workout>>
    suspend fun getWorkoutById(id: String): Workout?
    suspend fun saveWorkout(workout: Workout): Result<Workout>
    suspend fun deleteWorkout(id: String): Result<Unit>
}
