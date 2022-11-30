package com.cmpt362.fitcheck.ui.settings

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.cmpt362.fitcheck.R
import com.cmpt362.fitcheck.firebase.Firebase
import com.cmpt362.fitcheck.ui.authentication.AuthenticationUtil
import com.google.firebase.auth.EmailAuthProvider

class ChangeEmailActivity: AppCompatActivity() {

    private lateinit var currentEmailText: EditText
    private lateinit var currentPasswordText: EditText
    private lateinit var newEmailText: EditText
    private lateinit var changeEmailButton: Button

    private var currentEmailIsValid = false
    private var currentPasswordIsValid = false
    private var newEmailIsValid = false

    private lateinit var changeEmailErrorTV: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.change_email_activity)

        initializeVariables()
    }

    private fun initializeVariables() {
        currentEmailText = findViewById(R.id.currentEmail)
        currentPasswordText = findViewById(R.id.currentPassword)
        newEmailText = findViewById(R.id.newEmail)
        changeEmailButton = findViewById(R.id.changeEmailButton)
        changeEmailErrorTV = findViewById(R.id.changeEmailErrorMessage)

        currentEmailText.doOnTextChanged { text, _, _, _ ->
            currentEmailIsValid = if (text.toString().isNotEmpty()) {
                AuthenticationUtil.isValidEmail(text)
            } else {
                false
            }
            changeEmailButton.isEnabled = currentEmailIsValid && currentPasswordIsValid && newEmailIsValid
        }

        currentPasswordText.doOnTextChanged { text, _, _, _ ->
            currentPasswordIsValid = if (text.toString().isNotEmpty()) {
                AuthenticationUtil.isValidPassword(text, resources)
            } else {
                false
            }
        }

        newEmailText.doOnTextChanged { text, _, _, _ ->
            newEmailIsValid = if (text.toString().isNotEmpty()) {
                AuthenticationUtil.isValidEmail(text)
            } else {
                false
            }
            changeEmailButton.isEnabled = currentEmailIsValid && currentPasswordIsValid && newEmailIsValid
        }
    }

    /**
     * Changes email
     *
     * TODO: refactor this so that [Firebase] will handle all the logic and return an error with a message or success
     */
    fun onChangeEmail(view: View) {
        val user = Firebase.getUser()
        if (user != null) {
            val credential = EmailAuthProvider.getCredential(
                currentEmailText.text.toString(),
                currentPasswordText.text.toString()
            )
            user.reauthenticate(credential)
                .addOnCompleteListener { reauthenticationResult ->
                    if (reauthenticationResult.isSuccessful) {
                        user.updateEmail(newEmailText.text.toString())
                            .addOnCompleteListener { updateEmailResult ->
                                if (updateEmailResult.isSuccessful) {
                                    Firebase.changeUserEmailInDB(newEmailText.text.toString())
                                    Toast.makeText(baseContext, "Email changed",
                                        Toast.LENGTH_SHORT).show()
                                    changeEmailErrorTV.text = ""
                                    finish()
                                } else {
                                    println("debug: unable to change email. Error message: ${updateEmailResult.exception?.message}")
                                    changeEmailErrorTV.text = updateEmailResult.exception?.message
                                }
                            }
                    } else {
                        println("debug: unable to re-authenticate user. Error message: ${reauthenticationResult.exception?.message}")
                        changeEmailErrorTV.text = reauthenticationResult.exception?.message
                    }
                }
        }
    }
}