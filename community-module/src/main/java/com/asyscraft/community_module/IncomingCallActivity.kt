package com.asyscraft.community_module

import android.os.Bundle
import com.asyscraft.community_module.databinding.ActivityIncommingCallBinding
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IncomingCallActivity : BaseActivity() {
    private lateinit var binding: ActivityIncommingCallBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIncommingCallBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}