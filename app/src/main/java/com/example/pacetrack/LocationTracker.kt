
package com.example.pacetrack

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager

class LocationTracker(
    context: Context,
    private val onLocationUpdate: (Location) -> Unit
) {

    // Get access to Androids location system.
    private val locationManager =
        context.applicationContext.getSystemService(
            Context.LOCATION_SERVICE
        ) as LocationManager

    //Listen for new GPS coordinates.
    private val locationListener = object : LocationListener {

        override fun onLocationChanged(location : Location) {

            //Pass the latest location to the workout
            onLocationUpdate(location)
        }
    }
}
