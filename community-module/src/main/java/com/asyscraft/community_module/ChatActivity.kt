package com.asyscraft.community_module

import android.os.Bundle
import com.asyscraft.community_module.adpaters.ViewPagerAdapterChatActivity
import com.asyscraft.community_module.databinding.ActivityChatBinding
import com.careavatar.core_network.base.BaseActivity
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatActivity : BaseActivity() {
    private lateinit var binding: ActivityChatBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }


        // Setup ViewPager with adapter
        val adapter = ViewPagerAdapterChatActivity(this)
        binding.viewPager.adapter = adapter

        // Attach TabLayout with ViewPager
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Chats"
                1 -> tab.text = "Requests"
            }
        }.attach()

    }
}