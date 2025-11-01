package com.asyscraft.medical_reminder.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.navigation.fragment.findNavController
import com.asyscraft.medical_reminder.R
import com.asyscraft.medical_reminder.databinding.FragmentSlotTimeBinding
import com.careavatar.core_network.base.BaseFragment
import com.careavatar.core_utils.DateTimePickerUtil.pickTime
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SlotTimeFragment : BaseFragment() {

    private lateinit var binding: FragmentSlotTimeBinding
    private var selectedSlot: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSlotTimeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSlotSelection()
        setupNextButton()
        setupTimePickers()
    }

    private fun setupSlotSelection() {
        val slots = listOf(
            binding.layoutMorning,   // Morning
            binding.afternoonLayout,  // Afternoon
            binding.eveningLayout,    // Evening
            binding.nightLayout       // Night
        )

        slots.forEach { slot ->
            slot.setOnClickListener { selectSlot(slot, slots) }
        }
    }

    private fun setupTimePickers() {
        val timeViewMap = mapOf(
            binding.tvMorningTime to binding.layoutMorning,
            binding.tvAfterTime to binding.afternoonLayout,
            binding.tvEveningTime to binding.eveningLayout,
            binding.tvNightTime to binding.nightLayout
        )

        timeViewMap.forEach { (timeView, slotLayout) ->
            timeView.setOnClickListener {
                if (selectedSlot == slotLayout) {
                    // Only allow picking time if this slot is selected
                    pickTime(requireContext()) { selectedTime ->
                        timeView.text = selectedTime
                    }
                } else {
                    // Optional: give subtle feedback (vibration or color blink)
                    timeView.animate().alpha(0.5f).setDuration(100).withEndAction {
                        timeView.alpha = 1f
                    }.start()
                }
            }
        }
    }

    private fun selectSlot(selected: View, allSlots: List<View>) {
        // Reset all slots
        allSlots.forEach { slot ->
            slot.setBackgroundResource(com.careavatar.core_ui.R.drawable.slot_time_unselected)

            val group = slot as ViewGroup
            val checkIcon = group.getChildAt(0)
            checkIcon.visibility = View.GONE

            // Reset time text color + background
            val timeTextView = group.findViewById<TextView>(
                when (slot.id) {
                    R.id.layoutMedicine -> R.id.tvMorningTime
                    R.id.afternoonLayout -> R.id.tvAfterTime
                    R.id.eveningLayout -> R.id.tvEveningTime
                    R.id.nightLayout -> R.id.tvNightTime
                    else -> R.id.tvMorningTime
                }
            )
            timeTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            timeTextView.setBackgroundResource(0)
            timeTextView.setTextColor(
                ContextCompat.getColor(slot.context, com.careavatar.core_ui.R.color.textColor)
            )
        }

        // Highlight selected slot
        selected.setBackgroundResource(com.careavatar.core_ui.R.drawable.slot_time_selected_bg)
        val selectedGroup = selected as ViewGroup
        val checkIcon = selectedGroup.getChildAt(0)
        checkIcon.visibility = View.VISIBLE

        // Highlight selected time text
        val selectedTimeText = selectedGroup.findViewById<TextView>(
            when (selected.id) {
                R.id.layoutMedicine -> R.id.tvMorningTime
                R.id.afternoonLayout -> R.id.tvAfterTime
                R.id.eveningLayout -> R.id.tvEveningTime
                R.id.nightLayout -> R.id.tvNightTime
                else -> R.id.tvMorningTime
            }
        )
        selectedTimeText.setBackgroundResource(com.careavatar.core_ui.R.drawable.slot_time_text_bg)
        selectedTimeText.setTextColor(ContextCompat.getColor(selected.context, com.careavatar.core_ui.R.color.textColor))
        selectedTimeText.setCompoundDrawablesWithIntrinsicBounds(0, 0, com.careavatar.core_ui.R.drawable.edit_line_box, 0)

        selectedSlot = selected
        enableNextButton(true)
    }

    private fun setupNextButton() {
        enableNextButton(false)
        binding.btnNext.setOnClickListener {
            if (selectedSlot != null) {
                findNavController().navigate(R.id.action_slotTimeFragment_to_addMedicineFragement)
            }
        }
    }

    private fun enableNextButton(enable: Boolean) {
        binding.btnNext.isEnabled = enable
        val color = if (enable) "#49A24E" else "#E0E0E0"
        binding.btnNext.setCardBackgroundColor(color.toColorInt())
    }
}
