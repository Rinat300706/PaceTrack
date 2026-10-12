package com.example.pacetrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.mutableStateOf
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Icon


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column {
                WelcomeMessage()

                Button(onClick = {
                    if(session_button.value == "Start Session")
                        session_button.value = "Stop Session"
                    else
                        session_button.value = "Start Session"
                }) {
                    Text(session_button.value)
                }

                for (i in 0 until display_states.size){

                    val display_name =
                        if (i == 0) "Time"
                        else if (i == 1) "Distance"
                        else "Pace"

                    Button(onClick = {
                        if(display_states[i] == "off")
                            display_states[i] = "on"
                        else
                            display_states[i] = "off"
                    }) {
                        Text(display_name + ": " + display_states[i])
                    }
                }

                Button(onClick = {
                    for(i in 0 until display_states.size){
                        display_states[i] = "off"
                    }
                }) {
                    Text("Reset Display options")
                }

                Text("New Sessions: ${new_session_count.intValue}")

                FloatingActionButton(onClick = {
                    new_session_count.intValue++
                }) {
                    Icon(
                        imageVector = add,
                        contentDescription = "New Session",
                        tint = Color.White
                    )
                }

            }
        }

        Log.i("PaceTrack", "PaceTrack MainActivity created")
    }
    var session_button = mutableStateOf("Start Session")
    var display_states = mutableStateListOf("off", "off", "off")
    var new_session_count = mutableIntStateOf(0)
}

@Composable
fun WelcomeMessage() {
    Column {
        Text("Welcome to PaceTrack")
        Text("Track your walks")
        Text("Track your runs")


        Row {
            Text("Walk ")
            Text("Run ")
            Text("Progress ")
        }
    }
}



