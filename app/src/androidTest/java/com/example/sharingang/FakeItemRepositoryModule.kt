package com.example.sharingang

import android.content.Context
import androidx.room.Room
import com.example.sharingang.items.*
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [ItemRepositoryModule::class]
)
object FakeItemRepositoryModule {
    @Singleton
    @Provides
    fun provideItemStore(): ItemStore {
        return InMemoryItemRepository()
    }

    @Singleton
    @Provides
    fun provideItemRepository(repo: CachedItemRepository): ItemRepository {
        return repo
    }

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext app: Context): ItemDatabase {
        return Room.inMemoryDatabaseBuilder(app, ItemDatabase::class.java).build()
    }

    @Singleton
    @Provides
    fun provideItemDao(db: ItemDatabase): ItemDao {
        return db.itemDao
    }
}