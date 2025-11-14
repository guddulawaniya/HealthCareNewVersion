package com.asyscraft.community_module.adpaters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.careavatar.core_ui.R
import com.careavatar.core_utils.Constants

class ImageAdapter(
    private val images: MutableList<String>,
    private val hideDelete: Boolean = false,
    private val onClickItem: (position: Int) -> Unit
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

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

        Log.d("ImageAdapter", "Loading image from path: $imagePath")

        val glideRequest = Glide.with(holder.itemView.context)


        if (imagePath.startsWith("content://") || imagePath.startsWith("file://")) {

            glideRequest.load(imagePath.toUri())
                .into(holder.imageView)
        } else {

            val fullUrl = Constants.socket_URL + imagePath
            glideRequest.load(fullUrl)
                .into(holder.imageView)
        }
        if (hideDelete) {
            holder.crossimage.visibility = View.GONE
        }

        holder.crossimage.setOnClickListener {
            onClickItem(position)
        }
    }

    override fun getItemCount(): Int = minOf(images.size, 3)

}
