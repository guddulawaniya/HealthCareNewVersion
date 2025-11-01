package com.asyscraft.community_module

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.asyscraft.community_module.adpaters.ViewPagerAdapterChatActivity
import com.asyscraft.community_module.databinding.ActivityChatBinding
import com.careavatar.core_network.base.BaseActivity
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatActivity : BaseActivity() {
    private lateinit var binding: ActivityChatBinding

    private var isSearchVisible : Boolean = false


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


        // Handle toolbar menu clicks (if you have menu/chat_menu)
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_search -> {
                    if (isSearchVisible){
                        binding.searchBar.etSearch.visibility = View.GONE
                        isSearchVisible = false
                    }else{
                        binding.searchBar.etSearch.visibility = View.VISIBLE
                        isSearchVisible = true
                    }
                    true
                }
                R.id.action_history -> {
                    startActivity(Intent(this, CommunityCallHistoryActivity::class.java))
                    true
                }
                else -> false
            }
        }

    }


}