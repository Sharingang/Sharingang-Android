package com.example.sharingang

import android.content.Context
import androidx.room.Room
import com.example.sharingang.items.*
import com.example.sharingang.users.*
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
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
object FakeRepositoryModule {
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

    @Singleton
    @Provides
    fun provideCurrentUserProvider(currentUserProvider: FakeCurrentUserProvider): CurrentUserProvider {
        return currentUserProvider
    }

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        val auth = FirebaseAuth.getInstance()
        auth.useEmulator("10.0.2.2", 9099)
        return auth
    }

    @Singleton
    @Provides
    fun provideFirebaseAuthUI(): AuthUI {
        val authUI = AuthUI.getInstance()
        authUI.useEmulator("10.0.2.2", 9099)
        return authUI
    }

    @Singleton
    @Provides
    fun provideImageStore(imageStore: PlaceholderImageStore): ImageStore {
        return imageStore
    }
}

