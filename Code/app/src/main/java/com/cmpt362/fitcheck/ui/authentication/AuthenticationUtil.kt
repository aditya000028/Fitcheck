package com.cmpt362.fitcheck.ui.authentication

import android.content.res.Resources
import android.util.Patterns
import com.cmpt362.fitcheck.R
import java.util.regex.Pattern

object AuthenticationUtil {

    fun isValidEmail(email: CharSequence?): Boolean {
        return if (email.isNullOrBlank()) {
            false
        } else {
            val emailPattern: Pattern = Patterns.EMAIL_ADDRESS
            emailPattern.matcher(email).matches()
        }
    }

    fun isValidPassword(password: CharSequence?, resources: Resources): Boolean {
        return if (password.isNullOrBlank()) {
            false
        } else {
            return password.length > resources.getInteger(R.integer.minimum_password_length)
        }
    }
}