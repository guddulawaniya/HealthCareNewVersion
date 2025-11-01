package com.asyscraft.community_module

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import kotlin.getValue
import androidx.core.net.toUri
import com.asyscraft.community_module.databinding.ActivityCreateCommunityTypeBinding
import com.asyscraft.community_module.viewModels.SocialMeetViewmodel
import com.careavatar.core_network.base.BaseActivity
import com.careavatar.core_utils.FileUtils

@AndroidEntryPoint
class CreateCommunityTypeActivity : BaseActivity() {
    private lateinit var binding: ActivityCreateCommunityTypeBinding
    private var isPublicSelected = -1
    private val viewModel: SocialMeetViewmodel by viewModels()
    private var selectedImageFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateCommunityTypeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val selectedImage = intent.getStringExtra("selectedImageUri")

        selectedImage?.let { uriString ->
            val uri = uriString.toUri()
            selectedImageFile = FileUtils.uriToFile(this, uri)
        }



        binding.toolbar.btnBack.setOnClickListener { finish() }
        binding.toolbar.tvTitle.text = getString(com.careavatar.core_ui.R.string.create_community)


        binding.layoutPublic.setOnClickListener {
            isPublicSelected = 1
            updateSelection()
        }

        binding.layoutPrivate.setOnClickListener {
            isPublicSelected = 2
            updateSelection()
        }

        binding.addMemberBtn.setOnClickListener {
          startActivity(Intent(this, AddMemberCommunityActivity::class.java))
        }

        binding.includedbtn.buttonNext.setOnClickListener {
            hitCreateCommunity()
        }
        updateSelection()
        observeViewModel()
    }

    private fun observeViewModel() = with(viewModel) {
        collectApiResultOnStarted(createCommunityResponse) {
            if(it.status){
                startActivity(Intent(this@CreateCommunityTypeActivity, AddMemberCommunityActivity::class.java)
                    .putExtra("communityData",it.data))
            }

        }

    }

    private fun hitCreateCommunity() {
        launchIfInternetAvailable {
            if (!isValidInput()) return@launchIfInternetAvailable

            val name = intent.getStringExtra("communityTitle").orEmpty()
            val interestsId = intent.getStringExtra("interests").orEmpty()
            val type = if (isPublicSelected == 1) "public" else "private"

            viewModel.hitCreateCommunity(
                name = name,
                type = type,
                interestsId = interestsId,
                image = selectedImageFile
            )
        }
    }


    private fun isValidInput(): Boolean {
        val name = intent.getStringExtra("communityTitle").orEmpty()
        val interestsId = intent.getStringExtra("interests").orEmpty()
        var isValid = true

        when {
            name.isBlank() -> {
                showToast("Please enter a community name")
                isValid = false
            }
            interestsId.isBlank() -> {
                showToast("Please select at least one interest")
                isValid = false
            }
            isPublicSelected == -1 -> {
                showToast("Please select the community type")
                isValid = false
            }
            selectedImageFile == null -> {
                showToast("Please select a community image")
                isValid = false
            }
        }

        return isValid
    }




    private fun updateSelection() {
        if (isPublicSelected == 1) {
            // Public selected
            binding.layoutPublic.setBackgroundResource(com.careavatar.core_ui.R.drawable.selected_gender_bg)
            binding.layoutPrivate.setBackgroundResource(com.careavatar.core_ui.R.drawable.unselected_gender_bg)
            binding.imgPublic.isChecked = true
            binding.imgPrivate.isChecked = false
            binding.imgPublic.setBackgroundResource(com.careavatar.core_ui.R.drawable.selected_icon)
            binding.imgPrivate.setBackgroundResource(com.careavatar.core_ui.R.drawable.radio_btn_outline_bg)
        } else if (isPublicSelected == 2) {
            // Private selected
            binding.layoutPrivate.setBackgroundResource(com.careavatar.core_ui.R.drawable.selected_gender_bg)
            binding.layoutPublic.setBackgroundResource(com.careavatar.core_ui.R.drawable.unselected_gender_bg)
            binding.imgPrivate.isChecked = true
            binding.imgPublic.isChecked = false
            binding.imgPrivate.setBackgroundResource(com.careavatar.core_ui.R.drawable.selected_icon)
            binding.imgPublic.setBackgroundResource(com.careavatar.core_ui.R.drawable.radio_btn_outline_bg)
        }

        // ✅ Enable or disable Next button dynamically
        binding.includedbtn.buttonNext.isEnabled = isPublicSelected != -1
    }


}