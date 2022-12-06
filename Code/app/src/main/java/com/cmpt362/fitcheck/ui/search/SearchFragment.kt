package com.cmpt362.fitcheck.ui.search

import android.R.id
import android.R.id.closeButton
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cmpt362.fitcheck.R
import com.cmpt362.fitcheck.databinding.ActivityAddPhotoBinding.inflate
import com.cmpt362.fitcheck.databinding.FragmentSearchBinding
import com.cmpt362.fitcheck.databinding.SearchViewBinding.inflate
import com.cmpt362.fitcheck.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private lateinit var searchView: SearchView
    private lateinit var textView: TextView
    val arrayList: ArrayList<com.cmpt362.fitcheck.ui.search.SearchView> = arrayListOf()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val searchViewModel =
            ViewModelProvider(this).get(SearchViewModel::class.java)

        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root

        searchView = root.findViewById(R.id.photo_search_query)
        textView = root.findViewById(R.id.noImageTextView)
        val searchArrayAdapter = SearchViewAdapter(root.context, arrayList)
        searchView.queryHint = "Search for fits by tags"
        var searchText = ""
        searchView.setOnQueryTextListener( object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    searchText = query
                    val isSuccess = checkDatabase(searchText, searchArrayAdapter)
                    if(isSuccess == 1){

                    } else {

                    }
                    searchArrayAdapter.notifyDataSetChanged()
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText?.isBlank() == true){
                    textView.text = ""
                    textView.visibility = View.GONE
                    arrayList.clear()
                    searchArrayAdapter.notifyDataSetChanged()
                    return false
                }
                return false
            }
        })

        val numbersListView: ListView = root.findViewById(R.id.listView)

        numbersListView.adapter = searchArrayAdapter

        val closeBtnId = searchView.context.resources
            .getIdentifier("android:id/search_close_btn", null, null)
        val closeBtn = searchView.findViewById<ImageView>(closeBtnId)
        closeBtn?.setOnClickListener {
            arrayList.clear()
            textView.text = ""
            textView.visibility = View.GONE
            searchView.setQuery("", false)
            searchArrayAdapter.notifyDataSetChanged()
        }
        return root
    }

    fun checkDatabase(searchText: String, adapter: SearchViewAdapter) : Int {
        val tagReference = Firebase.getTag()
        var success: Int = 0
        val eventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                arrayList.clear()
                if(!dataSnapshot.exists()){
                    println("inside empty arraylist")
                    textView.text = "No photos found"
                    textView.visibility = View.VISIBLE
                }
                for (ds: DataSnapshot in dataSnapshot.children) {
                    val keyValue: String = ds.value as String
                    textView.text = ""
                    textView.visibility = View.GONE
                    println("should be a success $success")
                    arrayList.add(SearchView(keyValue, "1", "One"))
                    adapter.notifyDataSetChanged()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        }
        tagReference.child(searchText).addListenerForSingleValueEvent(eventListener)
        return success
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}