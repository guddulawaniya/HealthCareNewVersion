package com.asyscraft.community_module

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.asyscraft.community_module.adpaters.GalleryImageAdapter
import com.asyscraft.community_module.adpaters.UserDetailsCommunityAdapter
import com.asyscraft.community_module.databinding.ActivityChatDetailsBinding
import com.asyscraft.community_module.viewModels.SocialMeetViewmodel
import com.bumptech.glide.Glide
import com.careavatar.core_network.base.BaseActivity
import com.careavatar.core_utils.Constants.Companion.IMAGE_BASEURL
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityChatDetailsBinding
    private lateinit var imageAdapter: GalleryImageAdapter
    private val viewModel: SocialMeetViewmodel by viewModels()
    private lateinit var adapter: UserDetailsCommunityAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpImageRecyclerView()
        binding.gallerybtn.setOnClickListener {
            startActivity(Intent(this@ChatDetailsActivity, SharedGalleryActcivity::class.java))
        }
        binding.communityCreateBtn.setOnClickListener {
            startActivity(Intent(this@ChatDetailsActivity, CreateCommunityActivity::class.java))
        }
        hitUserDetailsbyId()
        observer()
    }

    private fun observer() {
        collectApiResultOnStarted(viewModel.userDetailsbyIdResponse) {
            Log.d("datasocialmeetchat", it.data.toString())

            val user = it.user

            binding.profileName.text = user.name.orEmpty()
//            binding.mobilenumber.text = user.phoneNumber.orEmpty()
//            status = it.user.status
//            handleStatus(it.user.status)

            Glide.with(this)
                .load(IMAGE_BASEURL + user.avatar)
                .placeholder(com.careavatar.core_ui.R.drawable.profile_1)
                .into(binding.profile)

            val communityList = it.data

            if (!communityList.isNullOrEmpty()) {
                adapter = UserDetailsCommunityAdapter(this, communityList.toMutableList())
                binding.rvMutualCommunity.adapter = adapter
            } else {
                // Optionally handle empty state
                binding.rvMutualCommunity.adapter = null
            }

        }
    }

    private fun hitUserDetailsbyId() {
        val userId = intent.getStringExtra("userId").toString()

        launchIfInternetAvailable {
            viewModel.hitUserDetailsbyId(userId)

        }
    }

    private fun setUpImageRecyclerView() {
        val imageDataList = mutableListOf<Int>()
        imageDataList.add(com.careavatar.core_ui.R.drawable.sample_image)
        imageDataList.add(com.careavatar.core_ui.R.drawable.group_sample)
        imageDataList.add(com.careavatar.core_ui.R.drawable.service_userprofile_image_sample)
        imageDataList.add(com.careavatar.core_ui.R.drawable.sample_image)

        imageAdapter = GalleryImageAdapter(imageDataList)
        binding.shareGalleryRecyclerview.adapter = imageAdapter
        binding.shareGalleryRecyclerview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }
}