package com.asyscraft.community_module.adpaters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.asyscraft.community_module.databinding.ItemEventRowLayoutBinding
import com.asyscraft.community_module.databinding.UpcomingEventRowLayoutBinding
import com.bumptech.glide.Glide
import com.careavatar.core_model.UpcomingTodayEventList
import com.careavatar.core_utils.Constants
import com.careavatar.core_utils.DateTimePickerUtil.formatDateToReadable
import com.careavatar.core_utils.DateTimePickerUtil.formatDateToReadable1


class EventAdapter(
    val content: Context,
    val datalist: MutableList<UpcomingTodayEventList>,
    private val onItemClicked: (UpcomingTodayEventList) -> Unit
) : RecyclerView.Adapter<EventAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemEventRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEventRowLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = datalist[position]

        holder.binding.tvEventTitle.text = item.title
        holder.binding.tvEventDesc.text = item.description
        holder.binding.tvEventDateTime.text = formatDateToReadable1(item.eventDate)

        if (item.attachment?.isNotEmpty() ?: false){
            Glide.with(holder.itemView.context).load(Constants.IMAGE_BASEURL+ item.attachment!![0]).into(holder.binding.eventImage)
        }

        holder.itemView.setOnClickListener { onItemClicked(item) }

    }

    override fun getItemCount(): Int {
        return datalist.size
    }
}