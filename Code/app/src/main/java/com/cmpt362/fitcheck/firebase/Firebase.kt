package com.cmpt362.fitcheck.firebase

import android.content.Context
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.cmpt362.fitcheck.R
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
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
    private const val TAGS_METADATA_NAME = "Tags"

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

    // Add the new user to the database so we can search all users
    fun addUserToDatabase(email: String, uid: String){
        usersReference.child(uid).setValue(User(email, uid))
    }

    fun getTag(): DatabaseReference {
        return photosTagsReference
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
    fun getPhoto(year: Int, month: Int, day: Int, imageView: ImageView, notesText: TextView, tagsGroup: ChipGroup, context: Context) {
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

                val tags = metadata.getCustomMetadata(TAGS_METADATA_NAME)
                if(tags != null){
                    val arrayOfTags = fromStringToArrayList(tags)
                    for(item in arrayOfTags){
                        addChipToGroup(item, tagsGroup, context)
                    }
                }

            }
        }

    }

    private fun addChipToGroup(tag: String, group: ChipGroup, context: Context) {
        val chip = Chip(context)
        chip.text = tag
        chip.chipIcon = ContextCompat.getDrawable(context, R.drawable.ic_launcher_background)
        chip.isChipIconVisible = false
        chip.isCloseIconVisible = true
        chip.isClickable = false
        chip.isCheckable = false
        group.addView(chip as View)
        chip.setOnCloseIconClickListener {
            group.removeView(chip as View)
        }
    }

    fun fromStringToArrayList(s: String): ArrayList<String> {
        val newArray: ArrayList<String> = arrayListOf()
        val strs = s.split(",")
        for(item in strs){
            newArray.add(item)
        }
        newArray.removeLast()
        return newArray
    }

    // function for getting all users from the database with a view Model
    fun loadAllUsers(allUsers: MutableLiveData<List<User>>) {
        usersReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {

                    val userList : List<User> = snapshot.children.map { dataSnapshot ->
                        println("debug: dataSnapshot - $dataSnapshot")
                        dataSnapshot.getValue(User::class.java)!!

                    }
                    println("debug: userList - $userList")
                    allUsers.postValue(userList)
                } catch (e: Exception){
                    println("debug: Exception when loading users $e")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("debug: onCancelled when loading Users $error")
            }
        })
    }
}