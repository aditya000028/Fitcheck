package com.cmpt362.fitcheck.ui.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.cmpt362.fitcheck.MainActivity
import com.cmpt362.fitcheck.R
import com.cmpt362.fitcheck.firebase.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var emailText: EditText
    private lateinit var passwordText: EditText
    private lateinit var signInButton: Button
    private lateinit var signUpTV: TextView

    private var emailIsValid = false
    private var passwordIsValid = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        initializeVariables()
    }

    private fun initializeVariables() {
        emailText = findViewById(R.id.signInEmail)
        passwordText = findViewById(R.id.signInPassword)
        signInButton = findViewById(R.id.signInButton)
        signUpTV = findViewById(R.id.noAccountSignUp)

        emailText.doOnTextChanged { text, _, _, _ ->
            emailIsValid = if (text.toString().isNotEmpty()) {
                AuthenticationUtil.isValidEmail(text)
            } else {
                false
            }
            signInButton.isEnabled = emailIsValid && passwordIsValid
        }

        passwordText.doOnTextChanged { text, _, _, _ ->
            passwordIsValid = if (text.toString().isNotEmpty()) {
                AuthenticationUtil.isValidPassword(text, resources)
            } else {
                false
            }
            signInButton.isEnabled = emailIsValid && passwordIsValid
        }

        signUpTV.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    fun signInUser(view: View) {
        val email = emailText.text.toString()
        val password = passwordText.text.toString()

        Firebase.signInUser(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    Toast.makeText(baseContext, "Authentication succeeded.",
                        Toast.LENGTH_SHORT).show()
                    // finishing login activity before starting main activity
                    finish()
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    Toast.makeText(baseContext, "Incorrect credentials",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}