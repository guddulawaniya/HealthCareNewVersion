package com.asyscraft.alzimer_module.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.asyscraft.alzimer_module.R
import com.careavatar.core_model.alzimer.MessageData
import com.careavatar.core_utils.Constants
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class AdapterPlannerMessage(
    private val context : Context,
    private val datalist: List<MessageData>,
): RecyclerView.Adapter<AdapterPlannerMessage.ViewHolderClass>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.planner_message_layout, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun getItemCount(): Int {
        return datalist.size
    }


    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {

        val item = datalist[position]


        holder.message.text = item.message

        Picasso.get()
            .load(Constants.IMAGE_BASEURL + item.senderInfo.image)
            .into(holder.image)

    }


    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val image = itemView.findViewById<CircleImageView>(R.id.image)
        val message = itemView.findViewById<TextView>(R.id.message)
    }
}