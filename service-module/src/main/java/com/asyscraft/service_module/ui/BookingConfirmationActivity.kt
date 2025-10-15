package com.asyscraft.service_module.ui

import android.content.Intent
import android.os.Bundle
import com.asyscraft.service_module.databinding.ActivityBookingConfirmationBinding
import com.careavatar.core_network.base.BaseActivity
import com.careavatar.core_ui.R

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookingConfirmationActivity : BaseActivity() {
    private lateinit var binding: ActivityBookingConfirmationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        binding.explorebtn.setOnClickListener {
//            startActivity(Intent(this, DashboardActivity::class.java).apply {
//                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
//            })
//            finish()
//        }

        binding.toolbar.btnBack.setOnClickListener {
            finish()
        }
        binding.toolbar.tvTitle.text = getString(R.string.booking_confirmation)
    }
}