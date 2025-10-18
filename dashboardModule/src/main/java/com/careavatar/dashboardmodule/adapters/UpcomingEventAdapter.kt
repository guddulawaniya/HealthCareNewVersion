package com.careavatar.dashboardmodule.adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.careavatar.core_model.UpcomingTodayEventList
import com.careavatar.dashboardmodule.databinding.UpcomingEventRowLayoutBinding
import androidx.core.view.isVisible
import com.careavatar.core_utils.DateTimePickerUtil.formatDateToReadable

class UpcomingEventAdapter(
    val content: Context,
    val datalist: MutableList<UpcomingTodayEventList>,
    private val listener: (UpcomingTodayEventList) -> Unit
) : RecyclerView.Adapter<UpcomingEventAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: UpcomingEventRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = UpcomingEventRowLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = datalist[position]

        holder.binding.eventTitle.text = item.title
        holder.binding.tvDescription.text = item.description
        holder.binding.eventDate.text = formatDateToReadable(item.eventDate)

        holder.itemView.setOnClickListener {
            listener(item)
        }

        holder.binding.arrowBtn.setOnClickListener {
            if (holder.binding.imageRecylerview.isVisible) {
                holder.binding.imageRecylerview.visibility = View.GONE
            }else{
                holder.binding.imageRecylerview.visibility = View.VISIBLE
            }

        }

    }

    override fun getItemCount(): Int {
        return datalist.size
    }
}