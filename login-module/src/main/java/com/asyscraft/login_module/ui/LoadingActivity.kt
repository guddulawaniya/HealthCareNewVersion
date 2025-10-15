package com.asyscraft.login_module.ui

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.asyscraft.login_module.databinding.ActivityLoadingBinding
import com.careavatar.core_network.base.BaseActivity
import com.asyscraft.login_module.ui.healthonboardingactivity.InformationOnboardingActivity
import com.careavatar.core_service.repository.viewModels.OnboardingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class LoadingActivity : BaseActivity() {
    private lateinit var binding: ActivityLoadingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ObjectAnimator.ofFloat(binding.customLoader, "rotation", 0f, 360f).apply {
            duration = 700
            repeatCount = ObjectAnimator.INFINITE
            start()
        }


        lifecycleScope.launch {
            delay(2000) // 2 seconds
            startActivity(Intent(this@LoadingActivity, InformationOnboardingActivity::class.java))
            finish()
        }

    }
}