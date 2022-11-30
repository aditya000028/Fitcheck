package com.cmpt362.fitcheck.ui.friends.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cmpt362.fitcheck.firebase.Firebase
import com.cmpt362.fitcheck.models.User

class FriendsViewModel : ViewModel() {

    private val _friends = MutableLiveData<List<User>>()
    val friends : LiveData<List<User>> = _friends

    init {
        Firebase.loadAllFriends(_friends)
    }
}