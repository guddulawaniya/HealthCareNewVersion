package com.asyscraft.medical_reminder.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.asyscraft.medical_reminder.R
import com.asyscraft.medical_reminder.databinding.FragmentUploadPrescriptionBinding
import com.careavatar.core_network.base.BaseFragment
import com.careavatar.core_utils.ImagePickerManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UploadPrescriptionFragment : BaseFragment() {

    private lateinit var binding: FragmentUploadPrescriptionBinding
    private var isDoctorDetailsRequired = false  // true only when user clicks "No"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUploadPrescriptionBinding.inflate(inflater, container, false)
        ImagePickerManager.init(this, requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handlePrescription()
        setupValidation()

        binding.btnNext.setOnClickListener {
            findNavController().navigate(R.id.action_uploadPrescriptionFragment_to_slotTimeFragment)
        }
    }

    private fun handlePrescription() {
        binding.uploadImageBtn.setOnClickListener { pickImage() }

        binding.crossbtn.setOnClickListener {
            binding.itemImageView.setImageDrawable(null)
            binding.imagepicker.visibility = View.VISIBLE
            binding.uploadImagelayout.visibility = View.GONE
        }

        // YES → doctor details not required
        binding.tvYes.setOnClickListener {
            isDoctorDetailsRequired = false
            binding.layoutDoctorSection.visibility = View.GONE

            binding.tvYes.setBackgroundResource(com.careavatar.core_ui.R.drawable.button_circle_bg)
            binding.tvNo.setBackgroundResource(com.careavatar.core_ui.R.drawable.button_circle_outline_bg)

            validateFields() // Re-check validation instantly
        }

        // NO → doctor details required
        binding.tvNo.setOnClickListener {
            isDoctorDetailsRequired = true
            binding.layoutDoctorSection.visibility = View.VISIBLE

            binding.tvNo.setBackgroundResource(com.careavatar.core_ui.R.drawable.button_circle_bg)
            binding.tvYes.setBackgroundResource(com.careavatar.core_ui.R.drawable.button_circle_outline_bg)

            validateFields()
        }
    }

    private fun setupValidation() {
        val allFields = listOf(
            binding.editTextNumberOfDays,
            binding.editTextDoctorName,
            binding.editTextSpecialization,
            binding.editTextContactNumber
        )

        val watcher = object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) = validateFields()
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        allFields.forEach { it.addTextChangedListener(watcher) }

        binding.btnNext.isEnabled = false
        binding.btnNext.setCardBackgroundColor("#E0E0E0".toColorInt())
    }

    private fun validateFields() {
        val numberOfDays = binding.editTextNumberOfDays.text?.toString()?.trim().orEmpty()

        var isValid = numberOfDays.isNotEmpty()

        if (isDoctorDetailsRequired) {
            val doctorName = binding.editTextDoctorName.text?.toString()?.trim().orEmpty()
            val specialization = binding.editTextSpecialization.text?.toString()?.trim().orEmpty()
            val contact = binding.editTextContactNumber.text?.toString()?.trim().orEmpty()

            isValid = isValid &&
                    doctorName.isNotEmpty() &&
                    specialization.isNotEmpty() &&
                    contact.length == 10
        }

        if (isValid) {
            binding.btnNext.isEnabled = true
            binding.btnNext.setCardBackgroundColor("#4CAF50".toColorInt()) // Green
        } else {
            binding.btnNext.isEnabled = false
            binding.btnNext.setCardBackgroundColor("#E0E0E0".toColorInt()) // Grey
        }
    }

    private fun pickImage() {
        ImagePickerManager.showImageSourceDialog(requireContext()) { uri ->
            uri?.let {
                binding.itemImageView.setImageURI(uri)
                binding.imagepicker.visibility = View.GONE
                binding.uploadImagelayout.visibility = View.VISIBLE
            }
        }
    }
}
