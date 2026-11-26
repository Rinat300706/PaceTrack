
package com.example.pacetrack

import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.content.Context
import androidx.compose.ui.res.stringResource

class HistoryActivity : ComponentActivity() {

    // Apply the language selected in Settings.
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(
            PaceTrackLanguage.apply(newBase)
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Modern Android exit transition
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(
                Activity.OVERRIDE_TRANSITION_CLOSE,
                0,
                R.anim.slide_out_down
            )
        }

        setContent {

            PaceTrackTheme {

                BackHandler {
                    returnToHome()
                }

                Column(
                    modifier = Modifier.fillMaxSize()
                ) {

                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        HistoryScreen()
                    }

                    OutlinedButton(
                        onClick = {
                            returnToHome()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(stringResource(R.string.settings_back_home))
                    }
                }
            }
        }
    }

    private fun returnToHome() {
        finish()

        // Exit animation on older Android versions
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            @Suppress("DEPRECATION")
            overridePendingTransition(
                0,
                R.anim.slide_out_down
            )
        }
    }
}
