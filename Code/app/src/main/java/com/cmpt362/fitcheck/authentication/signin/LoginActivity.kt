package com.cmpt362.fitcheck.authentication.signin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.cmpt362.fitcheck.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailText: EditText
    private lateinit var passwordText: EditText
    private lateinit var signInButton: Button
    private lateinit var emailPattern: Pattern
    private lateinit var signUpTV: TextView
    private val EMAIL_KEY = "EMAIL_KEY"
    private val PASSWORD_KEY = "PASSWORD_KEY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        initializeVariables(savedInstanceState)
    }

    private fun initializeVariables(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        emailText = findViewById(R.id.signInEmail)
        passwordText = findViewById(R.id.signInPassword)
        signInButton = findViewById(R.id.signInButton)
        emailPattern = Patterns.EMAIL_ADDRESS
        signUpTV = findViewById(R.id.noAccountSignUp)

        val maybeEmail = savedInstanceState?.getString(EMAIL_KEY)
        if (maybeEmail != null) {
            emailText.setText(maybeEmail)
        }

        val maybePassword = savedInstanceState?.getString(PASSWORD_KEY)
        if (maybePassword != null) {
            passwordText.setText(maybePassword)
        }

        emailText.doOnTextChanged { text, start, before, count ->
            if (!text.toString().isEmpty()) {
                checkIfAbleToSignIn()
            }
        }

        passwordText.doOnTextChanged { text, start, before, count ->
            if (!text.toString().isEmpty()) {
                checkIfAbleToSignIn()
            }
        }

        signUpTV.setOnClickListener {
            // Direct user to sign up page
        }
    }

    private fun checkIfAbleToSignIn() {
        val emailIsEmpty = emailText.text.toString().isEmpty()
        val passwordIsEmpty = passwordText.text.toString().isEmpty()

        signInButton.isEnabled = !(emailIsEmpty || passwordIsEmpty)
    }

    private fun isValidEmail(email: CharSequence?): Boolean {
        return emailPattern.matcher(email!!).matches()
    }

    fun attemptSignIn(view: View) {
        if (!isValidEmail(emailText.text)) {
            emailText.setError(getString(R.string.invalid_email_format))
        } else {
            emailText.setError(null)
            signInUser()
        }
    }

    private fun signInUser() {
        val email = emailText.text.toString()
        val password = passwordText.text.toString()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    Toast.makeText(baseContext, "Authentication succeeded.",
                        Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(baseContext, "Incorrect credentials",
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