package com.asyscraft.medical_reminder

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.asyscraft.medical_reminder.databinding.ActivityCreateWaterReminderBinding
import com.asyscraft.medical_reminder.viewModels.MedicalViewModel
import com.careavatar.core_model.medicalReminder.CreateWaterReminderRequest
import com.careavatar.core_model.medicalReminder.WaterData
import com.careavatar.core_network.base.BaseActivity
import com.careavatar.core_utils.DateTimePickerUtil.pickTime
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateWaterReminderActivity : BaseActivity() {

    private lateinit var binding: ActivityCreateWaterReminderBinding
    private val viewModel: MedicalViewModel by viewModels()
    private var selectedFrequency: String? = null
    private var selectedAmount: String? = null
    private var startTime: String? = null
    private var endTime: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateWaterReminderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
        observeViewModel()

    }

    private fun observeViewModel() {
        collectApiResultOnStarted(viewModel.getCreateWaterReminderResponse) {
            if (intent.getStringExtra("update") == "update") {
                showToast("Reminder updated successfully")
                finish()
            } else {
                showToast("Reminder created successfully")
                startActivity(
                    Intent(
                        this,
                        MedicalReminderDashboardActivity::class.java
                    ).putExtra("reminder_type", "Water")
                )
                finish()
            }


        }

        collectApiResultOnStarted(viewModel.waterHistoryResponse) { it ->
            if (it.success) {
                // fill UI if existing reminder is present
                it.data?.let { data ->
                    fillReminderData(data)
                }
            }
        }

    }


    private fun fillReminderData(data: WaterData) {
        // 🕒 Time Window
        startTime = data.startDate
//        endTime = data.startDate
        binding.tvStartTime.text = startTime
        binding.tvEndTime.text = endTime

        // 🔁 Frequency
        when (data.intervalTime) {
            "60" -> {
                binding.optionEveryHour.performClick()
            }

            "120" -> {
                binding.option2Hours.performClick()
            }

            else -> {
                binding.optionCustom.performClick()
                binding.customInterval.setText(data.intervalTime)
            }
        }

        // 💧 Amount
        when (data.perSip) {
            "250" -> {
                binding.chip250.performClick()
            }

            "500" -> {
                binding.chip500.performClick()
            }

            "700" -> {
                binding.chip700.performClick()
            }

            else -> {
                binding.chipCustom.performClick()
                binding.amountCustomInterval.setText(data.perSip)
            }
        }

    }


    private fun setupUI() {
        /** 🔙 Back button **/
        binding.btnBack.setOnClickListener { finish() }

        /** ☑️ Frequency selection **/
        setupFrequencySelection()

        /** 🕒 Time pickers **/
        setupTimePickers()

        /** 💧 Amount selection **/
        setupAmountSelection()

        /** ✅ Save Reminder button **/
        binding.custombtn.buttonNext.text = "Save Reminder"
        binding.custombtn.buttonNext.setOnClickListener {
            if (validateFields()) {
                if (intent.getStringExtra("update") == "update") {
                    hitWaterReminderUpdate()
                } else {
                    hitWaterReminderCreate()
                }

            }
        }

        if (intent.getStringExtra("update") == "update") {
            hitGetWaterHistory()
        }
    }

    // ✅ VALIDATION FUNCTION
    private fun validateFields(): Boolean {

        if (startTime.isNullOrEmpty()) {
            showToast("Please select start time")
            return false
        }

        if (endTime.isNullOrEmpty()) {
            showToast("Please select end time")
            return false
        }

        if (selectedFrequency.isNullOrEmpty()) {
            showToast("Please select reminder frequency")
            return false
        }

        if (selectedFrequency == "custom" && binding.customInterval.text.toString().isEmpty()) {
            showToast("Please enter a custom interval")
            return false
        }

        if (selectedAmount.isNullOrEmpty()) {
            showToast("Please select water amount")
            return false
        }

        if (selectedAmount == "custom" && binding.amountCustomInterval.text.toString().isEmpty()) {
            showToast("Please enter a custom amount")
            return false
        }

        return true
    }

    private fun hitGetWaterHistory() {
        launchIfInternetAvailable {
            viewModel.hitWaterHistory()
        }
    }

    // ✅ API CALL
    private fun hitWaterReminderCreate() {

        val interval = if (selectedFrequency == "custom") {
            binding.customInterval.text.toString()
        } else {
            selectedFrequency!!
        }

        val perSipValue = if (selectedAmount == "custom") {
            binding.amountCustomInterval.text.toString()
        } else {
            selectedAmount!!
        }

        val request = CreateWaterReminderRequest(
            isWaterRemainder = true,
            startDate = startTime.toString(),
            targetWaterIntake = "3000",
            intervalTime = interval,
            BMI = "",
            perSip = perSipValue,
            user = "12345"
        )

        launchIfInternetAvailable {
            viewModel.hitWaterReminderCreate(request)
        }

        showToast("Reminder saved successfully 🎉")
    }

    // ✅ API CALL Update
    private fun hitWaterReminderUpdate() {

        val interval = if (selectedFrequency == "custom") {
            binding.customInterval.text.toString()
        } else {
            selectedFrequency!!
        }

        val perSipValue = if (selectedAmount == "custom") {
            binding.amountCustomInterval.text.toString()
        } else {
            selectedAmount!!
        }

        val request = CreateWaterReminderRequest(
            isWaterRemainder = true,
            startDate = startTime.toString(),
            targetWaterIntake = "3000",
            intervalTime = interval,
            BMI = "",
            perSip = perSipValue,
            user = "12345"
        )
        val id = intent.getStringExtra("id").toString()

        launchIfInternetAvailable {
            viewModel.hitWaterReminderUpdate(id, request)
        }

        showToast("Reminder saved successfully 🎉")
    }

    // ✅ FREQUENCY SELECTION
    private fun setupFrequencySelection() {
        val radioHour = binding.radioEveryHour
        val radio2Hr = binding.radio2Hours
        val radioCustom = binding.radioCustom
        val customEdit = binding.customInterval

        fun resetRadios() {
            radioHour.setImageResource(com.careavatar.core_ui.R.drawable.water_radio_btn_outline_bg)
            radio2Hr.setImageResource(com.careavatar.core_ui.R.drawable.water_radio_btn_outline_bg)
            radioCustom.setImageResource(com.careavatar.core_ui.R.drawable.water_radio_btn_outline_bg)
            customEdit.visibility = View.GONE
        }

        binding.optionEveryHour.setOnClickListener {
            resetRadios()
            radioHour.setImageResource(com.careavatar.core_ui.R.drawable.water_checked_bg)
            selectedFrequency = "60"
        }

        binding.option2Hours.setOnClickListener {
            resetRadios()
            radio2Hr.setImageResource(com.careavatar.core_ui.R.drawable.water_checked_bg)
            selectedFrequency = "120"
        }

        binding.optionCustom.setOnClickListener {
            resetRadios()
            radioCustom.setImageResource(com.careavatar.core_ui.R.drawable.water_checked_bg)
            customEdit.visibility = View.VISIBLE
            selectedFrequency = "custom"
        }
    }

    // ✅ TIME PICKERS
    private fun setupTimePickers() {
        binding.startBox.setOnClickListener {
            pickTime(this) {
                startTime = it
                binding.tvStartTime.text = it
            }
        }

        binding.endBox.setOnClickListener {
            pickTime(this) {
                endTime = it
                binding.tvEndTime.text = it
            }
        }
    }

    // ✅ AMOUNT SELECTION
    private fun setupAmountSelection() {
        val chips = listOf(
            binding.chip250,
            binding.chip500,
            binding.chip700,
            binding.chipCustom
        )

        val customAmountField = binding.amountCustomInterval

        fun resetChips() {
            chips.forEach {
                it.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
                it.setTextColor(
                    ContextCompat.getColor(this, com.careavatar.core_ui.R.color.textColor)
                )
            }
            customAmountField.visibility = View.GONE
        }

        binding.chip250.setOnClickListener {
            resetChips()
            setSelectedChip(binding.chip250)
            selectedAmount = "250"
        }

        binding.chip500.setOnClickListener {
            resetChips()
            setSelectedChip(binding.chip500)
            selectedAmount = "500"
        }

        binding.chip700.setOnClickListener {
            resetChips()
            setSelectedChip(binding.chip700)
            selectedAmount = "700"
        }

        binding.chipCustom.setOnClickListener {
            resetChips()
            setSelectedChip(binding.chipCustom)
            customAmountField.visibility = View.VISIBLE
            selectedAmount = "custom"
        }
    }

    private fun setSelectedChip(chip: TextView) {
        chip.setBackgroundResource(com.careavatar.core_ui.R.drawable.pill_selected)
        chip.setTextColor(ContextCompat.getColor(this, com.careavatar.core_ui.R.color.white))
    }
}
