package com.cmpt362.fitcheck.ui.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmpt362.fitcheck.databinding.FragmentFriendsBinding

class FriendsFragment : Fragment() {

    private var _binding: FragmentFriendsBinding? = null

    private lateinit var allUsersViewModel : AllUsersViewModel
    private lateinit var allUsersRecyclerView: RecyclerView
    lateinit var allUsersAdapter: FriendsAdapter

//    private lateinit var pendingRequestsViewModel : AllUsersViewModel
//    private lateinit var pendingRequestsRecyclerView: RecyclerView
//    lateinit var pendingRequestsAdapter: FriendsAdapter

    private lateinit var friendsViewModel : FriendsViewModel
    private lateinit var friendsRecyclerView: RecyclerView
    lateinit var friendsAdapter: FriendsAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFriendsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        allUsersRecyclerView = binding.allUsersList
        allUsersRecyclerView.layoutManager = LinearLayoutManager(context)
        allUsersRecyclerView.setHasFixedSize(true)
        allUsersAdapter = FriendsAdapter(null)
        allUsersRecyclerView.adapter = allUsersAdapter

        allUsersViewModel = ViewModelProvider(this)[AllUsersViewModel::class.java]

        allUsersViewModel.allUsers.observe(viewLifecycleOwner, Observer {
            allUsersAdapter.updateUserList(it)
        })

//        pendingRequestsRecyclerView = binding.pendingFriendList
//        pendingRequestsRecyclerView.layoutManager = LinearLayoutManager(context)
//        pendingRequestsRecyclerView.setHasFixedSize(true)
//        pendingRequestsAdapter = FriendsAdapter()
//        pendingRequestsRecyclerView.adapter = pendingRequestsAdapter
//        pendingRequestsViewModel = ViewModelProvider(this)[AllUsersViewModel::class.java]
//        pendingRequestsViewModel.allUsers.observe(viewLifecycleOwner, Observer {
//            pendingRequestsAdapter.updateUserList(it)
//        })

        friendsRecyclerView = binding.friendsList
        friendsRecyclerView.layoutManager = LinearLayoutManager(context)
        friendsRecyclerView.setHasFixedSize(true)
        friendsAdapter = FriendsAdapter(FriendshipStatus.FRIENDS)
        friendsRecyclerView.adapter = friendsAdapter
        friendsViewModel = ViewModelProvider(this)[FriendsViewModel::class.java]
        friendsViewModel.friends.observe(viewLifecycleOwner, Observer {
            println("debug: friends list - $it")
            friendsAdapter.updateUserList(it)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}