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
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.cmpt362.fitcheck.databinding.FragmentFriendsBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class FriendsFragment : Fragment() {

    private var _binding: FragmentFriendsBinding? = null

    private lateinit var currentFriendsFragment: CurrentFriendsFragment
    private lateinit var discoverFriendsFragment: DiscoverFriendsFragment
    private lateinit var viewPager2: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var fragmentStateAdapter: FriendsFragmentStateAdapter
    private lateinit var fragmentList: ArrayList<Fragment>
    private lateinit var tabTitles: ArrayList<String>
    private lateinit var tabConfigurationStrategy: TabLayoutMediator.TabConfigurationStrategy
    private lateinit var tabLayoutMediator: TabLayoutMediator

//    private lateinit var allUsersViewModel : AllUsersViewModel
//    private lateinit var allUsersRecyclerView: RecyclerView
//    lateinit var allUsersAdapter: FriendsAdapter
//
//    private lateinit var friendsViewModel : FriendsViewModel
//    private lateinit var friendsRecyclerView: RecyclerView
//    lateinit var friendsAdapter: FriendsAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFriendsBinding.inflate(inflater, container, false)

        initializeTabs()

        return binding.root
    }

    private fun initializeTabs() {
        viewPager2 = binding.viewpager
        tabLayout = binding.tabs

        currentFriendsFragment = CurrentFriendsFragment()
        discoverFriendsFragment = DiscoverFriendsFragment()

        fragmentList = ArrayList()
        fragmentList.add(discoverFriendsFragment)
        fragmentList.add(currentFriendsFragment)

        tabTitles = ArrayList()
        tabTitles.add(DiscoverFriendsFragment.TAB_TITLE)
        tabTitles.add(CurrentFriendsFragment.TAB_TITLE)

        fragmentStateAdapter = FriendsFragmentStateAdapter(requireActivity(), fragmentList)
        viewPager2.adapter = fragmentStateAdapter

        tabConfigurationStrategy = TabLayoutMediator.TabConfigurationStrategy { tab, position ->
            tab.text = tabTitles[position]
        }
        tabLayoutMediator = TabLayoutMediator(tabLayout, viewPager2, tabConfigurationStrategy)
        tabLayoutMediator.attach()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        allUsersRecyclerView = binding.allUsersList
//        allUsersRecyclerView.layoutManager = LinearLayoutManager(context)
//        allUsersRecyclerView.setHasFixedSize(true)
//        allUsersAdapter = FriendsAdapter(null)
//        allUsersRecyclerView.adapter = allUsersAdapter
//
//        allUsersViewModel = ViewModelProvider(this)[AllUsersViewModel::class.java]
//
//        allUsersViewModel.allUsers.observe(viewLifecycleOwner, Observer {
//            allUsersAdapter.updateUserList(it)
//        })
//
//        friendsRecyclerView = binding.friendsList
//        friendsRecyclerView.layoutManager = LinearLayoutManager(context)
//        friendsRecyclerView.setHasFixedSize(true)
//        friendsAdapter = FriendsAdapter(FriendshipStatus.FRIENDS)
//        friendsRecyclerView.adapter = friendsAdapter
//        friendsViewModel = ViewModelProvider(this)[FriendsViewModel::class.java]
//        friendsViewModel.friends.observe(viewLifecycleOwner) {
//            friendsAdapter.updateUserList(it)
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabLayoutMediator.detach()
        _binding = null
    }
}