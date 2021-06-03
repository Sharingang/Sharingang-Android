package com.example.sharingang.dependencyinjection

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.example.sharingang.BuildConfig
import com.example.sharingang.database.room.CacheDatabase
import com.example.sharingang.imagestore.FirebaseImageStore
import com.example.sharingang.imagestore.ImageStore
import com.example.sharingang.auth.CurrentUserProvider
import com.example.sharingang.auth.FirebaseCurrentUserProvider
import com.example.sharingang.database.cache.CachedItemRepository
import com.example.sharingang.database.cache.CachedUserRepository
import com.example.sharingang.database.firestore.FirestoreItemStore
import com.example.sharingang.database.firestore.FirestoreUserStore
import com.example.sharingang.database.repositories.ItemRepository
import com.example.sharingang.database.repositories.UserRepository
import com.example.sharingang.database.room.ItemDao
import com.example.sharingang.database.room.UserDao
import com.example.sharingang.database.store.ItemStore
import com.example.sharingang.database.store.UserStore
import com.example.sharingang.payment.PaymentProvider
import com.example.sharingang.payment.StripePaymentProvider
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
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

    private const val emulatorIP = "10.0.2.2"

    private val useEmulator: Boolean
        get() {
            return BuildConfig.DEBUG
        }

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
        if (useEmulator) {
            Log.d("RepositoryModule", "Using Firestore emulator.")
            // 10.0.2.2 is the special IP address to connect to the 'localhost' of
            // the host computer from an Android emulator.
            firestore.useEmulator(emulatorIP, 8080)
        } else {
            Log.d("RepositoryModule", "Using production Firestore.")
        }

        return firestore
    }

    @Singleton
    @Provides
    fun provideFirebaseStorage(): FirebaseStorage {
        val storage = Firebase.storage
        if (useEmulator) {
            Log.d("RepositoryModule", "Using FirebaseStorage emulator.")
            storage.useEmulator(emulatorIP, 9199)
        } else {
            Log.d("RepositoryModule", "Using production FirebaseStorage.")
        }
        return storage
    }

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        val auth = Firebase.auth
        if (useEmulator) {
            Log.d("RepositoryModule", "Using FirebaseAuth emulator.")
            auth.useEmulator(emulatorIP, 9099)
        } else {
            Log.d("RepositoryModule", "Using production FirebaseAuth.")
        }
        return auth
    }

    @Singleton
    @Provides
    fun provideFirebaseAuthUI(): AuthUI {
        val authUI = AuthUI.getInstance()
        if (useEmulator) {
            Log.d("RepositoryModule", "Using FirebaseAuth emulator.")
            authUI.useEmulator(emulatorIP, 9099)
        } else {
            Log.d("RepositoryModule", "Using production FirebaseAuth.")
        }
        return authUI
    }

    @Singleton
    @Provides
    fun provideFirebaseFunctions(): FirebaseFunctions {
        val functions = Firebase.functions("europe-west6") // Zurich
        if (useEmulator) {
            Log.d("RepositoryModule", "Using FirebaseFunctions emulator.")
            functions.useEmulator(emulatorIP, 5001)
        } else {
            Log.d("RepositoryModule", "Using production FirebaseFunctions.")
        }
        return functions
    }

    @Singleton
    @Provides
    fun provideCurrentUserProvider(currentUserProvider: FirebaseCurrentUserProvider): CurrentUserProvider {
        return currentUserProvider
    }

    @Singleton
    @Provides
    fun provideImageStore(imageStore: FirebaseImageStore): ImageStore {
        return imageStore
    }

    @Provides
    fun providePaymentProvider(stripePaymentProvider: StripePaymentProvider): PaymentProvider {
        return stripePaymentProvider
    }
}
