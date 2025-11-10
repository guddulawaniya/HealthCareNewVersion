package com.asyscraft.medical_reminder.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asyscraft.medical_reminder.databinding.ItemReminderCardBinding
import com.asyscraft.medical_reminder.databinding.ItemReminderCardOuterLayoutBinding
import com.careavatar.core_model.medicalReminder.HistoryData
import com.careavatar.core_ui.R
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
class ReminderHistoryAdapter(
    dataList: MutableList<HistoryData>
) : RecyclerView.Adapter<ReminderHistoryAdapter.ViewHolder>() {

    private val groupedData: Map<String, List<HistoryData>>

    init {
        groupedData = groupByDate(dataList)
    }

    class ViewHolder(val binding: ItemReminderCardOuterLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemReminderCardOuterLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dateKey = groupedData.keys.toList()[position]
        val items = groupedData[dateKey] ?: emptyList()

        holder.binding.tvDate.text = dateKey
        holder.binding.rvIntakeWaterTime.apply {
            layoutManager = LinearLayoutManager(holder.itemView.context)
            adapter = ReminderHistoryAdapterInner(items.toMutableList())
        }
    }

    override fun getItemCount(): Int = groupedData.size

    // Group data by readable date (Today, Yesterday, or formatted date)
    @RequiresApi(Build.VERSION_CODES.O)
    private fun groupByDate(dataList: List<HistoryData>): Map<String, List<HistoryData>> {
        val grouped = mutableMapOf<String, MutableList<HistoryData>>()

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

        for (item in dataList) {
            try {
                val zonedDateTime = ZonedDateTime.parse(item.historyDate, formatter)
                    .withZoneSameInstant(ZoneId.systemDefault())
                val localDate = zonedDateTime.toLocalDate()

                val label = when {
                    localDate.isEqual(LocalDate.now()) -> "Today"
                    localDate.isEqual(LocalDate.now().minusDays(1)) -> "Yesterday"
                    else -> localDate.toString() // you can use a prettier format
                }

                if (!grouped.containsKey(label)) grouped[label] = mutableListOf()
                grouped[label]?.add(item)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Sort by date (most recent first)
        return grouped.toSortedMap(compareByDescending { key ->
            when (key) {
                "Today" -> LocalDate.now()
                "Yesterday" -> LocalDate.now().minusDays(1)
                else -> try { LocalDate.parse(key) } catch (_: Exception) { LocalDate.MIN }
            }
        })
    }
}


// ----------------- INNER ADAPTER --------------------

class ReminderHistoryAdapterInner(
    var dataList: MutableList<HistoryData>,
) : RecyclerView.Adapter<ReminderHistoryAdapterInner.ViewHolder>() {

    class ViewHolder(val binding: ItemReminderCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemReminderCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        holder.binding.tvTitle.text = data.medicine.name

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val zonedDateTime = ZonedDateTime.parse(data.historyDate, formatter)
            .withZoneSameInstant(ZoneId.systemDefault())
        val timeOnly = zonedDateTime.toLocalTime().truncatedTo(ChronoUnit.MINUTES)
        holder.binding.tvTime.text = timeOnly.toString()

        when (data.takenStatus.toInt()) {
            1 -> {
                holder.binding.indicatorDot.setImageResource(R.drawable.green_dot)
                holder.binding.tvStatus.text = "Taken"
                holder.binding.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.primaryColor))
            }
            0 -> {
                holder.binding.indicatorDot.setImageResource(R.drawable.red_dot)
                holder.binding.tvStatus.text = "Not Taken"
                holder.binding.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.red))
            }
            2 -> {
                holder.binding.indicatorDot.setImageResource(R.drawable.yellow_dot)
                holder.binding.tvStatus.text = "Skipped"
                holder.binding.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.activemodule))
            }
        }
    }

    override fun getItemCount(): Int = dataList.size
}
