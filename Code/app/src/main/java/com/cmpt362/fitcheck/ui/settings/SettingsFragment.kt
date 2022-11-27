package com.cmpt362.fitcheck.ui.settings

import android.content.Intent
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.cmpt362.fitcheck.R
import com.cmpt362.fitcheck.firebase.Firebase
import com.cmpt362.fitcheck.ui.authentication.LoginActivity

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        initializePreferencesBehaviour()
    }

    private fun initializePreferencesBehaviour() {
        val logoutPreference = findPreference<Preference>(getString(R.string.logout))
        logoutPreference?.setOnPreferenceClickListener {
            Firebase.signOut()
            activity?.finish()
            startActivity(Intent(requireContext(), LoginActivity::class.java))

            true
        }
    }
}