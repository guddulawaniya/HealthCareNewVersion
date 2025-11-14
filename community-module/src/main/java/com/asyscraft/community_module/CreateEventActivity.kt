package com.asyscraft.community_module

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.asyscraft.community_module.adpaters.ImageAdapter
import com.asyscraft.community_module.databinding.ActivityCreateEventBinding
import com.asyscraft.community_module.viewModels.SocialMeetViewmodel
import com.bumptech.glide.Glide
import com.careavatar.core_model.DeleteEventRequest
import com.careavatar.core_network.base.BaseActivity
import com.careavatar.core_ui.R
import com.careavatar.core_utils.Constants
import com.careavatar.core_utils.FileUtils.uriToFile
import com.careavatar.core_utils.ImagePickerManager
import com.careavatar.core_utils.convertFormFileToMultipartBody
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateEventActivity : BaseActivity() {
    private lateinit var binding: ActivityCreateEventBinding

    private lateinit var imageAdapter: ImageAdapter
    private var selectedImages = mutableListOf<String>()

    private var selectedImageUri: Uri? = null
    private val viewmodel: SocialMeetViewmodel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
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
        }

        binding.btninclude.buttonNext.setOnClickListener {
            if (validateInputs()) {
                val communityId = intent.getStringExtra("communityId").toString()
                val intent = Intent(this, CreateEventNextActivity::class.java).apply {
                    putExtra("updateEvent", intent.getStringExtra("updateEvent"))
                    putExtra("eventId", intent.getStringExtra("eventId"))
                    putExtra("title", binding.title.text.toString())
                    putExtra("description", binding.description.text.toString())
                    putExtra("mainImage", selectedImageUri?.toString())
                    putExtra("communityId", communityId)
                    putStringArrayListExtra("selectedImages", ArrayList(selectedImages))
                }
                startActivity(intent)
                finish()
            }

        }

        setupRecyclerview()

        if (intent.getStringExtra("updateEvent") == "update") {
            binding.toolbar.tvTitle.text = "Edit Event Info"
            binding.btninclude.buttonNext.text = "Save & Changes"
            fetchEventDetails()
            observer()
        }


    }

    private fun fetchEventDetails() {
        val eventId = intent.getStringExtra("eventId").toString()
        launchIfInternetAvailable {
            viewmodel.hitGetEventdetailbyid(eventId)
        }
    }

    private fun hitDeleteEventImage(imagePath: String) {
        val eventId = intent.getStringExtra("eventId").toString()

        launchIfInternetAvailable {
            viewmodel.hitDeleteEventImage(eventId, request = DeleteEventRequest(imagePath))
        }

    }

    private fun hitUploadEventImage(image: Uri) {
        val eventId = intent.getStringExtra("eventId").toString()
        val file = uriToFile(this, image)
        launchIfInternetAvailable {
            viewmodel.hitUploadEventImage(
                eventId,
                image = convertFormFileToMultipartBody("attachments", file)
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun observer() {
        collectApiResultOnStarted(viewmodel.getEventDetailResponse) { it ->
            if (it.success) {
                val event = it.data
                // Load main image
                if (event.attachment.isNotEmpty()) {
                    binding.imagepicker.visibility = View.GONE
                    binding.imagelayout.visibility = View.VISIBLE

                    Glide.with(this)
                        .load(Constants.socket_URL + event.attachment[0])
                        .placeholder(R.drawable.logo)
                        .into(binding.itemImageView)
                }
                selectedImageUri = event.attachment[0].toUri()
                selectedImages = event.attachment.map { it }.toMutableList()
                setupRecyclerview()

                binding.title.setText(event.title.toString())
                binding.description.setText(event.description)

            }
        }


        collectApiResultOnStarted(viewmodel.eventImageUploadResponse) {
            if (it.success) {
                selectedImages.clear()
                selectedImages.addAll(it.attachments)
                imageAdapter.notifyDataSetChanged()
            }
        }
    }


    private fun validateInputs(): Boolean {
        val title = binding.title.text.toString().trim()
        val description = binding.description.text.toString().trim()

        return when {
            title.isEmpty() -> {
                showInputError(binding.description, "Please enter a title")
                false
            }

            description.isEmpty() -> {
                showInputError(binding.description, "Please enter a description")
                false
            }

//            selectedImageUri == null -> {
//                showToast("Please select a main image")
//                false
//            }

            selectedImages.isEmpty() -> {
                showToast("Please select at least one additional image")
                false
            }

            else -> true
        }
    }


    private fun setupRecyclerview() {
        imageAdapter = ImageAdapter(selectedImages, onClickItem = { position ->
            val flag = intent.getStringExtra("updateEvent")=="update"
            val imageIdentifier = selectedImages[position]

            if (flag) {
                hitDeleteEventImage(imageIdentifier)
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
                if (intent.getStringExtra("updateEvent") == "update") {

                    hitUploadEventImage(uri)
                }
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