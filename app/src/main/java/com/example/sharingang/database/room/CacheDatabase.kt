package com.example.sharingang.database.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.sharingang.models.Item
import com.example.sharingang.models.User

@Database(entities = [Item::class, User::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class, ListConverter::class, MapConverter::class)
abstract class CacheDatabase : RoomDatabase() {
    abstract val itemDao: ItemDao
    abstract val userDao: UserDao
}
