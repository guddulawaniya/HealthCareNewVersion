package com.asyscraft.medical_reminder

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.asyscraft.medical_reminder.databinding.ActivityAddVaaccinationBinding
import com.asyscraft.medical_reminder.viewModels.MedicalViewModel
import com.careavatar.core_model.medicalReminder.AddVaccinationRequest
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class AddVaaccinationActivity : BaseActivity() {
    private lateinit var binding: ActivityAddVaaccinationBinding
    private val viewModel: MedicalViewModel by viewModels()
    private var takendate = ""
    private var nextdate = ""
    var takenDateMillis: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddVaaccinationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }


        // Listen for text changes
        binding.tvVaccineName.addTextChangedListener { validateFields() }
        binding.tvPurpose.addTextChangedListener { validateFields() }

        binding.lastDateEditText.setOnClickListener {

            showDatePickerDialog { formattedDate, secFormattedDate ->
                binding.lastDateEditText.text = secFormattedDate
                takendate = formattedDate

                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                sdf.timeZone = TimeZone.getTimeZone("UTC")
                takenDateMillis = sdf.parse(formattedDate)?.time

                validateFields()
            }

        }

        binding.nextDateEditText.setOnClickListener {
            showDatePickerDialog(minDate = takenDateMillis) { formattedDate, secFormattedDate ->
                binding.nextDateEditText.text = secFormattedDate
                nextdate = formattedDate
                validateFields()
            }
        }

        binding.buttonNext.isEnabled = false
        binding.buttonNext.alpha = 0.5f

        binding.buttonNext.setOnClickListener {
            val request = AddVaccinationRequest(
                label = binding.tvVaccineName.text.toString().trim(),
                taken_date = takendate,
                next_date = nextdate
            )

            hitCreateVaccination(request)
        }


        observer()
    }

    private fun validateFields() {
        val vaccineName = binding.tvVaccineName.text.toString().trim()
        val purpose = binding.tvPurpose.text.toString().trim()
        val lastDate = binding.lastDateEditText.text.toString().trim()
        val nextDate = binding.nextDateEditText.text.toString().trim()

        val isValid = vaccineName.isNotEmpty() &&
                purpose.isNotEmpty() &&
                lastDate.isNotEmpty() &&
                nextDate.isNotEmpty()

        binding.buttonNext.isEnabled = isValid

        // Change button color dynamically
        if (isValid) {
            binding.buttonNext.alpha = 1f
        } else {
            binding.buttonNext.alpha = 0.5f
        }
    }


    private fun observer() {
        collectApiResultOnStarted(viewModel.vaccinationResponse) {
            if (it.success) {
                finish()
            }
        }
    }

    private fun hitCreateVaccination(request : AddVaccinationRequest) {
        lifecycleScope.launch {
            launchIfInternetAvailable {
                viewModel.hitCreateVaccination(request)
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
            this, com.careavatar.core_ui.R.style.CustomDatePickerDialog,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth, selectedDay)
                }.time

                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                val secDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.US)

                val formattedDate = dateFormat.format(selectedDate)
                val secFormattedDate = secDateFormat.format(selectedDate)

                onDateSelected(formattedDate, secFormattedDate)
            },
            year, month, day
        )

        // Set minDate if provided
        minDate?.let {
            datePickerDialog.datePicker.minDate = it
        }

        datePickerDialog.show()
    }
}