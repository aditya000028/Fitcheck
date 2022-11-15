package com.cmpt362.fitcheck.ui.calendar

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cmpt362.fitcheck.databinding.FragmentCalendarBinding


class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val calendarViewModel = ViewModelProvider(this).get(CalendarViewModel::class.java)

        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val calendarView: CalendarView = binding.simpleCalendarView
        val selectedDate: Long = calendarView.date
        calendarView.date = selectedDate
        calendarView.firstDayOfWeek = 1


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}