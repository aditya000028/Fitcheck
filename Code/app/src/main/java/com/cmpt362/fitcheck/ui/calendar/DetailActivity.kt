package com.cmpt362.fitcheck.ui.calendar

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cmpt362.fitcheck.R

class DetailActivity: AppCompatActivity() {
    private lateinit var dateText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val year: Int = intent.getIntExtra("year", 0)
        var month: Int = intent.getIntExtra("month", 0)
        month += 1
        val day: Int = intent.getIntExtra("day", 0)

        dateText = findViewById(R.id.dateTextView)
        dateText.text = convertToString(month) + " $day, $year"
    }

    fun convertToString(month: Int): String {
        var monthString = "default"
        when (month) {
            1 -> monthString = "Jan"
            2 -> monthString = "Feb"
            3 -> monthString = "Mar"
            4 -> monthString = "Apr"
            5 -> monthString = "May"
            6 -> monthString = "June"
            7 -> monthString = "July"
            8 -> monthString = "Aug"
            9 -> monthString = "Sept"
            10 -> monthString = "Oct"
            11 -> monthString = "Nov"
            12 -> monthString = "Dec"
        }
        return monthString
    }

    fun onDone(view: View){
        this@DetailActivity.finish()
    }

    fun onEdit(view: View){
        this@DetailActivity.finish()
    }
}