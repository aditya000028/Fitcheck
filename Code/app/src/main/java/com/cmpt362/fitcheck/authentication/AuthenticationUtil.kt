package com.cmpt362.fitcheck.authentication

import android.util.Patterns
import java.util.regex.Pattern

object AuthenticationUtil {

    fun isValidEmail(email: CharSequence?): Boolean {
        val emailPattern: Pattern = Patterns.EMAIL_ADDRESS
        return emailPattern.matcher(email!!).matches()
    }
}