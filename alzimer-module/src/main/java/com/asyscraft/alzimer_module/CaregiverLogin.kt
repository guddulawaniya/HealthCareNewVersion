package com.asyscraft.alzimer_module

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.asyscraft.alzimer_module.viewModel.YourViewModel
import com.asyscraft.alzimer_module.databinding.ActivityCaregiverLoginBinding
import com.asyscraft.alzimer_module.utils.Progresss
import com.asyscraft.alzimer_module.utils.Resource
import com.asyscraft.alzimer_module.utils.androidExtension
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CaregiverLogin : BaseActivity() {

    private lateinit var binding: ActivityCaregiverLoginBinding
    private val viewModel: YourViewModel by viewModels()

    private lateinit var usertype:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCaregiverLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        var mobile = ""

        usertype = intent.getStringExtra("type").toString() //Default to Patient

        binding.save.setOnClickListener{

            mobile = binding.phone.text.toString().trim()
            Log.d("button clicl", "buuton")
            if (mobile.isNotEmpty()) {
                viewModel.Login(
                    phonenumber = mobile

                )
            }else {
                Toast.makeText(this, "Please enter mobile number", Toast.LENGTH_SHORT).show()
            }
        }

        binding.register.setOnClickListener{
            val intent = Intent(this@CaregiverLogin, CaregiverForm::class.java)
            intent.putExtra("type",usertype) //Pass Patient
            startActivity(intent)
        }

        observeResponselogin()
    }



    private fun observeResponselogin() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.login.observe(this@CaregiverLogin) { response ->
                    when (response) {
                        is Resource.Success -> {
                            Progresss.stop()
                            response.data?.let { loginResponse ->
                                if (loginResponse.success) {
                                    val intent = Intent(this@CaregiverLogin, CaregiverVerification::class.java).apply {
                                        putExtra("otp", loginResponse.otp)
                                        putExtra("phoneno", loginResponse.phoneNumber)
                                        putExtra("type", usertype) // Pass Patient
                                    }
                                    startActivity(intent)
                                } else {
                                    androidExtension.alertBox("Login failed", this@CaregiverLogin)
                                }
                            } ?: androidExtension.alertBox("Unexpected response", this@CaregiverLogin)
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            response.message?.let { message ->
                                androidExtension.alertBox(message, this@CaregiverLogin)
                            }
                        }

                        is Resource.Loading -> {
                            Progresss.start(this@CaregiverLogin)
                        }

                        else -> {
                            Log.w("CaregiverLogin", "Unhandled login state: $response")
                            Progresss.stop()
                            androidExtension.alertBox(response.toString(), this@CaregiverLogin)
                        }
                    }
                }
            }
        }
    }

}