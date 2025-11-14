package com.careavatar.dashboardmodule

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.careavatar.core_network.base.BaseActivity
import com.careavatar.dashboardmodule.dashboardfragments.CommunityFragment
import com.careavatar.dashboardmodule.dashboardfragments.HomeFragment
import com.careavatar.dashboardmodule.dashboardfragments.ProfileFragment
import com.careavatar.dashboardmodule.dashboardfragments.ServicesFragment
import com.careavatar.dashboardmodule.dashboardfragments.UpscaleFragment
import com.careavatar.dashboardmodule.databinding.ActivityDashboardBinding
import com.careavatar.dashboardmodule.stepCount.FitPermissionManager
import com.careavatar.dashboardmodule.stepCount.StepViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardActivity : BaseActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var viewModel: StepViewModel

    private val TAG = "StepCounts"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        selectTab()


        setupClicks()
        viewModel = ViewModelProvider(this)[StepViewModel::class.java]
        setupObservers()

        checkAllPermissionsAndLoadSteps()

    }
    private fun selectTab(){
        val selectIndex = intent.getIntExtra("selectIndex", 0)
        selectTab(selectIndex)
        when(selectIndex){
            0 -> openFragment(HomeFragment())
            1 -> openFragment(ServicesFragment())
            2 -> openFragment(CommunityFragment())
            3 -> openFragment(UpscaleFragment())
            4 -> openFragment(ProfileFragment())
        }
    }

    // ---------------------------------------------------------
    // STEP OBSERVERS
    // ---------------------------------------------------------
    private fun setupObservers() {
        viewModel.todaySteps.observe(this) { result ->
            result.onSuccess { steps ->
                showToast("Today Steps: $steps")
                Log.d(TAG, "Today Step: ${steps}")
            }.onFailure { e ->
                Log.e(TAG, "Step error: ${e.message}")
                handleStepError(e)
            }
        }
        viewModel.last7Days.observe(this) { result ->
            result.onSuccess { steps ->
                showToast("last7days Steps: $steps")
                Log.d(TAG, "last7Days Step: ${steps}")
            }.onFailure { e ->
                Log.e(TAG, "Step error: ${e.message}")
                handleStepError(e)
            }
        }


        viewModel.liveSteps.observe(this) { steps ->
            Log.d(TAG, "Live Step: ${steps}")
            showToast("Live Steps: $steps")
        }

    }

    // ---------------------------------------------------------
    // PERMISSION FLOW ENTRY POINT
    // ---------------------------------------------------------
    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkAllPermissionsAndLoadSteps() {
        // 1. activity recognition
        if (!hasActivityPermission()) {
            requestActivityPermission()
            return
        }


        // 2. google account login (silent)
        FitPermissionManager.silentSignIn(this) {
            // 3. google fit permission
            if (!FitPermissionManager.hasGoogleFitPermission(this)) {
                requestGoogleFitPermissionDialog()
                return@silentSignIn
            }

            // 4. load steps
            loadStepsNow()
        }
    }

    // ---------------------------------------------------------
    // ACTIVITY PERMISSION
    // ---------------------------------------------------------
    private fun hasActivityPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        } else true
    }

    private fun requestActivityPermission() {
        FitPermissionManager.requestActivityRecognitionPermission(this)
    }

    // ---------------------------------------------------------
    // GOOGLE FIT PERMISSION DIALOG
    // ---------------------------------------------------------
    private fun requestGoogleFitPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle("Google Fit Access")
            .setMessage("We need Google Fit permission to read your step count.")
            .setPositiveButton("Allow") { _, _ ->
                FitPermissionManager.requestGoogleFitPermission(this)
            }
            .setNegativeButton("Cancel") { _, _ ->
                showToast("Step tracking disabled")
            }
            .setCancelable(false)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopLiveSteps()
    }


    // ---------------------------------------------------------
    // LOAD STEPS
    // ---------------------------------------------------------
    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadStepsNow() {
        viewModel.loadHistoricalSteps(FitPermissionManager.fitnessOptions)
        viewModel.startLiveSteps(FitPermissionManager.fitnessOptions)
    }

    // ---------------------------------------------------------
    // ON ACTIVITY RESULT
    // ---------------------------------------------------------
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FitPermissionManager.GOOGLE_FIT_PERMISSION) {
            if (resultCode == Activity.RESULT_OK) {
                showToast("Google Fit Permission Granted")
                loadStepsNow()
            } else {
                showToast("Google Fit Permission Denied")
            }
        }
    }

    // ---------------------------------------------------------
    // ACTIVITY PERMISSION RESULT
    // ---------------------------------------------------------
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == FitPermissionManager.ACTIVITY_PERMISSION) {
            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                checkAllPermissionsAndLoadSteps()
            } else {
                showActivityPermissionGuide()
            }
        }
    }

    private fun showActivityPermissionGuide() {
        AlertDialog.Builder(this)
            .setTitle("Permission Needed")
            .setMessage("Please enable Physical Activity permission in settings.")
            .setPositiveButton("Open Settings") { _, _ ->
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", packageName, null)
                )
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun handleStepError(e: Throwable) {
        showToast("Failed to load steps: ${e.message}")
    }


    // ---------------------------------------------------------
    // UI NAVIGATION
    // ---------------------------------------------------------
    private fun setupClicks() = with(binding.includeBottom) {

        tabHome.setOnClickListener {
            selectTab(0)
            openFragment(HomeFragment())
        }

        tabServices.setOnClickListener {
            selectTab(1)
            openFragment(ServicesFragment())
        }

        tabCommunity.setOnClickListener {
            selectTab(2)
            openFragment(CommunityFragment())
        }

        tabUpscale.setOnClickListener {
            selectTab(3)
            openFragment(UpscaleFragment())
        }

        tabProfile.setOnClickListener {
            selectTab(4)
            openFragment(ProfileFragment())
        }
    }

    private fun selectTab(tab: Int) {
        val containers = listOf(
            binding.includeBottom.homeContainer,
            binding.includeBottom.servicesContainer,
            binding.includeBottom.communityContainer,
            binding.includeBottom.upscaleContainer,
            binding.includeBottom.profileContainer
        )

        val icons = listOf(
            binding.includeBottom.iconHome,
            binding.includeBottom.iconServices,
            binding.includeBottom.iconCommunity,
            binding.includeBottom.iconUpscale,
            binding.includeBottom.iconProfile
        )

        val texts = listOf(
            binding.includeBottom.textHome,
            binding.includeBottom.tabServicesText,
            binding.includeBottom.tabCommunityText,
            binding.includeBottom.tabUpscaleText,
            binding.includeBottom.tabProfileText
        )

        containers.forEachIndexed { index, container ->
            if (index == tab) {
                container.setBackgroundResource(com.careavatar.core_ui.R.drawable.bottom_item_bg)
                texts[index].visibility = View.VISIBLE
                icons[index].setColorFilter(Color.WHITE)
            } else {
                container.setBackgroundResource(0)
                texts[index].visibility = View.GONE
                icons[index].setColorFilter(
                    resources.getColor(com.careavatar.core_ui.R.color.grayTextColor)
                )
            }
        }
    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
