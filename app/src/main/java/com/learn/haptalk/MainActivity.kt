package com.learn.haptalk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.learn.haptalk.data.ChatDatabase
import com.learn.haptalk.data.PreferencesManager
import com.learn.haptalk.network.WebSocketManager
import com.learn.haptalk.repo.ChatRepository
import com.learn.haptalk.ui.ChatScreen
import com.learn.haptalk.ui.SplashScreen
import com.learn.haptalk.ui.theme.HapTalkTheme
import com.learn.haptalk.vm.ChatViewModel
import com.learn.haptalk.vm.ChatViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var database: ChatDatabase
    private lateinit var repository: ChatRepository
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var webSocketManager: WebSocketManager
    private lateinit var viewModel: ChatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize dependencies
        database = Room.databaseBuilder(
            applicationContext,
            ChatDatabase::class.java,
            "haptalk_database"
        ).build()

        repository = ChatRepository(database.messageDao())
        preferencesManager = PreferencesManager(applicationContext)

        // WebSocket URL - change this for your server
        // For local development with emulator: ws://10.0.2.2:3000
        // For production: wss://your-server.com
        val wsUrl = "ws://10.0.2.2:3000"

        webSocketManager = WebSocketManager(wsUrl) { message ->
            viewModel.onMessageReceived(message)
        }

        // Create ViewModel
        val factory = ChatViewModelFactory(repository, preferencesManager, webSocketManager)
        viewModel = ViewModelProvider(this, factory)[ChatViewModel::class.java]

        setContent {
            HapTalkTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HapTalkApp(
                        viewModel = viewModel,
                        preferencesManager = preferencesManager
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        webSocketManager.disconnect()
    }
}

@Composable
fun HapTalkApp(
    viewModel: ChatViewModel,
    preferencesManager: PreferencesManager
) {
    var showSplash by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    val messages by viewModel.messages.collectAsState()
    val connectionState by viewModel.connectionState.collectAsState()

    if (showSplash) {
        SplashScreen(
            onEnterChat = {
                coroutineScope.launch {
                    // Create session if doesn't exist
                    val sessionId = viewModel.sessionId.value
                    if (sessionId.isEmpty()) {
                        preferencesManager.createSessionId()
                    }

                    // Connect to WebSocket
                    viewModel.connectToChat()

                    // Navigate to chat
                    showSplash = false
                }
            }
        )
    } else {
        ChatScreen(
            messages = messages,
            connectionState = connectionState,
            onSendMessage = { text ->
                viewModel.sendMessage(text)
            }
        )
    }
}

