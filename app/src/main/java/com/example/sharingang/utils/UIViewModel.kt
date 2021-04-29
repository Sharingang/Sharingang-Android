package com.example.sharingang.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UIViewModel : ViewModel() {
    private val _ascendingDescendingPosition = MutableLiveData(0)
    val ascendingDescendingPosition: LiveData<Int>
        get() = _ascendingDescendingPosition

    private val _orderByPosition = MutableLiveData(0)
    val orderByPosition: LiveData<Int>
        get() = _orderByPosition

    private val _hasBeenOrderedOnce = MutableLiveData(false)

    fun orderBy(): Boolean {
        val returnValue = _hasBeenOrderedOnce.value!!
        _hasBeenOrderedOnce.postValue(true)
        return returnValue
    }

    fun setAscendingDescendingPosition(position: Int) {
        _ascendingDescendingPosition.postValue(position)
    }

    fun setOrderByPosition(position: Int) {
        _orderByPosition.postValue(position)
    }
}