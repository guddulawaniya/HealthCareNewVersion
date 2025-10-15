package com.asyscraft.service_module.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.asyscraft.service_module.databinding.ActivityOrderSummaryBinding
import com.careavatar.core_ui.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderSummaryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderSummaryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderSummaryBinding.inflate(layoutInflater)

        setContentView(binding.root)
        binding.toolbar.apply {
            btnBack.setOnClickListener {
                finish()
            }
            tvTitle.text = getString(R.string.order_summary)

        }

        binding.btnConfirmOrder.setOnClickListener {
            startActivity(Intent(this, YourOrderConfirmedActivity::class.java))
        }
    }
}