package com.asyscraft.community_module.callActivity

import android.os.Bundle
import com.asyscraft.community_module.databinding.ActivityAudioCallBinding
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AudioCallActivity : BaseActivity() {
    private lateinit var binding: ActivityAudioCallBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioCallBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}