package com.asyscraft.login_module.ui.healthonboardingactivity

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.animation.doOnEnd
import com.asyscraft.login_module.databinding.ActivityCommitBinding
import com.careavatar.core_network.base.BaseActivity
import com.careavatar.core_ui.R
import com.careavatar.dashboardmodule.DashboardActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommitActivity : BaseActivity() {

    private lateinit var binding: ActivityCommitBinding
    private val sizesDp = listOf(150, 250, 350, 1000)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommitBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.fingerprintLayout.setOnLongClickListener {

            // Hide top TextViews
            binding.textView2.visibility = View.GONE
            binding.description.visibility = View.GONE

            // Start the full 3-step animation sequence automatically
            playStepAnimation(0)

            true
        }

    }

    private fun playStepAnimation(stepIndex: Int) {
        val density = resources.displayMetrics.density
        val sizesPx = sizesDp.map { if (it == -1) -1 else (it * density).toInt() }

        if (stepIndex >= sizesPx.size) return

        val startWidth = binding.fingerprintLayout.width
        val startHeight = binding.fingerprintLayout.height
        val targetWidth = if (sizesPx[stepIndex] == -1) binding.root.width else sizesPx[stepIndex]
        val targetHeight = targetWidth // same for circle

        val widthAnimator = ValueAnimator.ofInt(startWidth, targetWidth)
        val heightAnimator = ValueAnimator.ofInt(startHeight, targetHeight)

        widthAnimator.addUpdateListener { lp ->
            val layoutParams = binding.fingerprintLayout.layoutParams
            layoutParams.width = lp.animatedValue as Int
            binding.fingerprintLayout.layoutParams = layoutParams
        }

        heightAnimator.addUpdateListener { lp ->
            val layoutParams = binding.fingerprintLayout.layoutParams
            layoutParams.height = lp.animatedValue as Int
            binding.fingerprintLayout.layoutParams = layoutParams
        }

        AnimatorSet().apply {
            playTogether(widthAnimator, heightAnimator)
            duration = 300
            doOnEnd {
                when (stepIndex) {
                    sizesPx.size - 1 -> { // Last step (full screen)
                        binding.fingerprintIcon.visibility = View.GONE
                        binding.fingerprintText2.text = getString(R.string.welcome_to_care_avatar)
                        binding.fingerprintText2.textSize = 25f

                        binding.fingerprintLayout.postDelayed({
                            goToNextScreen()
                        }, 2000)
                    }

                    sizesPx.size / 2 -> { // Middle step
                        binding.fingerprintText2.visibility = View.VISIBLE
                        binding.fingerprintText2.text = getString(R.string.get_ready)
                        binding.fingerprintText2.textSize = 20f
                    }

                    else -> {
                        binding.fingerprintText.visibility = View.GONE
                        binding.fingerprintText2.visibility = View.VISIBLE
                        binding.fingerprintText2.text = getString(R.string.almost_there)
                        binding.fingerprintText2.textSize = 15f
                    }
                }

                // Automatically play the next step after delay
                binding.fingerprintLayout.postDelayed({
                    playStepAnimation(stepIndex + 1)
                }, 1000) // delay between steps
            }
            start()
        }
    }

    private fun goToNextScreen() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }
}
