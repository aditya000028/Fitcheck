package com.cmpt362.fitcheck.ui.search

import android.net.Uri

class SearchView(path: String, NumbersInDigit: String?, NumbersInText: String?) {
    // the resource ID for the imageView
    private var path: String = path

    // getter method for returning the ID of the imageview
    fun getImagePath(): String {
        return path
    }
}