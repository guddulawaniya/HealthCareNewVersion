package com.asyscraft.upscale_module

import android.content.Intent
import android.os.Bundle
import com.asyscraft.upscale_module.databinding.ActivityUpscaledetailBinding
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpscaledetailActivity : BaseActivity() {
    private lateinit var binding : ActivityUpscaledetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpscaledetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.apply {
            btnBack.setOnClickListener { finish() }
            tvTitle.text = getString(com.careavatar.core_ui.R.string.item_detail)
        }

        binding.btninclude.buttonNext.text = "Chat"

        binding.btninclude.buttonNext.setOnClickListener {
            startActivity(Intent(this, Upscale_Meesage_Activity::class.java))

        }
    }
}