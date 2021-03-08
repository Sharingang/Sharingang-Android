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
        val model = ItemsViewModel()
        val value = model.items.getOrAwaitValue()
        assert(value.isEmpty())
    }

    @Test
    fun weCanAddAndRetrieveItems() {
        val model = ItemsViewModel()
        val items = listOf(Item("A"), Item("B"))
        model.addItem(items[0])
        model.addItem(items[1])
        assert(model.items.getOrAwaitValue() == items)
    }
}