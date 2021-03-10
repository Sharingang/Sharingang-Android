package com.example.sharingang.items

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ItemDatabaseTest {
    private lateinit var itemDao: ItemDao
    private lateinit var db: ItemDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        db =
            Room.inMemoryDatabaseBuilder(context, ItemDatabase::class.java).allowMainThreadQueries()
                .build()
        itemDao = db.itemDatabaseDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetItem() {
        val item = Item("an item")
        itemDao.insert(item)
    }
}