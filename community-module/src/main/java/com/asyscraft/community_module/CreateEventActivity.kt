package com.asyscraft.community_module

import android.os.Bundle
import android.view.View
import com.asyscraft.community_module.databinding.ActivityCreateEventBinding
import com.careavatar.core_network.base.BaseActivity
import com.careavatar.core_utils.ImagePickerManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateEventActivity : BaseActivity() {
    private lateinit var binding: ActivityCreateEventBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btninclude.buttonNext.text = "Confirm & Share in Group"

        binding.toolbar.btnBack.setOnClickListener { finish() }
        binding.toolbar.tvTitle.text = "Create Event"

        // Initialize the ImagePickerManager once
        ImagePickerManager.init(this, this)

        binding.imagepicker.setOnClickListener {
            pickImage()
        }
        binding.crossbtn.setOnClickListener {
            binding.itemImageView.setImageDrawable(null)
            binding.imagepicker.visibility = View.VISIBLE
        }



    }




    private fun pickImage() {
        ImagePickerManager.showImageSourceDialog(this) { uri ->
            uri?.let {
                // Use the selected image URI here, e.g., load into ImageView
                binding.itemImageView.setImageURI(uri)
                binding.imagepicker.visibility = View.GONE
            }
        }
    }
}