package com.asyscraft.alzimer_module.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.asyscraft.alzimer_module.R
import com.careavatar.core_model.alzimer.VideoItem
import com.squareup.picasso.Picasso
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class AdapterVideo(
    private val context : Context,
    private val datalist: List<VideoItem>,
    private val itemClickListener: (VideoItem) -> Unit
) : RecyclerView.Adapter<AdapterVideo.ViewHolderClass>() {



//    interface OnItemClickListener {
//        fun onItemClick(dataClass: game_response)
//
//    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.videoslayout, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun getItemCount(): Int {
        return datalist.size
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {

        val item = datalist[position]

//        Picasso.get().load(item.Image).into(holder.image)
        val baseUrl = "http://172.104.206.4:5000/uploads/"

        Picasso.get()
            .load(baseUrl + item.thumbnail) // Ensure this is the full URL to the thumbnail image
//            .placeholder(R.drawable.placeholder) // Optional placeholder image
//            .error(R.drawable.error_image)       // Optional error image
            .into(holder.image)

        // Set text
        holder.videoname.text = item.title
        holder.views.text = item.totalViews.toString()

        val parsedDate = OffsetDateTime.parse(item.createdAt)
        val formattedDate = parsedDate.format(DateTimeFormatter.ofPattern("dd-MMMM-YYYY"))
        holder.date.text = formattedDate

        // Handle click
        holder.itemView.setOnClickListener {
            itemClickListener(item)
        }
    }


    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image =itemView.findViewById<ImageView>(R.id.videoimg)
        val videoname = itemView.findViewById<TextView>(R.id.heading)
        val views = itemView.findViewById<TextView>(R.id.view)
        val date = itemView.findViewById<TextView>(R.id.date)
    }
}