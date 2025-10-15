package com.asyscraft.service_module.ui

import android.os.Bundle
import com.asyscraft.service_module.databinding.ActivityYourOrderConfirmedBinding
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class YourOrderConfirmedActivity : BaseActivity() {
    private lateinit var binding: ActivityYourOrderConfirmedBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityYourOrderConfirmedBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        binding.btnExploreMore.setOnClickListener {
//           startActivity(Intent(this,MainActivity::class.java).apply {
//               flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
//           })
//            finish()
//        }


    }
}