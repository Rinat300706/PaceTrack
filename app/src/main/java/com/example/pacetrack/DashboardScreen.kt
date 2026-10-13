package com.example.pacetrack

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.material3.RadioButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.Button
import androidx.compose.runtime.MutableState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.MutableIntState
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Checkbox
import androidx.compose.material3.TextField
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.material3.HorizontalDivider
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
@Composable
fun WelcomeMessage() {
    Column {
        Text(
            text = "PaceTrack",
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp
        )

        Text("Track your walks and runs")
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


@Composable
fun SessionControl(session_button: MutableState<String>) {
    Button(onClick = {
        if (session_button.value == "Start Session")
            session_button.value = "Stop Session"
        else
            session_button.value = "Start Session"
    }) {
        Text(session_button.value)
    }
}

@Composable
fun DisplayControls(display_states: MutableList<String>) {

    for (i in 0 until display_states.size) {

        val display_name =
            if (i == 0) "Time"
            else if (i == 1) "Distance"
            else "Pace"

        Button(onClick = {
            if (display_states[i] == "off")
                display_states[i] = "on"
            else
                display_states[i] = "off"
        }) {
            Text(display_name + ": " + display_states[i])
        }
    }

    Button(onClick = {
        for (i in 0 until display_states.size) {
            display_states[i] = "off"
        }
    }) {
        Text("Reset Display options")
    }
}

@Composable
fun NewSessionControl(new_session_count: MutableIntState) {

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

@Composable
fun GoalControls(
    goal_enabled: MutableState<Boolean>,
    activity_options: List<String>,
    selected_activity: MutableState<Int>,
    goal_distance: MutableState<String>
) {
    Text("Distance goal enabled: ${goal_enabled.value}")

    Checkbox(
        checked = goal_enabled.value,
        onCheckedChange = {
            goal_enabled.value = it
        }
    )

    Text("Selected activity: ${activity_options[selected_activity.value]}")

    RadioButtonGroup(
        activity_options,
        selected_activity.value
    ) {
        selected_activity.value = it
    }

    Text("Enter your distance goal (km):")

    TextField(
        value = goal_distance.value,
        onValueChange = {
            goal_distance.value = it
        }
    )

    Text("Your distance goal is: ${goal_distance.value} km")
}

@Composable
fun DashboardScreen() {

    val session_button = remember { mutableStateOf("Start Session") }
    val display_states = remember {
        mutableStateListOf("off", "off", "off")
    }
    val new_session_count = remember { mutableIntStateOf(0) }
    val goal_enabled = remember { mutableStateOf(false) }
    val activity_options = listOf("Walk", "Run")
    val selected_activity = remember { mutableStateOf(0) }
    val goal_distance = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),

        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        WelcomeMessage()

        HorizontalDivider()

        Text(
            text = "Session Controls",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        SessionControl(session_button)

        DisplayControls(display_states)

        NewSessionControl(new_session_count)

        HorizontalDivider()

        Text(
            text = "Distance Goal",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        GoalControls(
            goal_enabled,
            activity_options,
            selected_activity,
            goal_distance
        )
    }
}

