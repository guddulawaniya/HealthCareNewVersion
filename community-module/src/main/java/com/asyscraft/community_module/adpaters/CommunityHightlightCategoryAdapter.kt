package com.asyscraft.community_module.adpaters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asyscraft.community_module.databinding.CategoryRowLayoutBinding
import com.careavatar.core_model.CategoryPost


class CommunityHightlightCategoryAdapter(
    val content: Context,
    val datalist: MutableList<CategoryPost>,
    val onItemClick: (position: String) -> Unit
) : RecyclerView.Adapter<CommunityHightlightCategoryAdapter.ViewHolder>() {
    private var selectedPosition = -1 // no selection initially
    inner class ViewHolder(val binding: CategoryRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CategoryRowLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = datalist[position]
        holder.binding.categoryName.text = item.name


        if (position == selectedPosition) {
            holder.binding.categoryName.setBackgroundResource(com.careavatar.core_ui.R.drawable.category_select_highlight_bg) // selected color
        } else {
            holder.binding.categoryName.setBackgroundResource(com.careavatar.core_ui.R.drawable.edit_text_bg) // default color
        }
        val currentPosition = position

        holder.binding.root.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = currentPosition

            // Notify items to redraw backgrounds
            if (previousPosition >= 0) notifyItemChanged(previousPosition)
            notifyItemChanged(currentPosition)

            // Trigger callback
            onItemClick(item.id)
        }
    }

    override fun getItemCount(): Int {
        return datalist.size
    }
}