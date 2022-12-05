package com.cmpt362.fitcheck.ui.calendar

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cmpt362.fitcheck.R
import com.cmpt362.fitcheck.firebase.Firebase
import com.google.android.material.chip.ChipGroup


class DetailActivity: AppCompatActivity() {
    private lateinit var dateText: TextView
    private lateinit var imageView: ImageView
    private lateinit var notesText: TextView
    private lateinit var tagsText: AutoCompleteTextView
    private lateinit var chipGroup: ChipGroup

    private var yearIntent: Int = 0
    private var monthIntent: Int = 0
    private var dayIntent: Int = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val year: Int = intent.getIntExtra("year", 0)
        yearIntent = year
        var month: Int = intent.getIntExtra("month", 0)
        monthIntent = month
        month += 1

        val day: Int = intent.getIntExtra("day", 0)
        dayIntent = day

        dateText = findViewById(R.id.dateTextView)
        dateText.text = convertToString(month) + " $day, $year"

        imageView = findViewById(R.id.outfitImage)
        notesText = findViewById(R.id.notesText)
        chipGroup = findViewById(R.id.detailChipGroup)

        Firebase.getPhoto(year, month, day, imageView, notesText, chipGroup, this)
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
        val myIntent = Intent(this, EditActivity::class.java)
        myIntent.putExtra("year", yearIntent)
        myIntent.putExtra("month", monthIntent)
        myIntent.putExtra("day", dayIntent)
        startActivity(myIntent)
        finish()
    }
}