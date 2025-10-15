package com.careavatar.dashboardmodule

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.careavatar.core_network.base.BaseActivity
import com.careavatar.dashboardmodule.databinding.ActivityDashboardBinding
import com.careavatar.dashboardmodule.dashboardfragments.CommunityFragment
import com.careavatar.dashboardmodule.dashboardfragments.HomeFragment
import com.careavatar.dashboardmodule.dashboardfragments.ProfileFragment
import com.careavatar.dashboardmodule.dashboardfragments.ServicesFragment
import com.careavatar.dashboardmodule.dashboardfragments.UpscaleFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardActivity : BaseActivity()  {
    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        openFragment(HomeFragment())
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    openFragment(HomeFragment())
                    true
                }
                R.id.nav_search -> {
                    openFragment(ServicesFragment())
                    true
                }
                R.id.nav_add -> {
                    openFragment(CommunityFragment())
                    true
                }
                R.id.nav_notifications -> {
                    openFragment(UpscaleFragment())
                    true
                }
                R.id.nav_profile -> {
                    openFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}