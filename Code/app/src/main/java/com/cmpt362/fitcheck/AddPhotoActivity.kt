package com.cmpt362.fitcheck

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.cmpt362.fitcheck.firebase.Firebase
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.io.File
import java.util.*

class AddPhotoActivity : AppCompatActivity(), LocationListener {
    private lateinit var imageView: ImageView
    private lateinit var notesText: EditText
    private lateinit var tagEditText: AutoCompleteTextView
    private var tagArray: ArrayList<String> = arrayListOf()
    private var databaseTagArray: ArrayList<String> = arrayListOf()
    private lateinit var tempImgUri: Uri
    private lateinit var tempImgFile: File
    private val tempImgFileName = "fitcheck_temp_img.jpg"
    private lateinit var cameraResult: ActivityResultLauncher<Intent>
    private var photoTaken = 0

    private lateinit var locationManager: LocationManager
    private lateinit var locationText: TextView
    private lateinit var locationStr: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        locationText = findViewById(R.id.locationText)

        checkPermission()

        imageView = findViewById(R.id.outfitImage)
        notesText = findViewById(R.id.notesText)

        tempImgFile = File(getExternalFilesDir(null), tempImgFileName)
        tempImgUri = FileProvider.getUriForFile(
            this, "com.cmpt362.fitcheck", tempImgFile
        )
        photoTaken = 0

        // Display photo taken in imageView
        cameraResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result: ActivityResult ->
            if(result.resultCode == Activity.RESULT_OK){
                val bitmap = Util.getBitmap(this, tempImgUri)
                imageView.setImageBitmap(bitmap)
                photoTaken = 1

            }
        }
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
                val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
                tagEditText.text.clear()
                return@OnKeyListener true
            }
            return@OnKeyListener false
        })
    }

    private fun addChipToGroup(tag: String) {
        val chip = Chip(this)
        chip.text = tag
        chip.chipIcon = ContextCompat.getDrawable(this, R.drawable.ic_launcher_background)
        chip.isChipIconVisible = false
        chip.isCloseIconVisible = true
        chip.isClickable = false
        chip.isCheckable = false
        val chipGroup  = findViewById<ChipGroup>(R.id.chipGroup)
        chipGroup.addView(chip as View)
        chip.setOnCloseIconClickListener {
            chipGroup.removeView(chip as View)
            tagArray.remove(tag)
        }
    }

    /**
     * When capture fit button is click, open camera for user to take a picture
     */
    fun onCapture(view: View){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImgUri)
        cameraResult.launch(intent)
    }

    /**
     * When save button is click, save the upload and finish the activity
     */
    fun onSaveUpload(view: View){
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

        for(item in tagArray){
            // if the tag DOES NOT exist in the database then add it
            if(databaseTagArray.indexOf(item) == -1) {
                val newData = tagReference.child("tags").push()
                newData.setValue(item)
            }
        }
        // Check that photo was taken
        if (photoTaken == 1) {
            // Get any notes from user
            val notes = notesText.text.toString()

            // Add photo and notes to cloud storage
            val uploadTask = Firebase.addPhoto(tempImgUri, notes, locationStr, fromArrayToString(tagArray))

            // Check that UploadTask was created
            if (uploadTask != null) {
                // Add listeners for file success, progress, and failure
                uploadTask.addOnSuccessListener { taskSnapshot ->
                    // If successful, update user and finish activity
                    Toast.makeText(this, R.string.save_message, Toast.LENGTH_SHORT).show()
                    this.finish()
                    }.addOnProgressListener { taskSnapshot ->
                            val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
                            println("Upload is $progress% done")
                    }.addOnFailureListener { exception ->
                        // On failure, inform user and output error
                        println(exception)
                        Toast.makeText(this, R.string.failure_message, Toast.LENGTH_SHORT).show()
                    }
            }
        } else {
            // Inform user that picture must be taken
            Toast.makeText(this, R.string.take_picture_message, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * When cancel button is click, finish the activity without saving changes
     */
    fun onCancelUpload(view: View){
        Toast.makeText(this, R.string.cancel_message, Toast.LENGTH_SHORT).show()
        this.finish()
    }

    fun fromArrayToString(array: ArrayList<String>): String {
        var string = ""
        for (s in array) string += "${s}, "
        println("latlng convert" + string)

        return string
    }

    private fun initLocationManager() {
        try {
            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_FINE
            val provider: String? = locationManager.getBestProvider(criteria, true)
            if(provider != null) {
                val location = locationManager.getLastKnownLocation(provider)
                if (location != null)
                    onLocationChanged(location)
                locationManager.requestLocationUpdates(provider, 0, 0f, this)
            }
        } catch (e: SecurityException) {
        }
    }

    override fun onLocationChanged(location: Location) {
        try {
            val lat = location.latitude
            val lng = location.longitude
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            val cityName = addresses[0].subAdminArea
            val provinceName = addresses[0].adminArea
            val countryName = addresses[0].countryName

            locationStr = "$cityName, $provinceName, $countryName"
            locationText.text = locationStr

        } catch (e: Exception) {
        }
    }

    override fun onProviderDisabled(provider: String) {}
    override fun onProviderEnabled(provider: String) {}

    override fun onDestroy() {
        super.onDestroy()
        if (locationManager != null)
            locationManager.removeUpdates(this)
    }

    fun checkPermission() {
        if (Build.VERSION.SDK_INT < 23) return
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) ActivityCompat.requestPermissions(this, arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION), 0) else initLocationManager()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) initLocationManager()
        }
    }
}