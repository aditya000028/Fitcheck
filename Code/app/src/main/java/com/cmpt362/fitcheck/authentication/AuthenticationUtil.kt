package com.cmpt362.fitcheck.authentication

import android.util.Patterns
import java.util.regex.Pattern

object AuthenticationUtil {

    const val MIN_PASSWORD_LENGTH = 6

    fun isValidEmail(email: CharSequence?): Boolean {
        val emailPattern: Pattern = Patterns.EMAIL_ADDRESS
        return emailPattern.matcher(email!!).matches()
    }
}