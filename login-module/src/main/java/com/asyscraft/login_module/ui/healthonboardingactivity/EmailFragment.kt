package com.asyscraft.login_module.ui.healthonboardingactivity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import android.util.Patterns
import com.asyscraft.login_module.R
import com.asyscraft.login_module.databinding.FragmentEmailBinding
import com.careavatar.core_network.base.BaseFragment
import com.careavatar.core_service.repository.viewModels.OnboardingViewModel


@AndroidEntryPoint
class EmailFragment : BaseFragment() {

    private lateinit var binding: FragmentEmailBinding
    private val viewModel: OnboardingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEmailBinding.inflate(inflater, container, false)

        val activity = requireActivity() as? InformationOnboardingActivity
        activity?.updateStep(2)
        binding.btninclude.buttonNext.isEnabled = false



        binding.btninclude.buttonNext.setOnClickListener {
            val email = binding.etUserEmailId.text.toString().trim()

            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showToast("Please enter a valid email address")
            } else {
                viewModel.updateEmail(email)
                findNavController().navigate(R.id.action_email_to_gender)
            }
        }


        setupNameValidation()



        return binding.root
    }

    private fun setupNameValidation() {
        binding.etUserEmailId.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val input = s.toString().trim()
                binding.btninclude.buttonNext.isEnabled = input.isNotEmpty()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

}