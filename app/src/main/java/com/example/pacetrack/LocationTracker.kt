
package com.example.pacetrack

import android.content.Context
import android.location.LocationManager

class LocationTracker(context: Context) {

    // Get access to Androids location system.
    private val locationManager =
        context.applicationContext.getSystemService(
            Context.LOCATION_SERVICE
        ) as LocationManager

}
