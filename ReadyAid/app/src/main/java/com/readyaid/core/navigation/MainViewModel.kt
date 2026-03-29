package com.readyaid.core.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.readyaid.data.profile.UserProfileDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AppState {
    object Loading : AppState()
    object Onboarding : AppState()
    object Disclaimer : AppState()
    object Home : AppState()
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userProfileDao: UserProfileDao
) : ViewModel() {

    private val _appState = MutableStateFlow<AppState>(AppState.Loading)
    val appState: StateFlow<AppState> = _appState

    init {
        viewModelScope.launch {
            userProfileDao.getUserProfile()
                .catch {
                    // If DB read fails, fail open to onboarding instead of hanging on launch.
                    _appState.value = AppState.Onboarding
                }
                .collect { profile ->
                    _appState.value = when {
                        profile?.profileCompleted != true -> AppState.Onboarding
                        profile.disclaimerAccepted != true -> AppState.Disclaimer
                        else -> AppState.Home
                    }
                }
        }
    }

    fun acceptDisclaimer() {
        viewModelScope.launch {
            val profile = userProfileDao.getUserProfileSync() ?: return@launch
            userProfileDao.insertProfile(profile.copy(disclaimerAccepted = true))
        }
    }
}
