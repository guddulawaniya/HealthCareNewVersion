package com.careavatar.dashboardmodule.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.careavatar.core_model.BannerResponse
import com.careavatar.core_ui.R
import com.careavatar.core_utils.Constants
import com.careavatar.dashboardmodule.databinding.ViewPagerListBinding
import kotlin.apply

class ViewPagerAdapter(
    val context: Context,
    val list: ArrayList<BannerResponse.BannerResponseItem>
) : RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder>() {

    class ViewPagerViewHolder(val binding: ViewPagerListBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {

        var binding =
            ViewPagerListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewPagerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size

    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {

        holder.binding.apply {
            Glide.with(context).load(Constants.IMAGE_BASEURL + list[position].dashBoardBanner)
                .placeholder(
                    R.drawable.logo
                ).into(image)
        }
    }
}