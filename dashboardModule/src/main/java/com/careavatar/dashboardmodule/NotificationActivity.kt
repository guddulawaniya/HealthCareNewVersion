package com.careavatar.dashboardmodule

import android.os.Bundle
import com.careavatar.core_network.base.BaseActivity
import com.careavatar.dashboardmodule.adapters.ViewPagerAdapterNotification
import com.careavatar.dashboardmodule.databinding.ActivityNotificationBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationActivity : BaseActivity() {
    private lateinit var binding: ActivityNotificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backbtn.setOnClickListener { finish() }

        // Setup ViewPager with adapter
        val adapter = ViewPagerAdapterNotification(this)
        binding.viewPager.adapter = adapter

        // Attach TabLayout with ViewPager
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Reminders"
                1 -> tab.text = "Announcements"
            }
        }.attach()
    }
}
