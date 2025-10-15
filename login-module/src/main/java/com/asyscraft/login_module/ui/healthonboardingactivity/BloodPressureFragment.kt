package com.asyscraft.login_module.ui.healthonboardingactivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.asyscraft.login_module.R
import com.asyscraft.login_module.databinding.FragmentBloodpressureBinding
import com.careavatar.core_network.base.BaseFragment
import com.careavatar.core_service.repository.viewModels.OnboardingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BloodPressureFragment : BaseFragment() {

    private lateinit var binding: FragmentBloodpressureBinding
    private val viewModel: OnboardingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBloodpressureBinding.inflate(inflater, container, false)

        val activity = requireActivity() as? InformationOnboardingActivity
        activity?.updateStep(7)


        binding.btninclude.buttonNext.setOnClickListener {
            val bpValue = binding.bloodPressureEditText.text.toString().toIntOrNull()

            if (bpValue == null || bpValue <= 0) {
                showToast("Please enter a valid blood pressure")
            } else {
                viewModel.updateBloodPressure( bpValue.toString())
                findNavController().navigate(R.id.action_bloodPressure_to_sugarLevel) // replace with actual action
            }
        }

        return binding.root
    }

}
