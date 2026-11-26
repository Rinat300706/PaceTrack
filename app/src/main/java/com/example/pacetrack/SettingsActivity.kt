
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
import androidx.compose.ui.res.stringResource
import android.content.Context

class SettingsActivity : ComponentActivity() {

    // Apply the user's selected language to Settings.
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(
            PaceTrackLanguage.apply(newBase)
        )
    }

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

            // Remember the user's language selection.
            val languageCode = remember {
                mutableStateOf(
                    preferences.getString("language_code", "en") ?: "en"
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
                        text = stringResource(R.string.settings_title),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )

                    HorizontalDivider()


                    // Appearance settings.
                    Text(
                        text = stringResource(R.string.settings_appearance),
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


                            Text(
                                text = when (option) {
                                    "System" -> stringResource(R.string.theme_system)
                                    "Light" -> stringResource(R.string.theme_light)
                                    "Dark" -> stringResource(R.string.theme_dark)
                                    else -> option
                                }
                            )

                        }
                    }


                    // Language settings.
                    Text(
                        text = stringResource(R.string.settings_language),
                        fontWeight = FontWeight.Bold
                    )

                    listOf(
                        "en" to R.string.language_english,
                        "ga" to R.string.language_irish
                    ).forEach { (code, label) ->

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = languageCode.value == code,
                                onClick = {
                                    // Remember the selected language.
                                    languageCode.value = code

                                    // Save it on the phone.
                                    preferences.edit()
                                        .putString("language_code", code)
                                        .apply()

                                    recreate()
                                }
                            )

                            Text(stringResource(label))
                        }
                    }


                    // Accessibility settings.
                    Text(stringResource(R.string.settings_accessibility))

                    // App information.
                    Text(stringResource(R.string.settings_about))

                    Spacer(modifier = Modifier.weight(1f))

                    // Return to the Home screen.
                    Button(
                        onClick = {
                            finish()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.settings_back_home))
                    }
                }
            }
        }
    }
}
