package com.asyscraft.alzimer_module.utils

import android.app.DatePickerDialog
import android.content.Context
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

fun showDatePicker(
    context: Context,
    targetTextView: TextView
) {
    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = sdf.format(selectedDate.time)

            targetTextView.text = formattedDate
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // Prevent selection of past dates
    datePickerDialog.datePicker.minDate = calendar.timeInMillis

    datePickerDialog.show()
}
