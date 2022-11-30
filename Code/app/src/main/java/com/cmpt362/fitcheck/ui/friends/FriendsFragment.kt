package com.cmpt362.fitcheck.ui.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.cmpt362.fitcheck.databinding.FragmentFriendsBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class FriendsFragment : Fragment() {

    private var _binding: FragmentFriendsBinding? = null

    private lateinit var discoverFriendsFragment: DiscoverFriendsFragment
    private lateinit var currentFriendsFragment: CurrentFriendsFragment
    private lateinit var requestsFragment: RequestsFragment
    private lateinit var viewPager2: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var fragmentStateAdapter: FriendsFragmentStateAdapter
    private lateinit var fragmentList: ArrayList<Fragment>
    private lateinit var tabTitles: ArrayList<String>
    private lateinit var tabConfigurationStrategy: TabLayoutMediator.TabConfigurationStrategy
    private lateinit var tabLayoutMediator: TabLayoutMediator

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

        discoverFriendsFragment = DiscoverFriendsFragment()
        currentFriendsFragment = CurrentFriendsFragment()
        requestsFragment = RequestsFragment()

        fragmentList = ArrayList()
        fragmentList.add(discoverFriendsFragment)
        fragmentList.add(requestsFragment)
        fragmentList.add(currentFriendsFragment)

        tabTitles = ArrayList()
        tabTitles.add(DiscoverFriendsFragment.TAB_TITLE)
        tabTitles.add(RequestsFragment.TAB_TITLE)
        tabTitles.add(CurrentFriendsFragment.TAB_TITLE)

        fragmentStateAdapter = FriendsFragmentStateAdapter(requireActivity(), fragmentList)
        viewPager2.adapter = fragmentStateAdapter

        tabConfigurationStrategy = TabLayoutMediator.TabConfigurationStrategy { tab, position ->
            tab.text = tabTitles[position]
        }
        tabLayoutMediator = TabLayoutMediator(tabLayout, viewPager2, tabConfigurationStrategy)
        tabLayoutMediator.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabLayoutMediator.detach()
        _binding = null
    }
}