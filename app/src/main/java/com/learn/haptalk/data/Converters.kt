package com.learn.haptalk.data

import androidx.room.TypeConverter

/**
 * Type converters for Room database
 * Converts enum types to storable formats
 */
class Converters {
    @TypeConverter
    fun fromMessageStatus(status: MessageStatus): String {
        return status.name
    }

    @TypeConverter
    fun toMessageStatus(value: String): MessageStatus {
        return MessageStatus.valueOf(value)
    }
}

