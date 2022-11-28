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
import com.cmpt362.fitcheck.firebase.User

class SignUpActivity : AppCompatActivity() {

    private lateinit var emailText: EditText
    private lateinit var passwordText: EditText
    private lateinit var firstNameText: EditText
    private lateinit var lastNameText: EditText
    private lateinit var signUpButton: Button
    private lateinit var signInTV: TextView

    private var emailIsValid = false
    private var passwordIsValid = false
    private var firstNameIsValid = false
    private var lastNameIsValid = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up_activity)

        initializeVariables()
    }

    private fun initializeVariables() {
        emailText = findViewById(R.id.signUpEmail)
        passwordText = findViewById(R.id.signUpPassword)
        signUpButton = findViewById(R.id.signUpButton)
        signInTV = findViewById(R.id.accountSignIn)
        firstNameText = findViewById(R.id.signUpFirstName)
        lastNameText = findViewById(R.id.signUpLastName)

        emailText.doOnTextChanged { text, _, _, _ ->
            emailIsValid = if (text.toString().isNotEmpty()) {
                AuthenticationUtil.isValidEmail(text)
            } else {
                false
            }
            checkSignUpValidity()
        }

        passwordText.doOnTextChanged { text, _, _, _ ->
            passwordIsValid = if (text.toString().isNotEmpty()) {
                AuthenticationUtil.isValidPassword(text, resources)
            } else {
                false
            }
            checkSignUpValidity()
        }

        firstNameText.doOnTextChanged { text, _, _, _ ->
            firstNameIsValid = text.toString().isNotBlank()
            checkSignUpValidity()
        }

        lastNameText.doOnTextChanged { text, _, _, _ ->
            lastNameIsValid = text.toString().isNotBlank()
            checkSignUpValidity()
        }

        signInTV.setOnClickListener {
            // Return back to previous, sign in activity
            finish()
        }
    }

    private fun checkSignUpValidity() {
        signUpButton.isEnabled = emailIsValid && passwordIsValid && firstNameIsValid && lastNameIsValid
    }

    fun signUpUser(view: View) {
        val email = emailText.text.toString()
        val password = passwordText.text.toString()

        Firebase.createUser(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    val user = User(
                        email,
                        Firebase.getUserId()!!,
                        firstNameText.text.toString(),
                        lastNameText.text.toString()
                    )
                    Firebase.addUserToDatabase(user)

                    Toast.makeText(baseContext, "User created",
                        Toast.LENGTH_SHORT).show()

                    finish()
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    Toast.makeText(baseContext, "Unable to create user",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}