package com.fitlog.app.data.remote.datasource

import com.fitlog.app.data.remote.dto.SessionExerciseDto
import com.fitlog.app.data.remote.dto.WorkoutSessionDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import javax.inject.Inject

class SessionRemoteDataSource @Inject constructor(
    private val client: SupabaseClient
) {
    suspend fun upsertSession(dto: WorkoutSessionDto) {
        client.from("workout_sessions").upsert(dto)
    }

    suspend fun upsertSessionExercises(exercises: List<SessionExerciseDto>) {
        if (exercises.isNotEmpty()) {
            client.from("session_exercises").upsert(exercises)
        }
    }
}
