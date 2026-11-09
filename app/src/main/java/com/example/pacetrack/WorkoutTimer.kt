
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


    // Pauses the workout timer without losing elapsed time.
    fun pause() {

        // Only pause if the timer is currently running.
        if (running) {

            // Add the time since the timer was last started.
            accumulatedTime +=
                SystemClock.elapsedRealtime() - startedAt

            // Mark the timer as paused.
            running = false
        }
    }

}

