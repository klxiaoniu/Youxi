package com.glittering.youxi.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.glittering.youxi.R
import com.glittering.youxi.databinding.ActivitySettingBinding

class SettingActivity : BaseActivity<ActivitySettingBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
    }

    class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            findPreference<Preference>("skin")?.onPreferenceChangeListener = this
        }

        override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
            AppCompatDelegate.setDefaultNightMode(
                when (newValue) {
                    "0" -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    "1" -> AppCompatDelegate.MODE_NIGHT_YES
                    "2" -> AppCompatDelegate.MODE_NIGHT_NO
                    else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
            )
            return true
        }

    }
}