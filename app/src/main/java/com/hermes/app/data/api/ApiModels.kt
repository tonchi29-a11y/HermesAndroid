package com.hermes.app.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ── OpenAI Chat Completions API ──

@Serializable
data class ChatCompletionRequest(
    @SerialName("model") val model: String = "hermes-agent",
    @SerialName("messages") val messages: List<ChatMessage>,
    @SerialName("stream") val stream: Boolean = false,
    @SerialName("temperature") val temperature: Double? = null,
    @SerialName("max_tokens") val maxTokens: Int? = null,
)

@Serializable
data class ChatMessage(
    @SerialName("role") val role: String,
    @SerialName("content") val content: String,
)

@Serializable
data class ChatCompletionResponse(
    @SerialName("id") val id: String,
    @SerialName("object") val obj: String = "chat.completion",
    @SerialName("created") val created: Long,
    @SerialName("model") val model: String,
    @SerialName("choices") val choices: List<Choice>,
    @SerialName("usage") val usage: Usage? = null,
)

@Serializable
data class Choice(
    @SerialName("index") val index: Int,
    @SerialName("message") val message: ChatMessage,
    @SerialName("finish_reason") val finishReason: String? = null,
)

@Serializable
data class Usage(
    @SerialName("prompt_tokens") val promptTokens: Int = 0,
    @SerialName("completion_tokens") val completionTokens: Int = 0,
    @SerialName("total_tokens") val totalTokens: Int = 0,
)

// ── SSE (Streaming) Event ──

@Serializable
data class ChatCompletionChunk(
    @SerialName("id") val id: String,
    @SerialName("object") val obj: String = "chat.completion.chunk",
    @SerialName("created") val created: Long,
    @SerialName("model") val model: String,
    @SerialName("choices") val choices: List<ChunkChoice>,
)

@Serializable
data class ChunkChoice(
    @SerialName("index") val index: Int,
    @SerialName("delta") val delta: Delta,
    @SerialName("finish_reason") val finishReason: String? = null,
)

@Serializable
data class Delta(
    @SerialName("role") val role: String? = null,
    @SerialName("content") val content: String? = null,
)

// ── Models ──

@Serializable
data class ModelList(
    @SerialName("object") val obj: String = "list",
    @SerialName("data") val data: List<ModelInfo>,
)

@Serializable
data class ModelInfo(
    @SerialName("id") val id: String,
    @SerialName("object") val obj: String = "model",
    @SerialName("created") val created: Long = 0,
    @SerialName("owned_by") val ownedBy: String = "hermes",
)

// ── Health ──

@Serializable
data class HealthStatus(
    @SerialName("status") val status: String,
)

// ── Sessions API ──

@Serializable
data class SessionList(
    @SerialName("sessions") val sessions: List<SessionInfo>,
)

@Serializable
data class SessionInfo(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String? = null,
    @SerialName("created_at") val createdAt: Long? = null,
    @SerialName("updated_at") val updatedAt: Long? = null,
    @SerialName("message_count") val messageCount: Int? = null,
)

@Serializable
data class CreateSessionResponse(
    @SerialName("id") val id: String,
)

@Serializable
data class SessionMessages(
    @SerialName("messages") val messages: List<SessionMessage>,
)

@Serializable
data class SessionMessage(
    @SerialName("role") val role: String,
    @SerialName("content") val content: String,
    @SerialName("created_at") val createdAt: String? = null,
)

// ── Error ──

@Serializable
data class ApiError(
    @SerialName("error") val error: ErrorDetail,
)

@Serializable
data class ErrorDetail(
    @SerialName("message") val message: String,
    @SerialName("type") val type: String? = null,
)
