package com.learn.haptalk.network

import com.google.gson.Gson

/**
 * Network message format for WebSocket communication
 * Compact JSON format: {"u":"userId","m":"message","t":timestamp}
 */
data class ChatMessage(
    val u: String,  // senderId (user/sessionId)
    val m: String,  // message text
    val t: Long     // timestamp (unix epoch in milliseconds)
) {
    companion object {
        private val gson = Gson()

        fun fromJson(json: String): ChatMessage {
            return gson.fromJson(json, ChatMessage::class.java)
        }
    }

    fun toJson(): String {
        return gson.toJson(this)
    }
}

