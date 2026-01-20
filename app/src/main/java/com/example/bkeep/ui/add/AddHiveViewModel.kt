package com.example.bkeep.ui.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lib.data.location.PickedLocation

class AddHiveViewModel : ViewModel() {

    private val _pickedLocation = MutableLiveData<PickedLocation?>()
    val pickedLocation: LiveData<PickedLocation?> = _pickedLocation

    fun setLocation(lat: Double, lng: Double) {
        _pickedLocation.value = PickedLocation(lat, lng)
    }

    fun clearLocation() {
        _pickedLocation.value = null
    }
}
