package com.example.pacetrack

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_sessions")
data class WorkoutSession(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val activityType: String,

    val startTime: Long,

    val durationMillis: Long,

    val distanceMetres: Double,


    val averagePaceSecondsPerKm: Long? = null,

    val distanceGoalKm: Double? = null
)

