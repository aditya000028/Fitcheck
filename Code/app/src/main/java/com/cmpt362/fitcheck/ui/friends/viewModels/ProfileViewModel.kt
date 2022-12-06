package com.cmpt362.fitcheck.ui.friends.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cmpt362.fitcheck.firebase.Firebase
import com.cmpt362.fitcheck.models.User

class ProfileViewModel: ViewModel() {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _friendshipStatus = MutableLiveData<Int?>()
    val friendshipStatus: LiveData<Int?> = _friendshipStatus

    fun loadUser(targetUID: String) {
        Firebase.getUser(_user, targetUID)
    }

    fun loadFriendships(targetUID: String) {
        Firebase.getFriendshipStatus(_friendshipStatus, targetUID)
    }

    fun loadProfile(targetUID: String) {
        loadUser(targetUID)
        loadFriendships(targetUID)
    }
}