package com.asyscraft.medical_reminder.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.asyscraft.medical_reminder.R
import com.asyscraft.medical_reminder.databinding.FragmentAddManuallyBinding
import com.careavatar.core_network.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddManuallyFragment : BaseFragment() {
    private lateinit var binding: FragmentAddManuallyBinding
    private lateinit var optionList: List<TextView>
    private lateinit var dayViews: List<TextView>
    private val selectedDays = mutableSetOf<TextView>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddManuallyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNextButton()
        setupDaysSelection()
        setupReminderOptions()
    }

    /** ✅ Next button logic */
    private fun setupNextButton() {
        binding.btnNext.setOnClickListener {
            findNavController().navigate(R.id.action_uploadPrescriptionFragment_to_slotTimeFragment)
//            selectedDisease?.let {
//                saveDataViewModel.updateDiseaseId(it._id)
//                findNavController().navigate(R.id.action_diseaseFragment_to_uploadPrescriptionFragment)
//
//            } ?: showToast("Please select a disease")
        }
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

    /** ✅ Selects the reminder option button */
    private fun selectOption(selected: TextView) {
        optionList.forEach { option ->
            if (option == selected) {
                option.setBackgroundResource(com.careavatar.core_ui.R.drawable.button_bg)
                option.setTextColor(ContextCompat.getColor(requireContext(), com.careavatar.core_ui.R.color.white))
            } else {
                option.setBackgroundResource(0)
                option.setTextColor(Color.parseColor("#5E6960"))
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
            day.setTextColor(ContextCompat.getColor(requireContext(), com.careavatar.core_ui.R.color.primaryColor))
        }
        selectedDays.clear()
    }

    /** ✅ Select a single day visually */
    private fun selectDay(day: TextView) {
        day.setBackgroundResource(com.careavatar.core_ui.R.drawable.circle)
        day.setTextColor(ContextCompat.getColor(requireContext(), com.careavatar.core_ui.R.color.white))
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

    /** ✅ Handles weekday selection toggle */
    private fun toggleDaySelection(dayView: TextView) {
        val isSelected = selectedDays.contains(dayView)
        if (isSelected) {
            dayView.setBackgroundResource(com.careavatar.core_ui.R.drawable.water_log_card_bg)
            dayView.setTextColor(ContextCompat.getColor(requireContext(), com.careavatar.core_ui.R.color.primaryColor))
            selectedDays.remove(dayView)
        } else {
            dayView.setBackgroundResource(com.careavatar.core_ui.R.drawable.circle)
            dayView.setTextColor(ContextCompat.getColor(requireContext(), com.careavatar.core_ui.R.color.white))
            selectedDays.add(dayView)
        }
    }

}