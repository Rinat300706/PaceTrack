package com.example.pacetrack

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [WorkoutSession::class],
    version = 1,
    exportSchema = false
)
abstract class PaceTrackDatabase : RoomDatabase() {

    //access to the save/load/delete functions.
    abstract fun workoutDao(): WorkoutDao
}