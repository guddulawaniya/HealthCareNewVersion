package com.asyscraft.community_module.adpaters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.asyscraft.community_module.MessageActivity
import com.asyscraft.community_module.databinding.FindPeopleRowLayoutBinding
import com.bumptech.glide.Glide
import com.careavatar.core_model.GetUserByCategoryResponse
import com.careavatar.core_ui.R
import com.careavatar.core_utils.Constants.Companion.IMAGE_BASEURL

class CategoryMemberListAdapter(
    val context: Context,
    val UserId: String?,
    var list: MutableList<GetUserByCategoryResponse.User>,
    private val onItemClick: (GetUserByCategoryResponse.User, Int) -> Unit
):RecyclerView.Adapter<CategoryMemberListAdapter.ContactViewHolder>() {

    class ContactViewHolder(val binding: FindPeopleRowLayoutBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = FindPeopleRowLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val user = list[position]

        holder.binding.tvName.text = user.name
        holder.binding.tvDistance.text = "${user.distanceInKm.toUInt()}+ Km ways"

        if (user._id==UserId){
            holder.binding.addMemberBtn.visibility = View.GONE
            user.status = "approved"
        }


        Glide.with(context)
            .load(IMAGE_BASEURL + user.avatar)
            .placeholder(R.drawable.profile_1)
            .into(holder.binding.ivProfile)


        // Set button UI based on request status
        when (user.status) {
            "approved" -> {
                holder.binding.addMemberBtn.setCardBackgroundColor(ContextCompat.getColor(context, R.color.primaryColor))
                holder.binding.requestSendImage.background = ContextCompat.getDrawable(context, R.drawable.request_send_icon)
            }
            "pending" -> {
                holder.binding.addMemberBtn.setCardBackgroundColor(ContextCompat.getColor(context, R.color.lightGray))
                holder.binding.requestSendImage.background = ContextCompat.getDrawable(context, R.drawable.clock_icon)

            }
            else -> {
                holder.binding.addMemberBtn.setCardBackgroundColor(ContextCompat.getColor(context, R.color.backgroundColor))
                holder.binding.requestSendImage.background = ContextCompat.getDrawable(context, R.drawable.add_user_icon)
            }
        }

        holder.itemView.setOnClickListener{
            onItemClick(user, 1)
        }

        // Click listener
        holder.binding.addMemberBtn.setOnClickListener {
            when (user.status) {
                "approved"-> {
                    val intent = Intent(context, MessageActivity::class.java).apply {
                        putExtra("userName", user.name)
                        putExtra("userImage", user.avatar)
                        putExtra("id", user._id)
                    }
                    context.startActivity(intent)
                }
                "pending" -> {
                    user.status = ""
                    notifyItemChanged(position)
                    onItemClick(user, 0)

                }
                else -> {
                    user.status = "pending"
                    notifyItemChanged(position)
                    onItemClick(user, 0)
                }
            }
        }
    }

    fun updateList(newList: MutableList<GetUserByCategoryResponse.User>) {
        list = newList
        notifyDataSetChanged()
    }


}