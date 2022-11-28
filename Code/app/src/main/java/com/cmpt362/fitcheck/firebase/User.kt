package com.cmpt362.fitcheck.firebase

class User {
    var email: String? = null
    var uid: String? = null

    constructor(email: String?, uid: String?){
        this.email = email
        this.uid = uid
    }
}