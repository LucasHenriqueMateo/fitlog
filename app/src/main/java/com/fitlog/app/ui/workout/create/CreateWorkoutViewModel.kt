package com.fitlog.app.ui.workout.create

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitlog.app.data.repository.WorkoutRepository
import com.fitlog.app.domain.model.Exercise
import com.fitlog.app.domain.model.Workout
import com.fitlog.app.util.DateUtils
import com.fitlog.app.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExerciseFormState(
    val id: String = "",
    val name: String = "",
    val sets: String = "",
    val reps: String = "",
    val weightKg: String = "",
    val notes: String = ""
)

data class CreateWorkoutUiState(
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val workoutId: String = "",
    val name: String = "",
    val date: String = DateUtils.today(),
    val notes: String = "",
    val exercises: List<ExerciseFormState> = listOf(ExerciseFormState()),
    val error: String? = null,
    val validationError: String? = null
)

@HiltViewModel
class CreateWorkoutViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateWorkoutUiState())
    val uiState: StateFlow<CreateWorkoutUiState> = _uiState.asStateFlow()

    init {
        val workoutId = savedStateHandle.get<String>("workoutId")
        val aiSuggestion = savedStateHandle.get<String>("aiSuggestion")

        if (!workoutId.isNullOrBlank()) {
            loadWorkout(workoutId)
        } else if (!aiSuggestion.isNullOrBlank()) {
            _uiState.update { it.copy(notes = aiSuggestion) }
        }
    }

    private fun loadWorkout(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val workout = workoutRepository.getWorkoutById(id)
            if (workout != null) {
                _uiState.update {
                    it.copy(
                        isEditMode = true,
                        isLoading = false,
                        workoutId = workout.id,
                        name = workout.name,
                        date = workout.date,
                        notes = workout.notes,
                        exercises = workout.exercises.map { ex ->
                            ExerciseFormState(
                                id = ex.id,
                                name = ex.name,
                                sets = ex.sets.toString(),
                                reps = ex.reps.toString(),
                                weightKg = ex.weightKg?.toString() ?: "",
                                notes = ex.notes
                            )
                        }.ifEmpty { listOf(ExerciseFormState()) }
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updateName(name: String) = _uiState.update { it.copy(name = name, validationError = null) }
    fun updateDate(date: String) = _uiState.update { it.copy(date = date) }
    fun updateNotes(notes: String) = _uiState.update { it.copy(notes = notes) }

    fun addExercise() = _uiState.update { it.copy(exercises = it.exercises + ExerciseFormState()) }

    fun removeExercise(index: Int) = _uiState.update {
        it.copy(exercises = it.exercises.toMutableList().also { list -> list.removeAt(index) })
    }

    fun updateExercise(index: Int, exercise: ExerciseFormState) = _uiState.update {
        it.copy(
            exercises = it.exercises.toMutableList().also { list -> list[index] = exercise },
            validationError = null
        )
    }

    fun saveWorkout() {
        val state = _uiState.value
        val validationError = validate(state)
        if (validationError != null) {
            _uiState.update { it.copy(validationError = validationError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, validationError = null) }

            val exercises = state.exercises.mapIndexed { index, ex ->
                Exercise(
                    id = ex.id,
                    name = ex.name.trim(),
                    sets = ex.sets.toIntOrNull() ?: 0,
                    reps = ex.reps.toIntOrNull() ?: 0,
                    weightKg = ex.weightKg.toDoubleOrNull(),
                    notes = ex.notes.trim(),
                    order = index
                )
            }

            val workout = Workout(
                id = state.workoutId,
                name = state.name.trim(),
                date = state.date,
                notes = state.notes.trim(),
                exercises = exercises
            )

            when (val result = workoutRepository.saveWorkout(workout)) {
                is Result.Success -> _uiState.update { it.copy(isLoading = false, isSaved = true) }
                is Result.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
                else -> _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun validate(state: CreateWorkoutUiState): String? {
        if (state.name.isBlank()) return "Nome do treino é obrigatório"
        if (state.exercises.isEmpty()) return "Adicione pelo menos um exercício"
        val incomplete = state.exercises.any { it.name.isBlank() || it.sets.isBlank() || it.reps.isBlank() }
        if (incomplete) return "Preencha nome, séries e repetições de todos os exercícios"
        return null
    }

    fun clearError() = _uiState.update { it.copy(error = null) }
    fun clearValidationError() = _uiState.update { it.copy(validationError = null) }
}
