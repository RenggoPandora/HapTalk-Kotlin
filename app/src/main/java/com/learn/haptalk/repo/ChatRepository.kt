package com.learn.haptalk.repo

import android.util.Log
import com.learn.haptalk.data.MessageDao
import com.learn.haptalk.data.MessageEntity
import com.learn.haptalk.data.MessageStatus
import com.learn.haptalk.network.ChatMessage
import kotlinx.coroutines.flow.Flow

/**
 * Repository for chat messages
 * Manages local persistence and coordinates between network and database
 */
class ChatRepository(
    private val messageDao: MessageDao
) {
    companion object {
        private const val TAG = "ChatRepository"
    }

    /**
     * Observe all messages from database
     */
    fun observeMessages(): Flow<List<MessageEntity>> {
        return messageDao.observeAllMessages()
    }

    /**
     * Insert a new message
     */
    suspend fun insertMessage(message: MessageEntity): Long {
        return messageDao.insert(message)
    }

    /**
     * Update message status
     */
    suspend fun updateMessageStatus(messageId: Long, status: MessageStatus) {
        messageDao.updateStatus(messageId, status)
    }

    /**
     * Get all pending messages (to be sent)
     */
    suspend fun getPendingMessages(): List<MessageEntity> {
        return messageDao.getMessagesByStatus(MessageStatus.PENDING)
    }

    /**
     * Insert message from network if it doesn't exist
     * Prevents duplicate messages
     */
    suspend fun insertIfNotExists(chatMessage: ChatMessage, mySessionId: String) {
        val exists = messageDao.messageExists(chatMessage.u, chatMessage.t) > 0
        if (!exists) {
            val message = MessageEntity(
                senderId = chatMessage.u,
                text = chatMessage.m,
                timestamp = chatMessage.t,
                status = MessageStatus.SENT,
                isMine = chatMessage.u == mySessionId
            )
            messageDao.insert(message)
            Log.d(TAG, "Inserted message from ${chatMessage.u}")
        }
    }

    /**
     * Delete message by ID
     */
    suspend fun deleteMessage(messageId: Long) {
        messageDao.deleteById(messageId)
    }
}

