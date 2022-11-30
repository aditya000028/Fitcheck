package com.cmpt362.fitcheck.ui.friends.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cmpt362.fitcheck.firebase.Firebase
import com.cmpt362.fitcheck.models.User

class UserQueryViewModel : ViewModel() {

    private val _queriedUsers = MutableLiveData<List<User>>()
    val queriedUsers : LiveData<List<User>> = _queriedUsers


    fun getQueriedUsers(query: String) {
        Firebase.getQueriedUsers(_queriedUsers, query)
    }

    fun clearQueriedUsers() {
        _queriedUsers.postValue(ArrayList())
    }
}