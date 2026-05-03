package com.fitlog.app.ui.workout.manage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitlog.app.data.repository.WorkoutRepository
import com.fitlog.app.domain.model.Workout
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ManageWorkoutsUiState {
    data object Loading : ManageWorkoutsUiState()
    data class Success(val templates: List<Workout>) : ManageWorkoutsUiState()
    data object Empty : ManageWorkoutsUiState()
    data class Error(val message: String) : ManageWorkoutsUiState()
}

@HiltViewModel
class ManageWorkoutsViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ManageWorkoutsUiState>(ManageWorkoutsUiState.Loading)
    val uiState: StateFlow<ManageWorkoutsUiState> = _uiState.asStateFlow()

    init {
        loadTemplates()
    }

    private fun loadTemplates() {
        viewModelScope.launch {
            workoutRepository.getTemplates().collect { templates ->
                _uiState.value = if (templates.isEmpty()) ManageWorkoutsUiState.Empty
                else ManageWorkoutsUiState.Success(templates)
            }
        }
    }

    fun deleteTemplate(id: String) {
        viewModelScope.launch {
            workoutRepository.deleteWorkout(id)
        }
    }
}
