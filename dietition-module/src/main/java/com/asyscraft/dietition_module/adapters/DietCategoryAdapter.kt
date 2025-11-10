package com.asyscraft.dietition_module.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.asyscraft.dietition_module.databinding.CheckRowLayoutBinding
import com.careavatar.core_model.dietition.DietCategoryData
import com.careavatar.core_ui.R

class DietCategoryAdapter(
    private val context: Context,
    private var datalist: List<DietCategoryData>,
    private val onClickItem: (Int) -> Unit
) : RecyclerView.Adapter<DietCategoryAdapter.ViewHolder>() {

    private var selectedPosition: Int = -1   // ✅ Only one selected

    inner class ViewHolder(val binding: CheckRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = CheckRowLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = datalist[position]

        val isSelected = position == selectedPosition

        holder.binding.tvText.text = item.name

        if (isSelected) {
            holder.binding.tvText.background =
                ContextCompat.getDrawable(context, R.drawable.button_circle_bg)
            holder.binding.tvText.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            holder.binding.tvText.background =
                ContextCompat.getDrawable(context, R.drawable.medical_types_bg)
            holder.binding.tvText.setTextColor(ContextCompat.getColor(context, R.color.black))
        }

        holder.itemView.setOnClickListener {
            val oldPosition = selectedPosition
            selectedPosition = holder.adapterPosition

            // ✅ refresh only old and new selected item
            notifyItemChanged(oldPosition)
            notifyItemChanged(selectedPosition)

            onClickItem(selectedPosition)
        }
    }

    override fun getItemCount(): Int = datalist.size
}
