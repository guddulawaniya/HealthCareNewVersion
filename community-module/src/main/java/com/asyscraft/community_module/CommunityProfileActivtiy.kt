package com.asyscraft.community_module

import android.os.Bundle
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommunityProfileActivtiy : BaseActivity() {
    private lateinit var binding: ActivityCommunityProfileActivtiyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommunityProfileActivtiyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupHandleBtn()

    }
    private fun setupHandleBtn() {


    }
}