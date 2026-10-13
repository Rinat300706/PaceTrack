package com.example.pacetrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class HistoryActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HistoryScreen()
        }
    }
}