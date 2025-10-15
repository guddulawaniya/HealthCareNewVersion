package com.careavatar.dashboardmodule.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.asyscraft.community_module.fragments.ExploreFragment
import com.asyscraft.community_module.fragments.JoinedFragment

class TabsPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ExploreFragment()
            else -> JoinedFragment()
        }
    }
}
