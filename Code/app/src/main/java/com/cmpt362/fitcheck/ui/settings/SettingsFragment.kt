package com.cmpt362.fitcheck.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.cmpt362.fitcheck.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}