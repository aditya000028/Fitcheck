package com.cmpt362.fitcheck.ui.calendar

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import com.cmpt362.fitcheck.R
import com.cmpt362.fitcheck.Util
import com.cmpt362.fitcheck.firebase.Firebase
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class DetailActivity: AppCompatActivity() {
    companion object {
        val USER_ID_KEY = "user_id"
        val USER_FULL_NAME_KEY = "user_full_name"
        val YEAR_KEY = "year"
        val MONTH_KEY = "month"
        val DAY_KEY = "day"
    }
    private lateinit var dateText: TextView
    private lateinit var titleText: TextView
    private lateinit var imageView: ImageView
    private lateinit var notesText: TextView
    private lateinit var locationText: TextView
    private lateinit var tagsText: AutoCompleteTextView
    private lateinit var chipGroup: ChipGroup
    private lateinit var editButtonView: Button

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val year: Int = intent.getIntExtra(YEAR_KEY, 2022)
        var month: Int = intent.getIntExtra(MONTH_KEY, 10)
        month += 1
        val day: Int = intent.getIntExtra(DAY_KEY, 30)

        dateText = findViewById(R.id.dateTextView)
        dateText.text = Util.convertMonthIntToString(month) + " $day, $year"

        imageView = findViewById(R.id.outfitImage)
        notesText = findViewById(R.id.notesText)
        locationText = findViewById(R.id.locationText)
        chipGroup  = findViewById(R.id.detailChipGroup)

        // Get user id if viewing a friends detail view
        val userID = intent.getStringExtra(USER_ID_KEY)

        if (userID != null) {
            editButtonView = findViewById(R.id.btnEdit)
            editButtonView.visibility = View.GONE

            titleText = findViewById(R.id.titleText)
            val fullName = intent.getStringExtra(USER_FULL_NAME_KEY)
            titleText.text = "${fullName}'s Fit On:"
        }

        Firebase.getPhoto(year, month, day, imageView, notesText, locationText, chipGroup, this, userID)
    }

    fun onDone(view: View){
        this@DetailActivity.finish()
    }

    fun onEdit(view: View){
        this@DetailActivity.finish()
    }
}