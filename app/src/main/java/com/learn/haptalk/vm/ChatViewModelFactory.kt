package com.learn.haptalk.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.learn.haptalk.data.PreferencesManager
import com.learn.haptalk.network.WebSocketManager
import com.learn.haptalk.repo.ChatRepository

/**
 * Factory for creating ChatViewModel with dependencies
 */
class ChatViewModelFactory(
    private val repository: ChatRepository,
    private val preferencesManager: PreferencesManager,
    private val webSocketManager: WebSocketManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            return ChatViewModel(repository, preferencesManager, webSocketManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

