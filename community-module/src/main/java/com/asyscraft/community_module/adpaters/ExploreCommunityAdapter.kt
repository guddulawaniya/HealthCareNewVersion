package com.asyscraft.community_module.adpaters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asyscraft.community_module.databinding.CommunityExploreRowLayoutBinding
import com.bumptech.glide.Glide
import com.careavatar.core_model.ExploreCommunityModel
import com.careavatar.core_model.SearchCommunityResponse
import com.careavatar.core_ui.R
import com.careavatar.core_utils.Constants

class ExploreCommunityAdapter(
    private val content: Context,
    private val datalist: MutableList<ExploreCommunityModel.CommunityData>,
    private val onItemClick: (ExploreCommunityModel.CommunityData) -> Unit
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

        if (position==1){
            holder.binding.newcreatepost.visibility = View.VISIBLE
        }else{
            holder.binding.newcreatepost.visibility = View.GONE
        }

        holder.binding.communityTitle.text = item.name
        holder.binding.communitySubtitle.text = "${item.members.size} Members , ${item.category.name} , ${item.type}"

        if (item.communityLogo.isNotEmpty()){
            Glide.with(content).load(Constants.IMAGE_BASEURL+item.communityLogo).placeholder(R.drawable.commuinty_placeholder_image).into(holder.binding.communityImage)

        }else
        {
            holder.binding.communityImage.setImageResource(R.drawable.commuinty_placeholder_image)
        }
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }

    }

    override fun getItemCount(): Int {
        return datalist.size
    }
}