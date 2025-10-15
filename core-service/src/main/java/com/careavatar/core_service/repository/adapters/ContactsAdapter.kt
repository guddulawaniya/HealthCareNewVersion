package com.careavatar.core_service.repository.adapters

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.careavatar.core_ui.R

import de.hdodenhof.circleimageview.CircleImageView
import kotlin.random.Random


class ContactsAdapter : PagingDataAdapter<Contact, ContactsAdapter.ViewHolder>(ContactDiffCallback) {

    private val maxSelection = 3
    private val drawableCache = mutableMapOf<String, GradientDrawable>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageView: CircleImageView = itemView.findViewById(R.id.ivProfile)
        val tvInitials: TextView = itemView.findViewById(R.id.tvInitials)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvNumber: TextView = itemView.findViewById(R.id.tvNumber)
        val checkBoxImage: TextView = itemView.findViewById(R.id.plusbutton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.emergency_row_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = getItem(position) ?: return

        holder.tvName.text = contact.name
        holder.tvNumber.text = contact.number

        // Update the selection icon
        holder.checkBoxImage.text = if (contact.isSelected)  "Remove"  else "Add Contact"
        holder.checkBoxImage.setBackgroundResource(
            if (contact.isSelected) 0 else R.drawable.add_emergency_btn_bg
        )
        holder.checkBoxImage.setTextColor(
            ContextCompat.getColor(
                holder.itemView.context,
                if (contact.isSelected) R.color.red else R.color.black
            )
        )

        if (contact.imageUri != null) {
            holder.imageView.visibility = View.VISIBLE
            holder.tvInitials.visibility = View.GONE
            Glide.with(holder.itemView.context)
                .load(contact.imageUri)
                .placeholder(R.drawable.sample_image)
                .into(holder.imageView)
        } else {
            holder.imageView.visibility = View.GONE
            holder.tvInitials.visibility = View.VISIBLE

            val initials = getInitials(contact.name)
            holder.tvInitials.text = initials

            val drawable = drawableCache.getOrPut(initials) {
                GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(getRandomColor())
                }
            }
            holder.tvInitials.background = drawable
        }

        holder.checkBoxImage.setOnClickListener {
            val selectedCount = snapshot().items.count { it.isSelected }

            if (!contact.isSelected && selectedCount >= maxSelection) {
                Toast.makeText(holder.itemView.context, "Maximum 3 members allowed", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            contact.isSelected = !contact.isSelected
            notifyItemChanged(position)
        }
    }

    object ContactDiffCallback : DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem.number == newItem.number
        }

        override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem == newItem
        }
    }

    private fun getInitials(name: String): String {
        val words = name.split(" ")
        return when (words.size) {
            0 -> ""
            1 -> words[0].take(2).uppercase()
            else -> "${words[0].take(1)}${words[1].take(1)}".uppercase()
        }
    }

    private fun getRandomColor(): Int {
        val random = Random
        return Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256))
    }
}



data class Contact(
    val name: String,
    val number: String,
    val imageUri: Uri?,
    var isSelected: Boolean = false
)
