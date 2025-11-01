package com.asyscraft.alzimer_module

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.asyscraft.alzimer_module.databinding.ActivitySosCaregiverBinding
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SosCaregiver : BaseActivity() {

    private lateinit var binding: ActivitySosCaregiverBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySosCaregiverBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener{
            finish()
        }


        val type = intent.getStringExtra("type").toString()

        binding.backhome.setOnClickListener{
            val intent = Intent(this, HomePage::class.java)
            intent.putExtra("type", type)
            startActivity(intent)
            finish()
        }
    }
}