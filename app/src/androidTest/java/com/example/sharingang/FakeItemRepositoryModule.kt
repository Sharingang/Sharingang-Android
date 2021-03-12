package com.example.sharingang

import com.example.sharingang.items.InMemoryItemRepository
import com.example.sharingang.items.ItemRepository
import com.example.sharingang.items.ItemRepositoryModule
import dagger.Binds
import dagger.Module
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(
    components = [ViewModelComponent::class],
    replaces = [ItemRepositoryModule::class]
)
abstract class FakeItemRepositoryModule {

    @Binds
    abstract fun bindItemRepository(
        firestoreItemRepository: InMemoryItemRepository
    ): ItemRepository
}