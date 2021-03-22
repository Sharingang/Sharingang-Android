package com.example.sharingang.items

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import javax.inject.Singleton

/**
 * Tells Hilt to use an instance of FirestoreItemStore when we require an ItemStore
 */
@Module
@InstallIn(ViewModelComponent::class)
object ItemRepositoryModule {

    @Provides
    fun provideItemRepository(): ItemRepository {
        return CachedItemRepository(FirestoreItemStore())
    }
}