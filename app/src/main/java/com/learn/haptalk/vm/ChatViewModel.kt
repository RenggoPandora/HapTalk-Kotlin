package com.learn.haptalk.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.haptalk.data.MessageEntity
import com.learn.haptalk.data.MessageStatus
import com.learn.haptalk.data.PreferencesManager
import com.learn.haptalk.network.ChatMessage
import com.learn.haptalk.network.ConnectionState
import com.learn.haptalk.network.WebSocketManager
import com.learn.haptalk.repo.ChatRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for chat screen
 * Manages WebSocket connection, message sending/receiving, and UI state
 */
class ChatViewModel(
    private val repository: ChatRepository,
    private val preferencesManager: PreferencesManager,
    private val webSocketManager: WebSocketManager
) : ViewModel() {

    companion object {
        private const val TAG = "ChatViewModel"
    }

    // Observe messages from repository
    val messages: StateFlow<List<MessageEntity>> = repository.observeMessages()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Session ID
    private val _sessionId = MutableStateFlow("")
    val sessionId: StateFlow<String> = _sessionId.asStateFlow()

    // Connection state
    val connectionState: StateFlow<ConnectionState> = webSocketManager.connectionState

    init {
        // Load session ID
        viewModelScope.launch {
            preferencesManager.sessionId.collect { id ->
                if (id.isNotEmpty()) {
                    _sessionId.value = id
                }
            }
        }
    }

    /**
     * Connect to WebSocket server
     */
    fun connectToChat() {
        webSocketManager.connect(viewModelScope)

        // Send any pending messages once connected
        viewModelScope.launch {
            connectionState.collect { state ->
                if (state == ConnectionState.CONNECTED) {
                    sendPendingMessages()
                }
            }
        }
    }

    /**
     * Send a new text message
     */
    fun sendMessage(text: String) {
        if (text.isBlank()) return

        viewModelScope.launch {
            try {
                val timestamp = System.currentTimeMillis()
                val message = MessageEntity(
                    senderId = _sessionId.value,
                    text = text,
                    timestamp = timestamp,
                    status = MessageStatus.PENDING,
                    isMine = true
                )

                // Insert into local database (optimistic UI)
                val messageId = repository.insertMessage(message)

                // Try to send via WebSocket
                if (connectionState.value == ConnectionState.CONNECTED) {
                    try {
                        val chatMessage = ChatMessage(
                            u = _sessionId.value,
                            m = text,
                            t = timestamp
                        )
                        webSocketManager.send(chatMessage.toJson())

                        // Update status to SENT
                        repository.updateMessageStatus(messageId, MessageStatus.SENT)
                        Log.d(TAG, "Message sent successfully")
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to send message: ${e.message}")
                        // Message remains PENDING and will be sent on reconnect
                    }
                } else {
                    Log.d(TAG, "Not connected, message queued as pending")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error sending message: ${e.message}")
            }
        }
    }

    /**
     * Handle incoming message from WebSocket
     */
    fun onMessageReceived(json: String) {
        viewModelScope.launch {
            try {
                val chatMessage = ChatMessage.fromJson(json)

                // Don't insert our own messages again (they're already in DB)
                if (chatMessage.u != _sessionId.value) {
                    repository.insertIfNotExists(chatMessage, _sessionId.value)
                } else {
                    // Update status of our sent message if it exists
                    Log.d(TAG, "Received echo of our own message")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing message: ${e.message}")
            }
        }
    }

    /**
     * Send all pending messages
     */
    private suspend fun sendPendingMessages() {
        try {
            val pendingMessages = repository.getPendingMessages()
            Log.d(TAG, "Found ${pendingMessages.size} pending messages")

            for (message in pendingMessages) {
                try {
                    val chatMessage = ChatMessage(
                        u = message.senderId,
                        m = message.text,
                        t = message.timestamp
                    )
                    webSocketManager.send(chatMessage.toJson())
                    repository.updateMessageStatus(message.id, MessageStatus.SENT)
                    Log.d(TAG, "Sent pending message ${message.id}")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to send pending message ${message.id}: ${e.message}")
                    repository.updateMessageStatus(message.id, MessageStatus.FAILED)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error sending pending messages: ${e.message}")
        }
    }

    override fun onCleared() {
        super.onCleared()
        webSocketManager.disconnect()
    }
}

