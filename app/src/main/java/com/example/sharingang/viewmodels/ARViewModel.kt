package com.example.sharingang.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.sharingang.models.Heading
import com.example.sharingang.models.Location

private fun <A, B, C> transformBoth(
    liveA: LiveData<A>,
    liveB: LiveData<B>,
    f: (A, B) -> C
): LiveData<C> =
    Transformations.switchMap(liveA) { a ->
        Transformations.map(liveB) { b ->
            f(a, b)
        }
    }

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

    // We have this private to hide mutability
    private val _heading: MutableLiveData<Heading> = MutableLiveData(Heading(0.0))

    /**
     * Represents the current heading of the device, which we use to orient ourselves
     */
    val heading: LiveData<Heading>
        get() = _heading

    /**
     * Set the current heading of the user.
     */
    fun setHeading(heading: Heading) {
        _heading.value = heading
    }

    // We have this private to hide mutability
    private val _location: MutableLiveData<Location> = MutableLiveData(Location(0.0, 0.0))

    /**
     * The current location of the user
     */
    val location: LiveData<Location>
        get() = _location

    /**
     * Set the location of the current user.
     */
    fun setLocation(location: Location) {
        _location.value = location
    }


    /**
     * The current distance in meters between us and the item
     */
    val distance: LiveData<Double> =
        transformBoth(location, itemLocation) { a, b -> a.crowDistance(b) }

    /**
     * The required heading between our location and their location
     */
    val requiredHeading: LiveData<Heading> =
        transformBoth(location, itemLocation) { a, b -> a.requiredHeading(b) }
}