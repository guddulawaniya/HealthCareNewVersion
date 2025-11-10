package com.asyscraft.dietition_module.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asyscraft.dietition_module.databinding.BookDieticianRowLayoutBinding
import com.asyscraft.dietition_module.databinding.RecommendedDieticianRowLayoutBinding
import com.bumptech.glide.Glide
import com.careavatar.core_model.dietition.GetExpertResponse2
import com.careavatar.core_utils.Constants

class DietitianAdapter(
    private var array: MutableList<GetExpertResponse2.Expert>,
    private val layoutType: Int,
    private val listener: (GetExpertResponse2.Expert) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val LAYOUT_NORMAL = 1
        const val LAYOUT_ALT = 2
    }

    private var selectedPos = -1 // ✅ for single selection

    override fun getItemViewType(position: Int): Int = layoutType

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == LAYOUT_ALT) {
            AltViewHolder(
                RecommendedDieticianRowLayoutBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        } else {
            NormalViewHolder(
                BookDieticianRowLayoutBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }
    }

    override fun getItemCount(): Int = array.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = array[position]

        if (holder is NormalViewHolder) {
            holder.bind(item, position == selectedPos)

            // ✅ button click = select expert
            holder.binding.selecteBtn.setOnClickListener {
                val previous = selectedPos
                selectedPos = position

                if (previous != -1) {
                    array[previous].isSelected = false
                    notifyItemChanged(previous)
                }

                item.isSelected = true
                notifyItemChanged(position)

                listener(item) // ✅ send selected item back
            }

            holder.binding.viewProfileBtn.setOnClickListener {
                listener(item)
            }

        } else if (holder is AltViewHolder) {
            holder.bind(item)

            // ✅ whole alt item click passes data
            holder.itemView.setOnClickListener {
                listener(item)
            }
        }
    }

    // ✅ Normal layout ViewHolder
    class NormalViewHolder(val binding: BookDieticianRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GetExpertResponse2.Expert, isSelected: Boolean) {
            binding.tvDoctorName.text = item.expert.name

            // selected UI
            binding.selecteBtn.setBackgroundResource(
                if (isSelected) com.careavatar.core_ui.R.drawable.button_circle_bg
                else com.careavatar.core_ui.R.drawable.expert_button_bg
            )

            Glide.with(binding.root.context)
                .load(Constants.IMAGE_BASEURL + item.expert.avatar)
                .placeholder(com.careavatar.core_ui.R.drawable.profile_1)
                .into(binding.doctorImage)
        }
    }

    // ✅ Alt layout ViewHolder
    class AltViewHolder(val binding: RecommendedDieticianRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GetExpertResponse2.Expert) {
            binding.tvDoctorNameAlt.text = item.expert.name

            Glide.with(binding.root.context)
                .load(Constants.IMAGE_BASEURL + item.expert.avatar)
                .placeholder(com.careavatar.core_ui.R.drawable.profile_1)
                .into(binding.doctorImage)
        }
    }

    fun updateList(newList: MutableList<GetExpertResponse2.Expert>) {
        array = newList
        notifyDataSetChanged()
    }
}

