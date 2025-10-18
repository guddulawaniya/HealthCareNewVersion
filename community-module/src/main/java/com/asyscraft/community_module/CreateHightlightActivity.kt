package com.asyscraft.community_module

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.net.toUri
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.asyscraft.community_module.adpaters.CommunityHightlightCategoryAdapter
import com.asyscraft.community_module.databinding.ActivityCreateHightlightBinding
import com.asyscraft.community_module.viewModels.SocialMeetViewmodel
import com.careavatar.core_model.CategoryPost
import com.careavatar.core_model.GetCategoryRquest
import com.careavatar.core_network.base.BaseActivity
import com.careavatar.core_ui.AddressPickerActivity
import com.careavatar.core_utils.FileUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateHightlightActivity : BaseActivity() {

    private lateinit var binding: ActivityCreateHightlightBinding
    private val viewModel: SocialMeetViewmodel by viewModels()
    private val categoryList = mutableListOf<CategoryPost>()
    private lateinit var adapter: CommunityHightlightCategoryAdapter
    private var selectedInterest: String? = null

    private lateinit var addressPickerLauncher: ActivityResultLauncher<Intent>
    private var latitude = 0.0
    private var longitude = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateHightlightBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backbtn.setOnClickListener {
            finish()
        }


        binding.addressText.setOnClickListener {
            val intent = Intent(this, AddressPickerActivity::class.java)
            addressPickerLauncher.launch(intent)
        }

        val imageUri = intent.getStringExtra("uri")?.toUri()

        // Show image preview if available
        imageUri?.let { binding.postImage.setImageURI(it) }

        binding.buttonNext.setOnClickListener {
            if (validation()) {
                createThePost()
            }

        }

        fetchCategoryData()
        observeViewModel()
        getLocation()
        setupFieldValidation()
        observer()
    }

    private fun observer() {
        collectApiResultOnStarted(viewModel.hightlightPostResponse) {
            if (it.success) {
                showToast("Post created successfully")
                finish()
            }
        }
    }

    private fun createThePost() {
        val imageUri = intent.getStringExtra("uri")?.toUri()

        // Show image preview if available
        imageUri?.let { binding.postImage.setImageURI(it) }

        // Convert to File if exists
        val imageFile = imageUri?.let { FileUtils.uriToFile(this, it) }

        // Validate inputs before proceeding
        if (!validation()) return
        launchIfInternetAvailable {
            viewModel.hitHightLightPost(
                binding.titleEdittext.text.toString(),
                selectedInterest.toString(),
                binding.descriptionEdittext.text.toString(),
                binding.addressText.text.toString(),
                latitude.toString(),
                longitude.toString(),
                imageFile


            )
        }

    }

    private fun setupFieldValidation() {
        val watcher = {
            val isValid = binding.addressText.text.toString().isNotEmpty() &&
                    binding.titleEdittext.text.toString().isNotEmpty() &&
                    binding.descriptionEdittext.text.toString().isNotEmpty()

            binding.buttonNext.isEnabled = isValid
            binding.buttonNext.alpha = if (isValid) 1f else 0.5f  // optional visual feedback
        }

        // Add text change listeners
        binding.addressText.addTextChangedListener { watcher() }
        binding.titleEdittext.addTextChangedListener { watcher() }
        binding.descriptionEdittext.addTextChangedListener { watcher() }
    }


    private fun validation(): Boolean {
        if (binding.addressText.text.toString().isEmpty()) {
            showToast("Please select address")
        } else if (binding.titleEdittext.text.toString().isEmpty()) {
            showInputError(binding.titleEdittext, "Please enter title")
        } else if (binding.descriptionEdittext.text.toString().isEmpty()) {
            showInputError(binding.descriptionEdittext, "Please enter description")
        } else {
            return true
        }
        return false
    }

    private fun getLocation() {
        addressPickerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data

                val selectedaddress = data?.getStringExtra("selected_address")
                val selected_latitude = data?.getDoubleExtra("selected_latitude", 0.0)
                val selected_longitude = data?.getDoubleExtra("selected_longitude", 0.0)


                latitude = selected_latitude ?: 0.0
                longitude = selected_longitude ?: 0.0

                binding.addressText.text = selectedaddress

            }
        }
    }


    private fun observeViewModel() = with(viewModel) {
        collectApiResultOnStarted(categoryListResponse) { it ->
            if (it.status) {
                categoryList.clear()
                categoryList.addAll(it.categories)
                adapter = CommunityHightlightCategoryAdapter(
                    this@CreateHightlightActivity,
                    categoryList,
                    onItemClick = {
                        Log.d("category", it.toString())
                        selectedInterest = it
                    })

                binding.categoryRecyclerView.adapter = adapter
                binding.categoryRecyclerView.layoutManager = LinearLayoutManager(
                    this@CreateHightlightActivity,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )

            }
        }
    }

    private fun fetchCategoryData() {
        launchIfInternetAvailable {
            val request = GetCategoryRquest("0")
            viewModel.hitGetCategoryList(request)
        }
    }
}