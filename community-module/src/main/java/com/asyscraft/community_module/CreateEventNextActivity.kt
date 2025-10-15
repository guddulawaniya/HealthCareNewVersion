package com.asyscraft.community_module

import android.os.Bundle
import com.asyscraft.community_module.databinding.ActivityCreateEventNextBinding
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateEventNextActivity : BaseActivity() {
    private lateinit var binding: ActivityCreateEventNextBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateEventNextBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}