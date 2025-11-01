package com.asyscraft.alzimer_module

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.asyscraft.alzimer_module.databinding.ActivityMeditationBinding
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Meditation : BaseActivity() {

    private lateinit var binding: ActivityMeditationBinding

    private var lastSelected: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMeditationBinding.inflate(layoutInflater)
        setContentView(binding.root)


        var selectedTime = 15
        lastSelected = binding.fifmin
        binding.fifmin.setBackgroundResource(R.drawable.time_select_bg) // Set purple background
        binding.fifmin.setTextColor(ContextCompat.getColor(this, R.color.white))

        val timeOptions = mapOf(
            binding.fifmin to 15,
            binding.thimin to 30,
            binding.foumin to 45,
            binding.onehr to 60
        )

        // Set up click listeners
        timeOptions.forEach { (button, time) ->
            button.setOnClickListener {
                selectTimeOption(button)
                selectedTime = time // Update selected time
            }
        }

        binding.back.setOnClickListener{
            finish()
        }

        binding.playBtn.setOnClickListener{
            val intent = Intent(this, Timer_page::class.java)
            intent.putExtra("selected_time", selectedTime) // Pass the selected time
            startActivity(intent)
        }
    }



    private fun selectTimeOption(selected: TextView) {
        lastSelected?.apply {
            setBackgroundColor(Color.TRANSPARENT)
            setTextColor(ContextCompat.getColor(this@Meditation,R.color.C666363))
        }// Reset previous selection
        selected.setBackgroundResource(R.drawable.time_select_bg) // Set purple background
        selected.setTextColor(ContextCompat.getColor(this,R.color.white))
        lastSelected = selected
    }

}