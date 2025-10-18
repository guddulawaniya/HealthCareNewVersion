package com.asyscraft.community_module

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.asyscraft.community_module.adpaters.ImageAdapter
import com.asyscraft.community_module.databinding.ActivityCreateEventBinding
import com.careavatar.core_network.base.BaseActivity
import com.careavatar.core_utils.ImagePickerManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateEventActivity : BaseActivity() {
    private lateinit var binding: ActivityCreateEventBinding

    private lateinit var imageAdapter: ImageAdapter
    private val selectedImages = mutableListOf<String>()
    private var selectedImageUri: Uri? = null


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

        binding.btnAttachDocument.setOnClickListener {
            pickImageSubimage()
        }

        binding.crossbtn.setOnClickListener {
            binding.itemImageView.setImageDrawable(null)
            binding.imagepicker.visibility = View.VISIBLE
            binding.imagelayout.visibility = View.GONE
            if (selectedImages.size < 3) {
                binding.btnAttachDocument.visibility = View.GONE
            }
        }

        binding.btninclude.buttonNext.setOnClickListener {
            if(validateInputs()){
                val communityId =intent.getStringExtra("communityId").toString()
                val intent = Intent(this, CreateEventNextActivity::class.java).apply {
                    putExtra("title", binding.title.text.toString())
                    putExtra("description", binding.description.text.toString())
                    putExtra("mainImage", selectedImageUri)
                    putExtra("communityId", communityId)
                    putStringArrayListExtra("selectedImages", ArrayList(selectedImages))
                }
                startActivity(intent)
                finish()
            }

        }

        setupRecyclerview()


    }

    private fun validateInputs(): Boolean {
        val title = binding.title.text.toString().trim()
        val description = binding.description.text.toString().trim()

        return when {
            title.isEmpty() -> {
                showInputError(binding.description,"Please enter a title")
                false
            }
            description.isEmpty() -> {
                showInputError(binding.description,"Please enter a description")
                false
            }
            selectedImageUri == null -> {
                showToast("Please select a main image")
                false
            }
            selectedImages.isEmpty() -> {
                showToast( "Please select at least one additional image")
                false
            }
            else -> true
        }
    }


    private fun setupRecyclerview() {
        imageAdapter = ImageAdapter(selectedImages, onClickItem = { position ->
            val flag = intent.getBooleanExtra("flag", false)
            val imageIdentifier = selectedImages[position]

            if (flag) {
//                deleteEventImage(imageIdentifier, position)
            }

            selectedImages.removeAt(position)
            imageAdapter.notifyItemRemoved(position)
        })
        binding.imageRecylerview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.imageRecylerview.adapter = imageAdapter
    }


    private fun pickImage() {
        ImagePickerManager.showImageSourceDialog(this) { uri ->
            uri?.let {
                // Use the selected image URI here, e.g., load into ImageView
                selectedImageUri = uri
                binding.itemImageView.setImageURI(uri)
                binding.imagepicker.visibility = View.GONE
                binding.imagelayout.visibility = View.VISIBLE
            }
        }
    }

    private fun pickImageSubimage() {

        // Check if the list already has 3 images
        if (selectedImages.size >= 3) {
            showToast("You can only add up to 3 images")
            return
        }

        ImagePickerManager.showImageSourceDialog(this) { uri ->
            uri?.let {
                // Add the new image to the list
                selectedImages.add(it.toString())

                // Update RecyclerView or ImageView
                imageAdapter.notifyDataSetChanged()

                // Optional: hide the picker if you reach max
                if (selectedImages.size == 3) {
                    binding.btnAttachDocument.visibility = View.GONE
                }
            }
        }
    }

}