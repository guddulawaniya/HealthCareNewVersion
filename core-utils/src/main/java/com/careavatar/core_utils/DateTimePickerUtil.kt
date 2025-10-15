package com.careavatar.core_utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

object DateTimePickerUtil {

    private val calendar = Calendar.getInstance()

    fun pickDateTime(
        context: Context,
        dateFormat: String = "yyyy-MM-dd HH:mm",
        onDateTimePicked: (String) -> Unit
    ) {
        // Step 1: Date picker
        val datePicker = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                // Step 2: After date is selected → show time picker
                val timePicker = TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)

                        val sdf = SimpleDateFormat(dateFormat, Locale.getDefault())
                        val formatted = sdf.format(calendar.time)
                        onDateTimePicked(formatted)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false
                )

                timePicker.show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePicker.show()
    }

    /**
     * Pick only a date and return formatted string.
     */
    fun pickDate(
        context: Context,
        dateFormat: String = "yyyy-MM-dd",
        onDatePicked: (String) -> Unit
    ) {
        val datePicker = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val sdf = SimpleDateFormat(dateFormat, Locale.getDefault())
                val formatted = sdf.format(calendar.time)
                onDatePicked(formatted)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    fun pickTime(
        context: Context,
        timeFormat: String = "HH:mm",
        onTimePicked: (String) -> Unit
    ) {
        val timePicker = TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)

                val sdf = SimpleDateFormat(timeFormat, Locale.getDefault())
                val formatted = sdf.format(calendar.time)
                onTimePicked(formatted)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        )
        timePicker.show()
    }
}
