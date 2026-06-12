package com.hermes.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.app.data.api.HermesApiService
import com.hermes.app.data.api.SessionInfo
import com.hermes.app.data.preferences.AppPreferences
import com.hermes.app.data.repository.HermesRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SessionsUiState(
    val sessions: List<SessionInfo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

class SessionsViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = AppPreferences(application)

    private val _state = MutableStateFlow(SessionsUiState())
    val state: StateFlow<SessionsUiState> = _state.asStateFlow()

    private var repo: HermesRepository? = null

    init {
        viewModelScope.launch {
            val host = prefs.host.first()
            val port = prefs.port.first()
            val key = prefs.apiKey.first()
            val url = "http://$host:$port"
            val api = HermesApiService(url, key.ifEmpty { null })
            repo = HermesRepository(api)
            loadSessions()
        }
    }

    fun loadSessions() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val result = repo?.listSessions()
            result?.onSuccess { list ->
                _state.update { it.copy(sessions = list.sessions, isLoading = false) }
            }?.onFailure { e ->
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun deleteSession(id: String) {
        viewModelScope.launch {
            repo?.deleteSession(id)
            loadSessions()
        }
    }
}
