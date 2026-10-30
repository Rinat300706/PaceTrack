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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.TextField
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.OutlinedButton
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType


@Composable
fun WelcomeMessage() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "PaceTrack",
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp
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
fun GoalControls(
    goal_enabled: MutableState<Boolean>,
    activity_options: List<String>,
    selected_activity: MutableState<Int>,
    goal_distance: MutableState<String>
) {

    Text(
        text = "Choose your activity",
        fontWeight = FontWeight.Bold
    )

    RadioButtonGroup(
        activity_options,
        selected_activity.value
    ) {
        selected_activity.value = it
    }

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
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Text("Your distance goal is: ${goal_distance.value} km")
    }
}


@Composable
fun DashboardScreen() {

    val context = LocalContext.current

    // 0 = Home
    // 1 = Workout Setup
    // 2 = Active Workout
    // 3 = Workout Completed
    val workoutStage = remember { mutableIntStateOf(0) }

    val goal_enabled = remember { mutableStateOf(false) }
    val activity_options = listOf("Walk", "Run")
    val selected_activity = remember { mutableStateOf(0) }
    val goal_distance = remember { mutableStateOf("") }

    val validGoal = !goal_enabled.value ||
            (goal_distance.value.matches(Regex("[0-9]+(\\.[0-9]+)?")) &&
                    goal_distance.value.toDoubleOrNull()?.let {
                        it > 0 && it.isFinite()
                    } == true)

    AnimatedContent(
        targetState = workoutStage.intValue,
        modifier = Modifier.fillMaxSize(),
        label = "Workout screen transition",
        transitionSpec = {
            if (targetState > initialState) {
                (slideInVertically { height -> height } + fadeIn()) togetherWith
                        (slideOutVertically { height -> -height } + fadeOut())
            } else {
                (slideInVertically { height -> -height } + fadeIn()) togetherWith
                        (slideOutVertically { height -> height } + fadeOut())
            }
        }
    ) { stage ->

        when (stage) {

            // SCREEN 1: Home
            0 -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    Spacer(modifier = Modifier.height(80.dp))

                    WelcomeMessage()

                    Spacer(modifier = Modifier.weight(1f))

                    OutlinedButton(
                        onClick = {
                            val intent = Intent(
                                context,
                                HistoryActivity::class.java
                            )
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text("View History")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            workoutStage.intValue = 1
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text("Start Workout")
                    }
                }
            }

            // SCREEN 2: Workout setup
            1 -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Text(
                        text = "Workout Setup",
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            GoalControls(
                                goal_enabled,
                                activity_options,
                                selected_activity,
                                goal_distance
                            )
                        }
                    }

                    if (!validGoal) {
                        Text("Enter a valid distance greater than 0 km.")
                    }

                    Button(
                        onClick = {
                            workoutStage.intValue = 2
                        },
                        enabled = validGoal,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text("Begin ${activity_options[selected_activity.value]}")
                    }

                    OutlinedButton(
                        onClick = {
                            workoutStage.intValue = 0
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Back to Home")
                    }
                }
            }

            // SCREEN 3: Active workout
            2 -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Text(
                        text = "${activity_options[selected_activity.value]} Session",
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {

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
                                        "-- km",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                }

                                Column {
                                    Text("Average Pace")
                                    Text(
                                        "-- /km",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                }
                            }

                            if (goal_enabled.value && validGoal) {
                                HorizontalDivider()
                                Text("Distance Goal")
                                Text("${goal_distance.value} km")
                            }
                        }
                    }

                    Text("Preview only: Real timer and GPS tracking are not connected yet.")

                    Button(
                        onClick = {
                            workoutStage.intValue = 3
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text("Finish Preview")
                    }
                }
            }

            // SCREEN 4: Workout completed
            3 -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Text(
                        text = "Workout Preview Finished",
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            Text("Activity: ${activity_options[selected_activity.value]}")

                            Text("Elapsed Time: --:--:--")

                            Text("Distance: -- km")

                            Text("Average Pace: -- /km")

                            if (goal_enabled.value && validGoal) {
                                Text("Distance Goal: ${goal_distance.value} km")
                            }
                        }
                    }

                    Text(
                        "This was an interface preview. No workout has been recorded or saved."
                    )

                    Button(
                        onClick = {
                            workoutStage.intValue = 1
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("New Workout")
                    }

                    OutlinedButton(
                        onClick = {
                            workoutStage.intValue = 0
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Back to Home")
                    }
                }
            }
        }
    }
}



