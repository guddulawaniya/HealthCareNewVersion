package com.asyscraft.community_module.callActivity

import android.os.Bundle
import com.asyscraft.community_module.databinding.ActivityGroupIncomingBinding
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GroupIncomingActivity : BaseActivity() {
    private lateinit var binding: ActivityGroupIncomingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupIncomingBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}