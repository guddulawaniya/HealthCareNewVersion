package com.asyscraft.community_module

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import com.asyscraft.community_module.databinding.ActivityCreateCommunityBinding
import com.asyscraft.community_module.viewModels.SocialMeetViewmodel
import com.careavatar.core_model.CategoryPost
import com.careavatar.core_model.GetCategoryRquest
import com.careavatar.core_network.base.BaseActivity
import com.careavatar.core_utils.ImagePickerManager
import com.careavatar.core_utils.FileUtils
import dagger.hilt.android.AndroidEntryPoint
import java.io.File


@AndroidEntryPoint
class CreateCommunityActivity : BaseActivity() {
    private lateinit var binding: ActivityCreateCommunityBinding
    private val viewModel: SocialMeetViewmodel by viewModels()
    private var selectedImageUri: Uri? = null
    private var selectedImageFile: File? = null
    var selectedCategoryId: String? = null
    private val categoryList = mutableListOf<CategoryPost>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateCommunityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ImagePickerManager.init(this, this)
        binding.includedbtn.buttonNext.text = "Next"

        // Pick image
        binding.imagepicker.setOnClickListener {
            pickImage()
        }

        // Remove image
        binding.removebtn.setOnClickListener {
            clearSelectedImage()
        }

        // For community title
        binding.communityTitle.addTextChangedListener {
            updateNextButtonState()
        }

        binding.interests.addTextChangedListener {
            updateNextButtonState()
        }

        // This triggers when the user selects from dropdown
        binding.interests.setOnItemClickListener { parent, _, position, _ ->
            binding.interests.setText(parent.getItemAtPosition(position).toString())
            updateNextButtonState()
        }


        // Disable Next initially
        binding.includedbtn.buttonNext.isEnabled = false

        // Handle Next click
        binding.includedbtn.buttonNext.setOnClickListener {
            if (isValidInput()) {
                startActivity(Intent(this, CreateCommunityTypeActivity::class.java).apply {
                    putExtra("selectedImageUri", selectedImageUri.toString())
                    putExtra("communityTitle", binding.communityTitle.text.toString().trim())
                    putExtra("interests", selectedCategoryId)
                })
            }
        }

        fetchCategoryData()
        observeViewModel()
    }

    private fun updateNextButtonState() {
        val isTitleFilled = !binding.communityTitle.text.isNullOrBlank()
        val isInterestFilled = !binding.interests.text.isNullOrBlank()

        binding.includedbtn.buttonNext.isEnabled =
            isTitleFilled && isInterestFilled
    }


    private fun isValidInput(): Boolean {
        val title = binding.communityTitle.text.toString().trim()
        val interest = binding.interests.text.toString().trim()

        return when {
            title.isEmpty() -> {
                showInputError(binding.communityTitle, "Please enter a community title")
                false
            }

            interest.isEmpty() -> {
                showInputError(binding.interests, "Please select an interest")
                false
            }

            else -> true
        }
    }

    private fun observeViewModel() = with(viewModel) {
        collectApiResultOnStarted(categoryListResponse) { it ->
            if (it.status) {
                categoryList.clear()
                categoryList.addAll(it.categories)

                val adapter = ArrayAdapter(
                    this@CreateCommunityActivity,
                    com.careavatar.core_ui.R.layout.dropdown_item,
                    categoryList.map { it.name }
                )

                binding.interests.setAdapter(adapter)

                binding.interests.setOnClickListener {
                    binding.interests.showDropDown()
                }

                // Handle item selection
                binding.interests.setOnItemClickListener { parent, view, position, id ->
                    val selectedCategory = categoryList[position]
                    val selectedId = selectedCategory.id
                    val selectedName = selectedCategory.name

                    // Example: store or log it
                    Log.d("CreateCommunity", "Selected category: $selectedName (id=$selectedId)")

                    // You can also save it in ViewModel or a variable
                    selectedCategoryId = selectedId
                }
            }
        }
    }



    private fun fetchCategoryData() {
        launchIfInternetAvailable {
            val request = GetCategoryRquest("0")
            viewModel.hitGetCategoryList(request)
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
        updateNextButtonState()
    }


    private fun pickImage() {
        ImagePickerManager.showImageSourceDialog(this) { uri ->
            uri?.let {
                selectedImageUri = it
                selectedImageFile = FileUtils.uriToFile(this, it)

                binding.userProfile.setImageURI(it)
                binding.imagepicker.visibility = View.GONE
                binding.userProfile.visibility = View.VISIBLE
                binding.removebtn.visibility = View.VISIBLE

                updateNextButtonState()
            }
        }
    }


}