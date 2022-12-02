package com.cmpt362.fitcheck.ui.settings

import android.content.Intent
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.cmpt362.fitcheck.R
import com.cmpt362.fitcheck.firebase.Firebase
import com.cmpt362.fitcheck.ui.authentication.LoginActivity

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var logoutPreference: Preference
    private lateinit var uploadTimePreference: Preference
    private lateinit var dailyUploadReminderTimeToggle: SwitchPreferenceCompat

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        initializePreferencesBehaviour()
    }

    private fun initializePreferencesBehaviour() {
        logoutPreference = findPreference(getString(R.string.logout))!!
        logoutPreference.setOnPreferenceClickListener {
            Firebase.signOut()
            activity?.finish()
            startActivity(Intent(requireContext(), LoginActivity::class.java))

            true
        }

        uploadTimePreference = findPreference(getString(R.string.daily_outfit_upload_reminder_time))!!
        uploadTimePreference.summaryProvider = Preference.SummaryProvider<Preference> { preference ->
            "test upload time"
        }

        dailyUploadReminderTimeToggle = findPreference(getString(R.string.daily_outfit_upload_reminder))!!
        dailyUploadReminderTimeToggle.setOnPreferenceChangeListener { _, newValue ->
            when (newValue as Boolean) {
                true -> uploadTimePreference.isEnabled = true
                false -> uploadTimePreference.isEnabled = false
            }
            true
        }
    }
}