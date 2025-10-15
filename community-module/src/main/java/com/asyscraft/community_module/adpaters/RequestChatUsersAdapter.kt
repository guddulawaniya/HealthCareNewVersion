package com.asyscraft.community_module.adpaters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asyscraft.community_module.databinding.ChatRequestRowLayoutBinding
import com.careavatar.core_model.RequestChatUserModel

class RequestChatUsersAdapter(
    val content : Context,
    val datalist : MutableList<RequestChatUserModel>,
    val onClickListener: (RequestChatUserModel, Boolean) -> Unit
): RecyclerView.Adapter<RequestChatUsersAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ChatRequestRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChatRequestRowLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = datalist[position]
        holder.binding.tvName.text = item.name
        holder.binding.tvTime.text = item.time
        holder.binding.ivProfile.setImageResource(item.image)

        holder.binding.acceptbtn.setOnClickListener {
            onClickListener(item,true)
        }
        holder.binding.rajectbtn.setOnClickListener {
            onClickListener(item,false)
        }

    }
    override fun getItemCount(): Int {
        return datalist.size
    }
}