package com.example.sharingang.database.repositories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.sharingang.utils.getOrAwaitValue
import com.example.sharingang.models.Item
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
            assert(repo.getAll().isEmpty())
            assert(repo.items().getOrAwaitValue().isEmpty())
        }
    }

    @Test
    fun canAddItem() {
        val repo: ItemRepository = InMemoryItemRepository()
        runBlocking {
            val item = generateSampleItem()
            val id = repo.set(item)
            assert(id != null)
        }
    }

    @Test
    fun canGetAddedItem() {
        val repo: ItemRepository = InMemoryItemRepository()
        runBlocking {
            val item = generateSampleItem()

            val id = repo.set(item)!!

            assert(repo.get(id) == item.copy(id = id))
        }
    }

    @Test
    fun canGetAllAddedItems() {
        val repo: ItemRepository = InMemoryItemRepository()
        runBlocking {
            val items = List(5) { generateSampleItem(it) }

            val addedItems = items.map { it.copy(id = repo.set(it)) }

            assert(repo.getAll().containsAll(addedItems))
        }
    }

    @Test
    fun canGetAllAddedItemsWithLiveData() {
        val repo: ItemRepository = InMemoryItemRepository()
        runBlocking {
            val itemsLiveData = repo.items()
            assert(itemsLiveData.getOrAwaitValue().isEmpty())

            val items = List(5) { generateSampleItem(it) }

            val addedItems = items.map { it.copy(id = repo.set(it)) }

            assert(itemsLiveData.getOrAwaitValue().containsAll(addedItems))
        }
    }

    @Test
    fun canUpdateItem() {
        val repo: ItemRepository = InMemoryItemRepository()
        runBlocking {
            val item = generateSampleItem()

            val addedItem = item.copy(id = repo.set(item))

            val updatedItem = addedItem.copy(description = "updated description")
            repo.set(updatedItem)

            assert(repo.get(updatedItem.id!!) == updatedItem)
        }
    }

    @Test
    fun deleteItemActuallyDeletesTheItem() {
        val repo: ItemRepository = InMemoryItemRepository()
        runBlocking {
            val item = generateSampleItem()
            val id = repo.set(item)

            assert(repo.delete(id!!))

            assert(repo.get(id) == null)
        }
    }

    private fun generateSampleItem(index: Int = 0): Item {
        return Item(title = "My title $index", description = "My description $index")
    }
}

