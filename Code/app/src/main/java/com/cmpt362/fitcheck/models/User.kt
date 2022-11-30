package com.cmpt362.fitcheck.models

/**
 * IMPORTANT: Update [UserKeys] if you are changing anything in this class
 */
data class User(
    var uid: String? = null,
    var email: String? = null,
    var firstName: String? = null,
    var lastName: String? = null
) {}