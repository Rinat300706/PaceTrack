
package com.example.pacetrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class SettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Settings screen heading.
                Text(
                    text = "Settings",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )

                HorizontalDivider()

                // Appearance settings.
                Text("Appearance")

                // Language settings.
                Text("Language")

                // Accessibility settings.
                Text("Accessibility")

                // App information.
                Text("About PaceTrack")

                Spacer(modifier = Modifier.weight(1f))

                // Return to the Home screen.
                OutlinedButton(
                    onClick = {
                        finish()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Back to Home")
                }
            }
        }
    }
}
