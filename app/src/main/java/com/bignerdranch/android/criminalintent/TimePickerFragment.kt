package com.bignerdranch.android.criminalintent

import android.app.Dialog
import android.app.TimePickerDialog
import java.util.Calendar
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.Date

private const val ARG_TIME = "time"

class TimePickerFragment: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val date = arguments?.getSerializable(ARG_TIME) as Date

        val calendar = Calendar.getInstance()

        calendar.time = date

        val hours = calendar.get(Calendar.HOUR)

        val minutes = calendar.get(Calendar.MINUTE)

        return TimePickerDialog(
            requireContext(),
            null,
            hours,
            minutes,
            true
        )
    }

    companion object{
        fun newInstance(date: Date): TimePickerFragment{
            val args = Bundle().apply {
                putSerializable(ARG_TIME, date)
            }

            return TimePickerFragment().apply {
                arguments = args
            }
        }
    }
}