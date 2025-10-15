package com.careavatar.dashboardmodule.dashboardfragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.asyscraft.core_navigation.AppNavigator

import com.careavatar.core_network.base.BaseFragment
import com.careavatar.dashboardmodule.databinding.FragmentProfileBinding
import com.careavatar.dashboardmodule.viewModels.DashBoardViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ProfileFragment : BaseFragment() {

    private lateinit var binding: FragmentProfileBinding

    private val viewModel: DashBoardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        observeViewModel()
        fetchUserData()

        binding.logoutbtn.setOnClickListener {
            lifecycleScope.launch {
                userPref.clearData()
            }

            navigateToLogin()

        }


    }

    private fun navigateToLogin() {
        try {
            val intent = Intent(
                requireContext(),
                Class.forName("com.asyscraft.login_module.ui.LoginActivity")
            ).apply {
                // YE FLAGS IMPORTANT HAI - back stack clear karne ke liye
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            startActivity(intent)

            // Current activity ko finish karein
            requireActivity().finishAffinity()

        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback: App restart
            restartApp()
        }
    }

    private fun restartApp() {
        val intent = requireContext().packageManager.getLaunchIntentForPackage(requireContext().packageName)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        requireContext().startActivity(intent)
        requireActivity().finishAffinity()
    }

    private fun observeViewModel() = with(viewModel) {
        collectApiResultOnStarted(userDetailsResponse) {
            if (it.success) {
                it.user.apply {
                    binding.apply {
                        tvUserName.text = name
                        stepCount.text = notificationCount.toString()
                        tvBloodPressure.text = BP
                        tvSugarLevel.text = sugar
                    }
                }

            }
        }


    }

    private fun fetchUserData() {
        launchIfInternetAvailable {
            viewModel.userDetails()
        }
    }

}