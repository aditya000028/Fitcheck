package com.cmpt362.fitcheck.ui.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmpt362.fitcheck.R
import com.cmpt362.fitcheck.ui.friends.viewModels.FriendsViewModel

class CurrentFriendsFragment: Fragment() {

    private lateinit var myView: View

    private lateinit var friendsViewModel : FriendsViewModel
    private lateinit var friendsRecyclerView: RecyclerView
    lateinit var friendsAdapter: FriendsListAdapter

    companion object {
        const val TAB_TITLE = "Current Friends"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myView = inflater.inflate(R.layout.current_friends, container, false)

        initVariables()

        return myView
    }

    private fun initVariables() {
        friendsRecyclerView = myView.findViewById(R.id.friends_list)
        friendsRecyclerView.layoutManager = LinearLayoutManager(context)
        friendsRecyclerView.setHasFixedSize(true)

        friendsAdapter = FriendsListAdapter(FriendshipStatus.FRIENDS)
        friendsRecyclerView.adapter = friendsAdapter

        friendsViewModel = ViewModelProvider(this)[FriendsViewModel::class.java]
        friendsViewModel.friends.observe(viewLifecycleOwner) {
            friendsAdapter.updateUserList(it)
        }
    }

}