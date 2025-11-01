package com.asyscraft.community_module.adpaters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.asyscraft.community_module.CommunityMessageActivity
import com.asyscraft.community_module.databinding.CommunityUserRowLayoutBinding
import com.bumptech.glide.Glide
import com.careavatar.core_model.Communitymember
import com.careavatar.core_ui.R
import com.careavatar.core_utils.Constants.Companion.IMAGE_BASEURL

class UserDetailsCommunityAdapter(
    val context: Context,
    var datalist: MutableList<Communitymember>
) : RecyclerView.Adapter<UserDetailsCommunityAdapter.ViewHolder>() {

    class ViewHolder(val binding: CommunityUserRowLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = CommunityUserRowLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return datalist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = datalist[position]

        holder.binding.apply {
            Glide.with(context).load(IMAGE_BASEURL + item.communityLogo)
                .placeholder(
                    R.drawable.profile_1
                ).into(ivProfile)

            tvCommunityName.text = item.name
            tvCommunityType.setTextColor(ContextCompat.getColor(context, R.color.textColor))
            tvCommunityType.text = item.memberCount+"Member"
            tvTime.visibility = View.GONE
        }




        holder.itemView.setOnClickListener {

            val memberIds = ArrayList<String>()

            for (member in item.members) {
                memberIds.add(member._id)
            }

            context.startActivity(
                Intent(context, CommunityMessageActivity::class.java)
                    .putExtra("communityId", item._id)
                    .putExtra("creatorId", item.creatorId)
                    .putStringArrayListExtra("members", memberIds)
                    .putExtra("communityName", item.name)
                    .putExtra("communityImage", item.communityLogo)
                    .putExtra("count", item.memberCount)
                    .putExtra("type1", item.type)
            )
        }


    }

    fun filterList(filterdNames: MutableList<Communitymember>) {
        this.datalist = filterdNames
        notifyDataSetChanged()
    }
}