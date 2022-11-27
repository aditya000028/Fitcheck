package com.cmpt362.fitcheck

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.cmpt362.fitcheck.firebase.Firebase
import java.io.File

class AddPhotoActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var notesText: EditText
    private lateinit var tempImgUri: Uri
    private lateinit var tempImgFile: File
    private val tempImgFileName = "fitcheck_temp_img.jpg"
    private lateinit var cameraResult: ActivityResultLauncher<Intent>
    private var photoTaken = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

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
        // Check that photo was taken
        if (photoTaken == 1) {
            // Get any notes from user
            val notes = notesText.text.toString()

            // Add photo and notes to cloud storage
            val uploadTask = Firebase.addPhoto(tempImgUri, notes)

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
}