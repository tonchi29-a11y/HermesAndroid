package com.hermes.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.app.data.api.ChatMessage
import com.hermes.app.data.api.HermesApiService
import com.hermes.app.data.preferences.AppPreferences
import com.hermes.app.data.repository.HermesRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ChatUiMessage(
    val id: Long,
    val role: String,
    val content: String,
    val isStreaming: Boolean = false,
)

data class ChatUiState(
    val messages: List<ChatUiMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isConnected: Boolean = false,
    val checkingConnection: Boolean = false,
    val currentSessionId: String? = null,
)

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = AppPreferences(application)
    private var messageIdCounter = 0L

    private val _state = MutableStateFlow(ChatUiState())
    val state: StateFlow<ChatUiState> = _state.asStateFlow()

    private var api: HermesApiService? = null
    private var repo: HermesRepository? = null

    init {
        viewModelScope.launch {
            prefs.host.collect { host ->
                prefs.port.collect { port ->
                    prefs.apiKey.collect { key ->
                        val url = "http://$host:$port"
                        val service = HermesApiService(url, key.ifEmpty { null })
                        api = service
                        repo = HermesRepository(service)
                        checkConnection()
                    }
                }
            }
        }
    }

    suspend fun getRepo(): HermesRepository? = repo

    fun checkConnection() {
        viewModelScope.launch {
            _state.update { it.copy(checkingConnection = true, error = null) }
            val result = api?.health()
            _state.update { it.copy(isConnected = result?.isSuccess == true, checkingConnection = false) }
        }
    }

    fun sendMessage(text: String) {
        val currentMessages = _state.value.messages
        val userMsg = ChatUiMessage(++messageIdCounter, "user", text)
        _state.update {
            it.copy(
                messages = it.messages + userMsg,
                isLoading = true,
                error = null,
            )
        }

        val assistantMsgId = ++messageIdCounter
        _state.update {
            it.copy(
                messages = it.messages + ChatUiMessage(assistantMsgId, "assistant", "", isStreaming = true),
            )
        }

        viewModelScope.launch {
            try {
                val chatMessages = currentMessages
                    .filter { !it.isStreaming }
                    .map { ChatMessage(it.role, it.content) } + ChatMessage("user", text)

                val result = repo?.sendMessage(chatMessages)
                result?.onSuccess { response ->
                    val reply = response.choices.firstOrNull()?.message?.content ?: ""
                    _state.update {
                        val updated = it.messages.map { msg ->
                            if (msg.id == assistantMsgId) {
                                msg.copy(content = reply, isStreaming = false)
                            } else msg
                        }
                        it.copy(messages = updated, isLoading = false)
                    }
                }?.onFailure { e ->
                    _state.update {
                        val filtered = it.messages.filter { msg -> msg.id != assistantMsgId }
                        it.copy(
                            messages = filtered,
                            isLoading = false,
                            error = "API error: ${e.message}",
                        )
                    }
                } ?: run {
                    // repo is null - still initializing
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "Hermes not initialized yet. Try again.",
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    val filtered = it.messages.filter { msg -> msg.id != assistantMsgId }
                    it.copy(
                        messages = filtered,
                        isLoading = false,
                        error = "Crash: ${e::class.simpleName}: ${e.message}",
                    )
                }
            }
        }
    }

    fun clearChat() {
        _state.update { ChatUiState(isConnected = it.isConnected) }
        messageIdCounter = 0
        checkConnection()
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
