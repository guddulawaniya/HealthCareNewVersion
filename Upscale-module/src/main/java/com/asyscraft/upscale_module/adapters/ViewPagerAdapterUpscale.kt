package com.asyscraft.upscale_module.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.asyscraft.upscale_module.BrowseItemsFragment
import com.asyscraft.upscale_module.UploadItemFragment

class ViewPagerAdapterUpscale(
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {

    private val fragments = listOf<Fragment>(
        UploadItemFragment(),
        BrowseItemsFragment()
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}