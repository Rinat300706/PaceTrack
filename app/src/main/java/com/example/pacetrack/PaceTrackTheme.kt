
package com.example.pacetrack

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

// Light mode colours.
private val LightColors = lightColorScheme(
    primary = Color(0xFF17324D),
    onPrimary = Color.White,
    background = Color(0xFFF7F9FC),
    onBackground = Color(0xFF172B40),
    surface = Color.White,
    onSurface = Color(0xFF172B40)
)

// Dark mode colours.
private val DarkColors = darkColorScheme(
    primary = Color(0xFFF4C542),
    onPrimary = Color(0xFF1E1E1E),
    secondary = Color(0xFFF4C542),
    onSecondary = Color(0xFF1E1E1E),
    background = Color(0xFF1E1E1E),
    onBackground = Color(0xFFF5F5F5),
    surface = Color(0xFF2B2B2B),
    onSurface = Color(0xFFF5F5F5),
    surfaceVariant = Color(0xFF363636),
    onSurfaceVariant = Color(0xFFCCCCCC),
    outline = Color(0xFF888888)
)

@Composable
fun PaceTrackTheme(
    darkTheme: Boolean? = null,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Read the appearance setting saved by SettingsActivity.
    val preferences = remember(context) {
        context.getSharedPreferences(
            "pacetrack_settings",
            Context.MODE_PRIVATE
        )
    }

    var appearanceMode by remember {
        mutableStateOf(
            preferences.getString("appearance_mode", "System") ?: "System"
        )
    }

    // Refresh the preference when returning to this screen.
    DisposableEffect(lifecycleOwner, preferences) {

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                appearanceMode =
                    preferences.getString("appearance_mode", "System")
                        ?: "System"
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Use the saved preference unless a screen overrides it.
    val useDarkTheme = darkTheme ?: when (appearanceMode) {
        "Light" -> false
        "Dark" -> true
        else -> isSystemInDarkTheme()
    }

    MaterialTheme(
        colorScheme = if (useDarkTheme) DarkColors else LightColors
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground
        ) {
            content()
        }
    }
}
