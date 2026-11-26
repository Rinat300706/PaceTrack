
package com.example.pacetrack

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.content.ContextCompat
import android.os.Looper

class LocationTracker(
    context: Context,
    private val onLocationUpdate: (Location, Float) -> Unit
) {

    private val appContext = context.applicationContext

    // Get access to Androids location system.
    private val locationManager =
        appContext.getSystemService(
            Context.LOCATION_SERVICE
        ) as LocationManager


    // Remember the previous GPS location.
    private var lastLocation: Location? = null

    // Total distance travelled in metres.
    var totalDistanceMeters = 0f
        private set
    // Store the GPS points visited during the workout.
    private val routePoints = mutableListOf<Location>()


    //Listen for new GPS coordinates.
    private val locationListener = object : LocationListener {


        override fun onLocationChanged(location: Location) {

            // Ignore GPS readings that are not accurate enough.
            if (!location.hasAccuracy() || location.accuracy > 30f) {
                return
            }

            // Calculate distance from the previous GPS reading.

            lastLocation?.let { previousLocation ->

                val distance = previousLocation.distanceTo(location)

                // Calculate the time between GPS readings in seconds.
                val elapsedSeconds =
                    (location.elapsedRealtimeNanos -
                            previousLocation.elapsedRealtimeNanos) / 1_000_000_000f

                // Ignore readings with invalid time differences.
                if (elapsedSeconds <= 0f) {
                    return
                }

                // Calculate the speed between the two GPS points.
                val speedMetersPerSecond = distance / elapsedSeconds

                // Ignore unusually large GPS jumps.
                if (speedMetersPerSecond > 8f) {
                    return
                }

                totalDistanceMeters += distance
            }

            // Save this GPS point for the workout route.
            routePoints.add(Location(location))
            // Remember this location for the next update.
            lastLocation = location

            // Pass the latest location to the workout.
            onLocationUpdate(location, totalDistanceMeters)
        }

    }
    // Check whether precise location permission is granted.
    fun hasLocationPermission(): Boolean {

        return ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Check whether GPS is enabled on the phone.
    fun isGpsEnabled(): Boolean {

        return locationManager.isProviderEnabled(
            LocationManager.GPS_PROVIDER
        )
    }

    // Start receiving GPS location updates.
    fun start(): Boolean {

        // Do not start if permission or GPS is unavailable.
        if (!hasLocationPermission() || !isGpsEnabled()) {
            return false
        }

        return try {

            // Request GPS updates approximately every 2 seconds.
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                2000L,
                0f,
                locationListener,
                Looper.getMainLooper()
            )

            true

        } catch (exception: SecurityException) {
            false
        }
    }

    // Stop receiving GPS updates.
    fun stop() {

        // Remove the registered GPS listener.
        locationManager.removeUpdates(locationListener)
        lastLocation = null
    }

    // Reset the distance when beginning a new workout.
    fun resetDistance() {

        totalDistanceMeters = 0f
        lastLocation = null
        routePoints.clear()
    }

    // Return a copy of the GPS route recorded so far.
    fun getRoutePoints(): List<Location> {
        return routePoints.toList()
    }

}

