package com.cmpt362.fitcheck.ui.settings.editProfile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.cmpt362.fitcheck.R
import com.cmpt362.fitcheck.firebase.Firebase
import com.cmpt362.fitcheck.ui.friends.viewModels.ProfileViewModel

class EditNameActivity : AppCompatActivity() {

    private lateinit var editFirstNameText: EditText
    private lateinit var editLastNameText: EditText
    private lateinit var saveProfileButton: Button
    private lateinit var profileViewModel: ProfileViewModel

    private var firstNameIsValid = false
    private var lastNameIsValid = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_name_activity)

        initVariables()
    }

    private fun initVariables() {
        editFirstNameText = findViewById(R.id.editFirstName)
        editLastNameText = findViewById(R.id.editLastName)
        saveProfileButton = findViewById(R.id.saveProfileButton)

        editFirstNameText.doOnTextChanged { text, start, before, count ->
            firstNameIsValid = !text.isNullOrBlank()
            saveProfileButton.isEnabled = firstNameIsValid && lastNameIsValid
        }

        editLastNameText.doOnTextChanged { text, start, before, count ->
            lastNameIsValid = !text.isNullOrBlank()
            saveProfileButton.isEnabled = firstNameIsValid && lastNameIsValid
        }

        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        if (profileViewModel.user.value == null) {
            profileViewModel.loadProfile(Firebase.getUserId()!!)
        }

        profileViewModel.user.observe(this) {
            editFirstNameText.setText(it.firstName)
            editLastNameText.setText(it.lastName)
        }
    }

    fun onSaveName(view: View) {
        val currentProfile = profileViewModel.user.value
        currentProfile?.let { profile ->
            profile.firstName = editFirstNameText.text.toString()
            profile.lastName = editLastNameText.text.toString()
            Firebase.addUserToDatabase(profile)

            Toast.makeText(this, getString(R.string.name_saved), Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}