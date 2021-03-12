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
    fun startsEmpty() {
        val repo: ItemRepository = InMemoryItemRepository()
        runBlocking {
            assert(repo.getAllItems().isEmpty())
            assert(repo.getAllItemsLiveData().getOrAwaitValue().isEmpty())
        }
    }

    @Test
    fun canAddItem() {
        val repo: ItemRepository = InMemoryItemRepository()
        runBlocking {
            val item = generateSampleItem()
            val id = repo.addItem(item)
            assert(id != null)
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun canAddItemThrowsWhenIdNonNull() {
        val repo: ItemRepository = InMemoryItemRepository()
        runBlocking {
            val item = generateSampleItem()
            item.id = "some-id"
            repo.addItem(item)
        }
    }

    @Test
    fun canGetAddedItem() {
        val repo: ItemRepository = InMemoryItemRepository()
        runBlocking {
            val item = generateSampleItem()

            // To be able to compare the items, we have to save the generated id
            item.id = repo.addItem(item)

            assert(repo.getItem(item.id!!) == item)
        }
    }

    @Test
    fun canGetAllAddedItems() {
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
    fun canGetAllAddedItemsWithLiveData() {
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

    @Test
    fun canUpdateItem() {
        val repo: ItemRepository = InMemoryItemRepository()
        runBlocking {
            val item = generateSampleItem()

            item.id = repo.addItem(item)

            val updatedItem = item.copy(description = "updated description")
            repo.updateItem(updatedItem)

            assert(repo.getItem(updatedItem.id!!) == updatedItem)
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun updateItemThrowsExceptionWhenIdNull() {
        val repo: ItemRepository = InMemoryItemRepository()
        runBlocking {
            // New Item with no id
            val item = generateSampleItem()
            repo.updateItem(item)
        }
    }

    @Test
    fun deleteItemActuallyDeletesTheItem() {
        val repo: ItemRepository = InMemoryItemRepository()
        runBlocking {
            val item = generateSampleItem()
            val id = repo.addItem(item)

            assert(repo.deleteItem(id!!))

            assert(repo.getItem(id) == null)
        }
    }

    private fun generateSampleItem(index: Int = 0): Item {
        return Item(title = "My title $index", description = "My description $index")
    }
}