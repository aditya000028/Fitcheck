package com.cmpt362.fitcheck.ui.friends

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmpt362.fitcheck.R
import com.cmpt362.fitcheck.firebase.Firebase
import com.cmpt362.fitcheck.ui.friends.viewModels.ItemsViewModel
import com.cmpt362.fitcheck.ui.friends.viewModels.ProfileViewModel
import java.util.*
import kotlin.collections.ArrayList


class FriendsFeedActivity : AppCompatActivity() {
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var userName: TextView
    private lateinit var friendshipButton: Button
    private lateinit var targetUserUID: String
    private lateinit var fullName: String

    companion object {
        const val USER_ID_KEY = "USER_ID_KEY"
        const val USER_FULL_NAME_KEY = "USER_FULL_NAME_KEY"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_feed)

        initializeVariables()

        // Get recyclerView
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerView)

        // Create linear layout manager
        recyclerview.layoutManager = LinearLayoutManager(this)

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
        friendshipButton = findViewById(R.id.friendshipBtn)

        targetUserUID = intent.extras!!.getString(USER_ID_KEY)!!
        fullName = intent.extras!!.getString(USER_FULL_NAME_KEY)!!

        userName.text = fullName

        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        profileViewModel.friendshipStatus.observe(this) {
            friendshipButton.setOnClickListener {
                Firebase.unfriend(targetUserUID)
            }
        }
        profileViewModel.loadProfile(targetUserUID)
    }
}