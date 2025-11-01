package com.asyscraft.alzimer_module.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.asyscraft.alzimer_module.R
import com.careavatar.core_model.alzimer.MeditationItem
import com.careavatar.core_utils.Constants
import com.squareup.picasso.Picasso

class AdapterMusic(
    private val context : Context,
    private val datalist: List<MeditationItem>,
    private val itemClickListener: (MeditationItem) -> Unit
): RecyclerView.Adapter<AdapterMusic.ViewHolderClass>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.music_layout_player, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun getItemCount(): Int {
        return datalist.size
    }


    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {

        val item = datalist[position]


        // Set text

        Picasso.get()
            .load(Constants.IMAGE_BASEURL+item.thumbnailUrl)
            .into(holder.image)

        holder.title.text = item.title





        // Handle click
        holder.itemView.setOnClickListener {


            itemClickListener(item)
        }
    }


    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val image = itemView.findViewById<ImageView>(R.id.image1)
        val title = itemView.findViewById<TextView>(R.id.title)

    }
}