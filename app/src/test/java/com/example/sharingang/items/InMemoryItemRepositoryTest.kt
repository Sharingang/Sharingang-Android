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
            assert(repo.items().getOrAwaitValue().isEmpty())
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
            val item = generateSampleItem().copy(id = "some-id")
            repo.addItem(item)
        }
    }

    @Test
    fun canGetAddedItem() {
        val repo: ItemRepository = InMemoryItemRepository()
        runBlocking {
            val item = generateSampleItem()

            val id = repo.addItem(item)!!

            assert(repo.getItem(id) == item.copy(id = id))
        }
    }

    @Test
    fun canGetAllAddedItems() {
        val repo: ItemRepository = InMemoryItemRepository()
        runBlocking {
            val items = List(5) { generateSampleItem(it) }

            val addedItems = items.map { it.copy(id = repo.addItem(it)) }

            assert(repo.getAllItems().containsAll(addedItems))
        }
    }

    @Test
    fun canGetAllAddedItemsWithLiveData() {
        val repo: ItemRepository = InMemoryItemRepository()
        runBlocking {
            val itemsLiveData = repo.items()
            assert(itemsLiveData.getOrAwaitValue().isEmpty())

            val items = List(5) { generateSampleItem(it) }

            val addedItems = items.map { it.copy(id = repo.addItem(it)) }

            assert(itemsLiveData.getOrAwaitValue().containsAll(addedItems))
        }
    }

    @Test
    fun canUpdateItem() {
        val repo: ItemRepository = InMemoryItemRepository()
        runBlocking {
            val item = generateSampleItem()

            val addedItem = item.copy(id = repo.addItem(item))

            val updatedItem = addedItem.copy(description = "updated description")
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