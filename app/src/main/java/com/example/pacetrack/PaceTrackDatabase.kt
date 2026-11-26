package com.example.pacetrack

import androidx.room.Database
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.Room

@Database(
    entities = [WorkoutSession::class],
    version = 1,
    exportSchema = false
)
abstract class PaceTrackDatabase : RoomDatabase() {

    //access to the save/load/delete functions.
    abstract fun workoutDao(): WorkoutDao

    companion object {

        // The single shared database. @Volatile makes every thread see the latest value.
        @Volatile
        private var instance: PaceTrackDatabase? = null

        // Returns the database, creating it the first time it's needed.
        fun getDatabase(context: Context): PaceTrackDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    PaceTrackDatabase::class.java,
                    "pacetrack_database"
                ).build().also { instance = it }
            }
        }
    }
}

