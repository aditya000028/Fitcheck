package com.cmpt362.fitcheck.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

/**
 * Singleton class for handling all Firebase related operations
 */
object Firebase {

    private const val USERS_REFERENCE_NAME = "users"
    private const val FRIENDSHIPS_REFERENCE_NAME = "friendships"
    private const val USER_PHOTOS_REFERENCE_NAME = "user_photos"
    private const val PHOTOS_TAGS_REFERENCE_NAME = "photos_tags"

    private val auth: FirebaseAuth = Firebase.auth
    private val database: FirebaseDatabase = Firebase.database

    private val usersReference: DatabaseReference
    private val friendshipsReference: DatabaseReference
    private val userPhotosReference: DatabaseReference
    private val photosTagsReference: DatabaseReference

    init {
        usersReference = database.getReference(USERS_REFERENCE_NAME)
        friendshipsReference = database.getReference(FRIENDSHIPS_REFERENCE_NAME)
        userPhotosReference = database.getReference(USER_PHOTOS_REFERENCE_NAME)
        photosTagsReference = database.getReference(PHOTOS_TAGS_REFERENCE_NAME)
    }

    fun isUserSignedIn(): Boolean {
        return auth.currentUser != null
    }

    fun signInUser(email: String, password: String): Task<AuthResult> {
        return auth.signInWithEmailAndPassword(email, password)
    }

    fun createUser(email: String, password: String): Task<AuthResult> {
        return auth.createUserWithEmailAndPassword(email, password)
    }

    fun signOut() {
        auth.signOut()
    }

    fun getTag(): DatabaseReference {
        return photosTagsReference
    }
}