package com.cmpt362.fitcheck

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cmpt362.fitcheck.firebase.Firebase
import com.cmpt362.fitcheck.ui.authentication.LoginActivity

/**
 * Splash Screen Activity which will take the user to either the login activity or "Main" activity on new start up
 * reference used: https://medium.com/geekculture/implementing-the-perfect-splash-screen-in-android-295de045a8dc
 */
class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if user is signed in (non-null) and update UI accordingly.
        if(Firebase.isUserSignedIn()){
            // User logged in so go to "Main" activity
            startActivity(Intent(this, MainActivity::class.java))
        }else {
            // User not logged in so go to Login activity
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish()
    }

}