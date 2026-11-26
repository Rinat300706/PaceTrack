
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
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment

class SettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContent {

            // Remember the user's appearance preference.
            val preferences = remember {
                getSharedPreferences("pacetrack_settings", MODE_PRIVATE)
            }

            val appearanceMode = remember {
                mutableStateOf(
                    preferences.getString("appearance_mode", "System") ?: "System"
                )
            }

            // Decide which colour scheme to use.
            val useDarkTheme = when (appearanceMode.value) {
                "Light" -> false
                "Dark" -> true
                else -> isSystemInDarkTheme()
            }

            PaceTrackTheme(darkTheme = useDarkTheme) {

                Column(

                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
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
                    Text(
                        text = "Appearance",
                        fontWeight = FontWeight.Bold
                    )

                    listOf("System", "Light", "Dark").forEach { option ->

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = appearanceMode.value == option,
                                onClick = {
                                    appearanceMode.value = option

                                    // Save the preference on the phone.
                                    preferences.edit()
                                        .putString("appearance_mode", option)
                                        .apply()
                                }
                            )

                            Text(option)
                        }
                    }


                    // Language settings.
                    Text("Language")

                    // Accessibility settings.
                    Text("Accessibility")

                    // App information.
                    Text("About PaceTrack")

                    Spacer(modifier = Modifier.weight(1f))

                    // Return to the Home screen.
                    Button(
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
}
