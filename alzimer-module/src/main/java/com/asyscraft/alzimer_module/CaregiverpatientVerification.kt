package com.asyscraft.alzimer_module

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asyscraft.alzimer_module.databinding.ActivityCaregiverpatientVerificationBinding
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CaregiverpatientVerification : BaseActivity() {

    private lateinit var binding: ActivityCaregiverpatientVerificationBinding


    private lateinit var usertype:String

    var otp = ""
    var phoneno = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCaregiverpatientVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)


        otp = intent.getStringExtra("otp").toString()
        phoneno = intent.getStringExtra("phoneno").toString()
        usertype = intent.getStringExtra("type").toString() //Default to Patient


        binding.phone.setText(otp)


        binding.save.setOnClickListener{

            otp = binding.phone.text.toString().trim()

            Log.d("button clicl", "otp")
            if (phoneno.isNotEmpty() && otp.isNotEmpty()) {
            }else {
                Toast.makeText(this, "Please enter mobile number", Toast.LENGTH_SHORT).show()
            }
        }

    }

}