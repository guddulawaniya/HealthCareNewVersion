package com.asyscraft.community_module

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.asyscraft.community_module.adpaters.CommunityGroupMessageAdapter
import com.asyscraft.community_module.databinding.ActivityCommunityMessageBinding
import com.asyscraft.community_module.viewModels.SocialMeetViewmodel
import com.bumptech.glide.Glide
import com.careavatar.core_model.GroupChatMessageModel
import com.careavatar.core_network.base.BaseActivity
import com.careavatar.core_utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class CommunityMessageActivity : BaseActivity() {

    private lateinit var binding: ActivityCommunityMessageBinding
    private var isMenuVisible = false
    private val viewModel: SocialMeetViewmodel by viewModels()
    private lateinit var adapter: CommunityGroupMessageAdapter
    private val messages = mutableListOf<GroupChatMessageModel>()

    val currentUserId: Flow<String> get() = userPref.userId.map { it.toString() }

    private var communityId: String = ""
    private var communityName: String = ""
    private var communityImage: String = ""
    private var creatorId: String = ""
    private var creatorName: String = ""
    private var memberCount: String = ""
    private var members: List<String> = emptyList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommunityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)


        getGroupMessage()
        observer()
        handleBtn()
        setupRecyclerView()
        extractIntentData()
        setupCommunityDetailsUI()

    }
    private fun setupCommunityDetailsUI() {
        binding.tvName.text = communityName
        binding.totalMember.text = "($memberCount Members)"

        Glide.with(this)
            .load(Constants.IMAGE_BASEURL + communityImage)
            .placeholder(com.careavatar.core_ui.R.drawable.group_icon)
            .into(binding.ivProfile)
    }

    private fun extractIntentData() {

        communityId = intent.getStringExtra("communityId") ?: ""
        communityName = intent.getStringExtra("communityName") ?: ""
        communityImage = intent.getStringExtra("communityImage") ?: ""
        creatorId = intent.getStringExtra("creatorId") ?: ""
        creatorName = intent.getStringExtra("creatorName") ?: ""
        memberCount = intent.getStringExtra("count") ?: ""
        members = intent.getStringArrayListExtra("members") ?: emptyList()

        binding.firstmessage.text = "Welcome $creatorName to $communityName! Say Hi! "
    }

    private fun setupRecyclerView() {
        adapter = CommunityGroupMessageAdapter(messages, onEventJoinClick = {

        }, onEventConfirmClick = { data, type -> })

        binding.messageRv.apply {
            adapter = adapter
            layoutManager = LinearLayoutManager(this@CommunityMessageActivity)
        }
    }

    private fun handleBtn() {
        binding.backbtn.setOnClickListener {
            finish()
        }

        binding.ivEventIcon.setOnClickListener {
            startActivity(Intent(this, EventActivity::class.java)
                .putExtra("communityId",communityId))
        }

        binding.communitydetailLayout.setOnClickListener {
            startActivity(Intent(this, CommunityDetailsActivity::class.java).putExtra("communityId",communityId))
        }

        binding.plusbutton.setOnClickListener {
            if (isMenuVisible) hideMenu() else showMenu()
        }

        // Handle menu item clicks
        binding.documentChatLayout.setOnClickListener {
            hideMenu()
        }

        binding.imageChatLayout.setOnClickListener {
            hideMenu()
        }

        binding.audioChatLayout.setOnClickListener {
            hideMenu()
        }
    }

    private fun observer() {
        lifecycleScope.launch {
            val currentUserIdValue = currentUserId.first() // suspend function to get the value once

            collectApiResultOnStarted(viewModel.groupMessageResponse) { response ->

                messages.clear()
                messages.addAll(response.messages.map { msg ->
                    val isSender = msg.senderId._id == currentUserIdValue
                    val formattedDate = formatDate(msg.createdAt)

                    GroupChatMessageModel(
                        type = msg.type,
                        event = msg.eventId ?: "",
                        messageId = msg._id,
                        from = msg.senderId.toString(),
                        message = msg.message,
                        isSender = isSender,
                        messagetime = formattedDate,
                        profileimage = msg.senderId.avatar,
                        username = msg.senderId.name,
                        userId = msg.senderId._id,
                        mobilenumber = msg.senderId.phoneNumber
                    )

                })
            }
        }
    }


    private fun formatDate(utcDate: String): String {
        return try {
            // Use correct ISO 8601 parser with milliseconds and timezone
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")

            // Output format in IST
            val outputFormat = SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
            outputFormat.timeZone = TimeZone.getTimeZone("Asia/Kolkata")

            val date: Date? = inputFormat.parse(utcDate)
            date?.let { outputFormat.format(it) } ?: "Invalid Date"
        } catch (e: Exception) {
            e.printStackTrace()
            "Invalid Date"
        }
    }

    private fun getGroupMessage() {
        val communityId = intent.getStringExtra("communityId").toString()
        launchIfInternetAvailable {
            viewModel.getGroupMessage(communityId)
        }
    }


    private fun showMenu() {
        val slideUp =
            AnimationUtils.loadAnimation(this, com.careavatar.core_ui.R.anim.slide_up_fade_in)
        binding.selectImageMenuLayout.visibility = View.VISIBLE
        binding.selectImageMenuLayout.startAnimation(slideUp)
        isMenuVisible = true
    }

    private fun hideMenu() {
        val slideDown = AnimationUtils.loadAnimation(this, com.careavatar.core_ui.R.anim.slide_down_fade_out)
        binding.selectImageMenuLayout.startAnimation(slideDown)
        binding.selectImageMenuLayout.postDelayed({
            binding.selectImageMenuLayout.visibility = View.GONE
        }, 200)
        isMenuVisible = false
    }
}
