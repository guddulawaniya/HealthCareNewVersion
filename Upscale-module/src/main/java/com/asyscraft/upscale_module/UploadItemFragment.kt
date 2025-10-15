package com.asyscraft.upscale_module

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.asyscraft.upscale_module.databinding.FragmentUploadItemBinding
import com.careavatar.core_network.base.BaseFragment
import com.careavatar.core_utils.ImagePickerManager
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class UploadItemFragment : BaseFragment() {

    private lateinit var binding: FragmentUploadItemBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUploadItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the ImagePickerManager once
        ImagePickerManager.init(this, requireContext())

        setupUrgencySwitch()
        setupAutoCompleteTextView()

        // Click listener to pick image
        binding.imagepicker.setOnClickListener {
            pickImage()
        }
        binding.crossbtn.setOnClickListener {
            binding.itemImageView.setImageDrawable(null)
            binding.imagepicker.visibility = View.VISIBLE
        }
    }

    private fun pickImage() {
        ImagePickerManager.showImageSourceDialog(requireContext()) { uri ->
            uri?.let {
                // Use the selected image URI here, e.g., load into ImageView
                 binding.itemImageView.setImageURI(uri)
                binding.imagepicker.visibility = View.GONE
            }
        }
    }


    private fun setupAutoCompleteTextView() {
        // Your list of categories
        val categories = listOf("Health", "Fitness", "Yoga", "Meditation", "Diet")

        // Create ArrayAdapter
        val adapter = ArrayAdapter(
            requireContext(),
            com.careavatar.core_ui.R.layout.dropdown_item,
            categories
        )

        // Set the adapter
        binding.categoryAutocomplete.setAdapter(adapter)

        binding.categoryAutocomplete.setOnClickListener {
            binding.categoryAutocomplete.showDropDown()
        }


        // Optional: handle item selection
        binding.categoryAutocomplete.setOnItemClickListener { parent, view, position, id ->

            val selectedItem = parent.getItemAtPosition(position).toString()
            showToast( "Selected: $selectedItem")
        }
    }



    private fun setupUrgencySwitch() {
        val activeBg = com.careavatar.core_ui.R.drawable.height_active_tab_bg
        binding.newtext.setBackgroundResource(activeBg)
        binding.usedtext.setBackgroundResource(0)

        binding.newtext.setOnClickListener {
            binding.newtext.setBackgroundResource(activeBg)
            binding.usedtext.setBackgroundResource(0)
            binding.durationUsed.visibility = View.GONE
        }

        binding.usedtext.setOnClickListener {
            binding.durationUsed.visibility = View.VISIBLE
            binding.usedtext.setBackgroundResource(activeBg)
            binding.newtext.setBackgroundResource(0)
        }
    }
}
