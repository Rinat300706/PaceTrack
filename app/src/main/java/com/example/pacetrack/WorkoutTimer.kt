
package com.example.pacetrack

import android.os.SystemClock

class WorkoutTimer {

    private var accumulatedTime = 0L


    private var startedAt = 0L


    private var running = false


    private var started = false


    fun start() {
        accumulatedTime = 0L
        startedAt = SystemClock.elapsedRealtime()
        running = true
        started = true
    }
}

