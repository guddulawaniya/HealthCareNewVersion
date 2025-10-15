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
import com.asyscraft.login_module.databinding.FragmentHeightBinding
import com.careavatar.core_network.base.BaseFragment
import com.careavatar.core_service.repository.viewModels.OnboardingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue
import kotlin.math.roundToInt


@AndroidEntryPoint
class HeightFragment : BaseFragment() {

    private lateinit var binding: FragmentHeightBinding
    private val viewModel: OnboardingViewModel by activityViewModels()
    private var isFeetMode = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHeightBinding.inflate(inflater, container, false)

        val activity = requireActivity() as? InformationOnboardingActivity
        activity?.updateStep(5)

        binding.btninclude.buttonNext.setOnClickListener {
            val heightCm: Int? = if (isFeetMode) {
                val feet = binding.feetEditText.text.toString().toIntOrNull() ?: 0
                val inch = binding.inchEditText.text.toString().toIntOrNull() ?: 0
                (feet * 30.48 + inch * 2.54).toInt()
            } else {
                binding.cmEditText.text.toString().toIntOrNull()
            }

            if (heightCm == null || heightCm <= 0) {
                showToast("Please enter a valid height")
            } else {
                viewModel.updateHeight(heightCm.toString())
                findNavController().navigate(R.id.action_height_to_weight)
            }
        }


        setupTabs()
        setupListeners()
        if (isFeetMode) {
            switchToFeetMode()
        } else {
            switchToCmMode()
        }
        return binding.root

    }

    private fun setupTabs() {
        binding.cmTextView.setOnClickListener {
            switchToCmMode()
        }

        binding.feetTabTextView.setOnClickListener {
            switchToFeetMode()
        }
    }




    private fun switchToFeetMode() {
        isFeetMode = true
        binding.cmEditText.visibility = View.GONE
        binding.feetInchLayout.visibility = View.VISIBLE

        val cm = binding.cmEditText.text.toString().toDoubleOrNull() ?: 0.0
        if (cm > 0) {
            val totalInches = cm / 2.54
            val feet = (totalInches / 12).toInt()
            val inch = ((totalInches - (feet * 12)).roundToInt()) // round instead of truncating
            binding.feetEditText.setText(feet.toString())
            binding.inchEditText.setText(inch.toString())
        }

        binding.feetTabTextView.setBackgroundResource(com.careavatar.core_ui.R.drawable.height_active_tab_bg)
        binding.feetTabTextView.setTextColor(ContextCompat.getColor(requireContext(), com.careavatar.core_ui.R.color.textColor))
        binding.cmTextView.setBackgroundResource(0)
        binding.cmTextView.setTextColor(ContextCompat.getColor(requireContext(), com.careavatar.core_ui.R.color.hintColor))
    }

    private fun switchToCmMode() {
        isFeetMode = false
        binding.cmEditText.visibility = View.VISIBLE
        binding.feetInchLayout.visibility = View.GONE

        val feet = binding.feetEditText.text.toString().toDoubleOrNull() ?: 0.0
        val inch = binding.inchEditText.text.toString().toDoubleOrNull() ?: 0.0
        if (feet > 0 || inch > 0) {
            val cm = (feet * 30.48 + inch * 2.54).roundToInt()  // round here
            binding.cmEditText.setText(cm.toString())
        }

        binding.cmTextView.setBackgroundResource(com.careavatar.core_ui.R.drawable.height_active_tab_bg)
        binding.cmTextView.setTextColor(ContextCompat.getColor(requireContext(), com.careavatar.core_ui.R.color.textColor))
        binding.feetTabTextView.setBackgroundResource(0)
        binding.feetTabTextView.setTextColor(ContextCompat.getColor(requireContext(), com.careavatar.core_ui.R.color.hintColor))
    }




    private fun setupListeners() {
        // Convert Feet + Inch to CM on change
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (isFeetMode) {
                    val feet = binding.feetEditText.text.toString().toIntOrNull() ?: 0
                    val inch = binding.inchEditText.text.toString().toIntOrNull() ?: 0
                    val cm = (feet * 30.48 + inch * 2.54)
                    binding.cmEditText.setText(cm.toInt().toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
    }

}