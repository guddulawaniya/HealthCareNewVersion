package com.asyscraft.community_module.adpaters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asyscraft.community_module.databinding.CommunityExploreRowLayoutBinding
import com.careavatar.userapploginmodule.model.ExploreCommunityModel

class ExploreCommunityAdapter(
    val content: Context,
    val datalist: MutableList<ExploreCommunityModel>
) : RecyclerView.Adapter<ExploreCommunityAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: CommunityExploreRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CommunityExploreRowLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = datalist[position]

        if (item.type){
            holder.binding.newcreatepost.visibility = View.VISIBLE
        }else{
            holder.binding.newcreatepost.visibility = View.GONE
        }

        holder.binding.communityTitle.text = item.title
        holder.binding.communitySubtitle.text = item.subtitle
        holder.binding.communityImage.setImageResource(item.image)

    }

    override fun getItemCount(): Int {
        return datalist.size
    }
}