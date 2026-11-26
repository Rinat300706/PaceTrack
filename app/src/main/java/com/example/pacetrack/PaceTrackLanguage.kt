
package com.example.pacetrack

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object PaceTrackLanguage {

    // Apply the language saved in Settings.
    fun apply(context: Context): Context {

        val preferences = context.getSharedPreferences(
            "pacetrack_settings",
            Context.MODE_PRIVATE
        )

        // English is the default language.
        val languageCode =
            preferences.getString("language_code", "en") ?: "en"

        // Select English or Irish.
        val locale = Locale.forLanguageTag(
            if (languageCode == "ga") "ga" else "en"
        )

        // Create a configuration for the selected language.
        val configuration = Configuration(
            context.resources.configuration
        )

        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)

        // Return a context that uses the selected language.
        return context.createConfigurationContext(configuration)
    }
}
