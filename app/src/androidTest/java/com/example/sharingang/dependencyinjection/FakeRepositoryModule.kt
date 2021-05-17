package com.example.sharingang.dependencyinjection

import android.content.Context
import androidx.room.Room
import com.example.sharingang.database.room.CacheDatabase
import com.example.sharingang.auth.FakeCurrentUserProvider
import com.example.sharingang.payment.FakePaymentProvider
import com.example.sharingang.imagestore.PlaceholderImageStore
import com.example.sharingang.auth.CurrentUserProvider
import com.example.sharingang.database.cache.CachedItemRepository
import com.example.sharingang.database.cache.CachedUserRepository
import com.example.sharingang.database.repositories.InMemoryItemRepository
import com.example.sharingang.database.repositories.InMemoryUserRepository
import com.example.sharingang.database.repositories.ItemRepository
import com.example.sharingang.database.repositories.UserRepository
import com.example.sharingang.database.room.ItemDao
import com.example.sharingang.database.room.UserDao
import com.example.sharingang.database.store.ItemStore
import com.example.sharingang.database.store.UserStore
import com.example.sharingang.imagestore.ImageStore
import com.example.sharingang.payment.PaymentProvider
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
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
  
    @Singleton
    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore {
        val firestore = Firebase.firestore
        firestore.useEmulator("10.0.2.2", 8080)
        firestore.firestoreSettings = firestoreSettings {
            isPersistenceEnabled = true
        }
        return firestore
    }

    @Provides
    fun providePaymentProvider(): PaymentProvider {
        return FakePaymentProvider()
    }
}

