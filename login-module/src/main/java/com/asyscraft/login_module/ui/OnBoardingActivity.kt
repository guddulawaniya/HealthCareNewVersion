package com.asyscraft.login_module.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.careavatar.core_network.base.BaseActivity
import com.careavatar.core_network.data.OnboardingData
import com.asyscraft.login_module.adapter.OnboardingAdapter
import com.asyscraft.login_module.databinding.ActivityOnBoardingBinding

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingActivity : BaseActivity() {

    private lateinit var binding: ActivityOnBoardingBinding
    private val pages by lazy { OnboardingData.getPages() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonNextInclude.buttonNext.visibility = View.GONE


        // Setup ViewPager
        binding.viewPager.adapter = OnboardingAdapter(pages)

        // Page change animation logic
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                if (position == pages.size - 1) {
                    // Last page → Show "Start" button, hide "Next" button
                    if (binding.btnNext.isVisible) {
                        binding.btnNext.animate()
                            .alpha(0f)
                            .translationY(-20f)
                            .setDuration(250)
                            .withEndAction { binding.btnNext.visibility = View.GONE }
                            .start()
                    }

                    if (binding.buttonNextInclude.buttonNext.visibility != View.VISIBLE) {
                        binding.buttonNextInclude.buttonNext.apply {
                            alpha = 0f
                            translationY = 20f
                            visibility = View.VISIBLE
                            animate().alpha(1f).translationY(0f).setDuration(250).start()
                        }
                    }

                } else {
                    // Any previous page → Show "Next" button, hide "Start" button
                    if (binding.buttonNextInclude.buttonNext.isVisible) {
                        binding.buttonNextInclude.buttonNext.animate()
                            .alpha(0f)
                            .translationY(20f)
                            .setDuration(250)
                            .withEndAction { binding.buttonNextInclude.buttonNext.visibility = View.GONE }
                            .start()
                    }

                    if (binding.btnNext.visibility != View.VISIBLE) {
                        binding.btnNext.apply {
                            alpha = 0f
                            translationY = -20f
                            visibility = View.VISIBLE
                            animate().alpha(1f).translationY(0f).setDuration(250).start()
                        }
                    }
                }
            }
        })

        // Start button click
        binding.buttonNextInclude.buttonNext.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Next button click
        binding.btnNext.setOnClickListener {
            if (binding.viewPager.currentItem < pages.size - 1) {
                binding.viewPager.currentItem += 1
            }
        }
    }
}

