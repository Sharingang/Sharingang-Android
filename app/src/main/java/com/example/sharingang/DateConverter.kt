package com.example.sharingang

import androidx.room.TypeConverter
import java.util.*

/**
 * A utility object needed to store Date fields in our Room entities.
 */
object DateConverter {
    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        if (timestamp == null) {
            return null
        }
        return Date(timestamp)
    }

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        if (date == null) {
            return null
        }
        return date.time
    }


}