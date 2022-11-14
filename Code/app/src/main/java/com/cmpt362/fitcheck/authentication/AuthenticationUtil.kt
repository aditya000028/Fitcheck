package com.cmpt362.fitcheck.authentication

import android.util.Patterns
import java.util.regex.Pattern

object AuthenticationUtil {

    const val MIN_PASSWORD_LENGTH = 6

    fun isValidEmail(email: CharSequence?): Boolean {
        return if (email.isNullOrBlank()) {
            false
        } else {
            val emailPattern: Pattern = Patterns.EMAIL_ADDRESS
            emailPattern.matcher(email).matches()
        }
    }
}