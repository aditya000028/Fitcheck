package com.cmpt362.fitcheck.ui.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import com.cmpt362.fitcheck.R
import com.cmpt362.fitcheck.ui.authentication.AuthenticationUtil

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var currentPasswordText: EditText
    private lateinit var newPasswordText: EditText
    private lateinit var changePasswordButton: Button

    private var currentPasswordIsValid = false
    private var newPasswordIsValid = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reset_password_activity)

        initVariables()
    }

    private fun initVariables() {
        currentPasswordText = findViewById(R.id.currentPassword)
        newPasswordText = findViewById(R.id.newPassword)
        changePasswordButton = findViewById(R.id.changePasswordButton)

        currentPasswordText.doOnTextChanged { text, _, _, _ ->
            currentPasswordIsValid = if (text.toString().isNotEmpty()) {
                AuthenticationUtil.isValidPassword(text, resources)
            } else {
                false
            }
            changePasswordButton.isEnabled = currentPasswordIsValid && newPasswordIsValid
        }

        newPasswordText.doOnTextChanged { text, _, _, _ ->
            newPasswordIsValid = if (text.toString().isNotEmpty()) {
                AuthenticationUtil.isValidPassword(text, resources)
            } else {
                false
            }
            changePasswordButton.isEnabled = currentPasswordIsValid && newPasswordIsValid
        }
    }

    fun onSavePassword(view: View) {

    }
}