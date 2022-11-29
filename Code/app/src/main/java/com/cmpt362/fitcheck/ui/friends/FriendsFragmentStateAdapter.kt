package com.cmpt362.fitcheck.ui.friends

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.util.ArrayList

class FriendsFragmentStateAdapter(activity: FragmentActivity, var fragmentList: ArrayList<Fragment>)
    : FragmentStateAdapter(activity){

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getItemCount(): Int {
        return fragmentList.size
    }
}