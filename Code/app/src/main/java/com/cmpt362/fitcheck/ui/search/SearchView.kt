package com.cmpt362.fitcheck.ui.search

import android.net.Uri

class SearchView(imageUri: Uri, NumbersInDigit: String?, NumbersInText: String?) {
    // the resource ID for the imageView
    private var iUri: Uri = imageUri

    // getter method for returning the ID of the imageview
    fun getImageUri(): Uri {
        return iUri
    }
}