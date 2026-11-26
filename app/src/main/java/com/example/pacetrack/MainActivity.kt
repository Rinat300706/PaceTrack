package com.example.pacetrack

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.content.Context

class MainActivity : ComponentActivity() {

    // Remember which language Home is currently displaying.
    private var displayedLanguageCode: String? = null

    // Apply the language selected in Settings.
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(
            PaceTrackLanguage.apply(newBase)
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            PaceTrackTheme {
                DashboardScreen()
            }
        }


        Log.i("PaceTrack", "PaceTrack MainActivity created")
    }

    override fun onResume() {
        super.onResume()

        // Read the language selected in Settings.
        val selectedLanguage = getSharedPreferences(
            "pacetrack_settings",
            MODE_PRIVATE
        ).getString("language_code", "en") ?: "en"

        if (displayedLanguageCode == null) {

            // Remember the language when Home first opens.
            displayedLanguageCode = selectedLanguage

        } else if (displayedLanguageCode != selectedLanguage) {

            // Reload Home if the language has changed.
            recreate()
        }
    }

}