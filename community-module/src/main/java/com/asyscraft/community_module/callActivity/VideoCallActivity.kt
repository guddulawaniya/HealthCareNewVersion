package com.asyscraft.community_module.callActivity

import android.os.Bundle
import com.asyscraft.community_module.databinding.ActivityVideoCallBinding
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideoCallActivity : BaseActivity() {
    private lateinit var binding: ActivityVideoCallBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoCallBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}