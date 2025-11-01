package com.asyscraft.community_module.adpaters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.asyscraft.community_module.databinding.CommunityUserRowLayoutBinding
import com.bumptech.glide.Glide
import com.careavatar.core_model.CommnityMemberListResponse
import com.careavatar.core_ui.R
import com.careavatar.core_utils.Constants.Companion.IMAGE_BASEURL


class CommunityMemberListAdapter(
    val context: Context,
    val userId: String,
    var array: MutableList<CommnityMemberListResponse.CommnityMemberListResponseItem.Community.Member>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<CommunityMemberListAdapter.ContactViewHolder>() {

    private var creatorId: String = ""
    private var isCurrentUserAdmin: Boolean = false
    private var currentUserId: String = ""

    class ContactViewHolder(val binding: CommunityUserRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = CommunityUserRowLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ContactViewHolder(binding)
    }

    override fun getItemCount(): Int = array.size

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = array[position]


        holder.binding.apply {
            tvCommunityName.text = contact.name
            tvCommunityType.text = "Member"
            tvCommunityType.setTextColor(ContextCompat.getColor(context, R.color.textColor))


            Glide.with(context)
                .load(IMAGE_BASEURL + contact.avatar)
                .placeholder(R.drawable.profile_1)
                .into(ivProfile)


            if (isCurrentUserAdmin) {
                if (contact._id == creatorId) {
                    tvCommunityType.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.primaryColor
                        )
                    )
                    tvTime.visibility = View.GONE
                    tvCommunityType.text = "Admin"
                    tvCommunityType.isEnabled = false
                    tvCommunityType.setPadding(16, 0, 16, 0)
                } else {

                    tvTime.text = ""
                    tvTime.isEnabled = true
                    tvTime.setPadding(0, 0, 0, 0)
                    tvTime.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
                    val drawable = ContextCompat.getDrawable(context, R.drawable.cross_black_icon)
                    tvTime.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)

                    tvTime.setOnClickListener {
                        onItemClick(position)
                    }
                }

            }

            if (contact._id == creatorId) {
                tvCommunityType.visibility = View.VISIBLE
                tvCommunityType.text = "Admin"
                tvCommunityType.isEnabled = false
                tvCommunityType.setPadding(16, 0, 16, 0)
            }

//            holder.itemView.setOnClickListener {
//                val intent = when {
//                    contact.joinStatus == "Approved" -> {
//                        Intent(context, MessageActivity::class.java).apply {
//                            putExtra("id", contact._id)
//                            putExtra("userName", contact.name)
//                            putExtra("mobilenumber", contact.phoneNumber)
//                            putExtra("userImage", contact.avatar)
//                        }
//                    }
//
//                    userId != contact._id -> {
//                        Intent(context, ChatDetailsActivity::class.java).apply {
//                            putExtra("id", contact._id)
//                        }
//                    }
//
//                    else -> null
//                }
//
//                intent?.let {
//                    context.startActivity(it)
//                } ?: run {
////                    Toast.makeText(context, "Cannot open chat with this user.", Toast.LENGTH_SHORT).show()
//                }
//            }

        }
    }

    fun updateList(
        newArray: MutableList<CommnityMemberListResponse.CommnityMemberListResponseItem.Community.Member>,
        creatorId: String,
        isCurrentUserAdmin: Boolean,
        currentUserId: String
    ) {
        this.array = newArray
        this.creatorId = creatorId
        this.isCurrentUserAdmin = isCurrentUserAdmin
        this.currentUserId = currentUserId
        notifyDataSetChanged()
    }
}
