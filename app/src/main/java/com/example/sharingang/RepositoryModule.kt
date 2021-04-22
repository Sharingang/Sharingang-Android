package com.example.sharingang

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.example.sharingang.items.*
import com.example.sharingang.users.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
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
object RepositoryModule {

    @Singleton
    @Provides
    fun provideItemStore(firestore: FirebaseFirestore): ItemStore {
        return FirestoreItemStore(firestore)
    }

    @Singleton
    @Provides
    fun provideUserStore(firestore: FirebaseFirestore): UserStore {
        return FirestoreUserStore(firestore)
    }

    @Singleton
    @Provides
    fun provideItemRepository(repo: CachedItemRepository): ItemRepository {
        return repo
    }

    @Singleton
    @Provides
    fun provideUserRepository(repo: CachedUserRepository): UserRepository {
        return repo
    }

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext app: Context): CacheDatabase {
        return Room.databaseBuilder(app, CacheDatabase::class.java, "cache_database").build()
    }

    @Singleton
    @Provides
    fun provideItemDao(db: CacheDatabase): ItemDao {
        return db.itemDao
    }

    @Singleton
    @Provides
    fun provideUserDao(db: CacheDatabase): UserDao {
        return db.userDao
    }

    @Singleton
    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore {
        val firestore = Firebase.firestore
        if (BuildConfig.DEBUG) {
            Log.d("RepositoryModule", "Using Firestore emulator.")
            // 10.0.2.2 is the special IP address to connect to the 'localhost' of
            // the host computer from an Android emulator.
            firestore.useEmulator("10.0.2.2", 8080)

            // Because the Firebase emulator doesn't persist data, we disable the local persistence
            // to avoid conflicting data.
            firestore.firestoreSettings = firestoreSettings {
                isPersistenceEnabled = false
            }
        } else {
            Log.d("RepositoryModule", "Using production Firestore.")
        }

        return firestore
    }

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        val auth = Firebase.auth
        if (BuildConfig.DEBUG) {
            Log.d("RepositoryModule", "Using FirebaseAuth emulator.")
            auth.useEmulator("10.0.2.2", 9099)
        } else {
            Log.d("RepositoryModule", "Using production FirebaseAuth.")
        }
        return auth
    }

    @Singleton
    @Provides
    fun provideCurrentUserProvider(currentUserProvider: FirestoreCurrentUserProvider): CurrentUserProvider {
        return currentUserProvider
    }
}
