package com.asyscraft.community_module.adpaters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.asyscraft.community_module.databinding.AddPoepleRowLayoutBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.careavatar.core_model.UsersInApp
import com.careavatar.core_ui.R
import com.careavatar.core_utils.Constants.Companion.IMAGE_BASEURL

class AddMemberCommunityAdapter(
    val context: Context,
    val userId: String,
    var array: MutableList<UsersInApp>,
    val listener: OnSelectedUsersListener
) : RecyclerView.Adapter<AddMemberCommunityAdapter.ContactViewHolder>() {


    class ContactViewHolder(val binding: AddPoepleRowLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = AddPoepleRowLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return array.size
    }

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

            // ✅ Always reset background and text to avoid wrong state from recycled views
            if (user.status) {
                radiobtn.setBackgroundResource(R.drawable.green_circle_btn)
            } else {
                radiobtn.setBackgroundResource(R.drawable.radio_btn_outline_bg)
            }

            radiobtn.setOnClickListener {
                // Toggle the status
                user.status = !user.status
                notifyItemChanged(position)

                // Pass the updated ID to listener
                listener.onUserSelected(user._id.toString())
            }
        }
    }

    fun updateList(newList: MutableList<UsersInApp>) {
        array = newList
        notifyDataSetChanged()
    }


    // Interface to return a single ID
    interface OnSelectedUsersListener {
        fun onUserSelected(userId: String)
    }

}
