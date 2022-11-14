package com.cmpt362.fitcheck

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

// Splash Screen Activity which will take the user to either the login activity or "Main" activity on new start up

// reference used: https://medium.com/geekculture/implementing-the-perfect-splash-screen-in-android-295de045a8dc
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // check current user authentication state and go to the corresponding activity

        auth = Firebase.auth
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser == null){
            // User not logged in so go to Login activity
            startActivity(Intent(this, MainActivity::class.java))
        }else {
            // User logged in so go to "Main" activity
            startActivity(Intent(this, MainActivity::class.java))
        }
        finish()
    }

}