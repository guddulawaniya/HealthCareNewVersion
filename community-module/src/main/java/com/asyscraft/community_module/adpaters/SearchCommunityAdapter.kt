package com.asyscraft.community_module.adpaters

import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.asyscraft.community_module.databinding.CommunityUserRowLayoutBinding
import com.bumptech.glide.Glide
import com.careavatar.core_model.SearchCommunityResponse
import com.careavatar.core_ui.R
import com.careavatar.core_utils.Constants
import com.careavatar.core_utils.DateTimePickerUtil.formatDateToReadable
import com.careavatar.core_utils.DateTimePickerUtil.formatDateToReadable1

class SearchCommunityAdapter(
    val context: Context,
    var array: MutableList<SearchCommunityResponse.Community>,
    private val onItemClick: (SearchCommunityResponse.Community) -> Unit
) : RecyclerView.Adapter<SearchCommunityAdapter.ViewHolder>() {

    class ViewHolder(val binding: CommunityUserRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding =
            CommunityUserRowLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return array.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.apply {

            Glide.with(context).load(Constants.IMAGE_BASEURL + array[position].communityLogo)
                .placeholder(
                    R.drawable.group_icon
                ).into(ivProfile)

            tvTime.text = formatDateToReadable1(array[position].createdAt)
            tvCommunityName.text = array[position].name
            tvCommunityType.text = array[position].type
        }


        holder.itemView.setOnClickListener {
            onItemClick(array[position])
        }


    }

    fun filterList(filterdNames: MutableList<SearchCommunityResponse.Community>) {
        this.array = filterdNames
        notifyDataSetChanged()
    }
}