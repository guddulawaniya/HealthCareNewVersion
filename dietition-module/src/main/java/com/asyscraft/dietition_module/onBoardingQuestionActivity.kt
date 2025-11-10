package com.asyscraft.dietition_module

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.asyscraft.dietition_module.databinding.ActivityOnBoardingQuestionBinding
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class onBoardingQuestionActivity : BaseActivity() {
    private lateinit var binding: ActivityOnBoardingQuestionBinding
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.backbtn.setOnClickListener {
            handleBack()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = handleBack()
        })

        updateStep(1)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

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
            R.id.dailyRoutineFragment -> 1
            R.id.lifeStyleFragment -> 2
            R.id.mainGoalFragment -> 3
            else -> binding.progressBar.progress
        }
    }

    fun updateStep(step: Int) {

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