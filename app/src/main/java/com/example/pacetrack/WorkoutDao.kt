
package com.example.pacetrack

import androidx.room.Dao
import androidx.room.Insert

// Database operations for saved workouts.
// Room writes the actual SQL code for these functions when the app builds.
@Dao
interface WorkoutDao {

    // Saves a completed workout. Returns the new workout's id.
    @Insert
    suspend fun insertWorkout(workout: WorkoutSession): Long
}