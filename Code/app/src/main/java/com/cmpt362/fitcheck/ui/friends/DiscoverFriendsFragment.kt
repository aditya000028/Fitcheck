package com.cmpt362.fitcheck.ui.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmpt362.fitcheck.R
import com.cmpt362.fitcheck.ui.friends.viewModels.UserQueryViewModel

class DiscoverFriendsFragment: Fragment() {

    private lateinit var myView: View

    companion object {
        const val TAB_TITLE = "Discover Friends"
    }

    private lateinit var userQueryViewModel: UserQueryViewModel
    private lateinit var userQueryRecyclerView: RecyclerView
    private lateinit var userQueryAdapter: FriendsListAdapter

    private lateinit var querySearchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myView = inflater.inflate(R.layout.fragment_users_search, container, false)

        initVariables()

        return myView
    }

    private fun initVariables() {
        userQueryRecyclerView = myView.findViewById(R.id.user_search_result_list)
        userQueryRecyclerView.layoutManager = LinearLayoutManager(context)
        userQueryRecyclerView.setHasFixedSize(true)

        userQueryAdapter = FriendsListAdapter(null)
        userQueryRecyclerView.adapter = userQueryAdapter

        userQueryViewModel = ViewModelProvider(this)[UserQueryViewModel::class.java]

        userQueryViewModel.queriedUsers.observe(viewLifecycleOwner) {
            userQueryAdapter.updateUserList(it)
        }

        querySearchView = myView.findViewById(R.id.user_search_query)

        querySearchView.setOnQueryTextListener( object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                println("debug: - search submit - $query")
                if (query != null) {
                    userQueryViewModel.getQueriedUsers(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                println("debug: - search change - $newText")

                if (newText == ""){
                    userQueryViewModel.clearQueriedUsers()
                }

                return false
            }

        })
    }
}