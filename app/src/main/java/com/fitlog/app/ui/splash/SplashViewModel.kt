package com.fitlog.app.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    supabaseClient: SupabaseClient
) : ViewModel() {

    sealed class Destination {
        data object Loading : Destination()
        data object Home : Destination()
        data object Auth : Destination()
    }

    val destination: StateFlow<Destination> = supabaseClient.auth.sessionStatus
        .map { status ->
            when (status) {
                is SessionStatus.Authenticated -> Destination.Home
                is SessionStatus.NotAuthenticated -> Destination.Auth
                SessionStatus.Initializing -> Destination.Loading
                is SessionStatus.RefreshFailure -> Destination.Auth
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, Destination.Loading)
}
