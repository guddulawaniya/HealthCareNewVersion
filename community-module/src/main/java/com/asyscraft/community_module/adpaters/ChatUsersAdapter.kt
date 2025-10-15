package com.asyscraft.community_module.adpaters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asyscraft.community_module.databinding.ItemChatBinding
import com.careavatar.core_model.ChatuserModel

class ChatUsersAdapter(
    val content : Context,
    val datalist : MutableList<ChatuserModel>,
    val onClickListener: (ChatuserModel) -> Unit
): RecyclerView.Adapter<ChatUsersAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = datalist[position]
        holder.binding.tvName.text = item.name
        holder.binding.tvMessage.text = item.lastMessage
        holder.binding.tvTime.text = item.time
        holder.binding.newMessagecount.text = item.newMessagecount
        holder.binding.ivProfile.setImageResource(item.image)

        holder.binding.root.setOnClickListener {
            onClickListener(item)
        }

    }
    override fun getItemCount(): Int {
        return datalist.size
    }
}