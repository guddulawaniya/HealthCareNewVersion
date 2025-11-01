package com.asyscraft.medical_reminder.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asyscraft.medical_reminder.R
import com.asyscraft.medical_reminder.databinding.ItemDateCardBinding
import com.careavatar.core_model.medicalReminder.CalenderDateModel

class DateAdapter(
    private val dateList: List<CalenderDateModel>,
    private val onDateSelected: (CalenderDateModel) -> Unit
) : RecyclerView.Adapter<DateAdapter.DateViewHolder>() {

    private var selectedPosition = 0

    inner class DateViewHolder(val binding: ItemDateCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CalenderDateModel, position: Int) {
            binding.tvDayName.text = item.day
            binding.tvDayNumber.text = item.date

            binding.root.setBackgroundResource(
                if (item.isSelected) {
                    binding.tvDayName.setTextColor(Color.WHITE)
                    binding.tvDayNumber.setTextColor(Color.WHITE)
                    com.careavatar.core_ui.R.drawable.selected_date_bg
                }
                else {
                    binding.tvDayName.setTextColor(Color.BLACK)
                    binding.tvDayNumber.setTextColor(Color.BLACK)
                    com.careavatar.core_ui.R.drawable.medical_types_bg
                }
            )

            binding.root.setOnClickListener {
                val previous = selectedPosition
                selectedPosition = position

                dateList[previous].isSelected = false
                dateList[selectedPosition].isSelected = true

                notifyItemChanged(previous)
                notifyItemChanged(selectedPosition)

                onDateSelected(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val binding = ItemDateCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        holder.bind(dateList[position], position)
    }

    override fun getItemCount(): Int = dateList.size
}
