package com.mobdeve.awitize.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*
import java.util.concurrent.Executors

private const val TAG = "LocationHelper"

@SuppressLint("MissingPermission")
class LocationHelper(context: Context) {

    private var context : Context? = context

    private var fusedLocationProviderClient : FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private var locationCallback = object: LocationCallback() {
        override fun onLocationResult(p0: LocationResult?) {
            if(p0 == null) {
                return
            }
            val locationUpdater = Runnable {
                val lat = p0.lastLocation.latitude
                val lon = p0.lastLocation.longitude
                Log.d(TAG, "onLocationResult: $lat $lon")
                if(Geocoder.isPresent()){
                    val geocoder = Geocoder(context)
                    try{
                        val res = geocoder.getFromLocation(lat, lon, 1)
                        if(res.size > 0){
                            val newCountry = res.first().countryName
                            if(newCountry !== country.value){
                                country.postValue(newCountry)
                            }
                        }
                    } catch (e : Exception){

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
        fusedLocationProviderClient.lastLocation.addOnCompleteListener {
            if(it.isSuccessful){
                if(it.result == null){
                    country.value = null
                    return@addOnCompleteListener
                }
                val loc = it.result
                val locationUpdater = Runnable {
                    val lat = loc.latitude
                    val lon = loc.longitude
                    Log.d(TAG, "onLocationResult: $lat $lon")
                    if(Geocoder.isPresent()){
                        val geocoder = Geocoder(context)
                        try{
                            val res = geocoder.getFromLocation(lat, lon, 1)
                            if(res.size > 0){
                                val newCountry = res.first().countryName
                                if(newCountry !== country.value){
                                    country.postValue(newCountry)
                                }
                            }
                        } catch (e : Exception){

                        }
                    }
                }
                Executors.newSingleThreadExecutor().execute(locationUpdater)
            }
        }

        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
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

    companion object{
        private var instance : LocationHelper? = null

        fun getInstance(context: Context) : LocationHelper?{
            if(instance == null){
                instance = LocationHelper(context)
            }
            return instance
        }

        fun destroy(){
            instance?.destroy()
            instance = null
        }
    }
}