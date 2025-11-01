package com.asyscraft.alzimer_module

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.asyscraft.alzimer_module.viewModel.YourViewModel
import com.asyscraft.alzimer_module.databinding.ActivitySosStartBinding
import com.asyscraft.alzimer_module.utils.Progresss
import com.asyscraft.alzimer_module.utils.Resource
import com.asyscraft.alzimer_module.utils.androidExtension
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SosStart : BaseActivity() {

    private lateinit var binding: ActivitySosStartBinding
    private val viewModel: YourViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySosStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener {
            finish()
        }

        lifecycleScope.launch {
            delay(5_000) // 10,000 milliseconds = 10 seconds
            viewModel.SoSNotification()
        }

        observeResponseSosnotification()

    }


    private fun observeResponseSosnotification() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sosnotification.observe(this@SosStart) { response ->

                    when (response) {
                        is Resource.Loading -> {
                            Progresss.start(this@SosStart)
                            Log.d("RadiusUpdate", "🔄 Updating radius...")
                        }

                        is Resource.Success -> {
                            Progresss.stop()

                            val type = intent.getStringExtra("type").toString()

                            val intent = Intent(this@SosStart, SosCaregiver::class.java)
                            intent.putExtra("type", type)
                            startActivity(intent)
                            finish()
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("RadiusUpdate", "❌ Error: ${response.message}")
                            androidExtension.alertBox(response.message ?: "Something went wrong", this@SosStart)
                        }

                        else -> {
                            Progresss.stop()
                            androidExtension.alertBox("Unexpected error occurred", this@SosStart)
                        }
                    }
                }
            }
        }
    }


}
