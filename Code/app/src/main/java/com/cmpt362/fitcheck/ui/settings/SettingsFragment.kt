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
import com.cmpt362.fitcheck.ui.settings.notifications.NotificationsViewModel
import com.cmpt362.fitcheck.ui.settings.notifications.TimePickerDialog
import java.util.*

class SettingsFragment : PreferenceFragmentCompat(), TimePickerDialog.TimePickerDialogListener {

    private lateinit var notificationsViewModel: NotificationsViewModel
    private lateinit var logoutPreference: Preference
    private lateinit var uploadTimePreference: Preference
    private lateinit var dailyUploadReminderTimeToggle: SwitchPreferenceCompat
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

        dailyUploadReminderTimeToggle = findPreference(getString(R.string.daily_outfit_upload_reminder))!!
        dailyUploadReminderTimeToggle.setOnPreferenceChangeListener { _, newValue ->
            val remindUser = newValue as Boolean
            when (remindUser) {
                true -> {
                    uploadTimePreference.isEnabled = true
                    NotificationHandler.changeOrStartNotification(requireContext(), dailyReminderTimeInMilli)
                }
                false -> {
                    uploadTimePreference.isEnabled = false
                    NotificationHandler.cancelRecurringNotification(requireContext())
                }
            }
            val newSetting = if (notificationsViewModel.settings.value != null) {
                val setting = notificationsViewModel.settings.value
                setting!!.dailyReminderToggle = remindUser
                setting
            } else {
                Settings(remindUser, Calendar.getInstance().timeInMillis)
            }
            Firebase.addUserSettings(newSetting)
            true
        }

        notificationsViewModel = ViewModelProvider(this)[NotificationsViewModel::class.java]
        notificationsViewModel.settings.observe(this) {
            dailyUploadReminderTimeToggle.isChecked = it.dailyReminderToggle == true
            dailyReminderTimeInMilli = it.dailyReminderTime!!
            uploadTimePreference.summary = Util.timeInMilliToString(requireContext(), dailyReminderTimeInMilli)
            uploadTimePreference.isEnabled = dailyUploadReminderTimeToggle.isChecked
        }
    }

    override fun onTimeSet(dialog: DialogFragment, time: Calendar) {
        if (dailyReminderTimeInMilli != time.timeInMillis) {
            val settings = if (notificationsViewModel.settings.value != null) {
                val settings = notificationsViewModel.settings.value
                settings!!.dailyReminderTime = time.timeInMillis
                settings
            } else {
                Settings(true, time.timeInMillis)
            }
            NotificationHandler.changeOrStartNotification(requireContext(), time.timeInMillis)
            Firebase.addUserSettings(settings)
        }
    }
}