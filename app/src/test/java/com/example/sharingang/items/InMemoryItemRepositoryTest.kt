package com.example.sharingang.items

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.sharingang.getOrAwaitValue
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test

class InMemoryItemRepositoryTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Test
    fun inMemoryItemRepositoryStartsEmpty() {
        val repo = InMemoryItemRepository()
        runBlocking {
            assert(repo.getAllItems().isEmpty())
        }
    }

    @Test
    fun inMemoryItemRepositoryCanRetrieveStoredData() {
        val repo = InMemoryItemRepository()
        runBlocking {
            val item = Item(title = "My title", description = "My description")
            // To be able to compare the items, we have to save the generated id
            item.id = repo.addItem(item)
            assert(item.id != null)

            assert(repo.getAllItems()[0] == item)
            assert(repo.getItem(item.id!!) == item)
            assert(repo.getAllItemsLiveData().getOrAwaitValue()[0] == item)

            val item2 = Item(title = "My title 2", description = "My description 2")
            item2.id = repo.addItem(item2)
            assert(item2.id != null)

            assert(repo.getAllItems().containsAll(listOf(item, item2)))
            assert(repo.getItem(item2.id!!) == item2)
            assert(repo.getAllItemsLiveData().getOrAwaitValue().containsAll(listOf(item, item2)))
        }
    }
}