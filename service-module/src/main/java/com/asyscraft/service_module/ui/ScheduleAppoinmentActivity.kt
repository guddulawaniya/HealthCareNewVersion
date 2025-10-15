package com.asyscraft.service_module.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.asyscraft.service_module.databinding.ActivityScheduleAppoinmentBinding
import com.careavatar.core_network.base.BaseActivity
import com.careavatar.core_ui.R

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScheduleAppoinmentActivity : BaseActivity() {
    private lateinit var binding: ActivityScheduleAppoinmentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScheduleAppoinmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.totalcost.text = intent.getStringExtra("totalcost")
        binding.toolbar.btnBack.setOnClickListener { finish() }
        binding.toolbar.tvTitle.text = "Schedule"
        binding.confirmButton.setOnClickListener {
            startActivity(Intent(this, BookingConfirmationActivity::class.java))
        }



        // Use binding.*
        setupSingleSelection(binding.tvMorning, binding.tvAfternoon, binding.tvNight)
        setupSingleSelection(binding.tv1Hour, binding.tv2Hour, binding.tv3Hour)

    }

    private fun setupSingleSelection(vararg views: TextView) {
        views.forEach { tv ->
            tv.setOnClickListener {
                // reset all in this group
                views.forEach { it.apply {
                    setBackgroundResource(R.drawable.duration_inactive_item_bg)
                    setTextColor(ContextCompat.getColor(context, R.color.black))
                } }

                // highlight selected
                tv.setBackgroundResource(R.drawable.schedule_time_bg)
                tv.setTextColor(ContextCompat.getColor(this, R.color.white))
            }
        }
    }


}