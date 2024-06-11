package com.app.qaimobile.util.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.app.qaimobile.data.remote.ApiService
import com.app.qaimobile.data.model.network.LocationUpdateRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class LocationManager @Inject constructor(
    private val context: Context,
    private val apiService: ApiService
) {
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 10000
        fastestInterval = 5000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private var locationCallback: LocationCallback? = null
    private var onLocationUpdated: ((Location) -> Unit)? = null

    fun startLocationUpdates(token: String, callback: (Location) -> Unit) {
        onLocationUpdated = callback
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    onLocationUpdated?.invoke(location)
                    Log.d("LocationManager", "Location updated: $location")
                    sendLocationToBackend(token, location)
                }
            }
        }

        val locationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val backgroundLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION)

        if (locationPermission == PackageManager.PERMISSION_GRANTED && backgroundLocationPermission == PackageManager.PERMISSION_GRANTED) {
            Log.d("LocationManager", "Starting location updates")
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback as LocationCallback, Looper.getMainLooper())
        } else {
            Log.d("LocationManager", "Location permission not granted")
        }
    }

    private fun sendLocationToBackend(token: String, location: Location) {
        val locationUpdateRequest = LocationUpdateRequest(
            permissionStatus = "granted",
            locationType = "foreground",
            latitude = location.latitude,
            longitude = location.longitude,
            altitude = location.altitude
        )
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.updateLocation(locationUpdateRequest, "Bearer $token")
                if (response.isSuccessful) {
                    Log.d("LocationManager", "Location update sent successfully")
                } else {
                    Log.e("LocationManager", "Failed to send location update: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("LocationManager", "Error sending location update", e)
            }
        }
    }

    fun stopLocationUpdates() {
        locationCallback?.let { callback ->
            Log.d("LocationManager", "Stopping location updates")
            fusedLocationClient.removeLocationUpdates(callback)
        }
        locationCallback = null
        onLocationUpdated = null
    }
}
