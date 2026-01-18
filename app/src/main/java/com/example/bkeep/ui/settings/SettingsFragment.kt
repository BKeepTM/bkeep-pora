package com.example.bkeep.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceFragmentCompat
import com.example.bkeep.R

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onResume() {
        super.onResume()
        // Register the listener
        preferenceScreen.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        // Unregister to avoid memory leaks
        preferenceScreen.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
    }

    // This triggers whenever a setting changes
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            "app_theme" -> {
                val themeValue = sharedPreferences?.getString(key, "system")
                applyTheme(themeValue)
            }
            "font_size" -> {
                // Note: Changing font size usually requires recreating the activity
                activity?.recreate()
            }
        }
    }

    private fun applyTheme(themeVal: String?) {
        when (themeVal) {
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}