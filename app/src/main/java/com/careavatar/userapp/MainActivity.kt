package com.careavatar.userapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.asyscraft.login_module.ui.OnBoardingActivity
import com.careavatar.core_network.base.BaseActivity
import com.careavatar.dashboardmodule.DashboardActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            false // change to true if you want to wait for initialization
        }

        setTheme(R.style.Theme_HealthCareNewVersion)
        setContentView(R.layout.activity_main)


        lifecycleScope.launch {
            val loggedIn = userPref.isLoggedIn.first()

            if (loggedIn) {
                // Go directly to dashboard
                startActivity(Intent(this@MainActivity, DashboardActivity::class.java))
            } else {
                // First time → go to OnBoarding (Login module)
                delay(2000)
                startActivity(Intent(this@MainActivity, OnBoardingActivity::class.java))
            }

            finish()
        }

    }
}