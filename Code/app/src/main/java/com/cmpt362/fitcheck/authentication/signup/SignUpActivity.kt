package com.cmpt362.fitcheck.authentication.signup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.cmpt362.fitcheck.R
import com.cmpt362.fitcheck.authentication.AuthenticationUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailText: EditText
    private lateinit var passwordText: EditText
    private lateinit var signUpButton: Button
    private lateinit var signInTV: TextView
    private val EMAIL_KEY = "EMAIL_KEY"
    private val PASSWORD_KEY = "PASSWORD_KEY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up_activity)

        initializeVariables(savedInstanceState)
    }

    private fun initializeVariables(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        emailText = findViewById(R.id.signUpEmail)
        passwordText = findViewById(R.id.signUpPassword)
        signUpButton = findViewById(R.id.signUpButton)
        signInTV = findViewById(R.id.accountSignIn)

        val maybeEmail = savedInstanceState?.getString(EMAIL_KEY)
        if (maybeEmail != null) {
            emailText.setText(maybeEmail)
        }

        val maybePassword = savedInstanceState?.getString(PASSWORD_KEY)
        if (maybePassword != null) {
            passwordText.setText(maybePassword)
        }

        emailText.doOnTextChanged { text, start, before, count ->
            if (text.toString().isNotEmpty()) {
                checkIfAbleToSignUp()
            }
        }

        passwordText.doOnTextChanged { text, start, before, count ->
            if (text.toString().isNotEmpty()) {
                checkIfAbleToSignUp()
            }
        }

        signInTV.setOnClickListener {
            // Return back to previous, sign in activity
            finish()
        }
    }

    private fun checkIfAbleToSignUp() {
        val emailIsEmpty = emailText.text.toString().isEmpty()
        val passwordIsEmpty = passwordText.text.toString().isEmpty()
        val passwordNotLongEnough = passwordText.text.toString().length < 6

        signUpButton.isEnabled = !(emailIsEmpty || passwordIsEmpty || passwordNotLongEnough)
    }

    fun attemptSignUp(view: View) {
        if (!AuthenticationUtil.isValidEmail(emailText.text)) {
            emailText.error = getString(R.string.invalid_email_format)
        } else {
            emailText.error = null
            signUpUser()
        }
    }

    private fun signUpUser() {
        val email = emailText.text.toString()
        val password = passwordText.text.toString()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    Toast.makeText(baseContext, "User created",
                        Toast.LENGTH_SHORT).show()
                    // TODO: Direct user to home page
                } else {
                    Toast.makeText(baseContext, "Unable to create user",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EMAIL_KEY, emailText.text.toString())
        outState.putString(PASSWORD_KEY, passwordText.text.toString())
    }
}