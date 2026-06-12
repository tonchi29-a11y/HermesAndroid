package com.hermes.app.data.repository

import com.hermes.app.data.api.*

class HermesRepository(
    private val api: HermesApiService,
) {
    suspend fun checkHealth(): Result<HealthStatus> = api.health()

    suspend fun listModels(): Result<ModelList> = api.listModels()

    suspend fun sendMessage(
        messages: List<ChatMessage>,
        temperature: Double? = null,
    ): Result<ChatCompletionResponse> = api.chatCompletion(
        ChatCompletionRequest(messages = messages, temperature = temperature)
    )

    suspend fun listSessions(): Result<SessionList> = api.listSessions()

    suspend fun createSession(): Result<CreateSessionResponse> = api.createSession()

    suspend fun getSessionMessages(sessionId: String): Result<SessionMessages> =
        api.getSessionMessages(sessionId)

    suspend fun deleteSession(sessionId: String): Result<Unit> =
        api.deleteSession(sessionId)

    suspend fun chatInSession(
        sessionId: String,
        message: String,
    ): Result<ChatCompletionResponse> = api.chatInSession(sessionId, message)
}
