package com.asyscraft.community_module.callActivity

import android.os.Bundle
import com.asyscraft.community_module.databinding.ActivityGroupVideoBinding
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GroupVideoActivity : BaseActivity() {

    private lateinit var binding: ActivityGroupVideoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}