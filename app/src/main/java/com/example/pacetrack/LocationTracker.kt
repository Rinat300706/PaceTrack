
package com.example.pacetrack

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.content.ContextCompat

class LocationTracker(
    context: Context,
    private val onLocationUpdate: (Location) -> Unit
) {

    private val appContext = context.applicationContext

    // Get access to Androids location system.
    private val locationManager =
        appContext.getSystemService(
            Context.LOCATION_SERVICE
        ) as LocationManager

    //Listen for new GPS coordinates.
    private val locationListener = object : LocationListener {

        override fun onLocationChanged(location : Location) {

            //Pass the latest location to the workout
            onLocationUpdate(location)
        }

        // Check whether precise location permission is granted.
        fun hasLocationPermission(): Boolean {

            return ContextCompat.checkSelfPermission(
                appContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
}
