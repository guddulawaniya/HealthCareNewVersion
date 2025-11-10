package com.careavatar.dashboardmodule.dashboardfragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.asyscraft.alzimer_module.AlzimerMainActivity
import com.asyscraft.dietition_module.DietDashboardActivity
import com.asyscraft.dietition_module.onBoardingQuestionActivity
import com.asyscraft.medical_reminder.ReminderTypeActivity
import com.asyscraft.service_module.ui.BookCaretakerActivity
import com.asyscraft.service_module.ui.MedicalServicesActivity
import com.careavatar.core_network.base.BaseFragment
import com.careavatar.dashboardmodule.NotificationActivity
import com.careavatar.dashboardmodule.adapters.ViewPagerAdapter
import com.careavatar.dashboardmodule.databinding.FragmentHomeBinding
import com.careavatar.dashboardmodule.viewModels.DashBoardViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    private lateinit var binding: FragmentHomeBinding

    private val viewModel: DashBoardViewModel by viewModels()
    private var dietIsFirstTime = 0
    private var exerciseIsFirst = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.notification.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationActivity::class.java))
        }

        binding.btnBookNow.setOnClickListener {
            startActivity(Intent(requireContext(), BookCaretakerActivity::class.java))
        }

        binding.medicalOrderNow.setOnClickListener {
            startActivity(Intent(requireContext(), MedicalServicesActivity::class.java))
        }
        binding.medicalReminder.setOnClickListener {

            startActivity(Intent(requireContext(), ReminderTypeActivity::class.java))
        }

        binding.alzimerLayout.setOnClickListener {
            startActivity(Intent(requireContext(), AlzimerMainActivity::class.java))
        }

        binding.dieticianLayout.setOnClickListener {
            val intent = if (dietIsFirstTime == 1)
                Intent(requireContext(), onBoardingQuestionActivity::class.java)
            else
                Intent(requireContext(), DietDashboardActivity::class.java)

            startActivity(intent)
        }


        fetchUserData()
        observeViewModel()

        return binding.root
    }

    private fun observeViewModel() = with(viewModel) {
        collectApiResultOnStarted(userDetailsResponse) {
            if (it.success) {
                if (it.user.notificationCount > 0) {
                    binding.badgeCount.visibility = View.VISIBLE
                    binding.badgeCount.text = it.user.notificationCount.toString()
                } else {
                    binding.badgeCount.visibility = View.GONE
                }
            }
        }

        collectApiResultOnStarted(healthResponse) {
        }

        collectApiResultOnStarted(bannerResponse) {
            binding.viewPager.adapter = ViewPagerAdapter(requireContext(), it)
            binding.dotsIndicator.attachTo(binding.viewPager)
        }

        collectApiResultOnStarted(upComingResponseModel) {

        }

        collectApiResultOnStarted(getHealthMonitorQuestions) {
            if (it.success) {
                dietIsFirstTime = it.services[2].isFirstTime.toInt()
                exerciseIsFirst = it.services[0].isFirstTime.toInt()
            }

        }

    }

    private fun fetchUserData() {
        launchIfInternetAvailable {
            // Main thread is fine here
            viewModel.hitCategoryWithSubcategories()
            viewModel.userDetails()
            viewModel.hitBanner()
            viewModel.hitDashBoardHealth()
            viewModel.hitGetUpComingClass()
        }
    }


}