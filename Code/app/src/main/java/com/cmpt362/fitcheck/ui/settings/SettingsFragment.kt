package com.cmpt362.fitcheck.ui.settings

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.cmpt362.fitcheck.R
import com.cmpt362.fitcheck.Util
import com.cmpt362.fitcheck.firebase.Firebase
import com.cmpt362.fitcheck.models.Settings
import com.cmpt362.fitcheck.ui.authentication.LoginActivity
import com.cmpt362.fitcheck.ui.settings.notifications.NotificationHandler
import com.cmpt362.fitcheck.ui.settings.notifications.SettingsViewModel
import com.cmpt362.fitcheck.ui.settings.notifications.TimePickerDialog
import java.util.*

class SettingsFragment : PreferenceFragmentCompat(), TimePickerDialog.TimePickerDialogListener {

    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var logoutPreference: Preference
    private lateinit var uploadTimePreference: Preference
    private lateinit var dailyUploadReminderTimeToggle: SwitchPreferenceCompat
    private lateinit var makeProfilePublicToggle: SwitchPreferenceCompat
    private var dailyReminderTimeInMilli: Long = -1

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
        uploadTimePreference.setOnPreferenceClickListener {
            val timePickerDialog = TimePickerDialog()
            val bundle = Bundle()
            bundle.putString(TimePickerDialog.TIME_KEY, uploadTimePreference.summary.toString())

            timePickerDialog.arguments = bundle
            timePickerDialog.setListener(this)
            timePickerDialog.show(parentFragmentManager, TimePickerDialog.timePickerDialogTag)

            true
        }

        makeProfilePublicToggle = findPreference(getString(R.string.make_profile_public))!!
        makeProfilePublicToggle.summaryOff = getString(R.string.make_profile_public_off_summary)
        makeProfilePublicToggle.summaryOn = getString(R.string.make_profile_public_on_summary)
        makeProfilePublicToggle.setOnPreferenceChangeListener { _, newValue ->
            val profileIsPublic = newValue as Boolean

            val newSetting = if (settingsViewModel.settings.value != null) {
                val setting = settingsViewModel.settings.value
                setting!!.profileIsPublic = profileIsPublic
                setting
            } else {
                Settings(true, Calendar.getInstance().timeInMillis, false)
            }

            Firebase.addUserSettings(newSetting)
            true
        }

        dailyUploadReminderTimeToggle = findPreference(getString(R.string.daily_outfit_upload_reminder))!!
        dailyUploadReminderTimeToggle.setOnPreferenceChangeListener { _, newValue ->
            val remindUser = newValue as Boolean
            when (remindUser) {
                true -> NotificationHandler.changeOrStartNotification(requireContext(), dailyReminderTimeInMilli)
                false -> NotificationHandler.cancelRecurringNotification(requireContext())
            }
            uploadTimePreference.isEnabled = remindUser

            val newSetting = if (settingsViewModel.settings.value != null) {
                val setting = settingsViewModel.settings.value
                setting!!.dailyReminderToggle = remindUser
                setting
            } else {
                Settings(remindUser, Calendar.getInstance().timeInMillis, false)
            }
            Firebase.addUserSettings(newSetting)
            true
        }

        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
        if (settingsViewModel.settings.value == null){
            settingsViewModel.loadUserSetting(Firebase.getUserId()!!)
        }

        settingsViewModel.settings.observe(this) {
            dailyUploadReminderTimeToggle.isChecked = it.dailyReminderToggle == true
            dailyReminderTimeInMilli = it.dailyReminderTime!!
            uploadTimePreference.summary = Util.timeInMilliToString(requireContext(), dailyReminderTimeInMilli)
            uploadTimePreference.isEnabled = dailyUploadReminderTimeToggle.isChecked
            makeProfilePublicToggle.isChecked = it?.profileIsPublic == true
        }
    }

    override fun onTimeSet(dialog: DialogFragment, time: Calendar) {
        if (dailyReminderTimeInMilli != time.timeInMillis) {
            val newSettings = if (settingsViewModel.settings.value != null) {
                val modifiedSettings = settingsViewModel.settings.value
                modifiedSettings!!.dailyReminderTime = time.timeInMillis
                modifiedSettings
            } else {
                Settings(true, time.timeInMillis, false)
            }
            NotificationHandler.changeOrStartNotification(requireContext(), time.timeInMillis)
            Firebase.addUserSettings(newSettings)
        }
    }
}