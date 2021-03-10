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
}