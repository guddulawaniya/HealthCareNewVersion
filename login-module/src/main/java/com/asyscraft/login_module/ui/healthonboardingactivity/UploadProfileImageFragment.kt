package com.asyscraft.login_module.ui.healthonboardingactivity

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.asyscraft.login_module.R
import com.asyscraft.login_module.databinding.FragmentUploadProfileImageBinding
import com.careavatar.core_utils.ImagePickerManager
import com.careavatar.core_network.base.BaseFragment
import com.careavatar.core_utils.FileUtils
import com.careavatar.core_service.repository.viewModels.OnboardingViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class UploadProfileImageFragment : BaseFragment() {

    private var _binding: FragmentUploadProfileImageBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OnboardingViewModel by activityViewModels()

    private var selectedImageUri: Uri? = null
    private var selectedImageFile: File? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUploadProfileImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity() as? InformationOnboardingActivity
        activity?.updateStep(9)
        // Initialize ImagePicker
        ImagePickerManager.init(this, requireContext())

        // Pick image
        binding.imagepicker.setOnClickListener {
            pickImage()
        }

        // Remove image
        binding.removebtn.setOnClickListener {
            clearSelectedImage()
        }

        // Disable Next initially
        binding.includedbtn.buttonNext.isEnabled = false

        // Handle Next click
        binding.includedbtn.buttonNext.setOnClickListener {
            selectedImageFile?.let { file ->
                viewModel.updateImage(file)
                findNavController().navigate(R.id.action_UploadProfile_to_emergencycontact)
            }
        }
    }

    private fun pickImage() {
        ImagePickerManager.showImageSourceDialog(requireContext()) { uri ->
            uri?.let {
                selectedImageUri = it

                // Convert Uri → File
                selectedImageFile = FileUtils.uriToFile(requireContext(), it)

                if (selectedImageFile != null) {
                    binding.includedbtn.buttonNext.isEnabled = true
                    binding.userProfile.setImageURI(it)
                    binding.imagepicker.visibility = View.GONE
                    binding.userProfile.visibility = View.VISIBLE
                    binding.removebtn.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun clearSelectedImage() {
        selectedImageUri = null
        selectedImageFile = null
        binding.userProfile.setImageDrawable(null)
        binding.includedbtn.buttonNext.isEnabled = false
        binding.imagepicker.visibility = View.VISIBLE
        binding.userProfile.visibility = View.GONE
        binding.removebtn.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
