package com.cmpt362.fitcheck.ui.settings.notifications

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import com.cmpt362.fitcheck.R
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class TimePickerDialog: DialogFragment(), TimePickerDialog.OnTimeSetListener {

    private val calendar = Calendar.getInstance()
    private var listener: TimePickerDialogListener? = null

    companion object {
        const val TIME_KEY = "TIME_KEY"
        const val timePickerDialogTag = "timePickerDialogTag"
    }

    // Use this to send data back to calling activity
    interface TimePickerDialogListener {
        fun onTimeSet(dialog: DialogFragment, time: Calendar)
    }

    fun setListener(listener: TimePickerDialogListener) {
        this.listener = listener
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // Reference: https://developer.android.com/develop/ui/views/components/dialogs#PassingEvents
        if (listener == null) {
            try {
                listener = context as TimePickerDialogListener
            } catch (e: ClassCastException) {
                // The activity doesn't implement the interface, throw exception
                throw ClassCastException(("$context must implement TimePickerDialogListener"))
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bundle = arguments
        val calendar: Calendar = Calendar.getInstance()
        var hourOfDay: Int = calendar.get(Calendar.HOUR_OF_DAY)
        var minute: Int = calendar.get(Calendar.MINUTE)

        if (!bundle?.getString(TIME_KEY).isNullOrBlank()) {
            val currentValue = bundle?.getString(TIME_KEY)
            try {
                val time = LocalTime.parse(currentValue, DateTimeFormatter.ofPattern(getString(R.string.time_formatter_pattern)))
                hourOfDay = time.hour
                minute = time.minute
            } catch (e: Exception) {
                println("debug: Unable to parse time. Exception: ${e.message}")
            }
        }
        return TimePickerDialog(
            requireActivity(), this,
            hourOfDay, minute, false
        )
    }

    override fun onTimeSet(p0: TimePicker?, hourOfDay: Int, minute: Int) {
        calendar.set(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
            hourOfDay,
            minute
        )
        listener!!.onTimeSet(this, calendar)
    }
}