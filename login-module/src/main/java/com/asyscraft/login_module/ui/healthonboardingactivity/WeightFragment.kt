package com.asyscraft.login_module.ui.healthonboardingactivity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.asyscraft.login_module.R
import com.asyscraft.login_module.databinding.FragmentWeightBinding
import com.careavatar.core_network.base.BaseFragment
import com.careavatar.core_service.repository.viewModels.OnboardingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class WeightFragment : BaseFragment() {

    private lateinit var binding: FragmentWeightBinding
    private val viewModel: OnboardingViewModel by activityViewModels()
    private var isKgMode = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWeightBinding.inflate(inflater, container, false)

        val activity = requireActivity() as? InformationOnboardingActivity
        activity?.updateStep(6)

        setupTabs()
        setupListeners()

        binding.btninclude.buttonNext.setOnClickListener {
            val weightKg: Int? = if (isKgMode) {
                binding.weightEditText.text.toString().toIntOrNull()
            } else {
                // lbs → kg conversion
                val lbs = binding.weightEditText.text.toString().toDoubleOrNull() ?: 0.0
                if (lbs > 0) (lbs * 0.453592).roundToInt() else null
            }

            if (weightKg == null || weightKg <= 0) {
                showToast("Please enter a valid weight")
            } else {
                viewModel.updateWeight(weightKg.toString())
                findNavController().navigate(R.id.action_weight_to_bloodPressure) // replace with actual action
            }
        }
        if (isKgMode) {
            switchToLbsMode()

        }else
        {   switchToKgMode()

        }


        return binding.root
    }

    private fun setupTabs() {
        binding.kgTextView.setOnClickListener { switchToKgMode() }
        binding.lbsTabTextView.setOnClickListener { switchToLbsMode() }
    }

    private fun switchToKgMode() {
        if (isKgMode) return // already in kg, do nothing

        // Convert lbs → kg
        val lbs = binding.weightEditText.text.toString().toDoubleOrNull() ?: 0.0
        if (lbs > 0) {
            val kg = (lbs * 0.453592).roundToInt()
            binding.weightEditText.setText(kg.toString())
        }

        isKgMode = true // set mode after conversion

        // Update tab UI
        binding.kgTextView.setBackgroundResource(com.careavatar.core_ui.R.drawable.height_active_tab_bg)
        binding.kgTextView.setTextColor(ContextCompat.getColor(requireContext(), com.careavatar.core_ui.R.color.textColor))
        binding.lbsTabTextView.setBackgroundResource(0)
        binding.lbsTabTextView.setTextColor(ContextCompat.getColor(requireContext(), com.careavatar.core_ui.R.color.hintColor))
    }

    private fun switchToLbsMode() {
        if (!isKgMode) return // already in lbs, do nothing

        // Convert kg → lbs
        val kg = binding.weightEditText.text.toString().toDoubleOrNull() ?: 0.0
        if (kg > 0) {
            val lbs = (kg / 0.453592).roundToInt()
            binding.weightEditText.setText(lbs.toString())
        }

        isKgMode = false // set mode after conversion

        // Update tab UI
        binding.lbsTabTextView.setBackgroundResource(com.careavatar.core_ui.R.drawable.height_active_tab_bg)
        binding.lbsTabTextView.setTextColor(ContextCompat.getColor(requireContext(), com.careavatar.core_ui.R.color.textColor))
        binding.kgTextView.setBackgroundResource(0)
        binding.kgTextView.setTextColor(ContextCompat.getColor(requireContext(), com.careavatar.core_ui.R.color.hintColor))
    }


    private fun setupListeners() {
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Optional: live conversion while typing
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        binding.weightEditText.addTextChangedListener(watcher)
    }
}
