package com.example.pacetrack

import kotlinx.coroutines.flow.Flow

class WorkoutRepository(private val workoutDao: WorkoutDao) {

    val allWorkouts: Flow<List<WorkoutSession>> = workoutDao.getAllWorkouts()

    suspend fun saveWorkout(workout: WorkoutSession): Long {
        return workoutDao.insertWorkout(workout)
    }

    suspend fun deleteWorkout(workout: WorkoutSession) {
        workoutDao.deleteWorkout(workout)
    }
}