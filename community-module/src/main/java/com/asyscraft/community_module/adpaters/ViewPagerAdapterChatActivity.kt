package com.asyscraft.community_module.adpaters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.asyscraft.community_module.fragments.ChatFragment
import com.asyscraft.community_module.fragments.RequestFragment

class ViewPagerAdapterChatActivity(
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {

    private val fragments = listOf<Fragment>(
        ChatFragment(),
        RequestFragment()
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}
