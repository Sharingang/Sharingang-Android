package com.example.sharingang.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UIViewModel : ViewModel() {

    // contains the spinner position containing we're ordering the items in ascending or descending position
    private val _ascendingDescendingPosition = MutableLiveData(0)
    val ascendingDescendingPosition: LiveData<Int>
        get() = _ascendingDescendingPosition

    // contains the spinner position containing how we're currently ordering the items
    private val _orderByPosition = MutableLiveData(0)
    val orderByPosition: LiveData<Int>
        get() = _orderByPosition

    fun setAscendingDescendingPosition(position: Int) {
        _ascendingDescendingPosition.postValue(position)
    }

    fun setOrderByPosition(position: Int) {
        _orderByPosition.postValue(position)
    }
}