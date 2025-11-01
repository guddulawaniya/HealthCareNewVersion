package com.asyscraft.community_module.adpaters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.asyscraft.community_module.ChatDetailsActivity
import com.asyscraft.community_module.MessageActivity
import com.asyscraft.community_module.databinding.AddPoepleRowLayoutBinding
import com.asyscraft.community_module.databinding.CommunityUserRowLayoutBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.careavatar.core_model.CommnityMemberListResponse
import com.careavatar.core_model.UsersInApp
import com.careavatar.core_ui.R
import com.careavatar.core_utils.Constants.Companion.IMAGE_BASEURL
import kotlin.toString


class LeaveCommunityMemberListAdapter(
    val context: Context,
    val userId: String,
    var array: MutableList<CommnityMemberListResponse.CommnityMemberListResponseItem.Community.Member>,
    private val onItemClick: (CommnityMemberListResponse.CommnityMemberListResponseItem.Community.Member) -> Unit
) : RecyclerView.Adapter<LeaveCommunityMemberListAdapter.ContactViewHolder>() {

    private var  selectedItem = -1

    class ContactViewHolder(val binding: AddPoepleRowLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = AddPoepleRowLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactViewHolder(binding)
    }

    override fun getItemCount(): Int = array.size

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.binding.apply {
            val user = array[position]
            tvUserName.text = user.name

            // Hide add button for the logged-in user
            if (user._id == userId) {
                radiobtn.visibility = View.GONE
            }

            // Load profile image safely
            Glide.with(context)
                .load(IMAGE_BASEURL + user.avatar)
                .placeholder(R.drawable.profile_1)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .thumbnail(0.25f)
                .into(tvProfile)


            radiobtn.setOnClickListener {
                onItemClick(array[position])
            }
        }
    }


    fun updateList(newList: MutableList<CommnityMemberListResponse.CommnityMemberListResponseItem.Community.Member>) {
        array = newList
        notifyDataSetChanged()
    }

}
