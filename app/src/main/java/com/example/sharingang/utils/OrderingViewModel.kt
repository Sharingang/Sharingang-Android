package com.example.sharingang.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * A class containing the currently selected positions of the spinners in the main page, so that when leaving ItemsListFragment the ordering remains unchanged.
 */
class OrderingViewModel : ViewModel() {

    // contains the spinner position containing whether we're ordering the items in ascending or descending order
    private val _ascendingDescendingPosition = MutableLiveData(0)
    val ascendingDescendingPosition: LiveData<Int>
        get() = _ascendingDescendingPosition

    // contains the spinner position containing how we're currently ordering the items (by date, price, name or category)
    private val _orderByPosition = MutableLiveData(0)
    val orderByPosition: LiveData<Int>
        get() = _orderByPosition

    fun setAscendingDescendingPosition(position: Int) {
        _ascendingDescendingPosition.value = position
    }

    fun setOrderByPosition(position: Int) {
        _orderByPosition.value = position
    }
}