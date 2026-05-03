package com.fitlog.app.data.repository

import com.fitlog.app.data.local.WorkoutWithExercises
import com.fitlog.app.data.local.dao.ExerciseDao
import com.fitlog.app.data.local.dao.WorkoutDao
import com.fitlog.app.data.local.entity.ExerciseEntity
import com.fitlog.app.data.local.entity.WorkoutEntity
import com.fitlog.app.data.local.toDomain
import com.fitlog.app.data.remote.datasource.WorkoutRemoteDataSource
import com.fitlog.app.data.remote.dto.ExerciseDto
import com.fitlog.app.data.remote.dto.WorkoutDto
import com.fitlog.app.data.remote.dto.toEntity
import com.fitlog.app.domain.model.Workout
import com.fitlog.app.util.Result
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

class WorkoutRepositoryImpl @Inject constructor(
    private val workoutDao: WorkoutDao,
    private val exerciseDao: ExerciseDao,
    private val remoteDataSource: WorkoutRemoteDataSource,
    private val supabaseClient: SupabaseClient
) : WorkoutRepository {

    override fun getWorkouts(): Flow<List<Workout>> = channelFlow {
        launch {
            try {
                val userId = supabaseClient.auth.currentUserOrNull()?.id ?: return@launch
                val remoteWorkouts = remoteDataSource.fetchWorkouts(userId)
                remoteWorkouts.forEach { dto ->
                    workoutDao.insert(dto.toEntity())
                    val exercises = remoteDataSource.fetchExercisesForWorkout(dto.id)
                    exerciseDao.deleteByWorkoutId(dto.id)
                    exercises.forEach { exerciseDao.insert(it.toEntity()) }
                }
            } catch (e: Exception) {
                // Offline — Room data remains valid
            }
        }
        workoutDao.getAllWithExercises()
            .map { list -> list.map(WorkoutWithExercises::toDomain) }
            .collect { send(it) }
    }

    override fun getTemplates(): Flow<List<Workout>> = channelFlow {
        launch {
            try {
                val userId = supabaseClient.auth.currentUserOrNull()?.id ?: return@launch
                val remoteTemplates = remoteDataSource.fetchTemplates(userId)
                remoteTemplates.forEach { dto ->
                    workoutDao.insert(dto.toEntity())
                    val exercises = remoteDataSource.fetchExercisesForWorkout(dto.id)
                    exerciseDao.deleteByWorkoutId(dto.id)
                    exercises.forEach { exerciseDao.insert(it.toEntity()) }
                }
            } catch (e: Exception) {
                // Offline
            }
        }
        workoutDao.getTemplatesWithExercises()
            .map { list -> list.map(WorkoutWithExercises::toDomain) }
            .collect { send(it) }
    }

    override fun getSessions(): Flow<List<Workout>> =
        workoutDao.getSessionWorkouts()
            .map { list -> list.map(WorkoutWithExercises::toDomain) }

    override suspend fun getWorkoutById(id: String): Workout? =
        workoutDao.getByIdWithExercises(id)?.toDomain()

    override suspend fun saveWorkout(workout: Workout): Result<Workout> {
        return try {
            val userId = supabaseClient.auth.currentUserOrNull()?.id ?: ""
            val id = workout.id.ifEmpty { UUID.randomUUID().toString() }
            val createdAt = workout.createdAt.ifEmpty { java.time.Instant.now().toString() }

            val entity = WorkoutEntity(
                id = id,
                userId = userId,
                name = workout.name,
                date = workout.date,
                notes = workout.notes,
                synced = false,
                createdAt = createdAt,
                isTemplate = workout.isTemplate
            )
            workoutDao.insert(entity)

            exerciseDao.deleteByWorkoutId(id)
            workout.exercises.forEachIndexed { index, exercise ->
                val exerciseId = exercise.id.ifEmpty { UUID.randomUUID().toString() }
                exerciseDao.insert(
                    ExerciseEntity(
                        id = exerciseId,
                        workoutId = id,
                        name = exercise.name,
                        sets = exercise.sets,
                        reps = exercise.reps,
                        weightKg = exercise.weightKg,
                        notes = exercise.notes,
                        order = index
                    )
                )
            }

            try {
                val workoutDto = WorkoutDto(
                    id = id,
                    userId = userId,
                    name = workout.name,
                    date = workout.date,
                    notes = workout.notes,
                    createdAt = createdAt,
                    isTemplate = workout.isTemplate
                )
                remoteDataSource.upsertWorkout(workoutDto)

                val exerciseDtos = workout.exercises.mapIndexed { index, exercise ->
                    ExerciseDto(
                        id = exercise.id.ifEmpty { UUID.randomUUID().toString() },
                        workoutId = id,
                        name = exercise.name,
                        sets = exercise.sets,
                        reps = exercise.reps,
                        weightKg = exercise.weightKg,
                        notes = exercise.notes,
                        order = index
                    )
                }
                remoteDataSource.deleteExercisesForWorkout(id)
                remoteDataSource.upsertExercises(exerciseDtos)
                workoutDao.insert(entity.copy(synced = true))
            } catch (e: Exception) {
                // Offline — will sync later
            }

            Result.Success(workout.copy(id = id, userId = userId, createdAt = createdAt))
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to save workout", e)
        }
    }

    override suspend fun deleteWorkout(id: String): Result<Unit> {
        return try {
            workoutDao.deleteById(id)
            try {
                remoteDataSource.deleteWorkout(id)
            } catch (e: Exception) {
                // Offline — local delete is sufficient for now
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to delete workout", e)
        }
    }
}
