package com.careavatar.dashboardmodule.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.careavatar.core_model.ActiveServiceModel
import com.careavatar.core_ui.R
import com.careavatar.dashboardmodule.databinding.ProgressServiceRowLayoutBinding

class ActiveServicesAdapter(
    val content: Context,
    val datalist: MutableList<ActiveServiceModel>
) : RecyclerView.Adapter<ActiveServicesAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ProgressServiceRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ProgressServiceRowLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = datalist[position]
        if (item.status=="In Progress"){
            holder.binding.status.setTextColor(content.getColor(R.color.primaryColor))
        }else{
            holder.binding.status.setTextColor(content.getColor(R.color.textColor))
        }
        holder.binding.tvName.text = item.title
        holder.binding.tvMessage.text = item.description
        holder.binding.status.text = item.status
        holder.binding.ivProfile.setImageResource(item.image)

    }

    override fun getItemCount(): Int {
        return datalist.size
    }
}