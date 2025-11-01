package com.asyscraft.medical_reminder.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.careavatar.core_model.medicalReminder.Disease
import com.careavatar.core_ui.R
import com.careavatar.core_ui.databinding.DropdownItemBinding

class DiseaseAdapter(
    private var dataList: MutableList<Disease>,
    private val onItemClicked: (Disease) -> Unit
) : RecyclerView.Adapter<DiseaseAdapter.ViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION
    private var searchQuery: String = ""

    fun updateSearchQuery(query: String) {
        searchQuery = query.lowercase()
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: DropdownItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DropdownItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        val name = data.diseaseName

        holder.binding.tvLevel.text = getHighlightedText(name, searchQuery)

        if (selectedPosition == position) {
            holder.binding.root.setBackgroundResource(R.drawable.selected_gender_bg)
        } else {
            holder.binding.root.setBackgroundResource(0)
        }

        holder.itemView.setOnClickListener {
            val currentPos = holder.bindingAdapterPosition
            if (currentPos != RecyclerView.NO_POSITION) {
                selectItem(currentPos)
                onItemClicked(data)
            }
        }
    }

    fun updateList(newList: List<Disease>) {
        dataList.clear()
        dataList.addAll(newList)
        notifyDataSetChanged()
    }

    override fun getItemCount() = dataList.size

    fun selectItem(position: Int) {
        val prev = selectedPosition
        selectedPosition = position
        if (prev != RecyclerView.NO_POSITION) notifyItemChanged(prev)
        notifyItemChanged(selectedPosition)
    }

    /** ✅ Highlight query letters */
    private fun getHighlightedText(fullText: String, query: String): CharSequence {
        if (query.isEmpty()) return fullText

        val lowerText = fullText.lowercase()
        val start = lowerText.indexOf(query)
        if (start == -1) return fullText

        val end = start + query.length
        val spannable = android.text.SpannableString(fullText)
        spannable.setSpan(
            android.text.style.ForegroundColorSpan(android.graphics.Color.parseColor("#388E3C")), // green color
            start, end,
            android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
            start, end,
            android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannable
    }
}

