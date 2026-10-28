package com.example.pacetrack

import android.R.attr.onClick
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.Card
import androidx.compose.animation.AnimatedVisibility


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
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        for (i in 0 until radio_options.size) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selected == i,
                    onClick = { onStateChanged(i) }
                )

                Text(radio_options[i])
            }
        }
    }
}

@Composable
fun SessionControl(session_button: MutableState<String>) {

    Button(
        onClick = {
            if (session_button.value == "Start Session")
                session_button.value = "Stop Session"
            else
                session_button.value = "Start Session"
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Text(
            text = session_button.value,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
    }
}

@Composable
fun DisplayControls(display_states: MutableList<String>) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        for (i in 0 until display_states.size) {

            val display_name =
                if (i == 0) "Time"
                else if (i == 1) "Distance"
                else "Pace"

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = display_name,
                    fontWeight = FontWeight.Bold
                )

                Button(onClick = {
                    if (display_states[i] == "off")
                        display_states[i] = "on"
                    else
                        display_states[i] = "off"
                }) {
                    Text(display_states[i])
                }
            }
        }
    }

    Button(
        onClick = {
            for (i in 0 until display_states.size) {
                display_states[i] = "off"
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
    ) {
        Text(
            text = "Reset Display Options",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
    }
}

@Composable
fun NewSessionControl(new_session_count: MutableIntState) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "New Sessions",
                fontWeight = FontWeight.Bold
            )

            Text("${new_session_count.intValue} sessions")
        }

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

@Composable
fun GoalControls(
    goal_enabled: MutableState<Boolean>,
    activity_options: List<String>,
    selected_activity: MutableState<Int>,
    goal_distance: MutableState<String>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Enable distance goal",
            fontWeight = FontWeight.Bold
        )

        Checkbox(
            checked = goal_enabled.value,
            onCheckedChange = {
                goal_enabled.value = it
            }
        )
    }

    Text(
        text = "Activity Type",
        fontWeight = FontWeight.Bold
    )

    RadioButtonGroup(
        activity_options,
        selected_activity.value
    ) {
        selected_activity.value = it
    }

    if (goal_enabled.value) {

        Text(
            text = "Distance Target (km)",
            fontWeight = FontWeight.Bold
        )

        TextField(
            value = goal_distance.value,
            onValueChange = {
                goal_distance.value = it
            },
            modifier = Modifier.fillMaxWidth()
        )

        Text("Your distance goal is: ${goal_distance.value} km")
    }
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
    val context = LocalContext.current
    val workoutExpanded = remember { mutableStateOf(false) }
    val workoutActive = remember { mutableStateOf(false) }

    val validGoal = !goal_enabled.value ||
            (goal_distance.value.matches(Regex("[0-9]+(\\.[0-9]+)?")) &&
                    goal_distance.value.toDoubleOrNull()?.let {
                        it > 0 && it.isFinite()
                    } == true)

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),

        Arrangement.spacedBy(12.dp)
    ) {
        WelcomeMessage()

        Button(
            onClick = {
                val intent = Intent(context, HistoryActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("View History")
        }



        if (!workoutActive.value) {
            Button(
                onClick = {
                    workoutExpanded.value = !workoutExpanded.value
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (workoutExpanded.value)
                        "Close Workout Setup"
                    else
                        "Start Workout"
                )
            }
        }



        AnimatedVisibility(visible = workoutExpanded.value) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HorizontalDivider()

                // First Card: Workout Setup
                if (!workoutActive.value) {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Workout Setup",
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
                }

                HorizontalDivider()

                // Second Card: Session Controls
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        Text(
                            text = if (workoutActive.value)
                                "Workout Preview"
                            else
                                "Ready to Begin",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )

                        if (workoutActive.value) {


                            Text(
                                text = "${activity_options[selected_activity.value]} Session",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )

                            HorizontalDivider()

                            Text("Elapsed Time")

                            Text(
                                text = "--:--:--",
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp
                            )

                            HorizontalDivider()

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Distance")
                                    Text(
                                        text = "-- km",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                }

                                Column {
                                    Text("Average Pace")
                                    Text(
                                        text = "-- /km",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                }
                            }


                            if (goal_enabled.value && goal_distance.value.isNotBlank()) {
                                HorizontalDivider()

                                Text(
                                    text = "Distance Goal",
                                    fontWeight = FontWeight.Bold
                                )

                                Text("${goal_distance.value} km")
                            }


                            Text("Preview only: Timer and GPS tracking are not connected yet.")


                            Button(
                                onClick = {
                                    workoutActive.value = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("End Preview")
                            }

                        } else {


                            if (!validGoal) {
                                Text("Enter a valid distance greater than 0 km.")
                            }

                            Button(
                                onClick = {
                                    workoutActive.value = true
                                },
                                enabled = validGoal,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Begin ${activity_options[selected_activity.value]}")
                            }

                        }

                    }
                }
            }
        }

    }
}


