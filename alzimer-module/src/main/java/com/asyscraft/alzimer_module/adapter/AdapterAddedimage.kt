package com.asyscraft.alzimer_module.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.asyscraft.alzimer_module.R

class AdapterAddedimage(
    private val context : Context,
    private val datalist: MutableList<Uri>
) : RecyclerView.Adapter<AdapterAddedimage.ViewHolderClass>()   {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.added_image_layout, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun getItemCount(): Int {
        return datalist.size
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val item = datalist[position]

//        Picasso.get()
//            .load(item.image)
//            .into(holder.image)

        holder.image.setImageURI(item)



        holder.cross.setOnClickListener{
            datalist.removeAt(holder.adapterPosition)
            notifyItemRemoved(holder.adapterPosition)
            notifyItemRangeChanged(holder.adapterPosition, datalist.size)
        }



    }

    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.image)
        val cross: ImageButton = itemView.findViewById(R.id.cross)
    }
}