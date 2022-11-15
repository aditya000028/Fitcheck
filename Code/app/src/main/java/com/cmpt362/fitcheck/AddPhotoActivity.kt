package com.cmpt362.fitcheck

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import java.io.File

class AddPhotoActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var tempImgUri: Uri
    private lateinit var tempImgFile: File
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
        Toast.makeText(this, R.string.save_message, Toast.LENGTH_SHORT).show()
        // ToDo: Save photo to firebase
        this.finish()
    }

    /**
     * When cancel button is click, finish the activity without saving changes
     */
    fun onCancelUpload(view: View){
        Toast.makeText(this, R.string.cancel_message, Toast.LENGTH_SHORT).show()
        this.finish()
    }
}