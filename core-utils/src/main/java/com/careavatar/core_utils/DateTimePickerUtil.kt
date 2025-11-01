package com.careavatar.core_utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDateToReadable1(inputDate: String): String {
        return try {
            val parsedDateTime = OffsetDateTime.parse(inputDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            val zonedDateTime = parsedDateTime.atZoneSameInstant(ZoneId.systemDefault())

            val localDateTime = zonedDateTime.toLocalDateTime()
            val messageDate = localDateTime.toLocalDate()
            val now = LocalDate.now()

            val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
            val dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")

            val timePart = localDateTime.format(timeFormatter)

            when {
                messageDate.isEqual(now) -> "Today, $timePart"
                messageDate.isEqual(now.minusDays(1)) -> "Yesterday, $timePart"
                else -> "${messageDate.format(dateFormatter)}, $timePart"
            }
        } catch (e: Exception) {
            inputDate // fallback
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDateToReadable2(inputDate: String): String {
        return try {
            val parsedDateTime = OffsetDateTime.parse(inputDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            val zonedDateTime = parsedDateTime.atZoneSameInstant(ZoneId.systemDefault())
            val localDateTime = zonedDateTime.toLocalDateTime()

            // Format → "Jan 15, 2024"
            val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")

            localDateTime.format(dateFormatter)
        } catch (e: Exception) {
            inputDate // fallback if parsing fails
        }
    }




    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDateToReadable(inputDate: String): String {
        return try {
            val parsedDate = LocalDate.parse(inputDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            val outputFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d")
            parsedDate.format(outputFormatter)
        } catch (e: DateTimeParseException) {
            inputDate // fallback to original if parsing fails
        }
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
            context,com.careavatar.core_ui.R.style.CustomDatePickerDialog,
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
        timeFormat: String = "HH:mm a",
        onTimePicked: (String) -> Unit
    ) {
        val timePicker = TimePickerDialog(
            context, com.careavatar.core_ui.R.style.MyTimePickerTheme,
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
