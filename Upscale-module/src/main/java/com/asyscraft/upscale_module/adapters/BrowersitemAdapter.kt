package com.asyscraft.upscale_module.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.asyscraft.upscale_module.databinding.BrowseItemRowLayoutBinding
import com.careavatar.core_model.BrowersitemModel
import com.careavatar.core_ui.R


class BrowersitemAdapter(
    val content: Context,
    var datalist: MutableList<BrowersitemModel>,
    val onClickListener: (BrowersitemModel) -> Unit
) : RecyclerView.Adapter<BrowersitemAdapter.ViewHolder>(), Filterable {

    private val originalList: List<BrowersitemModel> = datalist.toList() // keep full copy

    inner class ViewHolder(val binding: BrowseItemRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            BrowseItemRowLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = datalist[position]

        holder.binding.itemName.text = item.itemName
        holder.binding.featureText.text = item.featureText
        holder.binding.itemType.text = item.itemType
        holder.binding.itemImage.setImageResource(item.image)

        if (item.itemType == "For Donation") {
            holder.binding.itemTypeCard.setCardBackgroundColor("#CEFBD1".toColorInt())
            holder.binding.itemType.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.buttonColor)
            )
        } else {
            holder.binding.itemTypeCard.setCardBackgroundColor("#D6D6FF".toColorInt())
            holder.binding.itemType.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.expertBookingTextColor)
            )
        }

        holder.binding.viewDetailsBtn.setOnClickListener {
            onClickListener(item)
        }
    }

    override fun getItemCount(): Int = datalist.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(query: CharSequence?): FilterResults {
                val filtered = if (query.isNullOrBlank()) {
                    originalList // use original full list
                } else {
                    originalList.filter {
                        it.itemName.contains(query, ignoreCase = true) ||
                                it.featureText.contains(query, ignoreCase = true) ||
                                it.itemType.contains(query, ignoreCase = true)
                    }
                }
                return FilterResults().apply { values = filtered }
            }

            override fun publishResults(query: CharSequence?, results: FilterResults?) {
                val filteredList = results?.values
                if (filteredList is List<*>) {
                    @Suppress("UNCHECKED_CAST")
                    datalist = filteredList.filterIsInstance<BrowersitemModel>().toMutableList()
                } else {
                    datalist = mutableListOf()
                }
                notifyDataSetChanged()
            }

        }
    }
}
