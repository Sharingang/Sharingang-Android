package com.example.sharingang

import android.content.Context
import androidx.room.Room
import com.example.sharingang.items.*
import com.example.sharingang.users.*
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
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
    fun provideDatabase(@ApplicationContext app: Context): CacheDatabase {
        return Room.inMemoryDatabaseBuilder(app, CacheDatabase::class.java).build()
    }

    @Singleton
    @Provides
    fun provideItemDao(db: CacheDatabase): ItemDao {
        return db.itemDao
    }

    @Singleton
    @Provides
    fun provideUserStore(): UserStore {
        return InMemoryUserRepository()
    }

    @Singleton
    @Provides
    fun provideUserRepository(repo: CachedUserRepository): UserRepository {
        return repo
    }

    @Singleton
    @Provides
    fun provideUserDao(db: CacheDatabase): UserDao {
        return db.userDao
    }
}
