package com.fitlog.app.ui.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitlog.app.data.local.dao.SessionDao
import com.fitlog.app.data.repository.WorkoutRepository
import com.fitlog.app.domain.model.Workout
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StartSessionState(
    val templates: List<Workout> = emptyList(),
    val selectedTemplate: Workout? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class StartSessionViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val sessionDao: SessionDao
) : ViewModel() {

    private val _state = MutableStateFlow(StartSessionState())
    val state: StateFlow<StartSessionState> = _state.asStateFlow()

    companion object {
        val SEQUENCE = listOf("A1", "B1", "C1", "D", "E", "F", "A2", "B2", "C2", "D", "E", "F")

        fun getNextCode(lastCode: String?): String {
            if (lastCode == null) return SEQUENCE[0]
            val idx = SEQUENCE.indexOfFirst { it == lastCode }
            return if (idx == -1) SEQUENCE[0] else SEQUENCE[(idx + 1) % SEQUENCE.size]
        }
    }

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val lastSession = sessionDao.getLastSession()
            val nextCode = getNextCode(lastSession?.templateCode)

            workoutRepository.getTemplates().collect { templates ->
                val preSelected = templates.find { it.notes == nextCode } ?: templates.firstOrNull()
                _state.update {
                    it.copy(
                        templates = templates,
                        selectedTemplate = if (it.selectedTemplate == null) preSelected else it.selectedTemplate,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onTemplateSelected(workout: Workout) {
        _state.update { it.copy(selectedTemplate = workout) }
    }
}
