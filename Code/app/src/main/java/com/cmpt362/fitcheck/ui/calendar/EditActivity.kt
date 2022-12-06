package com.cmpt362.fitcheck.ui.calendar

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.cmpt362.fitcheck.R
import com.cmpt362.fitcheck.firebase.Firebase
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class EditActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var notesText: EditText
    private lateinit var tagEditText: AutoCompleteTextView
    private lateinit var dateText: TextView
    private lateinit var locationText: TextView
    private lateinit var chipGroup: ChipGroup
    private var tagArray: ArrayList<String> = arrayListOf()
    private var databaseTagArray: ArrayList<String> = arrayListOf()
    private var databaseUriArray: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        imageView = findViewById(R.id.outfitImage)
        notesText = findViewById(R.id.notesText)
        locationText = findViewById(R.id.locationText)
        chipGroup = findViewById(R.id.chipGroup)

        val year: Int = intent.getIntExtra("year", 0)
        var month: Int = intent.getIntExtra("month", 0)
        val day: Int = intent.getIntExtra("day", 0)

        val metadataTagArray = Firebase.getTags(year, month, day)

        for(item in metadataTagArray) {
            addChipToGroup(item)
            tagArray.add(item)
        }

        dateText = findViewById(R.id.dateTextView)
        dateText.text = convertToString(month) + " $day, $year"

        val tagReference = Firebase.getTag()

        val eventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds: DataSnapshot in dataSnapshot.children) {
                    val keyValue = ds.getValue(String::class.java)
                    databaseTagArray.add(keyValue!!)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        }
        tagReference.child("tags").addListenerForSingleValueEvent(eventListener)

        tagEditText = findViewById(R.id.photoTags)
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, databaseTagArray)
        tagEditText.setAdapter(adapter)
        tagEditText.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if ((keyCode == KeyEvent.KEYCODE_ENTER) && (event.action == KeyEvent.ACTION_UP)) {
                val value = tagEditText.text.toString()
                addChipToGroup(value)
                tagArray.add(value)
                val inputMethodManager = getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
                tagEditText.text.clear()
                return@OnKeyListener true
            }
            return@OnKeyListener false
        })

        // Get user id if viewing a friends detail view
        val userID = intent.getStringExtra(DetailActivity.USER_ID_KEY)

        Firebase.getPhoto(year, month, day, imageView, notesText, locationText, chipGroup, this, userID)
    }

    private fun addChipToGroup(tag: String) {
        val chip = Chip(this)
        chip.text = tag
        chip.chipIcon = ContextCompat.getDrawable(this, R.drawable.ic_launcher_background)
        chip.isChipIconVisible = false
        chip.isCloseIconVisible = true
        chip.isClickable = false
        chip.isCheckable = false
        chipGroup.addView(chip as View)
        chip.setOnCloseIconClickListener {
            chipGroup.removeView(chip as View)
            tagArray.remove(tag)
        }
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

    /**
     * When save button is click, save the upload and finish the activity
     */
    fun onSaveUpload(view: View){
        val year: Int = intent.getIntExtra("year", 0)
        var month: Int = intent.getIntExtra("month", 0)
        val day: Int = intent.getIntExtra("day", 0)
        val tagReference = Firebase.getTag()
        val eventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds: DataSnapshot in dataSnapshot.children) {
                    val keyValue = ds.getValue(String::class.java)
                    databaseTagArray.add(keyValue!!)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        }
        tagReference.child("tags").addListenerForSingleValueEvent(eventListener)

        val eventListenerImage = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds: DataSnapshot in dataSnapshot.children) {
                    val keyValue = ds.getValue(String::class.java)
                    databaseUriArray.add(keyValue!!)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        }

        var chipArray: ArrayList<String> = arrayListOf()
        chipGroup.children.forEach {
            val chip = it as Chip
            chipArray.add(chip.text.toString())
        }
        for(item in chipArray){
            // if the tag DOES NOT exist in the database then add it
            if(databaseTagArray.indexOf(item) == -1) {
                val newData = tagReference.child("tags").push()
                newData.setValue(item)
            }
        }

        val userID = Firebase.getUserId()
        val yearData: Int = intent.getIntExtra("year", 0)
        var monthData: Int = intent.getIntExtra("month", 0)
        val dayData: Int = intent.getIntExtra("day", 0)
        val date = LocalDate.of(yearData, monthData, dayData)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val dateStr = date.format(formatter)
        val path: String = "$userID/$dateStr"

        for(item in chipArray) {
            tagReference.child(item).addListenerForSingleValueEvent(eventListenerImage)
            //if url doesn't exist in tag database
            if(databaseUriArray.indexOf(path) == -1) {
                val newData = tagReference.child(item).push()
                newData.setValue(path)
            }
        }


        val notes = notesText.text.toString()
        Firebase.updateNotesAndTags(year, month, day, notes, fromArrayToString(chipArray))
        Toast.makeText(this, "Update Saved", Toast.LENGTH_SHORT).show()
        finish()
    }

    /**
     * When cancel button is click, finish the activity without saving changes
     */
    fun onCancelUpload(view: View){
        Toast.makeText(this, "Update Cancelled", Toast.LENGTH_SHORT).show()
        this.finish()
    }

    fun fromArrayToString(array: ArrayList<String>): String {
        var string = ""
        for (s in array) string += "${s}, "
        println("latlng convert" + string)

        return string
    }
}