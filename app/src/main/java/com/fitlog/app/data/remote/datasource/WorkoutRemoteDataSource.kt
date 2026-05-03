package com.fitlog.app.data.remote.datasource

import com.fitlog.app.data.remote.dto.ExerciseDto
import com.fitlog.app.data.remote.dto.WorkoutDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import javax.inject.Inject

class WorkoutRemoteDataSource @Inject constructor(
    private val client: SupabaseClient
) {
    suspend fun fetchWorkouts(userId: String): List<WorkoutDto> =
        client.from("workouts")
            .select { filter { eq("user_id", userId) } }
            .decodeList()

    suspend fun fetchExercisesForWorkout(workoutId: String): List<ExerciseDto> =
        client.from("exercises")
            .select { filter { eq("workout_id", workoutId) } }
            .decodeList()

    suspend fun upsertWorkout(dto: WorkoutDto) {
        client.from("workouts").upsert(dto)
    }

    suspend fun upsertExercises(exercises: List<ExerciseDto>) {
        if (exercises.isNotEmpty()) {
            client.from("exercises").upsert(exercises)
        }
    }

    suspend fun deleteWorkout(id: String) {
        client.from("workouts").delete { filter { eq("id", id) } }
    }

    suspend fun deleteExercisesForWorkout(workoutId: String) {
        client.from("exercises").delete { filter { eq("workout_id", workoutId) } }
    }

    suspend fun fetchTemplates(userId: String): List<WorkoutDto> =
        client.from("workouts")
            .select { filter {
                eq("user_id", userId)
                eq("is_template", true)
            } }
            .decodeList()
}
