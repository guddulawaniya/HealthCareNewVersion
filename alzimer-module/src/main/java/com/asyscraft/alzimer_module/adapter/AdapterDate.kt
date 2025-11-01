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
import com.careavatar.core_model.alzimer.date_response

class AdapterDate(
    private val context : Context,
    private val datalist: List<date_response>,
    private var selectedPosition: Int = -1,
    private val itemClickListener: (date_response) -> Unit,

) : RecyclerView.Adapter<AdapterDate.ViewHolderClass>() {


//    private var selectedPosition = -1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.date_layout, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun getItemCount(): Int {
        return datalist.size
    }


    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {

        val item = datalist[position]



        // Set text
        holder.date.text = item.Date.toString()
        holder.day.text = item.Day


        // Set background and text color based on selection
        val isSelected = position == selectedPosition
        holder.itemView.isSelected = isSelected

//        holder.whole.setBackgroundResource(R.drawable.date_time_selector_background)
        if (isSelected) {
            val textColor = ContextCompat.getColorStateList(context, R.color.white)
            holder.day.setTextColor(textColor)
            holder.date.setTextColor(textColor)
            holder.whole.setBackgroundResource(R.drawable.datechooseselect_background)
        }else
        {
            val textColor = ContextCompat.getColorStateList(context, R.color.CC6FF8)
            holder.day.setTextColor(textColor)
            holder.date.setTextColor(textColor)
            holder.whole.setBackgroundResource(R.drawable.datechoose_background)
        }




        // Handle click
        holder.itemView.setOnClickListener {
//            itemClickListener(item)

            selectedPosition = if (selectedPosition == position) -1 else position
            notifyDataSetChanged()
            itemClickListener(item)
        }
    }


    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val day = itemView.findViewById<TextView>(R.id.day)
        val date = itemView.findViewById<TextView>(R.id.date)
        val whole = itemView.findViewById<ConstraintLayout>(R.id.whole)
    }

}