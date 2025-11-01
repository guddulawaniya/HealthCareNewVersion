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
import com.careavatar.core_model.alzimer.Caregiverres
import com.squareup.picasso.Picasso

class AdapterCaregiver(
    private val context : Context,
//    private val datalist: List<Caregiverres>,
    private val datalist: ArrayList<Caregiverres>,
    private val itemClickListener: (Caregiverres) -> Unit
) : RecyclerView.Adapter<AdapterCaregiver.ViewHolderClass>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.caregiver_layout, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun getItemCount(): Int {
        return datalist.size
    }



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {

        val item = datalist[position]

         val baseUrl = "http://172.104.206.4:5000/uploads/"

        item.image?.let { imageUrl ->
            Picasso.get()
                .load(baseUrl+imageUrl)
                .placeholder(R.drawable.user_dummy_image) // Optional: shows while loading
                .error(R.drawable.user_dummy_image)             // Optional: shows on error
                .into(holder.image)
        } ?: run {
            // If image URL is null, load a default image or handle accordingly
            Picasso.get()
                .load(R.drawable.user_dummy_image)
                .placeholder(R.drawable.user_dummy_image) // Optional: shows while loading
                .error(R.drawable.user_dummy_image)             // Optional: shows on error
                .into(holder.image)
        }


//        Picasso.get().load(caregiver.image).into(holder.image)


//         Set text
        holder.Name.text = item.name


        holder.relation.text = item.relation

        // Handle click
        holder.itemView.setOnClickListener {
            itemClickListener(item)
        }
    }


    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image =itemView.findViewById<ImageView>(R.id.images)
        val Name = itemView.findViewById<TextView>(R.id.name)
        val relation = itemView.findViewById<TextView>(R.id.relation)
    }


}