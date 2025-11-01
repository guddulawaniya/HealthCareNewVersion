package com.asyscraft.alzimer_module.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.asyscraft.alzimer_module.R
import com.careavatar.core_model.alzimer.game_response
import com.squareup.picasso.Picasso

class AdapterGame(
    private val context : Context,
    private val datalist: List<game_response>,
    private val itemClickListener: (game_response) -> Unit,
    private var useAlternativeLayout: Boolean = false
) : RecyclerView.Adapter<AdapterGame.ViewHolderClass>() {



//    interface OnItemClickListener {
//        fun onItemClick(dataClass: game_response)
//
//    }



    // Function to switch layout
    fun setAlternativeLayout(useAlternative: Boolean) {
        this.useAlternativeLayout = useAlternative
        notifyDataSetChanged()
    }



    // 👇 This tells RecyclerView to use different view type when layout changes
    override fun getItemViewType(position: Int): Int {
        return if (useAlternativeLayout) 1 else 0
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {

        val layoutRes = if (useAlternativeLayout) {
            R.layout.suggestedgame_layout  // Your alternate layout XML
        } else {
            R.layout.game_layout  // Default layout XML
        }


        val itemView =
            LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun getItemCount(): Int {
        return datalist.size
    }


    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {

        val item = datalist[position]

        Picasso.get().load(item.imageUrl).into(holder.image)


        // Set text
        holder.gameName.text = item.gameName
        holder.rating.text = item.rating

        // Handle click
        holder.itemView.setOnClickListener {
            itemClickListener(item)
        }
    }


    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image =itemView.findViewById<ImageView>(R.id.image)
        val gameName = itemView.findViewById<TextView>(R.id.game_name)
        val rating = itemView.findViewById<TextView>(R.id.rating)
    }

}