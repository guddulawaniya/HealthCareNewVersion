package com.asyscraft.login_module.ui.healthonboardingactivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.asyscraft.login_module.R
import com.asyscraft.login_module.databinding.FragmentAgeBinding
import com.careavatar.core_network.base.BaseFragment
import com.careavatar.core_service.repository.viewModels.OnboardingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue


@AndroidEntryPoint
class AgeFragment : BaseFragment() {

    private lateinit var binding: FragmentAgeBinding
    private val viewModel: OnboardingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAgeBinding.inflate(inflater, container, false)


        val activity = requireActivity() as? InformationOnboardingActivity
        activity?.updateStep(4)

        binding.btninclude.buttonNext.setOnClickListener {
            if (binding.agenumber.text.isNotEmpty()) {
                viewModel.updateAge( binding.agenumber.text.toString())
                findNavController().navigate(R.id.action_age_to_height)
            } else {
                showToast("Please enter your age")
            }
        }



        return binding.root
    }



}