package com.asyscraft.community_module.adpaters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asyscraft.community_module.databinding.CategoryRowLayoutBinding
import com.asyscraft.community_module.databinding.CommunityStatusRowLyoutBinding
import com.bumptech.glide.Glide
import com.careavatar.core_model.CategoryPost
import com.careavatar.core_model.CommunityPostData


class CommunityHightlightCategoryAdapter(
    val content: Context,
    val datalist: MutableList<CategoryPost>,
    val onItemClick: (position: String) -> Unit
) : RecyclerView.Adapter<CommunityHightlightCategoryAdapter.ViewHolder>() {

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
        holder.binding.root.setOnClickListener {
            onItemClick(item.id)
        }
    }

    override fun getItemCount(): Int {
        return datalist.size
    }
}