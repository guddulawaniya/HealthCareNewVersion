package com.asyscraft.alzimer_module

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.asyscraft.alzimer_module.databinding.ActivityBrainHealthCheckBinding
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BrainHealthCheck : BaseActivity() {
    private lateinit var binding: ActivityBrainHealthCheckBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBrainHealthCheckBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val type = intent.getStringExtra("type")


        binding.getstart.setOnClickListener{
            val intent = Intent(this, MatchingPairsGame::class.java)
//            val intent = Intent(this, HomePage::class.java)
            intent.putExtra("type",type)
            startActivity(intent)
            finish()
        }

        binding.skip.visibility = View.GONE



        if(type == "Caregiver"){
            binding.skip.visibility = View.VISIBLE

            binding.skip.setOnClickListener{

                val intent = Intent(this, HomePage::class.java)
                intent.putExtra("type",type)
                startActivity(intent)
            }
        }
    }
}