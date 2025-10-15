package com.asyscraft.community_module.adpaters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asyscraft.community_module.databinding.ReminderNotificationRowLayoutBinding
import com.careavatar.core_model.NotificationResponse
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class ReminderNotificationAdapter(
    val context: Context,
    var array: MutableList<NotificationResponse.NotificationResponseItem>,

    ) : RecyclerView.Adapter<ReminderNotificationAdapter.ViewHolder>() {

    class ViewHolder(val binding: ReminderNotificationRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ReminderNotificationRowLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return array.size

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = array[position]

    }


    private fun formatEventDate(isoDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val outputFormat = SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm a", Locale.getDefault())

            val date: Date? = inputFormat.parse(isoDate)
            date?.let { outputFormat.format(it) } ?: "Invalid Date"
        } catch (e: Exception) {
            "Invalid Date"
        }
    }


}