package com.mobdeve.awitize.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*

private const val TAG = "LocationHelper"

class LocationHelper(context: Context) {

    private var context : Context? = context

    private var fusedLocationProviderClient : FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private var locationCallback = object: LocationCallback() {
        override fun onLocationResult(p0: LocationResult?) {
            if(p0 == null){
                country.value = null
                return
            }
            val geocoder = Geocoder(context)
            val lat = p0.lastLocation.latitude
            val lon = p0.lastLocation.longitude
            country.value = geocoder.getFromLocation(lat, lon, 1)?.first()?.countryName
        }

        override fun onLocationAvailability(p0: LocationAvailability) {
            Log.d(TAG, "onLocationAvailability: ${p0.isLocationAvailable}")
        }
    }
    private var locationRequest = LocationRequest.create()
    private var country : MutableLiveData<String?> = MutableLiveData(null)

    val currentCountry : LiveData<String?>
        get() = country

    init {
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 1000
        startLocationUpdates()
    }

    fun destroy(){
        context = null
        stopLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun stopLocationUpdates(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

}