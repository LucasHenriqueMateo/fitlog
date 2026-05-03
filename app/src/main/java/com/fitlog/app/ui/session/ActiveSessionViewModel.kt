package com.fitlog.app.ui.session

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitlog.app.data.local.dao.SessionDao
import com.fitlog.app.data.local.dao.SessionExerciseDao
import com.fitlog.app.data.local.entity.SessionExerciseEntity
import com.fitlog.app.data.local.entity.WorkoutSessionEntity
import com.fitlog.app.data.remote.datasource.SessionRemoteDataSource
import com.fitlog.app.data.remote.dto.SessionExerciseDto
import com.fitlog.app.data.remote.dto.WorkoutSessionDto
import com.fitlog.app.data.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

data class ExerciseSessionState(
    val exerciseName: String,
    val sets: Int,
    val reps: Int,
    val inputWeight: Double?,
    val recordWeight: Double?,
    val lastWeight: Double?
)

data class ActiveSessionUiState(
    val workoutName: String = "",
    val templateCode: String = "",
    val exercises: List<ExerciseSessionState> = emptyList(),
    val isLoading: Boolean = true,
    val isFinishing: Boolean = false,
    val finished: Boolean = false
)

@HiltViewModel
class ActiveSessionViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val sessionDao: SessionDao,
    private val sessionExerciseDao: SessionExerciseDao,
    private val sessionRemoteDataSource: SessionRemoteDataSource,
    private val supabaseClient: SupabaseClient,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val workoutId: String = checkNotNull(savedStateHandle["workoutId"])
    private val sessionStartTime = Instant.now().toString()

    private val _uiState = MutableStateFlow(ActiveSessionUiState())
    val uiState: StateFlow<ActiveSessionUiState> = _uiState.asStateFlow()

    init {
        loadTemplate()
    }

    private fun loadTemplate() {
        viewModelScope.launch {
            val workout = workoutRepository.getWorkoutById(workoutId) ?: run {
                _uiState.update { it.copy(isLoading = false) }
                return@launch
            }
            val exercises = workout.exercises.map { exercise ->
                val record = sessionExerciseDao.getMaxWeightForExercise(exercise.name)
                val last = sessionExerciseDao.getLastWeightForExercise(exercise.name)
                ExerciseSessionState(
                    exerciseName = exercise.name,
                    sets = exercise.sets,
                    reps = exercise.reps,
                    inputWeight = last,
                    recordWeight = record,
                    lastWeight = last
                )
            }
            _uiState.update {
                it.copy(
                    workoutName = workout.name,
                    templateCode = workout.notes,
                    exercises = exercises,
                    isLoading = false
                )
            }
        }
    }

    fun updateWeight(index: Int, value: String) {
        val parsed = value.toDoubleOrNull()
        _uiState.update { state ->
            val updated = state.exercises.toMutableList()
            if (index in updated.indices) {
                updated[index] = updated[index].copy(inputWeight = parsed)
            }
            state.copy(exercises = updated)
        }
    }

    fun finishSession() {
        _uiState.update { it.copy(isFinishing = true) }
        viewModelScope.launch {
            try {
                val userId = supabaseClient.auth.currentUserOrNull()?.id ?: ""
                val sessionId = UUID.randomUUID().toString()
                val now = Instant.now().toString()
                val state = _uiState.value

                val session = WorkoutSessionEntity(
                    id = sessionId,
                    userId = userId,
                    templateId = workoutId,
                    templateName = state.workoutName,
                    templateCode = state.templateCode,
                    startedAt = sessionStartTime,
                    finishedAt = now
                )
                sessionDao.insertSession(session)

                state.exercises.forEach { exercise ->
                    sessionExerciseDao.insertExercise(
                        SessionExerciseEntity(
                            id = UUID.randomUUID().toString(),
                            sessionId = sessionId,
                            exerciseName = exercise.exerciseName,
                            sets = exercise.sets,
                            reps = exercise.reps,
                            weightKg = exercise.inputWeight,
                            recordedAt = now
                        )
                    )
                }

                try {
                    sessionRemoteDataSource.upsertSession(
                        WorkoutSessionDto(
                            id = sessionId,
                            userId = userId,
                            templateId = workoutId,
                            templateName = state.workoutName,
                            templateCode = state.templateCode,
                            startedAt = sessionStartTime,
                            finishedAt = now
                        )
                    )
                    val exerciseDtos = state.exercises.map { exercise ->
                        SessionExerciseDto(
                            id = UUID.randomUUID().toString(),
                            sessionId = sessionId,
                            exerciseName = exercise.exerciseName,
                            sets = exercise.sets,
                            reps = exercise.reps,
                            weightKg = exercise.inputWeight,
                            recordedAt = now
                        )
                    }
                    sessionRemoteDataSource.upsertSessionExercises(exerciseDtos)
                } catch (e: Exception) {
                    // Offline — session saved locally
                }

                _uiState.update { it.copy(isFinishing = false, finished = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isFinishing = false) }
            }
        }
    }
}
