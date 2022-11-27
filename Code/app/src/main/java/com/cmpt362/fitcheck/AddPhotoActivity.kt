package com.cmpt362.fitcheck

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.cmpt362.fitcheck.firebase.Firebase
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.io.File

class AddPhotoActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var tagEditText: EditText
    private lateinit var tempImgUri: Uri
    private lateinit var tempImgFile: File
    private var tagArray: ArrayList<String> = arrayListOf()
    private val tempImgFileName = "fitcheck_temp_img.jpg"
    private lateinit var cameraResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        imageView = findViewById(R.id.outfitImage)

        tempImgFile = File(getExternalFilesDir(null), tempImgFileName)
        tempImgUri = FileProvider.getUriForFile(
            this, "com.cmpt362.fitcheck", tempImgFile
        )

        // Display photo taken in imageView
        cameraResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result: ActivityResult ->
            if(result.resultCode == Activity.RESULT_OK){
                val bitmap = Util.getBitmap(this, tempImgUri)
                imageView.setImageBitmap(bitmap)

            }
        }

        val tagReference = Firebase.getTag()

        tagEditText = findViewById(R.id.photoTags)
        tagEditText.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if ((keyCode == KeyEvent.KEYCODE_ENTER) && (event.action == KeyEvent.ACTION_UP)) {
                val value = tagEditText.text.toString()
                addChipToGroup(value)
                tagArray.add(value)

                val search = value
                val eventListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        var found: Boolean = false
                        for (ds: DataSnapshot in dataSnapshot.children) {
                            val key = ds.key
                            val keyValue = ds.getValue(String::class.java)
                            println("KEYVALUE $keyValue")
                            found = keyValue == search
                            println("FOUND1 $found")
                            if(found){
                                break
                            }
                            Log.d("TAG", "$key/$keyValue")
                        }

                        if(!found){
                            val newData = tagReference.child("tags").push()
                            newData.setValue(value)
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {}
                }

                tagReference.child("tags").addListenerForSingleValueEvent(eventListener)

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
        // necessary to get single selection working
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
//        val tagReference = Firebase.getTag()
//        val eventListener = object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                var found: Boolean = false
//                var foundArray: ArrayList<String> = arrayListOf()
//                var foundValue = ""
//                for (ds: DataSnapshot in dataSnapshot.children) {
//                    val key = ds.key
//                    val keyValue = ds.getValue(String::class.java)
////                    println("KEYVALUE $keyValue")
//                    for(item in tagArray){
//                        found = keyValue == item
//                        if(found){
//                            break
//                        }
//                    }
//                    if(!found){
//                        val newData = tagReference.child("tags").push()
//                        newData.setValue(foundValue)
//                    }
//                    Log.d("TAG", "$key/$keyValue")
//                }
//
//
//
//                if(!found){
//
//                }
//            }
//            override fun onCancelled(databaseError: DatabaseError) {}
//        }
//
//        tagReference.child("tags").addListenerForSingleValueEvent(eventListener)
        Toast.makeText(this, R.string.save_message, Toast.LENGTH_SHORT).show()
        // ToDo: Save photo to firebase
        this.finish()
    }

    /**
     * When cancel button is click, finish the activity without saving changes
     */
    fun onCancelUpload(view: View){
        val tagReference = Firebase.getTag()
        val eventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var found: Boolean = false
                var foundArray: ArrayList<String> = arrayListOf()
                var foundValue = ""
                for (ds: DataSnapshot in dataSnapshot.children) {
                    val key = ds.key
                    val keyValue = ds.getValue(String::class.java)
                    println("KEYVALUE $keyValue")
                    for(item in tagArray){
                        found = keyValue == item
                        if(found){
                            tagReference.child("tags").child(key!!).removeValue()
                        }
                    }
                    Log.d("TAG", "$key/$keyValue")
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        }

        tagReference.child("tags").addListenerForSingleValueEvent(eventListener)
        Toast.makeText(this, R.string.cancel_message, Toast.LENGTH_SHORT).show()
        this.finish()
    }
}