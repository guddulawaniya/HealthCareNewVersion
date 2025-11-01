package com.asyscraft.community_module.adpaters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.careavatar.core_model.shareDocumentModel

class AudioShareAdapter(
    private val documentList: MutableList<shareDocumentModel>,
) : RecyclerView.Adapter<AudioShareAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sharedbyName: TextView =
            itemView.findViewById(com.asyscraft.community_module.R.id.sharedbyName)
        val tvTime: TextView = itemView.findViewById(com.asyscraft.community_module.R.id.tvTime)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(com.asyscraft.community_module.R.layout.audio_row_layout, parent, false)
        return ImageViewHolder(view)
    }


    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val dataitem = documentList[position] // Renamed for clarity
        holder.sharedbyName.text = dataitem.filename
        holder.tvTime.text = dataitem.fileSize


    }

    override fun getItemCount(): Int = documentList.size

}
