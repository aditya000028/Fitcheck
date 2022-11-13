package com.cmpt362.fitcheck.authentication.signup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cmpt362.fitcheck.R
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        initializeVariables(savedInstanceState)
    }

    private fun initializeVariables(savedInstanceState: Bundle?) {

    }
}