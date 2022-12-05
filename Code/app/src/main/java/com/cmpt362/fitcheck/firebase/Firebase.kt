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
import com.cmpt362.fitcheck.models.User
import com.cmpt362.fitcheck.ui.friends.FriendshipStatus
import com.google.android.gms.tasks.Task
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
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

    fun getUser(): FirebaseUser? {
        return auth.currentUser
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

    fun addUserToDatabase(user: User){
        usersReference.child(user.uid!!).setValue(user)
    }

    fun getTag(): DatabaseReference {
        return photosTagsReference
    }

    /**
     * Given a file uri and notes, upload image with metafile to cloud storage
     * under user's id and then current date.
     * Return the UploadTask object so Listeners can be added.
     */
    fun addPhoto(file: Uri, notes: String, tags: String): UploadTask? {
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
                setCustomMetadata(TAGS_METADATA_NAME, tags)
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
    fun getPhoto(year: Int, month: Int, day: Int, imageView: ImageView, notesText: TextView, tagsGroup: ChipGroup, context: Context, userID: String?) {
        // Check that userId is not null
        var uid = getUserId()
        if (userID != null){
            uid = userID
        }
        if (uid != null) {
            // Get and format given date
            println("debug: $year $month $day")
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

    /**
     * Downloads photo from cloud storage based on year, month, day given
     * and places photo in given ImageView.
     * If no photo exists, replace image with no_fit_found image
     */
    fun getFriendsPhoto(uid: String, year: Int, month: Int, day: Int, imageView: ImageView, context: Context) {
        // Check that userId is not null
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

            } .addOnFailureListener{
                Glide.with(context /* context */)
                    .load(R.drawable.no_fit_found)
                    .into(imageView)
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

    fun getQueriedUsers(queriedUsersLiveData: MutableLiveData<List<User>>, query: String) {
        usersReference.orderByChild("firstName").startAt(query).endAt(query + "\uf8ff")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val userList = ArrayList<User>()

                        snapshot.children.forEach{ dataSnapshot ->
                            if (dataSnapshot.key != getUserId()){
                                userList.add(dataSnapshot.getValue(User::class.java)!!)
                            }
                        }
                        queriedUsersLiveData.postValue(userList)
                    } catch (e: Exception){
                        println("debug: Exception when querying for users $e")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println("debug: unable to load all friends. Error message: ${error.message}")
                }

            })
    }

    fun loadAllFriends(friendsLiveData: MutableLiveData<List<User>>) {
        friendshipsReference.child(getUserId()!!).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val friendsUids = ArrayList<String>()
                    val friends = ArrayList<User>()

                    snapshot.children.forEach { dataSnapshot: DataSnapshot? ->
                        if (dataSnapshot?.key != null && dataSnapshot.getValue<Int>() == FriendshipStatus.FRIENDS.ordinal) {
                            friendsUids.add(dataSnapshot.key!!)
                        }
                    }
                    friendsUids.forEach { uid ->
                        usersReference.child(uid).get().addOnCompleteListener {
                            if (it.isSuccessful) {
                                friends.add(it.result.getValue<User>()!!)
                                friendsLiveData.postValue(friends)
                            } else {
                                println("debug: unable to get use with uid $uid")
                            }
                        }
                    }
                } catch (e: Exception){
                    println("debug: Exception when loading friends $e")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("debug: unable to load all friends. Error message: ${error.message}")
            }
        })
    }

    fun loadReceivedRequests(receivedRequestsLiveData: MutableLiveData<List<User>>) {
        friendshipsReference.child(getUserId()!!).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val receivedRequestsUids = ArrayList<String>()
                    val receivedRequestsUsers = ArrayList<User>()

                    snapshot.children.forEach { dataSnapshot: DataSnapshot? ->
                        if (dataSnapshot?.key != null && dataSnapshot.getValue<Int>() == FriendshipStatus.FRIEND_REQUEST_RECEIVED.ordinal) {
                            receivedRequestsUids.add(dataSnapshot.key!!)
                        }
                    }

                    // need to update the live data if there are no longer any more received uids
                    if (receivedRequestsUids.isEmpty()) {
                        receivedRequestsLiveData.postValue(receivedRequestsUsers)
                        return
                    }

                    receivedRequestsUids.forEach { uid ->
                        usersReference.child(uid).get().addOnCompleteListener {
                            if (it.isSuccessful) {
                                receivedRequestsUsers.add(it.result.getValue<User>()!!)
                                receivedRequestsLiveData.postValue(receivedRequestsUsers)
                            } else {
                                println("debug: unable to get use with uid $uid")
                            }
                        }
                    }
                } catch (e: Exception){
                    println("debug: Exception when loading friends $e")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("debug: unable to load all friends. Error message: ${error.message}")
            }
        })
    }

    fun loadSentRequests(sentRequestsLiveData: MutableLiveData<List<User>>) {
        friendshipsReference.child(getUserId()!!).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val sentRequestsUids = ArrayList<String>()
                    val sentRequestsUsers = ArrayList<User>()

                    snapshot.children.forEach { dataSnapshot: DataSnapshot? ->
                        if (dataSnapshot?.key != null && dataSnapshot.getValue<Int>() == FriendshipStatus.FRIEND_REQUEST_SENT.ordinal) {
                            sentRequestsUids.add(dataSnapshot.key!!)
                        }
                    }

                    // need to update the live data if there are no longer any more received uids
                    if (sentRequestsUids.isEmpty()) {
                        sentRequestsLiveData.postValue(sentRequestsUsers)
                        return
                    }

                    sentRequestsUids.forEach { uid ->
                        usersReference.child(uid).get().addOnCompleteListener {
                            if (it.isSuccessful) {
                                sentRequestsUsers.add(it.result.getValue<User>()!!)
                                sentRequestsLiveData.postValue(sentRequestsUsers)
                            } else {
                                println("debug: unable to get use with uid $uid")
                            }
                        }
                    }
                } catch (e: Exception){
                    println("debug: Exception when loading friends $e")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("debug: unable to load all friends. Error message: ${error.message}")
            }
        })
    }

    fun changeUserEmailInDB(email: String) {
        usersReference.child(getUserId()!!).child(UserKeys.EMAIL).setValue(email)
    }

    fun getFriendshipStatus(friendshipStatusLiveData: MutableLiveData<Int?>, uid: String) {
        friendshipsReference.child(getUserId()!!).child(uid).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val status = snapshot.getValue<Int>()
                friendshipStatusLiveData.postValue(status)
            }

            override fun onCancelled(error: DatabaseError) {
                println("debug: unable to get friendship status for $uid")
            }

        })
    }

    fun getUser(userLiveData: MutableLiveData<User>, uid: String) {
        usersReference.child(uid).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val targetUser = it.result.getValue<User>()
                userLiveData.postValue(targetUser)
            } else {
                println("debug: unable to get use with uid $uid")
            }
        }
    }

    fun sendFriendRequest(targetUserId: String) {
        val currentUserId = getUserId()!!
        friendshipsReference.child(currentUserId).child(targetUserId).setValue(FriendshipStatus.FRIEND_REQUEST_SENT.ordinal)
        friendshipsReference.child(targetUserId).child(currentUserId).setValue(FriendshipStatus.FRIEND_REQUEST_RECEIVED.ordinal)
    }

    fun denyFriendRequest(targetUserId: String) {
        removeFriendsRelationship(targetUserId)
    }

    fun acceptFriendRequest(targetUserId: String) {
        val currentUserId = getUserId()!!
        friendshipsReference.child(currentUserId).child(targetUserId).setValue(FriendshipStatus.FRIENDS.ordinal)
        friendshipsReference.child(targetUserId).child(currentUserId).setValue(FriendshipStatus.FRIENDS.ordinal)
    }

    fun unfriend(targetUserId: String) {
        removeFriendsRelationship(targetUserId)
    }

    private fun removeFriendsRelationship(targetUserId: String) {
        val currentUserId = getUserId()!!
        friendshipsReference.child(currentUserId).child(targetUserId).setValue(null)
        friendshipsReference.child(targetUserId).child(currentUserId).setValue(null)
    }
}