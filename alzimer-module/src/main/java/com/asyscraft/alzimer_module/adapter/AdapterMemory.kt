package com.asyscraft.alzimer_module.adapter

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.asyscraft.alzimer_module.R
import com.careavatar.core_model.alzimer.MemoryItem
import com.careavatar.core_utils.Constants
import com.squareup.picasso.Picasso
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AdapterMemory(
    private val context: Context,
    private val datalist: List<MemoryItem>
) : RecyclerView.Adapter<AdapterMemory.ViewHolderClass>()   {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.memory_layout, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun getItemCount(): Int {
        return datalist.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val item = datalist[position]

//        Picasso.get()
//            .load(item.image)
//            .into(holder.image)

        if(position == 0){
            holder.recently.visibility = View.VISIBLE
        }


        Picasso.get()
            .load(Constants.IMAGE_BASEURL + item.thumbnail)
            .into(holder.image)

        holder.heading.text = item.title


        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val dateOnly = LocalDateTime.parse(item.createdAt, formatter).toLocalDate().toString()
        holder.date.text = dateOnly

        val background = holder.main.background as GradientDrawable

        when (item.color) {
            "violet" -> background.setColor(ContextCompat.getColor(holder.itemView.context, R.color.DEB5F7))
            "green"  -> background.setColor(ContextCompat.getColor(holder.itemView.context, R.color.B7F8B0))
            "red"    -> background.setColor(ContextCompat.getColor(holder.itemView.context, R.color.FD7373))
            "orange" -> background.setColor(ContextCompat.getColor(holder.itemView.context, R.color.FFB561))

        }


    }

    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.image)
        val recently: TextView = itemView.findViewById(R.id.recently)
        val heading: TextView = itemView.findViewById(R.id.heading)
        val date : TextView = itemView.findViewById(R.id.date)
        val main : ConstraintLayout = itemView.findViewById(R.id.main)
    }
}