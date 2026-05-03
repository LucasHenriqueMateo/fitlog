package com.fitlog.app.ui.workout.template

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitlog.app.data.repository.WorkoutRepository
import com.fitlog.app.domain.model.Exercise
import com.fitlog.app.domain.model.Workout
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class ExerciseTemplateForm(
    val id: String = "",
    val name: String = "",
    val sets: String = "",
    val reps: String = ""
)

data class CreateTemplateState(
    val workoutName: String = "",
    val exercises: List<ExerciseTemplateForm> = listOf(ExerciseTemplateForm()),
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null,
    val isEditMode: Boolean = false
)

@HiltViewModel
class CreateTemplateViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val templateId: String? = savedStateHandle["id"]

    private val _state = MutableStateFlow(CreateTemplateState())
    val state: StateFlow<CreateTemplateState> = _state.asStateFlow()

    init {
        templateId?.let { loadTemplate(it) }
    }

    private fun loadTemplate(id: String) {
        viewModelScope.launch {
            val workout = workoutRepository.getWorkoutById(id) ?: return@launch
            _state.update { state ->
                state.copy(
                    workoutName = workout.name,
                    exercises = workout.exercises.map { ex ->
                        ExerciseTemplateForm(
                            id = ex.id,
                            name = ex.name,
                            sets = ex.sets.toString(),
                            reps = ex.reps.toString()
                        )
                    }.ifEmpty { listOf(ExerciseTemplateForm()) },
                    isEditMode = true
                )
            }
        }
    }

    fun onWorkoutNameChange(name: String) {
        _state.update { it.copy(workoutName = name, error = null) }
    }

    fun onExerciseNameChange(index: Int, name: String) {
        _state.update { state ->
            val updated = state.exercises.toMutableList()
            updated[index] = updated[index].copy(name = name)
            state.copy(exercises = updated)
        }
    }

    fun onExerciseSetsChange(index: Int, sets: String) {
        _state.update { state ->
            val updated = state.exercises.toMutableList()
            updated[index] = updated[index].copy(sets = sets)
            state.copy(exercises = updated)
        }
    }

    fun onExerciseRepsChange(index: Int, reps: String) {
        _state.update { state ->
            val updated = state.exercises.toMutableList()
            updated[index] = updated[index].copy(reps = reps)
            state.copy(exercises = updated)
        }
    }

    fun addExercise() {
        _state.update { it.copy(exercises = it.exercises + ExerciseTemplateForm()) }
    }

    fun removeExercise(index: Int) {
        _state.update { state ->
            val updated = state.exercises.toMutableList()
            updated.removeAt(index)
            state.copy(exercises = if (updated.isEmpty()) listOf(ExerciseTemplateForm()) else updated)
        }
    }

    fun saveTemplate() {
        val state = _state.value
        if (state.workoutName.isBlank()) {
            _state.update { it.copy(error = "Nome do treino é obrigatório") }
            return
        }
        val validExercises = state.exercises.filter { it.name.isNotBlank() }
        if (validExercises.isEmpty()) {
            _state.update { it.copy(error = "Adicione pelo menos um exercício") }
            return
        }

        _state.update { it.copy(isSaving = true, error = null) }
        viewModelScope.launch {
            val templateCode = state.workoutName.trim().split(" ").firstOrNull() ?: ""
            val workout = Workout(
                id = templateId ?: "",
                name = state.workoutName.trim(),
                date = LocalDate.now().toString(),
                notes = templateCode,
                isTemplate = true,
                exercises = validExercises.mapIndexed { index, form ->
                    Exercise(
                        id = form.id,
                        workoutId = templateId ?: "",
                        name = form.name.trim(),
                        sets = form.sets.toIntOrNull() ?: 0,
                        reps = form.reps.toIntOrNull() ?: 0,
                        order = index
                    )
                }
            )
            val result = workoutRepository.saveWorkout(workout)
            when (result) {
                is com.fitlog.app.util.Result.Success ->
                    _state.update { it.copy(isSaving = false, saved = true) }
                is com.fitlog.app.util.Result.Error ->
                    _state.update { it.copy(isSaving = false, error = result.message) }
                is com.fitlog.app.util.Result.Loading ->
                    Unit
            }
        }
    }
}
