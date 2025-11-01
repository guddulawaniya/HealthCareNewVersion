package com.asyscraft.medical_reminder

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import com.asyscraft.medical_reminder.databinding.ActivityNewReminderBinding
import com.asyscraft.medical_reminder.viewModels.MedicalViewModel
import com.careavatar.core_model.medicalReminder.CreateMedicalReminderRequest
import com.careavatar.core_network.base.BaseActivity
import com.careavatar.core_ui.R
import com.careavatar.core_utils.DateTimePickerUtil.pickTime
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class NewReminderActivity : BaseActivity() {

    private lateinit var binding: ActivityNewReminderBinding

    private lateinit var dayViews: List<TextView>
    private val selectedDays = mutableSetOf<TextView>()
    private var startTime: String? = null
    private lateinit var optionList: List<TextView>

    private val viewModel: MedicalViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewReminderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.tvTitle.text = "New Reminder"
        binding.toolbar.btnBack.setOnClickListener { finish() }

        setupDaysSelection()
        setupReminderOptions()
        setupTimePicker()
        setupChips()

        binding.includedBtn.buttonNext.setOnClickListener {
            hitCreateReminder()
        }


    }

    private fun hitCreateReminder(){
        val title = binding.reminderTv.text.toString()

//        val request = CreateMedicalReminderRequest(
//            medicine = userPref.getMedicationID()?.toString() ?: "",
//            medicineForm = userPref.getMedicineTypeId()?.toString() ?: "",
//            disease = userPref.getdiseaseId()?.toString() ?: "",
//            strength = userPref.getstrength()?.toString() ?: "",
//            unit = userPref.getselectedUnit()?.toString() ?: "",
//            stock = userPref.getAvailableStock()?.takeIf { it.isNotEmpty() }?.toInt() ?: 0,
//            title = title,
//            scheduleMode = typeweek,
//            intervalDays = selectedNoDays?.toString() ?: "",
//            startDate = startDate?.toString() ?: "",
//            status = "0",
//            scheduleTimings = scheduleTimings ?: emptyList()
//        )

//        viewModel.hitCreateMedicalRemainder(request)

    }


    /** ✅ Reminder Type (One-time / Everyday / Custom) */
    private fun setupReminderOptions() {
        optionList = listOf(binding.tvOneTime, binding.tvEveryday, binding.tvCustomdays)

        optionList.forEach { option ->
            option.setOnClickListener {
                selectOption(option)

                when (option) {
                    binding.tvOneTime -> enableOneTimeMode()
                    binding.tvEveryday -> enableEverydayMode()
                    binding.tvCustomdays -> enableCustomMode()
                }
            }
        }

        // Default selection
        selectOption(binding.tvOneTime)
        enableOneTimeMode()
    }
    /** 🟦 One-time mode: only one selectable day */
    private fun enableOneTimeMode() {
        clearAllDaySelections()
        dayViews.forEach { day ->
            day.setOnClickListener {
                // Allow only one selected
                clearAllDaySelections()
                selectDay(day)
            }
        }
    }

    /** 🟩 Everyday mode: auto-select all days (and disable clicks) */
    private fun enableEverydayMode() {
        selectedDays.clear()
        dayViews.forEach { day ->
            selectDay(day)
            day.isEnabled = false // Disable manual click
        }
    }

    /** 🟨 Custom mode: allow multiple day selection manually */
    private fun enableCustomMode() {
        dayViews.forEach { day ->
            day.isEnabled = true
            day.setOnClickListener { toggleDaySelection(day) }
        }
    }

    /** 🔄 Clear all selected days */
    private fun clearAllDaySelections() {
        selectedDays.forEach { day ->
            day.setBackgroundResource(com.careavatar.core_ui.R.drawable.water_log_card_bg)
            day.setTextColor(ContextCompat.getColor(this, com.careavatar.core_ui.R.color.primaryColor))
        }
        selectedDays.clear()
    }

    /** ✅ Select a single day visually */
    private fun selectDay(day: TextView) {
        day.setBackgroundResource(com.careavatar.core_ui.R.drawable.circle)
        day.setTextColor(ContextCompat.getColor(this, com.careavatar.core_ui.R.color.white))
        selectedDays.add(day)
    }


    /** ✅ Weekday selection logic */
    private fun setupDaysSelection() {
        dayViews = listOf(
            binding.tvMon, binding.tvTue, binding.tvWed,
            binding.tvThu, binding.tvFri, binding.tvSat, binding.tvSun
        )

        dayViews.forEach { day ->
            day.setOnClickListener { toggleDaySelection(day) }
        }
    }

    /** ✅ Time picker click */
    private fun setupTimePicker() {
        binding.tvStartTime.setOnClickListener {
            pickTime(this) {
                startTime = it
                binding.tvStartTime.text = it
            }
        }
    }

    /** ✅ Dynamically adds chips with single-selection logic */
    private fun setupChips() {
        val primarycolor = ContextCompat.getColor(this, R.color.primaryColor)
        val whitecolor = ContextCompat.getColor(this, R.color.white)
        val blackcolor = ContextCompat.getColor(this, R.color.black)
        val chipGroup = binding.chipGroup
        val tags = listOf("Exercise", "Workout", "Meditation", "Walk", "Sleep", "Add Tag")

        chipGroup.isSingleSelection = true
        chipGroup.isSelectionRequired = false

        for (tag in tags) {
            val chip = Chip(this).apply {
                text = tag
                isCheckable = true
                isClickable = true

                // ✅ Use proper Material Shape
                shapeAppearanceModel = shapeAppearanceModel
                    .toBuilder()
                    .setAllCornerSizes(20f) // corner radius in px (use 20f or convert dp)
                    .build()

                setTextColor(
                    ColorStateList(
                        arrayOf(
                            intArrayOf(android.R.attr.state_checked),
                            intArrayOf(-android.R.attr.state_checked)
                        ),
                        intArrayOf(whitecolor, blackcolor)
                    )
                )

                chipBackgroundColor = ColorStateList(
                    arrayOf(
                        intArrayOf(android.R.attr.state_checked),
                        intArrayOf(-android.R.attr.state_checked)
                    ),
                    intArrayOf(primarycolor, Color.WHITE)
                )

                textSize = 15f

                chipStrokeWidth = 1f
                chipStrokeColor = ColorStateList(
                    arrayOf(
                        intArrayOf(android.R.attr.state_checked),
                        intArrayOf(-android.R.attr.state_checked)
                    ),
                    intArrayOf(0, "#33000000".toColorInt())
                )
            }

            chipGroup.addView(chip)
        }
    }





    /** ✅ Selects the reminder option button */
    private fun selectOption(selected: TextView) {
        optionList.forEach { option ->
            if (option == selected) {
                option.setBackgroundResource(com.careavatar.core_ui.R.drawable.button_bg)
                option.setTextColor(ContextCompat.getColor(this, com.careavatar.core_ui.R.color.white))
            } else {
                option.setBackgroundResource(0)
                option.setTextColor(Color.parseColor("#5E6960"))
            }
        }
    }

    /** ✅ Handles weekday selection toggle */
    private fun toggleDaySelection(dayView: TextView) {
        val isSelected = selectedDays.contains(dayView)
        if (isSelected) {
            dayView.setBackgroundResource(com.careavatar.core_ui.R.drawable.water_log_card_bg)
            dayView.setTextColor(ContextCompat.getColor(this, com.careavatar.core_ui.R.color.primaryColor))
            selectedDays.remove(dayView)
        } else {
            dayView.setBackgroundResource(com.careavatar.core_ui.R.drawable.circle)
            dayView.setTextColor(ContextCompat.getColor(this, com.careavatar.core_ui.R.color.white))
            selectedDays.add(dayView)
        }
    }
}
