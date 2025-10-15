package com.asyscraft.login_module.ui.healthonboardingactivity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.asyscraft.login_module.R
import com.asyscraft.login_module.databinding.FragmentNameBinding
import com.careavatar.core_network.base.BaseFragment
import com.careavatar.core_service.repository.viewModels.OnboardingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NameFragment : BaseFragment() {

    private lateinit var binding: FragmentNameBinding
    private val viewModel: OnboardingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNameBinding.inflate(inflater, container, false)

        val activity = requireActivity() as? InformationOnboardingActivity
        activity?.updateStep(1)
        binding.btninclude.buttonNext.isEnabled = false



        binding.btninclude.buttonNext.setOnClickListener {
            val userName = binding.etUserName.text.toString().trim()

            if (userName.isEmpty()) {
                showToast("Please enter your name")
            } else {
                viewModel.updateName(userName)
                findNavController().navigate(R.id.action_name_to_email)
            }
        }

        setupNameValidation()


        return binding.root
    }


    private fun setupNameValidation() {
        binding.etUserName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val input = s.toString().trim()
                binding.btninclude.buttonNext.isEnabled = input.isNotEmpty()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

}