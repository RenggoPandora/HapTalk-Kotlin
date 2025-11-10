package com.learn.haptalk.network

import android.util.Log
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.min
import kotlin.math.pow

/**
 * WebSocket Manager using Ktor client
 * Handles connection, reconnection with exponential backoff, and message sending
 */
class WebSocketManager(
    private val url: String,
    private val onMessage: (String) -> Unit
) {
    companion object {
        private const val TAG = "WebSocketManager"
        private const val INITIAL_RETRY_DELAY = 2000L // 2 seconds
        private const val MAX_RETRY_DELAY = 30000L // 30 seconds
        private const val PING_INTERVAL = 30000L // 30 seconds
    }

    private val client: HttpClient = HttpClient(CIO) {
        install(WebSockets) {
            pingInterval = PING_INTERVAL
        }
    }

    private var session: DefaultClientWebSocketSession? = null
    private var isRunning = false
    private var connectionJob: Job? = null
    private var pingJob: Job? = null

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState

    /**
     * Start WebSocket connection with auto-reconnect
     */
    fun connect(scope: CoroutineScope) {
        if (isRunning) {
            Log.d(TAG, "Already running")
            return
        }

        isRunning = true
        connectionJob = scope.launch {
            var retryCount = 0

            while (isRunning) {
                try {
                    Log.d(TAG, "Attempting to connect to $url")
                    _connectionState.value = ConnectionState.CONNECTING

                    // Parse URL
                    val (host, port, path) = parseWebSocketUrl(url)

                    client.webSocket(
                        host = host,
                        port = port,
                        path = path
                    ) {
                        session = this
                        _connectionState.value = ConnectionState.CONNECTED
                        retryCount = 0
                        Log.d(TAG, "Connected to WebSocket")

                        // Start ping mechanism
                        startPing(this@launch)

                        // Listen for incoming messages
                        try {
                            for (frame in incoming) {
                                when (frame) {
                                    is Frame.Text -> {
                                        val text = frame.readText()
                                        Log.d(TAG, "Received: $text")
                                        onMessage(text)
                                    }
                                    else -> {}
                                }
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error reading frames: ${e.message}")
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Connection error: ${e.message}")
                    _connectionState.value = ConnectionState.DISCONNECTED
                    session = null

                    if (isRunning) {
                        // Calculate exponential backoff delay
                        val delay = min(
                            MAX_RETRY_DELAY,
                            (INITIAL_RETRY_DELAY * 2.0.pow(retryCount.toDouble())).toLong()
                        )
                        retryCount++

                        Log.d(TAG, "Reconnecting in ${delay}ms (attempt $retryCount)")
                        delay(delay)
                    }
                }
            }
        }
    }

    /**
     * Send message through WebSocket
     */
    suspend fun send(message: String) {
        val currentSession = session
        if (currentSession == null || _connectionState.value != ConnectionState.CONNECTED) {
            throw IllegalStateException("WebSocket not connected")
        }

        try {
            currentSession.send(Frame.Text(message))
            Log.d(TAG, "Sent: $message")
        } catch (e: Exception) {
            Log.e(TAG, "Error sending message: ${e.message}")
            throw e
        }
    }

    /**
     * Stop WebSocket connection
     */
    fun disconnect() {
        Log.d(TAG, "Disconnecting")
        isRunning = false
        pingJob?.cancel()
        connectionJob?.cancel()
        session = null
        _connectionState.value = ConnectionState.DISCONNECTED
        client.close()
    }

    /**
     * Start periodic ping to keep connection alive
     */
    private fun startPing(scope: CoroutineScope) {
        pingJob?.cancel()
        pingJob = scope.launch {
            while (isActive && _connectionState.value == ConnectionState.CONNECTED) {
                delay(PING_INTERVAL)
                try {
                    session?.send(Frame.Ping(byteArrayOf()))
                } catch (e: Exception) {
                    Log.e(TAG, "Ping failed: ${e.message}")
                    break
                }
            }
        }
    }

    /**
     * Parse WebSocket URL into components
     */
    private fun parseWebSocketUrl(url: String): Triple<String, Int, String> {
        val cleanUrl = url.removePrefix("ws://").removePrefix("wss://")
        val parts = cleanUrl.split("/", limit = 2)
        val hostPort = parts[0]
        val path = if (parts.size > 1) "/${parts[1]}" else "/"

        val hostParts = hostPort.split(":")
        val host = hostParts[0]
        val port = if (hostParts.size > 1) hostParts[1].toInt() else 80

        return Triple(host, port, path)
    }
}

enum class ConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED
}

