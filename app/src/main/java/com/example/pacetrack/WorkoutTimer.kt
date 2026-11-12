package com.example.pacetrack

import android.os.SystemClock

// Tracks the elapsed time of a workout.
// The clock can be replaced in unit tests so time can be simulated.
class WorkoutTimer(
    private val clock: () -> Long = { SystemClock.elapsedRealtime() }
) {

    // Stores the total time recorded before pausing.
    private var accumulatedTime = 0L

    // Records when the current running period began.
    private var startedAt = 0L

    // Checks whether the timer is currently running.
    private var running = false

    // Checks whether a workout has started.
    private var started = false

    // Starts a new workout and resets the timer to zero.
    fun start() {
        accumulatedTime = 0L
        startedAt = clock()
        running = true
        started = true
    }

    // Pauses the workout timer without losing elapsed time.
    fun pause() {

        // Only pause if the timer is running.
        if (running) {

            // Save the time completed since starting or resuming.
            accumulatedTime += clock() - startedAt

            // Mark the timer as paused.
            running = false
        }
    }

    // Resumes the workout timer after it has been paused.
    fun resume() {

        // Only resume if the workout has started
        // and the timer is currently paused.
        if (started && !running) {

            // Record when the workout resumes.
            startedAt = clock()

            // Mark the timer as running again.
            running = true
        }
    }

    // Returns the total elapsed workout time in milliseconds.
    fun getElapsedTime(): Long {

        // If the timer is running, include the current running period.
        return if (running) {
            accumulatedTime + (clock() - startedAt)
        } else {
            // If paused or finished, return the saved duration.
            accumulatedTime
        }
    }

    // Checks whether the timer is currently counting.
    fun isRunning(): Boolean = running
}
