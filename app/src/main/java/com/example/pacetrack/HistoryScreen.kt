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

data class HistorySession(
    val title: String,
    val type: String,
    val distance: String,
    val time: String,
    val pace: String
)
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
fun HistoryScreen() {
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

            Text("Example sessions - layout preview")

            val sessions = listOf(
                HistorySession("Sample Walk", "Walk", "3 km", "45 min", "15:00 /km"),
                HistorySession("Sample Run", "Run", "5 km", "30 min", "6:00 /km"),
                HistorySession("Evening Walk", "Walk", "2 km", "28 min", "14:00 /km")
            )

            val filter_options = listOf("All", "Walk", "Run")
            val selected_filter = remember { mutableIntStateOf(0) }

            RadioButtonGroup(filter_options, selected_filter.intValue) {
                selected_filter.intValue = it
            }

            val shown_sessions =
                if (selected_filter.intValue == 0) sessions
                else sessions.filter { it.type == filter_options[selected_filter.intValue] }

            if (shown_sessions.isEmpty()) {
                EmptyHistoryMessage()
            } else {
                for (session in shown_sessions) {
                    HistoryCard(
                        title = session.title,
                        distance = session.distance,
                        time = session.time,
                        pace = session.pace
                    )
                }
            }
        }
    }
}