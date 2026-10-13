package com.example.pacetrack

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.material3.RadioButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

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

@Composable
fun RadioButtonGroup(
    radio_options: List<String>,
    selected: Int,
    onStateChanged: (Int) -> Unit
) {
    Column {
        for (i in 0 until radio_options.size) {
            Row {
                RadioButton(
                    selected = selected == i,
                    onClick = { onStateChanged(i) }
                )

                Text(
                    radio_options[i],
                    Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    }
}