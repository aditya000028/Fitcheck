package com.cmpt362.fitcheck.ui.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.cmpt362.fitcheck.R
import com.cmpt362.fitcheck.firebase.Firebase
import com.cmpt362.fitcheck.ui.authentication.AuthenticationUtil
import com.google.firebase.auth.EmailAuthProvider

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var currentPasswordText: EditText
    private lateinit var newPasswordText: EditText
    private lateinit var changePasswordButton: Button
    private lateinit var resetPasswordErrorTV: TextView

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
        resetPasswordErrorTV = findViewById(R.id.resetPasswordErrorMessage)

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
        val user = Firebase.getUser()
        if (user != null) {
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPasswordText.text.toString())
            user.reauthenticate(credential)
                .addOnCompleteListener { reauthenticateResult ->
                    if (reauthenticateResult.isSuccessful) {
                        user.updatePassword(newPasswordText.text.toString())
                            .addOnCompleteListener { updatePasswordResult ->
                                if (updatePasswordResult.isSuccessful) {
                                    println("debug: Password changed successfully")
                                    Toast.makeText(baseContext, "Password changed",
                                        Toast.LENGTH_SHORT).show()
                                    resetPasswordErrorTV.text = ""
                                    finish()
                                } else {
                                    println("debug: unable to change password. Error message: ${updatePasswordResult.exception?.message}")
                                    resetPasswordErrorTV.text = updatePasswordResult.exception?.message
                                }
                            }
                    } else {
                        println("debug: unable to re-authenticate user. Error message: ${reauthenticateResult.exception?.message}")
                        resetPasswordErrorTV.text = reauthenticateResult.exception?.message
                    }
                }
        }
    }
}