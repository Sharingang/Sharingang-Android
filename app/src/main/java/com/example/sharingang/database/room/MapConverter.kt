package com.example.sharingang.database.room

import androidx.room.TypeConverter
import kotlin.collections.ArrayList

object MapConverter {

    @TypeConverter
    fun fromMap(map: Map<String, Boolean>) : String {
        val users = ArrayList(map.keys)
        val reviews = ArrayList(map.values)
        return users.joinToString(separator = ",") + " " + reviews.joinToString(separator = ",")
    }

    @TypeConverter
    fun toMap(str: String) : Map<String, Boolean> {
        val lists = str.split(" ")
        val users = lists[0].split(",")
        val reviews = lists[1].split(",").map { it -> it.toBoolean() }
        return users.zip(reviews).toMap()
    }
}