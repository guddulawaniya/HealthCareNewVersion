package com.asyscraft.alzimer_module.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.asyscraft.alzimer_module.R
import com.careavatar.core_model.alzimer.AlbumData
import com.careavatar.core_utils.Constants
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class AdapterAlbum(
    private val context: Context,
    private val datalist: List<AlbumData>,
    private val itemClickListener: (AlbumData) -> Unit
) : RecyclerView.Adapter<AdapterAlbum.ViewHolderClass>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.album_layout, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun getItemCount(): Int {
        return datalist.size
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {

        val item = datalist[position]

        Picasso.get()
            .load(Constants.IMAGE_BASEURL + item.thumbnail.last())
            .into(holder.image)

        holder.name.text = item.title


        // Handle click
        holder.itemView.setOnClickListener {
            itemClickListener(item)
        }
    }


    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image = itemView.findViewById<CircleImageView>(R.id.image)
        val name = itemView.findViewById<TextView>(R.id.name)

    }
}