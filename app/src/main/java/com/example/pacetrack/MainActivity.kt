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




            }
            }

        Log.i("PaceTrack", "PaceTrack MainActivity created")
    }
    var session_button = mutableStateOf("Start Session")
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



