package com.example.pacetrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column {
                WelcomeMessage()

                SessionControl(session_button)

                DisplayControls(display_states)

                NewSessionControl(new_session_count)

                GoalControls(
                    goal_enabled,
                    activity_options,
                    selected_activity,
                    goal_distance
                )
            }
        }

        Log.i("PaceTrack", "PaceTrack MainActivity created")
    }
    var session_button = mutableStateOf("Start Session")
    var display_states = mutableStateListOf("off", "off", "off")
    var new_session_count = mutableIntStateOf(0)
    var goal_enabled = mutableStateOf(false)
    val activity_options = listOf("Walk", "Run")
    var selected_activity = mutableStateOf(0)
    var goal_distance = mutableStateOf("")

}




