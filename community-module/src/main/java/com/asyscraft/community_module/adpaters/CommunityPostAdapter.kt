package com.asyscraft.community_module.adpaters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asyscraft.community_module.databinding.CommunityStatusRowLyoutBinding
import com.bumptech.glide.Glide
import com.careavatar.core_model.CommunityPostData


class CommunityPostAdapter(
    val content: Context,
    val datalist: MutableList<CommunityPostData>,
) : RecyclerView.Adapter<CommunityPostAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: CommunityStatusRowLyoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CommunityStatusRowLyoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = datalist[position]

        holder.binding.tvUserName.text = item.user?.name
        Glide.with(content).load(item.user?.image).into(holder.binding.tvProfile)
        Glide.with(content).load(item.image).into(holder.binding.eventImage)
    }

    override fun getItemCount(): Int {
        return datalist.size
    }
}