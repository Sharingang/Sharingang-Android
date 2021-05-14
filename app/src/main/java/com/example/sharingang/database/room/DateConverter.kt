package com.example.sharingang.database.room

import androidx.room.TypeConverter
import java.util.*

/**
 * A utility object needed to store Date fields in our Room entities.
 */
object DateConverter {

    /**
     * Function to convert timestamps (Long) to Date.
     * @param[timestamp]
     * @return Date object created from the timestamp.
     */
    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        if (timestamp == null) {
            return null
        }
        return Date(timestamp)
    }

    /**
     * Function to convert Date to timestamp (Long).
     * @param[date]
     * @return Corresponding timestamp (Long).
     */
    @TypeConverter
    fun fromDate(date: Date?): Long? {
        if (date == null) {
            return null
        }
        return date.time
    }


}
