package com.cmpt362.fitcheck.firebase

data class User(
    var uid: String? = null,
    var email: String? = null,
    var firstName: String? = null,
    var lastName: String? = null
) {}