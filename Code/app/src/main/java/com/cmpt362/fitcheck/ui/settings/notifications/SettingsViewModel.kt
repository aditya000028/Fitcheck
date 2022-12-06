package com.cmpt362.fitcheck.ui.settings.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cmpt362.fitcheck.firebase.Firebase
import com.cmpt362.fitcheck.models.Settings

class SettingsViewModel: ViewModel() {

    private val _settings = MutableLiveData<Settings>()
    val settings: LiveData<Settings> = _settings

    fun loadUserSetting(targetUID: String) {
        Firebase.getUserSettings(_settings, targetUID)
    }
}