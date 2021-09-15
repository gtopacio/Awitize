package com.mobdeve.awitize.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*
import java.io.IOException
import java.util.concurrent.Executors

private const val TAG = "LocationHelper"

@SuppressLint("MissingPermission")
class LocationHelper(context: Context) {

    private var context : Context? = context

    private var fusedLocationProviderClient : FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private var locationCallback = object: LocationCallback() {
        override fun onLocationResult(p0: LocationResult?) {
            val locationUpdater = object : Runnable{
                override fun run() {
                    if(p0 == null){
                        country.postValue(null)
                        return
                    }
                    val lat = p0.lastLocation.latitude
                    val lon = p0.lastLocation.longitude
                    if(Geocoder.isPresent()){
                        val geocoder = Geocoder(context)
                        try{
                            val res = geocoder.getFromLocation(lat, lon, 1)
                            if(res.size > 0){
                                country.postValue(res.first().countryName)
                            }
                        }
                        catch (e : Exception){

                        }
                    }
                }
            }
            Executors.newSingleThreadExecutor().execute(locationUpdater)
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
        locationRequest.interval = 3000
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