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
    val hidebtn: Boolean = false,
    private val onItemClick: (CommnityMemberListResponse.CommnityMemberListResponseItem.Community.Member) -> Unit
) : RecyclerView.Adapter<LeaveCommunityMemberListAdapter.ContactViewHolder>() {

    private var selectedItem = -1

    class ContactViewHolder(val binding: AddPoepleRowLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = AddPoepleRowLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactViewHolder(binding)
    }

    override fun getItemCount(): Int = array.size

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val user = array[position]
        val binding = holder.binding

        binding.tvUserName.text = user.name

        // ✅ Highlight selected item
        if (selectedItem == position) {
            binding.radiobtn.setBackgroundResource(R.drawable.green_circle_btn)
        } else {
            binding.radiobtn.setBackgroundResource(R.drawable.radio_btn_outline_bg)
        }

        // ✅ Hide or show button
        if (hidebtn || user._id == userId) {
            binding.radiobtn.visibility = View.GONE
        } else {
            binding.radiobtn.visibility = View.VISIBLE
        }

        // ✅ Load profile image safely
        Glide.with(context)
            .load(IMAGE_BASEURL + user.avatar)
            .placeholder(R.drawable.profile_1)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .thumbnail(0.25f)
            .into(binding.tvProfile)

        // ✅ Safe single-selection logic
        binding.radiobtn.setOnClickListener {
            val adapterPos = holder.bindingAdapterPosition
            if (adapterPos == RecyclerView.NO_POSITION) return@setOnClickListener

            val previousSelected = selectedItem
            selectedItem = adapterPos

            // Update only changed items
            if (previousSelected != -1 && previousSelected < array.size) {
                notifyItemChanged(previousSelected)
            }
            notifyItemChanged(selectedItem)

            onItemClick(array[selectedItem])
        }

        // ✅ Handle full row click when hidebtn = true
        if (hidebtn) {
            holder.itemView.setOnClickListener {
                val adapterPos = holder.bindingAdapterPosition
                if (adapterPos != RecyclerView.NO_POSITION) {
                    onItemClick(array[adapterPos])
                }
            }
        }
    }

    fun updateList(newList: MutableList<CommnityMemberListResponse.CommnityMemberListResponseItem.Community.Member>) {
        array = newList
        selectedItem = -1  // Reset selection when list updates
        notifyDataSetChanged()
    }

    // ✅ Optional: helper to get selected user
    fun getSelectedUser(): CommnityMemberListResponse.CommnityMemberListResponseItem.Community.Member? {
        return array.getOrNull(selectedItem)
    }
}

