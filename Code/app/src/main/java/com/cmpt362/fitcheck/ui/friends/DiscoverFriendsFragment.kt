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
import com.cmpt362.fitcheck.ui.friends.viewModels.FriendRequestsViewModel

class DiscoverFriendsFragment: Fragment() {

    private lateinit var myView: View

    companion object {
        const val TAB_TITLE = "Discover"
    }

    private lateinit var friendRequestsViewModel: FriendRequestsViewModel
    private lateinit var receivedRequestsRecyclerView: RecyclerView
    lateinit var receivedRequestsAdapter: FriendsListAdapter

    private lateinit var sentRequestsRecyclerView: RecyclerView
    lateinit var sentRequestsAdapter: FriendsListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myView = inflater.inflate(R.layout.discover_friends, container, false)

        initVariables()

        return myView
    }

    private fun initVariables() {
        receivedRequestsRecyclerView = myView.findViewById(R.id.received_requests_list)
        receivedRequestsRecyclerView.layoutManager = LinearLayoutManager(context)
        receivedRequestsRecyclerView.setHasFixedSize(true)

        receivedRequestsAdapter = FriendsListAdapter(FriendshipStatus.FRIEND_REQUEST_RECEIVED)
        receivedRequestsRecyclerView.adapter = receivedRequestsAdapter

        friendRequestsViewModel = ViewModelProvider(this)[FriendRequestsViewModel::class.java]

        friendRequestsViewModel.receivedRequests.observe(viewLifecycleOwner) {
            receivedRequestsAdapter.updateUserList(it)
        }

        sentRequestsRecyclerView = myView.findViewById(R.id.sent_requests_list)
        sentRequestsRecyclerView.layoutManager = LinearLayoutManager(context)
        sentRequestsRecyclerView.setHasFixedSize(true)

        sentRequestsAdapter = FriendsListAdapter(FriendshipStatus.FRIEND_REQUEST_SENT)
        sentRequestsRecyclerView.adapter = sentRequestsAdapter

        friendRequestsViewModel.sentRequests.observe(viewLifecycleOwner) {
            sentRequestsAdapter.updateUserList(it)
        }
    }

}