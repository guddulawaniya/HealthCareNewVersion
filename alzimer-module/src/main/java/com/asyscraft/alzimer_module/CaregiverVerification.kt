package com.asyscraft.alzimer_module

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.asyscraft.alzimer_module.viewModel.YourViewModel
import com.asyscraft.alzimer_module.databinding.ActivityCaregiverVerificationBinding
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CaregiverVerification : BaseActivity() {

    private lateinit var binding: ActivityCaregiverVerificationBinding

    private val viewModel: YourViewModel by viewModels()

    private lateinit var usertype:String

    var otp = ""
    var phoneno = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCaregiverVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        otp = intent.getStringExtra("otp").toString()
        phoneno = intent.getStringExtra("phoneno").toString()
        usertype = intent.getStringExtra("type").toString() //Default to Patient

        binding.phone.setText(otp)


        binding.save.setOnClickListener{

            otp = binding.phone.text.toString().trim()


        }

    }

}