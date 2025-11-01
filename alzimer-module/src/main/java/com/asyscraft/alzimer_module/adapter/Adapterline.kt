package com.asyscraft.alzimer_module.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.asyscraft.alzimer_module.R
import com.careavatar.core_model.alzimer.line_respone

class Adapterline(
    private val context : Context,
    private val datalist: List<line_respone>,
): RecyclerView.Adapter<Adapterline.ViewHolderClass>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.line_layout, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun getItemCount(): Int {
        return datalist.size
    }


    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {

        val item = datalist[position]

        if (position == datalist.size - 1) {
            holder.line.visibility = View.GONE
        } else {
            holder.line.visibility = View.VISIBLE
        }

        if (position == 0) {
            holder.image.setImageResource(R.drawable.selected_circle)
//            selected_line
        }

    }


    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val image = itemView.findViewById<ImageView>(R.id.circle)
        val line = itemView.findViewById<ImageView>(R.id.line)
    }
}