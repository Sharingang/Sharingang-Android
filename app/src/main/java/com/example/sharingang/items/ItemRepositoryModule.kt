package com.example.sharingang.items

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class ItemRepositoryModule {

    @Binds
    abstract fun bindItemRepository(
        firestoreItemRepository: FirestoreItemRepository
    ): ItemRepository
}