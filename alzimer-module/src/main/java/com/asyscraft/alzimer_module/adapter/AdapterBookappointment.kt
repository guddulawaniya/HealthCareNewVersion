package com.asyscraft.alzimer_module.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.asyscraft.alzimer_module.R
import com.careavatar.core_model.alzimer.Doctor
import com.squareup.picasso.Picasso

class AdapterBookappointmentAdapterGame(
    private val context : Context,
    private val datalist: List<Doctor>,
    private val itemClickListener: (Doctor) -> Unit
) : RecyclerView.Adapter<AdapterBookappointmentAdapterGame.ViewHolderClass>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.doctor_layout, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun getItemCount(): Int {
        return datalist.size
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {

        val item = datalist[position]

        val imageUrl = item.image
        if (!imageUrl.isNullOrBlank()) {
            Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.image_dp) // Optional: shows while loading
                .error(R.drawable.image_dp)       // Optional: shows on error
                .into(holder.image)
        } else {
            holder.image.setImageResource(R.drawable.image_dp)
        }


        // Set text
        holder.gameName.text = item.name
        holder.rating.text = item.specialist
        if(item.status == "Scheduled"){
            holder.appointment.text = item.status
            holder.appointment.setBackgroundResource(R.drawable.appointmentbook)
            holder.appointment.setTextColor(ContextCompat.getColor(context, R.color.C34E224))

        }else{
            holder.appointment.text = item.status
            holder.appointment.setBackgroundResource(R.drawable.appointment_bg)
            holder.appointment.setTextColor(ContextCompat.getColor(context, R.color.A524E2))
        }

        // Handle click
        holder.itemView.setOnClickListener {
            itemClickListener(item)
        }
    }


    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image =itemView.findViewById<ImageView>(R.id.dctr_image)
        val gameName = itemView.findViewById<TextView>(R.id.dctr_name)
        val rating = itemView.findViewById<TextView>(R.id.speciality)
        val appointment = itemView.findViewById<TextView>(R.id.appointment_btn)
    }
}