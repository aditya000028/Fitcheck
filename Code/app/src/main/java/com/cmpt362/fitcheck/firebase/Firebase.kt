package com.cmpt362.fitcheck.firebase

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Singleton class for handling all Firebase related operations
 */
object Firebase {

    private const val DATE_TIME_FORMAT = "yyyy-MM-dd"
    private const val USERS_REFERENCE_NAME = "users"
    private const val FRIENDSHIPS_REFERENCE_NAME = "friendships"
    private const val USER_PHOTOS_REFERENCE_NAME = "user_photos"
    private const val PHOTOS_TAGS_REFERENCE_NAME = "photos_tags"
    private const val PHOTOS_REFERENCE_NAME = "photos"
    private const val NOTES_METADATA_NAME = "Notes"

    private val auth: FirebaseAuth = Firebase.auth
    private val database: FirebaseDatabase = Firebase.database
    private val storage: FirebaseStorage = Firebase.storage

    private val usersReference: DatabaseReference
    private val friendshipsReference: DatabaseReference
    private val userPhotosReference: DatabaseReference
    private val photosTagsReference: DatabaseReference
    private val storageRef: StorageReference

    init {
        usersReference = database.getReference(USERS_REFERENCE_NAME)
        friendshipsReference = database.getReference(FRIENDSHIPS_REFERENCE_NAME)
        userPhotosReference = database.getReference(USER_PHOTOS_REFERENCE_NAME)
        photosTagsReference = database.getReference(PHOTOS_TAGS_REFERENCE_NAME)
        storageRef = storage.getReference(PHOTOS_REFERENCE_NAME)
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

    fun getUserId(): String? {
        if (isUserSignedIn()) {
            return auth.currentUser!!.uid
        }
        return null
    }

    /**
     * Given a file uri and notes, upload image with metafile to cloud storage
     * under user's id and then current date.
     * Return the UploadTask object so Listeners can be added.
     */
    fun addPhoto(file: Uri, notes: String): UploadTask? {
        // Check that userId is not null
        val uid = getUserId()
        if (uid != null) {
            // Get and format current date
            val formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)
            val currentDate = LocalDateTime.now().format(formatter)

            // Store in user id -> date
            val photoRef = storageRef.child(uid).child(currentDate)

            // Add custom metadata
            val metadata = storageMetadata {
                setCustomMetadata(NOTES_METADATA_NAME, notes)
            }

            // Upload photo and metadata to cloud storage
            return photoRef.putFile(file, metadata)
        }

        return null
    }

    /**
     * Downloads photo from cloud storage based on year, month, day given
     * and places photo in given ImageView.
     */
    fun getPhoto(year: Int, month: Int, day: Int, imageView: ImageView, notesText: TextView, context: Context) {
        // Check that userId is not null
        val uid = getUserId()
        if (uid != null) {
            // Get and format given date
            val date = LocalDate.of(year, month, day)
            val formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)
            val dateStr = date.format(formatter)

            // Needed photo is stored in user id -> date
            val photoRef = storageRef.child(uid).child(dateStr)

            photoRef.downloadUrl.addOnSuccessListener {Uri->
                val imageURL = Uri.toString()

                // Download photo and place in ImageView
                Glide.with(context /* context */)
                    .load(imageURL)
                    .into(imageView)
            }

            photoRef.metadata.addOnSuccessListener { metadata ->
                // Get metadata and put entry notes into notes TextView
                val notes = metadata.getCustomMetadata(NOTES_METADATA_NAME)
                notesText.text = notes
            }
        }

    }
}