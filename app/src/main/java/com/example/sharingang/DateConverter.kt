package com.example.sharingang

import androidx.room.TypeConverter
import java.util.*
import kotlin.collections.ArrayList

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

    @TypeConverter
    fun toList(stringList: String?): List<String?>{
        if(stringList == null){
            return ArrayList()
        }
        return stringList.split(',')
    }

    @TypeConverter
    fun fromList(list : List<String?>): String {
        return list.joinToString(separator = ",")
    }
}