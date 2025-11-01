package com.asyscraft.community_module.adpaters

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.careavatar.core_ui.R
import com.careavatar.core_utils.Constants

class GalleryImageAdapter(
    private val images: MutableList<Int>,
) : RecyclerView.Adapter<GalleryImageAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageattech)
        val crossimage: ImageView = itemView.findViewById(R.id.crossimage)
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.event_add_image_row_layout, parent, false)
        return ImageViewHolder(view)
    }


    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imagePath = images[position] // Renamed for clarity
        holder.imageView.setBackgroundResource(imagePath)
        holder.crossimage.visibility = View.GONE

    }

    override fun getItemCount(): Int = minOf(images.size, 3)

}
