package com.example.sharingang

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.room.TypeConverter
import com.example.sharingang.items.Item
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.GlobalScope
import java.lang.StringBuilder
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


    @RequiresApi(Build.VERSION_CODES.N)
    @TypeConverter
    fun fromList(wishlist: MutableLiveData<List<String>>): String{
        val returnString : StringJoiner = StringJoiner(",")
        wishlist!!.value!!.map { str ->
            returnString.add(str)
        }
        return returnString.toString()
    }

    @TypeConverter
    fun toList(data: String): MutableLiveData<MutableList<String>>? {
        val list = data.split(',')
        return MutableLiveData(ArrayList(list))
    }
}