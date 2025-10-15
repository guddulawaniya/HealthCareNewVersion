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
    private var isPublicSelected = true
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
            isPublicSelected = true
            updateSelection()
        }

        binding.layoutPrivate.setOnClickListener {
            isPublicSelected = false
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
            startActivity(Intent(this@CreateCommunityTypeActivity, CommunityCreatedActivity::class.java).putExtra("communityId",it.data._id))
        }

    }


    private fun hitCreateCommunity() {
        launchIfInternetAvailable {
            val name = intent.getStringExtra("communityTitle").toString()
            val interestsId = intent.getStringExtra("interests").toString()
            val type = if (isPublicSelected) "public" else "private"

            viewModel.hitCreateCommunity(name,type,interestsId,selectedImageFile)
        }
    }



    private fun updateSelection() {
        if (isPublicSelected) {
            // Public selected
            binding.layoutPublic.setBackgroundResource(com.careavatar.core_ui.R.drawable.selected_gender_bg)
            binding.layoutPrivate.setBackgroundResource(com.careavatar.core_ui.R.drawable.unselected_gender_bg)

            binding.imgPublic.isChecked = true
            binding.imgPrivate.isChecked = false

            binding.imgPublic.setBackgroundResource(com.careavatar.core_ui.R.drawable.selected_icon)
            binding.imgPrivate.setBackgroundResource(com.careavatar.core_ui.R.drawable.radio_btn_outline_bg)
        } else {
            // Private selected
            binding.layoutPrivate.setBackgroundResource(com.careavatar.core_ui.R.drawable.selected_gender_bg)
            binding.layoutPublic.setBackgroundResource(com.careavatar.core_ui.R.drawable.unselected_gender_bg)

            binding.imgPrivate.isChecked = true
            binding.imgPublic.isChecked = false

            binding.imgPrivate.setBackgroundResource(com.careavatar.core_ui.R.drawable.selected_icon)
            binding.imgPublic.setBackgroundResource(com.careavatar.core_ui.R.drawable.radio_btn_outline_bg)
        }
    }

}