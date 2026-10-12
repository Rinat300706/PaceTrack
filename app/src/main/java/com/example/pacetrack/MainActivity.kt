package com.example.pacetrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import android.util.Log

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WelcomeMessage()
            }

        Log.i("PaceTrack", "PaceTrack MainActivity created")
    }
}

@Composable
fun WelcomeMessage() {
    Text("Welcome to PaceTrack")
}



