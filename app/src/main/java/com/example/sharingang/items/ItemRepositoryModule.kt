package com.example.sharingang.items

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Tells Hilt to use an instance of FirestoreItemStore when we require an ItemStore
 */
@Module
@InstallIn(SingletonComponent::class)
object ItemRepositoryModule {

    @Singleton
    @Provides
    fun provideItemStore(): ItemStore {
        return FirestoreItemStore()
    }

    @Singleton
    @Provides
    fun provideItemRepository(repo: CachedItemRepository): ItemRepository {
        return repo
    }

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext app: Context): ItemDatabase {
        return Room.databaseBuilder(app, ItemDatabase::class.java, "item_database").build()
    }

    @Singleton
    @Provides
    fun provideItemDao(db: ItemDatabase): ItemDao {
        return db.itemDao
    }
}
