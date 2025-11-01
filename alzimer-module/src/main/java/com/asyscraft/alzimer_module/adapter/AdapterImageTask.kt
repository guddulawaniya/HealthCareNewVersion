package com.asyscraft.alzimer_module.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.asyscraft.alzimer_module.R
import com.careavatar.core_utils.Constants
import com.squareup.picasso.Picasso

class AdapterImageTask(private val context : Context,
                       private val datalist: List<String>
) : RecyclerView.Adapter<AdapterImageTask.ViewHolderClass>()  {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.activity_image_layout, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun getItemCount(): Int {
        return datalist.size
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {

        val imageUrl = Constants.IMAGE_BASEURL + datalist[position]

        Picasso.get()
            .load(imageUrl)
            .into(holder.image)



    }


    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image = itemView.findViewById<ImageView>(R.id.image)

    }
}