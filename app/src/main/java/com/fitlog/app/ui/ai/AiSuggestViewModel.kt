package com.fitlog.app.ui.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitlog.app.data.remote.AnthropicService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AiSuggestUiState(
    val prompt: String = "",
    val response: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AiSuggestViewModel @Inject constructor(
    private val anthropicService: AnthropicService
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiSuggestUiState())
    val uiState: StateFlow<AiSuggestUiState> = _uiState.asStateFlow()

    fun updatePrompt(prompt: String) = _uiState.update { it.copy(prompt = prompt) }

    fun generateSuggestion(prompt: String) {
        if (prompt.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, response = "") }
            try {
                val result = anthropicService.generateWorkout(prompt)
                _uiState.update { it.copy(isLoading = false, response = result) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Erro ao gerar treino") }
            }
        }
    }

    fun clearError() = _uiState.update { it.copy(error = null) }
}
