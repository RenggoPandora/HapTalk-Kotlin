package com.learn.haptalk.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for messages
 * Provides methods to interact with the messages table
 */
@Dao
interface MessageDao {

    @Query("SELECT * FROM messages ORDER BY timestamp ASC")
    fun observeAllMessages(): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: MessageEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(messages: List<MessageEntity>)

    @Query("UPDATE messages SET status = :status WHERE id = :messageId")
    suspend fun updateStatus(messageId: Long, status: MessageStatus)

    @Query("SELECT * FROM messages WHERE status = :status")
    suspend fun getMessagesByStatus(status: MessageStatus): List<MessageEntity>

    @Query("DELETE FROM messages WHERE id = :messageId")
    suspend fun deleteById(messageId: Long)

    @Query("SELECT COUNT(*) FROM messages WHERE senderId = :senderId AND timestamp = :timestamp")
    suspend fun messageExists(senderId: String, timestamp: Long): Int
}

