package com.cmpt362.fitcheck.ui.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cmpt362.fitcheck.R

class DiscoverFriendsFragment: Fragment() {

    private lateinit var myView: View

    companion object {
        const val TAB_TITLE = "Discover"
    }

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

    }

}