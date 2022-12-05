package com.cmpt362.fitcheck

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat

object Util {

    const val MILLISECONDS_IN_A_DAY: Long = 86400000

    /**
     * Check for camera permission from user
     */
    fun checkPermissions(activity: Activity?) {
        if (Build.VERSION.SDK_INT < 23) return
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA), 0)
        }
    }

    /**
     * Given an image Uri, create a Bitmap object
     */
    fun getBitmap(context: Context, imgUri: Uri): Bitmap {
        var bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(imgUri))
        val matrix = Matrix()
        var ret = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        return ret
    }

    fun timeInMilliToString(context: Context, dailyReminderTimeInMilli: Long?): CharSequence? {
        return if (dailyReminderTimeInMilli != null) {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = dailyReminderTimeInMilli
            val timeFormatter = SimpleDateFormat(context.getString(R.string.time_formatter_pattern))
            try {
                timeFormatter.format(calendar.time)
            } catch (e: Exception) {
                println("debug: Unable to convert milli to string")
                ""
            }
        } else {
            ""
        }
    }
}