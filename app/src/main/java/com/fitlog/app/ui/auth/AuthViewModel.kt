package com.fitlog.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitlog.app.data.repository.AuthRepository
import com.fitlog.app.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthUiState {
    data object Idle : AuthUiState()
    data object Loading : AuthUiState()
    data object Success : AuthUiState()
    /** Signup succeeded but email confirmation is required before the user can log in. */
    data object EmailConfirmationSent : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            _uiState.value = when (val result = authRepository.signIn(email.trim(), password)) {
                is Result.Success -> AuthUiState.Success
                is Result.Error -> AuthUiState.Error(friendlyError(result.message))
                else -> AuthUiState.Idle
            }
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            _uiState.value = when (val result = authRepository.signUp(email.trim(), password)) {
                // Signup succeeded — a confirmation e-mail was sent; user must verify before logging in
                is Result.Success -> AuthUiState.EmailConfirmationSent
                is Result.Error -> AuthUiState.Error(friendlyError(result.message))
                else -> AuthUiState.Idle
            }
        }
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            _uiState.value = when (val result = authRepository.signInWithGoogle(idToken)) {
                is Result.Success -> AuthUiState.Success
                is Result.Error -> AuthUiState.Error(friendlyError(result.message))
                else -> AuthUiState.Idle
            }
        }
    }

    fun clearState() {
        _uiState.value = AuthUiState.Idle
    }

    private fun friendlyError(raw: String): String = when {
        raw.contains("email not confirmed", ignoreCase = true) ->
            "Confirme seu e-mail antes de entrar. Verifique sua caixa de entrada."
        raw.contains("invalid login credentials", ignoreCase = true) ->
            "E-mail ou senha incorretos."
        raw.contains("user already registered", ignoreCase = true) ->
            "E-mail já cadastrado. Tente entrar."
        raw.contains("password should be at least", ignoreCase = true) ->
            "A senha deve ter pelo menos 6 caracteres."
        raw.contains("unable to validate", ignoreCase = true) ->
            "Não foi possível validar as credenciais. Tente novamente."
        else -> raw
    }
}
