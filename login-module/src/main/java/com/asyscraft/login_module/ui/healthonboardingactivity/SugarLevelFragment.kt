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
import com.asyscraft.login_module.databinding.FragmentSugarLevelBinding
import com.careavatar.core_network.base.BaseFragment
import com.careavatar.core_service.repository.viewModels.OnboardingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SugarLevelFragment : BaseFragment() {

    private lateinit var binding: FragmentSugarLevelBinding
    private val viewModel: OnboardingViewModel by activityViewModels()
    private var isMgDlMode = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSugarLevelBinding.inflate(inflater, container, false)

        val activity = requireActivity() as? InformationOnboardingActivity
        activity?.updateStep(8)

        setupTabs()
        setupListeners()

        binding.btninclude.buttonNext.setOnClickListener {
            val sugarValue = binding.sugarEditTextview.text.toString().toFloatOrNull()
            if (sugarValue == null || sugarValue <= 0f) {
                showToast("Please enter a valid sugar level")
                return@setOnClickListener
            }

            // Always store internally as mg/dL
            val data = (if (isMgDlMode) {
                sugarValue
            } else {
                sugarValue * 18f
            }).toString()

            viewModel.updateSugarLevel(data)

            findNavController().navigate(R.id.action_sugar_to_UploadProfile)
        }

        if (isMgDlMode) {
            switchToMmolLMode()
        }else
        {  switchToMgDlMode()

        }



        return binding.root
    }

    private fun setupTabs() {
        binding.mgdlTextView.setOnClickListener {
            switchToMgDlMode()
        }

        binding.mmolTabTextView.setOnClickListener {
            switchToMmolLMode()
        }
    }

    private fun switchToMgDlMode() {
        if (isMgDlMode) return // already in mg/dL

        // Convert from mmol/L → mg/dL
        val mmolValue = binding.sugarEditTextview.text.toString().toFloatOrNull()
        if (mmolValue != null) {
            val mgValue = (mmolValue * 18f).toInt()
            binding.sugarEditTextview.setText(mgValue.toString())
        }

        isMgDlMode = true // set mode after conversion

        // Update tab UI
        binding.mgdlTextView.setBackgroundResource(com.careavatar.core_ui.R.drawable.height_active_tab_bg)
        binding.mgdlTextView.setTextColor(ContextCompat.getColor(requireContext(), com.careavatar.core_ui.R.color.textColor))
        binding.mmolTabTextView.setBackgroundResource(0)
        binding.mmolTabTextView.setTextColor(ContextCompat.getColor(requireContext(), com.careavatar.core_ui.R.color.hintColor))
    }

    private fun switchToMmolLMode() {
        if (!isMgDlMode) return // already in mmol/L

        // Convert from mg/dL → mmol/L
        val mgValue = binding.sugarEditTextview.text.toString().toFloatOrNull()
        if (mgValue != null) {
            val mmolValue = mgValue / 18f
            binding.sugarEditTextview.setText(String.format("%.1f", mmolValue))
        }

        isMgDlMode = false // set mode after conversion

        // Update tab UI
        binding.mmolTabTextView.setBackgroundResource(com.careavatar.core_ui.R.drawable.height_active_tab_bg)
        binding.mmolTabTextView.setTextColor(ContextCompat.getColor(requireContext(), com.careavatar.core_ui.R.color.textColor))
        binding.mgdlTextView.setBackgroundResource(0)
        binding.mgdlTextView.setTextColor(ContextCompat.getColor(requireContext(), com.careavatar.core_ui.R.color.hintColor))
    }


    private fun setupListeners() {
        binding.sugarEditTextview.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val value = s.toString().toFloatOrNull()
                binding.btninclude.buttonNext.isEnabled = value != null && value > 0f
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
}
