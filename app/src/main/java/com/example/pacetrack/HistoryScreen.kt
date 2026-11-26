package com.example.pacetrack

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material3.Button

fun formatDistance(metres: Double): String {
    return String.format(Locale.ROOT, "%.2f km", metres / 1000)
}

fun formatPace(secondsPerKm: Long?): String {
    if (secondsPerKm == null) return "--"
    return String.format(Locale.ROOT, "%d:%02d /km", secondsPerKm / 60, secondsPerKm % 60)
}

fun formatDate(millis: Long): String {
    return SimpleDateFormat("d MMM yyyy, HH:mm", Locale.getDefault()).format(Date(millis))
}

@Composable
fun HistoryStat(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            fontWeight = FontWeight.Bold
        )

        Text(text = value)
    }
}

@Composable
fun HistoryCard(
    title: String,
    distance: String,
    time: String,
    pace: String
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                HistoryStat("Distance", distance)
                HistoryStat("Time", time)
                HistoryStat("Pace", pace)
            }
        }
    }
}

@Composable
fun EmptyHistoryMessage() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "No activities recorded yet",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            Text(
                text = "Complete your first walk or run to see it here.",
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun HistoryScreen(viewModel: WorkoutViewModel = viewModel()) {
    val workouts by viewModel.allWorkouts.collectAsState(initial = emptyList())

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Activity History",
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp
            )

            Text("Review your previous walks and runs")


            // temporary - remove once the dashboard saves real workouts
            Button(onClick = {
                viewModel.saveWorkout(
                    WorkoutSession(
                        activityType = if (workouts.size % 2 == 0) "Walk" else "Run",
                        startTime = System.currentTimeMillis(),
                        durationMillis = 1_800_000,
                        distanceMetres = 5000.0,
                        averagePaceSecondsPerKm = 360
                    )
                )
            }) {
                Text("Add test workout")
            }
            val filter_options = listOf("All", "Walk", "Run")
            val selected_filter = remember { mutableIntStateOf(0) }

            RadioButtonGroup(filter_options, selected_filter.intValue) {
                selected_filter.intValue = it
            }

            val shown_workouts =
                if (selected_filter.intValue == 0) workouts
                else workouts.filter { it.activityType == filter_options[selected_filter.intValue] }

            if (shown_workouts.isEmpty()) {
                EmptyHistoryMessage()
            } else {
                for (workout in shown_workouts) {
                    HistoryCard(
                        title = "${workout.activityType} - ${formatDate(workout.startTime)}",
                        distance = formatDistance(workout.distanceMetres),
                        time = formatElapsedTime(workout.durationMillis),
                        pace = formatPace(workout.averagePaceSecondsPerKm)
                    )
                }
            }
        }
    }
}