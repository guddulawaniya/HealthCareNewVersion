package com.asyscraft.service_module.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.asyscraft.service_module.databinding.MedicalServicesRowLayoutBinding
import com.careavatar.core_model.BookCaretakerModel
import com.careavatar.core_ui.R

class MedicalServicesAdapter(
    val content : Context,
    val datalist : MutableList<BookCaretakerModel>,
    val onClickItem : (BookCaretakerModel) -> Unit
): RecyclerView.Adapter<MedicalServicesAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: MedicalServicesRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MedicalServicesRowLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = datalist[position]
        if (item.description=="Not Available"){
            holder.binding.description.setTextColor(
                ContextCompat.getColor(content, R.color.red)
            )
        }else
        {
            holder.binding.description.setTextColor(
                ContextCompat.getColor(content, R.color.primaryColor)
            )
        }
        holder.binding.title.text = item.title
        holder.binding.description.text = item.description
        holder.binding.imageView.setImageResource(item.image)
        holder.binding.root.setOnClickListener {
            onClickItem.invoke(item)
        }

    }
    override fun getItemCount(): Int {
        return datalist.size
    }



}