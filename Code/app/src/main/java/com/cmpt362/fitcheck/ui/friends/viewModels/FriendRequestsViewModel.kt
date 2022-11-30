package com.cmpt362.fitcheck.ui.friends.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cmpt362.fitcheck.firebase.Firebase
import com.cmpt362.fitcheck.models.User

class FriendRequestsViewModel : ViewModel() {

    private val _receivedRequests = MutableLiveData<List<User>>()
    val receivedRequests : LiveData<List<User>> = _receivedRequests

    private val _sentRequests = MutableLiveData<List<User>>()
    val sentRequests : LiveData<List<User>> = _sentRequests

    init {
        Firebase.loadReceivedRequests(_receivedRequests)

//        Firebase.loadAllUsers(_allUsers)
    }
}