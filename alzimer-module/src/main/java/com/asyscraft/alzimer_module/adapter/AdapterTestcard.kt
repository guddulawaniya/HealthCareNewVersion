package com.asyscraft.alzimer_module.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.asyscraft.alzimer_module.R
import com.asyscraft.alzimer_module.utils.TestCardItem
import com.squareup.picasso.Picasso

class AdapterTestcard(
    private val context: Context,
    private val datalist: List<TestCardItem>,
    private val itemClickListener: (TestCardItem) -> Unit
) : RecyclerView.Adapter<AdapterTestcard.ViewHolderClass>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.testcard_layout, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun getItemCount(): Int {
        return datalist.size
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val item = datalist[position]

        when (item) {
            is TestCardItem.Task -> {
                val task = item.task
                val baseUrl = "http://172.104.206.4:5000/uploads/"
                Picasso.get().load(baseUrl + task.thumbnail).into(holder.image)
                holder.heading.text = task.title
                holder.subheading.text = task.description
            }

            is TestCardItem.Activity -> {
                val activity = item.activity
                holder.image.setImageResource(R.drawable.spelling_bee)
//                holder.heading.text = activity.title
                holder.heading.setText("Surprise Activity")
                holder.subheading.text = activity.key
            }
        }

        holder.itemView.setOnClickListener {
            itemClickListener(item)
        }
    }

    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.image)
        val heading: TextView = itemView.findViewById(R.id.heading)
        val subheading: TextView = itemView.findViewById(R.id.subheading)
    }
}
