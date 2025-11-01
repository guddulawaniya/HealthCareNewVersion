package com.asyscraft.alzimer_module

import android.content.Intent
import android.os.Bundle
import com.asyscraft.alzimer_module.databinding.ActivityAlzimerMainBinding
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlzimerMainActivity : BaseActivity() {

    private lateinit var binding: ActivityAlzimerMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlzimerMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.caregiver.setOnClickListener {
            binding.caregiver.setBackgroundResource(R.drawable.box_presss)
            val intent = Intent(this, PatientList::class.java)
            intent.putExtra("type","Caregiver")
            startActivity(intent)
        }

        binding.patient.setOnClickListener {
            binding.patient.setBackgroundResource(R.drawable.box_presss)
            val intent = Intent(this, PatientParticipantform::class.java)
            intent.putExtra("type","Patient")
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        binding.caregiver.setBackgroundResource(R.drawable.selector_box)
        binding.patient.setBackgroundResource(R.drawable.selector_box)
    }
}