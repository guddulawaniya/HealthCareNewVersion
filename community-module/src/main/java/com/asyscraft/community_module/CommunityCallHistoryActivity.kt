package com.asyscraft.community_module

import android.os.Bundle
import com.asyscraft.community_module.databinding.ActivityCommunityCallHistoryBinding
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommunityCallHistoryActivity : BaseActivity() {

    private lateinit var binding: ActivityCommunityCallHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommunityCallHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}