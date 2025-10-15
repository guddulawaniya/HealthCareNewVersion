package com.asyscraft.service_module.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asyscraft.service_module.databinding.CareTakerRowLayoutBinding
import com.careavatar.core_model.CareTakerModel


class CareTakerAdapter(
    val content : Context,
    val datalist : MutableList<CareTakerModel>,
    val onClickListener: (CareTakerModel) -> Unit
): RecyclerView.Adapter<CareTakerAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: CareTakerRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CareTakerRowLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = datalist[position]
        holder.binding.name.text = item.title
        holder.binding.tvAvailability.text = "Available . "+item.availability
        holder.binding.tvExperience.text = item.experience+" years"
        holder.binding.price.text = "$${item.price}/hr"
        holder.binding.tvRating.text = item.rating
        holder.binding.imageView.setImageResource(item.image)

        holder.binding.root.setOnClickListener {
            onClickListener(item)
        }

    }
    override fun getItemCount(): Int {
        return datalist.size
    }
}