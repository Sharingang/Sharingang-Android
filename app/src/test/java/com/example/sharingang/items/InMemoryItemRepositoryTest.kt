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

    private fun generateSampleItem(index: Int = 0): Item {
        return Item(title = "My title $index", description = "My description $index")
    }
}