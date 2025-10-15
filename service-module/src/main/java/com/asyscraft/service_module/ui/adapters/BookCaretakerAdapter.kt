package com.asyscraft.service_module.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asyscraft.service_module.databinding.BookCareRowLayoutBinding
import com.careavatar.core_model.BookCaretakerModel

class BookCaretakerAdapter(
    val content : Context,
    val datalist : MutableList<BookCaretakerModel>,
    val onClickListener: (BookCaretakerModel) -> Unit
): RecyclerView.Adapter<BookCaretakerAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: BookCareRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = BookCareRowLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = datalist[position]
        holder.binding.title.text = item.title
        holder.binding.description.text = item.description
        holder.binding.imageView.setImageResource(item.image)

        holder.binding.root.setOnClickListener {
            onClickListener(item)
        }

    }
    override fun getItemCount(): Int {
        return datalist.size
    }
}