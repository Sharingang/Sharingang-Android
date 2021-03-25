package com.example.sharingang.items

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.sharingang.DateConverter

@Database(entities = [Item::class], version = 2)
@TypeConverters(DateConverter::class)
abstract class ItemDatabase : RoomDatabase() {
    abstract val itemDao: ItemDao
}