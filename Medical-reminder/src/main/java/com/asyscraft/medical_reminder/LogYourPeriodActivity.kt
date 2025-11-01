package com.asyscraft.medical_reminder

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.asyscraft.medical_reminder.databinding.ActivityLogYourPeriodBinding
import com.asyscraft.medical_reminder.viewModels.MedicalViewModel
import com.careavatar.core_model.medicalReminder.CreatePeriodRequestModel
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class LogYourPeriodActivity : BaseActivity() {

    private lateinit var binding: ActivityLogYourPeriodBinding
    private val viewModel: MedicalViewModel by viewModels()

    private var takenDateMillis: Long? = null
    private var startdate: String? = null
    private var lastDate: String? = null
    private var selectedFlowView: TextView? = null
    private var selectedFlowLevel: String? = null // "Light", "Moderate", or "Heavy"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogYourPeriodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.customtoolbar.tvTitle.text = "Log Your Period"
        binding.customtoolbar.btnBack.setOnClickListener { finish() }

        // Select Start Date
        binding.startDateLayout.setOnClickListener {
            showDatePickerDialog { formattedDate, displayDate ->
                binding.startDate.text = displayDate
                startdate = formattedDate

                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                sdf.timeZone = TimeZone.getTimeZone("UTC")
                takenDateMillis = sdf.parse(formattedDate)?.time

                validateFields()
            }
        }

        // Select End Date (next/last date)
        binding.lastDateLayout.setOnClickListener {
            showDatePickerDialog(minDate = takenDateMillis) { formattedDate, displayDate ->
                binding.lastdate.text = displayDate
                lastDate = formattedDate
                validateFields()
            }
        }

        // Save Button
        binding.buttonNext.setOnClickListener {
            if (validateFields()) {
                hitCreatePeriodLog()
            } else {
                showToast("Please select both start and end dates.")
            }
        }

        observer()
        setupFlowSelection()
    }

    private fun setupFlowSelection() {
        val light = binding.lightTextView
        val moderate = binding.moderateTextView
        val heavy = binding.heavyTextView

        val allViews = listOf(light, moderate, heavy)

        allViews.forEach { view ->
            view.setOnClickListener {
                // Reset all to unselected
                allViews.forEach {
                    it.setBackgroundResource(0)
                    it.setTextColor(getColor(com.careavatar.core_ui.R.color.pinkColor))
                }

                // Select clicked one
                view.setBackgroundResource(com.careavatar.core_ui.R.drawable.flow_intensity_bg)
                view.setTextColor(getColor(com.careavatar.core_ui.R.color.white))

                selectedFlowView = view
                selectedFlowLevel = view.text.toString()
            }
        }
    }

    private fun showDatePickerDialog(
        minDate: Long? = null,
        onDateSelected: (String, String) -> Unit
    ) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            com.careavatar.core_ui.R.style.CustomDatePickerDialog,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth, selectedDay)
                }.time

                val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                val displayFormat = SimpleDateFormat("dd MMM yyyy", Locale.US)

                onDateSelected(utcFormat.format(selectedDate), displayFormat.format(selectedDate))
            },
            year, month, day
        )

        minDate?.let { datePickerDialog.datePicker.minDate = it }
        datePickerDialog.show()
    }

    private fun validateFields(): Boolean {
        return !startdate.isNullOrEmpty() && !lastDate.isNullOrEmpty()
    }

    private fun observer() {
        collectApiResultOnStarted(viewModel.CreatePeriodResponseModel) {
            if (it.success) {
                showToast("Period logged successfully")
                finish()
            } else {
                showToast(it.msg ?: "Failed to log period")
            }
        }
    }

    private fun calculateDuration(start: String?, end: String?): String {
        if (start == null || end == null) return ""
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        val diff = (sdf.parse(end)?.time ?: 0) - (sdf.parse(start)?.time ?: 0)
        val days = diff / (1000 * 60 * 60 * 24)
        return "$days days"
    }


    private fun hitCreatePeriodLog() {
        lifecycleScope.launch {

            val request = CreatePeriodRequestModel(
                lastPeriodDate = lastDate.toString(),
                averageCycleLength = 28,
                periodDuration = calculateDuration(startdate, lastDate),
                flowIntensity = selectedFlowLevel.toString()
            )

            launchIfInternetAvailable {
                viewModel.hitCreatePeriodLog(request)
            }
        }
    }
}
