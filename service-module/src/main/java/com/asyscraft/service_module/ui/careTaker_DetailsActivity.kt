package com.asyscraft.service_module.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.asyscraft.service_module.databinding.ActivityCareTakerDetailsBinding
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class careTaker_DetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityCareTakerDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCareTakerDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.apply {
            btnBack.setOnClickListener {
                finish()
            }
            tvTitle.visibility = View.GONE
        }

        binding.btnBookNow.setOnClickListener {
            startActivity(Intent(this, ScheduleAppoinmentActivity::class.java).apply {
                putExtra("totalcost",binding.totalcost.text.toString())
            })
        }
    }
}