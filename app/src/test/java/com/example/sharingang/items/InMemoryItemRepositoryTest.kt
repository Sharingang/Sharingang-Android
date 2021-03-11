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
        val repo: ItemRepository = InMemoryItemRepository()
        runBlocking {
            assert(repo.getAllItems().isEmpty())
            assert(repo.getAllItemsLiveData().getOrAwaitValue().isEmpty())
        }
    }

    @Test
    fun inMemoryItemRepositoryCanAddItem() {
        val repo: ItemRepository = InMemoryItemRepository()
        runBlocking {
            val item = generateSampleItem()
            val id = repo.addItem(item)
            assert(id != null)
        }
    }

    @Test
    fun inMemoryItemRepositoryCanGetAddedItem() {
        val repo: ItemRepository = InMemoryItemRepository()
        runBlocking {
            val item = generateSampleItem()

            // To be able to compare the items, we have to save the generated id
            item.id = repo.addItem(item)

            assert(repo.getItem(item.id!!) == item)
        }
    }

    @Test
    fun inMemoryItemRepositoryCanGetAllAddedItems() {
        val repo: ItemRepository = InMemoryItemRepository()
        runBlocking {
            val items = List(5) { generateSampleItem(it) }

            for (item in items) {
                item.id = repo.addItem(item)
            }

            assert(repo.getAllItems().containsAll(items))
        }
    }

    @Test
    fun inMemoryItemRepositoryCanGetAllAddedItemsWithLiveData() {
        val repo: ItemRepository = InMemoryItemRepository()
        runBlocking {
            val itemsLiveData = repo.getAllItemsLiveData()
            assert(itemsLiveData.getOrAwaitValue().isEmpty())

            val items = List(5) { generateSampleItem(it) }

            for (item in items) {
                item.id = repo.addItem(item)
            }

            assert(itemsLiveData.getOrAwaitValue().containsAll(items))
        }
    }

    private fun generateSampleItem(index: Int = 0): Item {
        return Item(title = "My title $index", description = "My description $index")
    }
}