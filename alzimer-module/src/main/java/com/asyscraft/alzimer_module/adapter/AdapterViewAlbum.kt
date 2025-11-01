package com.asyscraft.alzimer_module.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.asyscraft.alzimer_module.R
import com.squareup.picasso.Picasso
import java.io.File

class AdapterViewAlbum(
    private val context: Context,
    private val datalist: MutableList<String>,
    var isEditMode: Boolean = false,
    private val onAddImageClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_NORMAL = 0
        private const val VIEW_TYPE_EDIT = 1
        private const val VIEW_TYPE_ADD_BUTTON = 2
    }



    override fun getItemViewType(position: Int): Int {
        return when {
            isEditMode && position < datalist.size -> VIEW_TYPE_EDIT
            isEditMode -> VIEW_TYPE_ADD_BUTTON
            else -> VIEW_TYPE_NORMAL
        }
    }

    override fun getItemCount(): Int {
        return if (isEditMode) 9 else datalist.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)

        return when (viewType) {
            VIEW_TYPE_ADD_BUTTON -> {
                val view = inflater.inflate(R.layout.item_add_button, parent, false)
                AddButtonViewHolder(view)
            }
            VIEW_TYPE_EDIT -> {
                val view = inflater.inflate(R.layout.albumview_recyclerview_layout_edit, parent, false)
                EditViewHolder(view)
            }
            else -> {
                val view = inflater.inflate(R.layout.albumview_recyclerview_layout, parent, false)
                NormalViewHolder(view)
            }
        }
    }

//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        val baseUrl = "http://172.104.206.4:5000/uploads/"
//
//        when (holder) {
//            is AddButtonViewHolder -> {
//                holder.addButton.setOnClickListener {
//                    onAddImageClick()
//                }
//            }
//
//
//
//            is EditViewHolder -> {
//                if (position < datalist.size) {
//                    val item = datalist[position]
//
//                    if (item.startsWith("/") || item.startsWith("content://")) {
//                        // Local file
//                        Picasso.get().load(File(item)).into(holder.image)
//                    } else {
//                        // Server image
//                        Picasso.get().load(baseUrl + item).into(holder.image)
//                    }
//
//                    holder.cross.setOnClickListener {
//                        datalist.removeAt(holder.adapterPosition)
//                        notifyItemRemoved(holder.adapterPosition)
//                        notifyItemRangeChanged(holder.adapterPosition, datalist.size)
//                    }
//                }
//            }
//
//
//
//            is NormalViewHolder -> {
//                val item = datalist[position]
//                Picasso.get().load(baseUrl + item).into(holder.image)
//            }
//        }
//
//
//
//
////        when (holder) {
////
////            is AddButtonViewHolder -> {
////                holder.addButton.setOnClickListener {
////                    onAddImageClick()
////                }
////            }
////
////            is EditViewHolder -> {
////                if (position < datalist.size) {
////                    val item = datalist[position]
////
////                    when {
////                        item.startsWith("server::") -> {
////                            val fileName = item.removePrefix("server::")
////                            Picasso.get().load(baseUrl + fileName).into(holder.image)
////                        }
////                        item.startsWith("/") || item.startsWith("content://") -> {
////                            Picasso.get().load(File(item)).into(holder.image)
////                        }
////                    }
////
////                    holder.cross.setOnClickListener {
////                        datalist.removeAt(holder.adapterPosition)
////                        notifyItemRemoved(holder.adapterPosition)
////                        notifyItemRangeChanged(holder.adapterPosition, datalist.size)
////                    }
////                }
////            }
////
////            is NormalViewHolder -> {
////                val item = datalist[position]
////                if (item.startsWith("server::")) {
////                    val fileName = item.removePrefix("server::")
////                    Picasso.get().load(baseUrl + fileName).into(holder.image)
////                }
////            }
////        }
//
//    }










    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val baseUrl = "http://172.104.206.4:5000/uploads/"

        when (holder) {
            is AddButtonViewHolder -> {
                holder.addButton.setOnClickListener {
                    onAddImageClick()
                }
                // Optionally clear image in add slots if reusing views
//                holder.addButton.setImageResource(R.drawable.ic_add_image_placeholder)
            }

            is EditViewHolder -> {
                if (position < datalist.size) {
                    val item = datalist[position]
                    if (item.startsWith("/") || item.startsWith("content://")) {
                        Picasso.get().load(File(item)).into(holder.image)
                    } else {
                        Picasso.get().load(baseUrl + item).into(holder.image)
                    }

                    holder.cross.setOnClickListener {
                        datalist.removeAt(holder.adapterPosition)
                        notifyItemRemoved(holder.adapterPosition)
                        notifyItemRangeChanged(holder.adapterPosition, 9) // always 9 items
                    }
                }
            }

            is NormalViewHolder -> {
                val item = datalist[position]
                Picasso.get().load(baseUrl + item).into(holder.image)
            }
        }
    }


    class NormalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.image)
    }

    class EditViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.image)
        val cross: ImageButton = itemView.findViewById(R.id.cross)
    }

    class AddButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val addButton: ImageView = itemView.findViewById(R.id.addButton)
    }
}
