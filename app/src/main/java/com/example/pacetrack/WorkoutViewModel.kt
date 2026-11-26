
package com.example.pacetrack

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.Flow

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WorkoutRepository(
        PaceTrackDatabase.getDatabase(application).workoutDao()
    )

    val allWorkouts: Flow<List<WorkoutSession>> = repository.allWorkouts
}