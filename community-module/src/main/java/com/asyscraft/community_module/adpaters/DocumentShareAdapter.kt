package com.asyscraft.community_module.adpaters

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.careavatar.core_model.shareDocumentModel
import com.careavatar.core_ui.R
import com.careavatar.core_utils.Constants
import org.w3c.dom.Text

class DocumentShareAdapter(
    private val documentList: MutableList<shareDocumentModel>,
) : RecyclerView.Adapter<DocumentShareAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val filename: TextView = itemView.findViewById(com.asyscraft.community_module.R.id.filename)
        val fileSize: TextView = itemView.findViewById(com.asyscraft.community_module.R.id.fileSize)
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(com.asyscraft.community_module.R.layout.document_row_layout, parent, false)
        return ImageViewHolder(view)
    }


    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val dataitem = documentList[position] // Renamed for clarity
        holder.filename.text =dataitem.filename
        holder.fileSize.text = "PDF ${dataitem.fileSize}MB"



    }

    override fun getItemCount(): Int = documentList.size

}
