
package com.example.pacetrack

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.Flow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WorkoutRepository(
        PaceTrackDatabase.getDatabase(application).workoutDao()
    )

    val allWorkouts: Flow<List<WorkoutSession>> = repository.allWorkouts


    fun saveWorkout(workout: WorkoutSession) {
        viewModelScope.launch {
            repository.saveWorkout(workout)
        }
    }

    fun deleteWorkout(workout: WorkoutSession) {
        viewModelScope.launch {
            repository.deleteWorkout(workout)
        }
    }
}