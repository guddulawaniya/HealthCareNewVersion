package com.asyscraft.upscale_module

import android.os.Bundle
import com.asyscraft.upscale_module.databinding.ActivityUpscaleMeesageBinding
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Upscale_Meesage_Activity : BaseActivity() {
    private lateinit var binding : ActivityUpscaleMeesageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpscaleMeesageBinding.inflate(layoutInflater)

        setContentView(binding.root)
        binding.backbtn.setOnClickListener {
            finish()
        }

    }
}