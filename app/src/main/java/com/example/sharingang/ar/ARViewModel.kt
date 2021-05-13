package com.example.sharingang.ar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * ARViewModel contains the logic for our AR class.
 *
 * As we receive data from various sensor classes, we immediately feed it into this class,
 * which calculates the corresponding data we should display.
 */
class ARViewModel : ViewModel() {
    // This is private and mutable, to get access to mutation
    private val _itemLocation: MutableLiveData<Location> = MutableLiveData(Location(0.0, 0.0))

    /**
     * The location of the item we're interested in.
     */
    val itemLocation: LiveData<Location>
        get() = _itemLocation

    /**
     * setItemLocation set's the current item's location so it can be located
     *
     * @param location the location of where the item location is located
     *
     * @return nothing, but this has the effect of setting the location of the located item
     */
    fun setItemLocation(location: Location) {
        _itemLocation.value = location
    }
}