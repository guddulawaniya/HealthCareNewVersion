package com.asyscraft.login_module.ui.healthonboardingactivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.asyscraft.login_module.R
import com.asyscraft.login_module.databinding.FragmentGenderBinding
import com.careavatar.core_network.base.BaseFragment
import com.careavatar.core_service.repository.viewModels.OnboardingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GenderFragment : BaseFragment() {

    private var _binding: FragmentGenderBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OnboardingViewModel by activityViewModels()

    private var selectedGender: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGenderBinding.inflate(inflater, container, false)

        // Update current step
        (requireActivity() as? InformationOnboardingActivity)?.updateStep(3)

        setupClickListeners()

        return binding.root
    }

    private fun setupClickListeners() {

        binding.btninclude.buttonNext.isEnabled = false
        // Next button
        binding.btninclude.buttonNext.setOnClickListener {
            selectedGender?.let {
                viewModel.updateGender(it)
                findNavController().navigate(R.id.action_gender_to_age)
            }
        }

        // Gender selections
        binding.layoutMale.setOnClickListener { updateSelection("Male") }
        binding.layoutFemale.setOnClickListener { updateSelection("Female") }
        binding.layoutOther.setOnClickListener { updateSelection("Others") }
    }

    private fun updateSelection(gender: String) {
        selectedGender = gender

        // Reset all
        binding.layoutMale.setBackgroundResource(com.careavatar.core_ui.R.drawable.unselected_gender_bg)
        binding.layoutFemale.setBackgroundResource(com.careavatar.core_ui.R.drawable.unselected_gender_bg)
        binding.layoutOther.setBackgroundResource(com.careavatar.core_ui.R.drawable.unselected_gender_bg)

        binding.maleSelectedIcon.visibility = View.GONE
        binding.femaleSelectedIcon.visibility = View.GONE
        binding.othersSelectedIcon.visibility = View.GONE
        binding.btninclude.buttonNext.isEnabled = true

        // Apply selected state
        when (gender) {
            "Male" -> {
                binding.layoutMale.setBackgroundResource(com.careavatar.core_ui.R.drawable.selected_gender_bg)
                binding.maleSelectedIcon.visibility = View.VISIBLE
            }
            "Female" -> {
                binding.layoutFemale.setBackgroundResource(com.careavatar.core_ui.R.drawable.selected_gender_bg)
                binding.femaleSelectedIcon.visibility = View.VISIBLE
            }
            "Others" -> {
                binding.layoutOther.setBackgroundResource(com.careavatar.core_ui.R.drawable.selected_gender_bg)
                binding.othersSelectedIcon.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
