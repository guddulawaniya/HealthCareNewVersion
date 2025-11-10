package com.asyscraft.dietition_module.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.asyscraft.dietition_module.databinding.DashboardRecipeRowLayoutBinding
import com.asyscraft.dietition_module.databinding.RecipeRowLayoutBinding
import com.bumptech.glide.Glide
import com.careavatar.core_model.dietition.Recipedata
import com.careavatar.core_ui.R
import com.careavatar.core_utils.Constants


class FamousDietRecipeAdapter(
    private var items: MutableList<Recipedata>,
    private val onClickItem: (Int, Int) -> Unit
) : RecyclerView.Adapter<FamousDietRecipeAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: DashboardRecipeRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DashboardRecipeRowLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.itemView.setOnClickListener {
            onClickItem(position, 1)
        }

        val ctx = holder.itemView.context

        Glide.with(holder.binding.imageView.context)
            .load(Constants.IMAGE_BASEURL+item.image)
            .placeholder(R.drawable.diet_sample_image)
            .into(holder.binding.imageView)

        // Set UI based on cart state
        if (item.isInCart) {
            holder.binding.additem.setBackgroundResource(R.drawable.selected_recipe_btn_bg)
            holder.binding.additemText.setTextColor(ContextCompat.getColor(ctx, R.color.white))
            holder.binding.additemText.text = "Added"
            holder.binding.plusIcon.visibility = View.GONE
        } else {
            holder.binding.additem.setBackgroundResource(R.drawable.unselected_recipe_btn_bg)
            holder.binding.additemText.setTextColor(ContextCompat.getColor(ctx, R.color.textColor))
            holder.binding.additemText.text = "Add"
            holder.binding.plusIcon.visibility = View.VISIBLE
        }

        holder.binding.tvRecipetitle.text = item.recipeName
        holder.binding.tvDescriptionText.text =
            "${String.format("%.1f", item.nutrents[0].amountG)} Cal | ${
                String.format("%.1f", item.nutrents[1].amountG)
            }g Protein"

        holder.binding.additem.setOnClickListener {
            item.isInCart = !item.isInCart
            notifyItemChanged(position)
            onClickItem(position, 2)
        }
    }


    override fun getItemCount(): Int = items.size

    fun updateList(newList: MutableList<Recipedata>) {
        items = newList
        notifyDataSetChanged()
    }
}
