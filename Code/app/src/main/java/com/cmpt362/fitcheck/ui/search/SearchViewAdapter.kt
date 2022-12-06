package com.cmpt362.fitcheck.ui.search

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.cmpt362.fitcheck.R
import com.cmpt362.fitcheck.models.User

class SearchViewAdapter(@NonNull context: Context, arrayList: ArrayList<SearchView>) :
    ArrayAdapter<SearchView>(context, 0, arrayList) {

    @NonNull
    override fun getView(position: Int, @Nullable convertView: View?, @NonNull parent: ViewGroup): View {

        // convertView which is recyclable view
        var currentItemView: View? = convertView

        // of the recyclable view is null then inflate the custom layout for the same
        if (currentItemView == null) {
            currentItemView =
                LayoutInflater.from(context).inflate(R.layout.search_view, parent, false)
        }

        // get the position of the view from the ArrayAdapter
        assert(getItem(position) != null)
        val currentNumberPosition: SearchView = getItem(position)!!

        // then according to the position of the view assign the desired image for the same
        val numbersImage: ImageView = currentItemView!!.findViewById(R.id.imageView)
        numbersImage.setImageURI(currentNumberPosition.getImageUri())
        notifyDataSetChanged()

        // then return the recyclable view
        return currentItemView
    }
}