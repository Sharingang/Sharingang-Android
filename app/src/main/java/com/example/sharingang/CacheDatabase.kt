package com.example.sharingang

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.sharingang.items.Item
import com.example.sharingang.items.ItemDao
import com.example.sharingang.users.User
import com.example.sharingang.users.UserDao

@Database(entities = [Item::class, User::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class, ListConverter::class)
abstract class CacheDatabase : RoomDatabase() {
    abstract val itemDao: ItemDao
    abstract val userDao: UserDao
}
