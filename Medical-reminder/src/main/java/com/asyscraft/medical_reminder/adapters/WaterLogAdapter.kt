package com.asyscraft.medical_reminder.adapters


import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asyscraft.medical_reminder.databinding.ItemWaterLogBinding
import com.asyscraft.medical_reminder.databinding.ItemWaterLogOuterRowLayoutBinding
import com.careavatar.core_model.medicalReminder.History

data class DateGroupedHistory(val title: String, val list: List<History>)

class WaterLogAdapter(
    private val context: Context, private var waterLogs: MutableList<DateGroupedHistory>
) : RecyclerView.Adapter<WaterLogAdapter.WaterLogViewHolder>() {

    inner class WaterLogViewHolder(val binding: ItemWaterLogOuterRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WaterLogViewHolder {
        val binding = ItemWaterLogOuterRowLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return WaterLogViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: WaterLogViewHolder, position: Int) {
        val item = waterLogs[position]
        with(holder.binding) {

            // Set the group title (e.g. "Today", "Yesterday", "2025-10-28")
            tvDate.text = item.title

            // Setup inner RecyclerView
            val adapter = WaterLogAdapterInner(item.list.toMutableList())
            rvIntakeWaterTime.layoutManager = LinearLayoutManager(context)
            rvIntakeWaterTime.adapter = adapter

            // 🟢 Calculate total water from all entries in this group
            val totalWaterMl = item.list.sumOf { history ->
                history.perSip.toIntOrNull() ?: 0
            }

            // Convert to liters if ≥ 1000 ml
            val totalText = if (totalWaterMl >= 1000) {
                val liters = totalWaterMl / 1000.0
                String.format("%.2f L", liters)
            } else {
                "$totalWaterMl ml"
            }

            tvTotalWater.text = "Total: $totalText"
        }
    }


    override fun getItemCount(): Int = waterLogs.size

}


class WaterLogAdapterInner(
    private var waterLogs: MutableList<History>
) : RecyclerView.Adapter<WaterLogAdapterInner.WaterLogViewHolder>() {

    inner class WaterLogViewHolder(val binding: ItemWaterLogBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WaterLogViewHolder {
        val binding = ItemWaterLogBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return WaterLogViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: WaterLogViewHolder, position: Int) {
        val item = waterLogs[position]
        with(holder.binding) {

            tvTimeQauntity.text = formatTimeToLocal(item.time) + "-" + item.perSip + "ml"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatTimeToLocal(timeString: String): String {
        return try {
            val instant = java.time.Instant.parse(timeString)
            val localDateTime = instant.atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()
            val formatter = java.time.format.DateTimeFormatter.ofPattern("hh:mm a")
            localDateTime.format(formatter)
        } catch (e: Exception) {
            ""
        }
    }


    override fun getItemCount(): Int = waterLogs.size

}
