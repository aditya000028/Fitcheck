package com.cmpt362.fitcheck.ui.calendar

import android.widget.CalendarView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CalendarViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Calendar Fragment"
    }
    val text: LiveData<String> = _text
}