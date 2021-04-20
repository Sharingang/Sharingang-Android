package com.example.sharingang


import androidx.room.TypeConverter
import java.util.*
import kotlin.collections.ArrayList

object ListConverter {
    @TypeConverter
    fun toList(stringList: String?): List<String?> {
        if (stringList == null) {
            return ArrayList()
        }
        return stringList.split(',')
    }

    @TypeConverter
    fun fromList(list: List<String?>): String {
        return list.joinToString(separator = ",")
    }
}