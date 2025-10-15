package com.asyscraft.login_module.ui

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.asyscraft.login_module.R
import com.careavatar.core_network.base.BaseActivity
import com.careavatar.dashboardmodule.DashboardActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashScreenActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        lifecycleScope.launch {
            val loggedIn = userPref.isLoggedIn.first()  // get current login state

            if (loggedIn) {
                // User already logged in → go directly to MainActivity
                startActivity(Intent(this@SplashScreenActivity, DashboardActivity::class.java))
                finish()
            } else {
                // Not logged in → show splash for 2 seconds, then go to onboarding
                delay(2000)
                startActivity(Intent(this@SplashScreenActivity, OnBoardingActivity::class.java))
                finish()
            }
        }
    }
}
