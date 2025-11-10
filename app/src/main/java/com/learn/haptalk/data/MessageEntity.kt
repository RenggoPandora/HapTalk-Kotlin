package com.learn.haptalk.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Message entity for Room database
 * Stores chat messages locally for offline support
 */
@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val senderId: String,        // sessionId of sender
    val text: String,             // message content
    val timestamp: Long,          // unix epoch in milliseconds
    val status: MessageStatus = MessageStatus.SENT,
    val isMine: Boolean = false   // flag to identify own messages
)

enum class MessageStatus {
    PENDING,    // Waiting to be sent
    SENT,       // Successfully sent
    FAILED      // Failed to send
}

