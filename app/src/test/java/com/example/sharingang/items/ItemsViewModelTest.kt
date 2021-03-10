package com.example.sharingang.items

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.sharingang.getOrAwaitValue
import org.junit.Rule
import org.junit.Test

class ItemsViewModelTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Test
    fun itemsViewModelStartsEmpty() {
        val model = ItemsViewModel(InMemoryItemRepository())
        val value = model.items.getOrAwaitValue()
        assert(value.isEmpty())
    }

    /*
    @Test
    fun weCanAddAndRetrieveItems() {
        val model = ItemsViewModel(MockItemRepository())
        val items = listOf(Item(description = "A"), Item(description = "B"))
        model.addItem(items[0])
        model.addItem(items[1])

        assert(model.items.getOrAwaitValue().map { it.description } == items.map { it.description })
    }
     */
}