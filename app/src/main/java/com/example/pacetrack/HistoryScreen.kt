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
    distance: String,     // replaces "details"
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

            // replaces the old "details" Text
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

            HistoryCard(
                title = "Sample Walk",
                distance = "3 km",
                time = "45 min",
                pace = "15:00 /km"
            )

            HistoryCard(
                title = "Sample Run",
                distance = "5 km",
                time = "30 min",
                pace = "6:00 /km"
            )
        }
    }
}