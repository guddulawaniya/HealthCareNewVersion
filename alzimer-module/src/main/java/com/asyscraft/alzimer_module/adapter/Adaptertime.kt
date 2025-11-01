package com.asyscraft.alzimer_module.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.asyscraft.alzimer_module.R
import com.careavatar.core_model.alzimer.time_response

class Adaptertime (
    private val context : Context,
    private val datalist: List<time_response>,
    private val itemClickListener: (time_response) -> Unit
) : RecyclerView.Adapter<Adaptertime.ViewHolderClass>() {


    private var selectedPosition = -1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.time_layout, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun getItemCount(): Int {
        return datalist.size
    }


    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {

        val item = datalist[position]


        val isSelected = position == selectedPosition
        holder.itemView.isSelected = isSelected


        if (isSelected) {
            val textColor = ContextCompat.getColorStateList(context, R.color.white)
            holder.time.setTextColor(textColor)
            holder.whole.setBackgroundResource(R.drawable.datechooseselect_background)
        }else
        {
            val textColor = ContextCompat.getColorStateList(context, R.color.CC6FF8)
            holder.time.setTextColor(textColor)
            holder.whole.setBackgroundResource(R.drawable.datechoose_background)
        }

        // Set text
        holder.time.text = item.Time.toString()

        // Handle click
        holder.itemView.setOnClickListener {
//            itemClickListener(item)

            selectedPosition = if (selectedPosition == position) -1 else position
            notifyDataSetChanged()
            itemClickListener(item)

        }
    }


    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val time = itemView.findViewById<TextView>(R.id.time)
        val whole = itemView.findViewById<ConstraintLayout>(R.id.whole)
    }
}