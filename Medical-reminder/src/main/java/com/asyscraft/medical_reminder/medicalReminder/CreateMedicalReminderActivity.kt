package com.asyscraft.medical_reminder.medicalReminder

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.asyscraft.medical_reminder.R
import com.asyscraft.medical_reminder.databinding.ActivityCreateMedicalReminderBinding
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateMedicalReminderActivity : BaseActivity() {

    private lateinit var binding: ActivityCreateMedicalReminderBinding
    private lateinit var navController: NavController
    private lateinit var progressBars: List<android.widget.ProgressBar>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateMedicalReminderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ✅ collect all progress bars (order matters!)
        progressBars = listOf(
            binding.toplinearlayout.findViewById(R.id.progressBar1),
            binding.toplinearlayout.findViewById(R.id.progressBar2),
            binding.toplinearlayout.findViewById(R.id.progressBar3),
            binding.toplinearlayout.findViewById(R.id.progressBar4),
            binding.toplinearlayout.findViewById(R.id.progressBar5)
        )

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // ✅ Update step initially when entering
        updateStep(1)

        // ✅ Listen to destination changes for forward navigation
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val step = getStepForDestination(destination.id)
            updateStep(step)
        }

        // ✅ Handle back press properly
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = handleBack()
        })

        binding.backbtn.setOnClickListener {
            handleBack()
        }
    }

    private fun handleBack() {
        if (!navController.popBackStack()) {
            finish() // exit activity
        } else {
            val currentDestination = navController.currentDestination?.id
            val step = getStepForDestination(currentDestination)
            updateStep(step)
        }
    }

    /** ✅ Step mapping for each fragment */
    private fun getStepForDestination(destinationId: Int?): Int {
        return when (destinationId) {
            R.id.diseaseFragment -> 1
            R.id.uploadPrescriptionFragment -> 2
            R.id.slotTimeFragment -> 3
            R.id.addMedicineFragement -> 4
            R.id.medicineListFragment -> 5
            else -> 1
        }
    }

    /** ✅ Visually update top progress indicators */
    private fun updateStep(step: Int) {
        // Update label text
        binding.tvStep.text = "$step of 5 Steps Completed"

        // Fill progress bars accordingly
        progressBars.forEachIndexed { index, bar ->
            bar.progress = if (index < step) 100 else 0
        }
    }
}
