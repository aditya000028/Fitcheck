package com.cmpt362.fitcheck.ui.friends

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.cmpt362.fitcheck.R
import com.cmpt362.fitcheck.firebase.Firebase
import com.cmpt362.fitcheck.ui.friends.viewModels.ProfileViewModel

class ProfileActivity: AppCompatActivity() {

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var firstName: TextView
    private lateinit var lastName: TextView
    private lateinit var email: TextView
    private lateinit var friendshipButton: Button
    private lateinit var friendshipDenyButton: Button
    private lateinit var targetUserUID: String

    companion object {
        const val USER_ID_KEY = "USER_ID_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        initializeVariables()
    }

    private fun initializeVariables() {
        firstName = findViewById(R.id.user_fname)
        lastName = findViewById(R.id.user_lname)
        email = findViewById(R.id.user_email)
        friendshipButton = findViewById(R.id.friendship_button)
        friendshipDenyButton = findViewById(R.id.friendship_deny_button)
        targetUserUID = intent.extras!!.getString(USER_ID_KEY)!!

        friendshipDenyButton.visibility = View.GONE

        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        profileViewModel.user.observe(this) {
            firstName.text = it.firstName
            lastName.text = it.lastName
            email.text = it.email
        }

        profileViewModel.friendshipStatus.observe(this) {
            updateFriendshipButton(it)
        }

        profileViewModel.loadProfile(targetUserUID)
    }

    private fun updateFriendshipButton(friendshipStatus: Int?) {
        when (friendshipStatus) {
            FriendshipStatus.FRIENDS.ordinal -> {
                friendshipDenyButton.visibility = View.GONE
                friendshipButton.text = "Unfriend"
                friendshipButton.isEnabled = true
                friendshipButton.isClickable = true
                friendshipButton.setOnClickListener {
                    Firebase.unfriend(targetUserUID)
                }
            }
            FriendshipStatus.FRIEND_REQUEST_SENT.ordinal -> {
                friendshipDenyButton.visibility = View.GONE
                friendshipButton.text = "Request Sent"
                friendshipButton.isEnabled = false
                friendshipButton.isClickable = false
            }
            FriendshipStatus.FRIEND_REQUEST_RECEIVED.ordinal -> {
                friendshipDenyButton.visibility = View.VISIBLE
                friendshipDenyButton.setOnClickListener {
                    Firebase.denyFriendRequest(targetUserUID)
                }
                friendshipButton.text = "Accept Request"
                friendshipButton.setOnClickListener {
                    Firebase.acceptFriendRequest(targetUserUID)
                }
            } else -> {
                friendshipDenyButton.visibility = View.GONE
                friendshipButton.text = "Add friend"
                friendshipButton.isEnabled = true
                friendshipButton.isClickable = true
                friendshipButton.setOnClickListener {
                    Firebase.sendFriendRequest(targetUserUID)
                }
            }
        }
    }
}