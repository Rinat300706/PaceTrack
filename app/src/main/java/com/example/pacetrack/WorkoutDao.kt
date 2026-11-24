
package com.example.pacetrack

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

//Database operations for saved workouts.
//Room writes the actual SQL code for these functions when the app builds.
@Dao
interface WorkoutDao {

    //Saves a completed workout. Returns the new workout's id.
    @Insert
    suspend fun insertWorkout(workout: WorkoutSession): Long


    //Gets every saved workout
    //History screen updates automatically when a workout is added.
    @Query("SELECT * FROM workout_sessions ORDER BY startTime DESC")
    fun getAllWorkouts(): Flow<List<WorkoutSession>>
}