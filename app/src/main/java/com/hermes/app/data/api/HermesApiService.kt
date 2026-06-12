package com.hermes.app.data.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class HermesApiService(
    private val baseUrl: String,
    private val apiKey: String? = null,
) {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(json)
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.HEADERS
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 60_000
            connectTimeoutMillis = 5_000
        }
        defaultRequest {
            contentType(ContentType.Application.Json)
            apiKey?.let {
                header("Authorization", "Bearer $it")
            }
        }
    }

    // ── Health ──

    suspend fun health(): Result<HealthStatus> = runCatching {
        client.get("$baseUrl/health").body<HealthStatus>()
    }

    // ── Models ──

    suspend fun listModels(): Result<ModelList> = runCatching {
        client.get("$baseUrl/v1/models").body<ModelList>()
    }

    // ── Chat (non-streaming) ──

    suspend fun chatCompletion(request: ChatCompletionRequest): Result<ChatCompletionResponse> = runCatching {
        client.post("$baseUrl/v1/chat/completions") {
            setBody(request)
        }.body()
    }

    // ── Sessions ──

    suspend fun listSessions(): Result<SessionList> = runCatching {
        client.get("$baseUrl/api/sessions").body<SessionList>()
    }

    suspend fun createSession(): Result<CreateSessionResponse> = runCatching {
        client.post("$baseUrl/api/sessions").body<CreateSessionResponse>()
    }

    suspend fun getSessionMessages(sessionId: String): Result<SessionMessages> = runCatching {
        client.get("$baseUrl/api/sessions/$sessionId/messages").body<SessionMessages>()
    }

    suspend fun deleteSession(sessionId: String): Result<Unit> = runCatching {
        client.delete("$baseUrl/api/sessions/$sessionId")
        Unit
    }

    suspend fun chatInSession(
        sessionId: String,
        message: String,
        stream: Boolean = false,
    ): Result<ChatCompletionResponse> = runCatching {
        client.post("$baseUrl/api/sessions/$sessionId/chat") {
            setBody(ChatCompletionRequest(
                messages = listOf(ChatMessage("user", message)),
                stream = stream,
            ))
        }.body()
    }

    // ── For streaming (SSE), we return raw lines ──

    suspend fun chatCompletionStream(
        request: ChatCompletionRequest,
        onChunk: (String) -> Unit,
    ): Result<Unit> = runCatching {
        client.preparePost("$baseUrl/v1/chat/completions") {
            setBody(request.copy(stream = true))
        }.execute { response ->
            val channel = response.bodyAsChannel()
            while (!channel.isClosedForRead) {
                val line = channel.readUTF8Line(limit = 4096) ?: break
                if (line.startsWith("data: ")) {
                    val data = line.removePrefix("data: ")
                    if (data != "[DONE]") {
                        onChunk(data)
                    }
                }
            }
        }
    }

    fun close() {
        client.close()
    }
}
