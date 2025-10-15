package com.asyscraft.login_module.ui.healthonboardingactivity

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.asyscraft.login_module.R
import com.asyscraft.login_module.databinding.ActivityInformationOnboardingBinding
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InformationOnboardingActivity : BaseActivity() {
    private lateinit var binding: ActivityInformationOnboardingBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInformationOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backbtn.setOnClickListener { handleBack() }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = handleBack()
        })

        updateStep(1)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.skipbtn.setOnClickListener {
            navController.let {
                val currentDestination = it.currentDestination?.id
                val nextAction = when (currentDestination) {
                    R.id.nameFragment -> R.id.action_name_to_email
                    R.id.emailFragment -> R.id.action_email_to_gender
                    R.id.genderFragment -> R.id.action_gender_to_age
                    R.id.ageFragment -> R.id.action_age_to_height
                    R.id.heightFragment -> R.id.action_height_to_weight
                    R.id.weightFragment -> R.id.action_weight_to_bloodPressure
                    R.id.bloodPressureFragment -> R.id.action_bloodPressure_to_sugarLevel
                    R.id.sugarLevelFragment -> R.id.action_sugar_to_UploadProfile
                    R.id.userProfileFragment -> R.id.action_UploadProfile_to_emergencycontact
                    else -> null
                }

                if (nextAction != null) {
                    it.navigate(nextAction)
                    // Animate progress bar
                    val step = getStepForDestination(nextAction)
                    updateStep(step)
                } else {
                    // If no more actions, finish or go to next screen
                    goToNextScreen()
                }
            }
        }



    }

    fun handleBack() {
        if (!navController.popBackStack()) {
            finish() // exit activity
        } else {
            val currentDestination = navController.currentDestination?.id
            val step = getStepForDestinationBack(currentDestination)
            updateStep(step)
        }
    }



    private fun getStepForDestinationBack(destinationId: Int?): Int {
        return when (destinationId) {
            R.id.nameFragment -> 1
            R.id.emailFragment -> 2
            R.id.genderFragment -> 3
            R.id.ageFragment -> 4
            R.id.heightFragment -> 5
            R.id.weightFragment -> 6
            R.id.bloodPressureFragment -> 7
            R.id.sugarLevelFragment -> 8
            R.id.userProfileFragment -> 9
            R.id.emergencyContactFragment -> 10
            else -> binding.progressBar.progress
        }
    }


    private fun goToNextScreen() {
        startActivity(Intent(this, Information_loading_Activity::class.java))
    }

    private fun getStepForDestination(actionId: Int): Int {
        return when (actionId) {

            R.id.action_name_to_email -> 2
            R.id.action_email_to_gender -> 3
            R.id.action_gender_to_age -> 4
            R.id.action_age_to_height -> 5
            R.id.action_height_to_weight -> 6
            R.id.action_weight_to_bloodPressure -> 7
            R.id.action_bloodPressure_to_sugarLevel -> 8
            R.id.action_sugar_to_UploadProfile -> 9
            R.id.action_UploadProfile_to_emergencycontact -> 10
            else -> binding.progressBar.progress
        }
    }

    fun updateStep(step: Int) {
        if (step>5){
            binding.skipbtn.visibility = View.VISIBLE
        }else{
            binding.skipbtn.visibility = View.GONE
        }
        val start = binding.progressBar.progress
        val animator = ValueAnimator.ofInt(start, step)
        animator.duration = 500
        animator.addUpdateListener { valueAnimator ->
            val progress = valueAnimator.animatedValue as Int
            binding.progressBar.progress = progress
            binding.tvStep.text = "Step $progress of ${binding.progressBar.max}"
        }
        animator.start()
    }


}