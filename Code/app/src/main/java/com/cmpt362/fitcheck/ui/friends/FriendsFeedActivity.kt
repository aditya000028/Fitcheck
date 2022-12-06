package com.cmpt362.fitcheck.ui.friends

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmpt362.fitcheck.R
import com.cmpt362.fitcheck.firebase.Firebase
import com.cmpt362.fitcheck.ui.friends.viewModels.ItemsViewModel
import com.cmpt362.fitcheck.ui.friends.viewModels.ProfileViewModel
import com.cmpt362.fitcheck.ui.settings.notifications.SettingsViewModel
import java.util.*
import kotlin.collections.ArrayList


class FriendsFeedActivity : AppCompatActivity() {
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var userName: TextView
    private lateinit var privateTextView: TextView
    private lateinit var friendshipButton: Button
    private lateinit var friendshipDenyButton: Button
    private lateinit var targetUserUID: String
    private lateinit var fullName: String

    private lateinit var recyclerview: RecyclerView

    private var targetUserIsPublic: Boolean? = null
    private var friendshipStatus: Int? = null

    companion object {
        const val USER_ID_KEY = "USER_ID_KEY"
        const val USER_FULL_NAME_KEY = "USER_FULL_NAME_KEY"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_feed)

        initializeVariables()

        // Get recyclerView
        recyclerview = findViewById(R.id.recyclerView)

        // Create linear layout manager
        recyclerview.layoutManager = GridLayoutManager(this,2)

        // ArrayList of class ItemsViewModel
        val data = ArrayList<ItemsViewModel>()

        // Loop through dates
        var flag = 0
        var i = 0
        while (flag == 0) {
            val date = Calendar.getInstance()
            date.add(Calendar.DATE, -i)
            // Starting date
            if (date.get(Calendar.YEAR) == 2022 && date.get(Calendar.MONTH) +1 == 11 && date.get(Calendar.DAY_OF_MONTH) == 29){
                flag = 1
            } else {
                data.add(ItemsViewModel(targetUserUID, fullName, date.get(Calendar.YEAR), date.get(Calendar.MONTH) +1, date.get(Calendar.DAY_OF_MONTH)))
            }
            i++
        }

        val adapter = RecyclerViewAdapter(data, this)
        recyclerview.adapter = adapter

    }

    private fun initializeVariables() {
        userName = findViewById(R.id.textUser)
        privateTextView = findViewById(R.id.private_user_text)
        friendshipButton = findViewById(R.id.friendship_button)
        friendshipDenyButton = findViewById(R.id.friendship_deny_button)

        privateTextView.visibility = View.GONE
        friendshipDenyButton.visibility = View.GONE

        targetUserUID = intent.extras!!.getString(USER_ID_KEY)!!
        fullName = intent.extras!!.getString(USER_FULL_NAME_KEY)!!

        userName.text = fullName

        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        // Update the full name of the target User if they make a change
        profileViewModel.user.observe(this) {
            userName.text =  "${it.firstName} ${it.lastName}";
        }

        profileViewModel.friendshipStatus.observe(this) {
            updateFriendshipButton(it)
            friendshipStatus = it
            displayPhotos()
        }
        profileViewModel.loadProfile(targetUserUID)

        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
        if (settingsViewModel.settings.value == null){
            settingsViewModel.loadUserSetting(targetUserUID)
        }
        settingsViewModel.settings.observe(this) {
            targetUserIsPublic = it?.profileIsPublic
            displayPhotos()
        }
    }

    private fun updateFriendshipButton(friendshipStatus: Int?) {
        when (friendshipStatus) {
            // currently friends
            FriendshipStatus.FRIENDS.ordinal -> {
                friendshipDenyButton.visibility = View.GONE
                friendshipButton.text = "Unfriend"
                friendshipButton.isEnabled = true
                friendshipButton.isClickable = true
                friendshipButton.setOnClickListener {
                    Firebase.unfriend(targetUserUID)
                }
            }
            // currently sent a friend request
            FriendshipStatus.FRIEND_REQUEST_SENT.ordinal -> {
                friendshipButton.text = "Request Sent"
                friendshipButton.isEnabled = false
                friendshipButton.isClickable = false
            }
            // currently received a friend request
            FriendshipStatus.FRIEND_REQUEST_RECEIVED.ordinal -> {
                friendshipDenyButton.visibility = View.VISIBLE
                friendshipDenyButton.setOnClickListener {
                    Firebase.denyFriendRequest(targetUserUID)
                }
                friendshipButton.text = "Accept Request"
                friendshipButton.setOnClickListener {
                    Firebase.acceptFriendRequest(targetUserUID)
                }
            // currently not friends
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

    private fun displayPhotos(){
        // if friends then can show photos
        if (friendshipStatus == FriendshipStatus.FRIENDS.ordinal || targetUserIsPublic == true){
            if (!recyclerview.isVisible){
                privateTextView.visibility = View.GONE
                recyclerview.visibility = View.VISIBLE
            }
        }
        // otherwise don't show photos
        else {
            if (recyclerview.isVisible){
                recyclerview.visibility = View.GONE
                privateTextView.visibility = View.VISIBLE
            }
        }
    }
}