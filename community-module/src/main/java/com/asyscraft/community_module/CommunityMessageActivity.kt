package com.asyscraft.community_module

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.asyscraft.community_module.adpaters.CommunityGroupMessageAdapter
import com.asyscraft.community_module.callActivity.GroupAudioCallActivity
import com.asyscraft.community_module.databinding.ActivityCommunityMessageBinding
import com.asyscraft.community_module.socketManager.SocketManager
import com.asyscraft.community_module.socketManager.webrtc.CallViewModel
import com.asyscraft.community_module.viewModels.SocialMeetViewmodel
import com.bumptech.glide.Glide
import com.careavatar.core_model.GroupChatMessageModel
import com.careavatar.core_network.base.BaseActivity
import com.careavatar.core_utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class CommunityMessageActivity : BaseActivity() {

    private lateinit var binding: ActivityCommunityMessageBinding
    private var isMenuVisible = false
    private val viewModel: SocialMeetViewmodel by viewModels()
    private lateinit var messageAdapter: CommunityGroupMessageAdapter
    private val messages = mutableListOf<GroupChatMessageModel>()
    var currentUserId: String = ""
    private var communityId: String = ""
    private var communityName: String = ""
    private var communityImage: String = ""
    private var creatorId: String = ""
    private var creatorName: String = ""
    private var memberCount: String = ""
    private var members: List<String> = emptyList()
    private lateinit var callViewModel: CallViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommunityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        callViewModel = CallViewModel(applicationContext)
        lifecycleScope.launch {
            val userId = userPref.userId.first()
            currentUserId = userId.toString()

        }
        setupSocket()

        observer()
        handleBtn()
        setupRecyclerView()
        extractIntentData()
        setupCommunityDetailsUI()
        setupSendButton()
        observeGroupMessages()

    }

    override fun onResume() {
        super.onResume()
        getGroupMessage()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return super.dispatchTouchEvent(ev)
    }

    private fun observeGroupMessages() {
        lifecycleScope.launchWhenStarted {
            SocketManager.onGroupMessageReceived.collect { data ->
                Log.d("GroupChatActivity", "Collecting message: $data")

                val senderJson = data.optJSONObject("senderId") ?: return@collect
                val senderId = senderJson.optString("_id", "Unknown")
                val message = data.optString("message", "")
                val createdAt = data.optString("createdAt", "")

                val chatMessage = GroupChatMessageModel(
                    type = "text",
                    event = "",
                    messageId = data.optString("_id", ""),
                    from = senderId,
                    message = message,
                    isSender = senderId == currentUserId,
                    messagetime = formatDate(createdAt),
                    profileimage = senderJson.optString("avatar", ""),
                    username = senderJson.optString("name", "Anonymous"),
                    userId = senderId,
                    mobilenumber = senderJson.optString("phoneNumber", "")
                )

                withContext(Dispatchers.Main) {
                    messages.add(chatMessage)
                    messageAdapter.notifyItemInserted(messages.size - 1)
                    binding.messageRv.scrollToPosition(messages.lastIndex)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SocketManager.disconnect()
    }

    private fun setupSocket() {
        lifecycleScope.launch {
            currentUserId = userPref.userId.first() ?: ""
            SocketManager.connect(currentUserId)
            SocketManager.addUser(currentUserId)
            SocketManager.joinRoom(currentUserId,communityId)

        }
    }

    private fun setupSendButton() {
        binding.btnSend.setOnClickListener {
            val messageText = binding.etMessage.text.toString().trim()

            if (messageText.isNotEmpty()) {
                val messageJson = JSONObject().apply {
                    put("message", messageText)
                    put("from", currentUserId)
                    put("communityId", communityId)
                }
                SocketManager.sendGroundMessage(messageJson)
                binding.etMessage.text.clear()
            }
        }
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

        lifecycleScope.launch {
            userPref.user.collect { user ->
                Log.d("UserPref", "User name: ${user?.name}")
                val user = userPref.getUser()

                binding.firstmessage.text = "Welcome ${user?.name} to $communityName! Say Hi! "
            }
        }

    }

    private fun setupRecyclerView() {
        messageAdapter = CommunityGroupMessageAdapter(
            messages,
            onEventJoinClick = {

            }, onEventConfirmClick = { data, type ->

            })

        binding.messageRv.apply {
            layoutManager = LinearLayoutManager(this@CommunityMessageActivity).apply {
                stackFromEnd = true
            }
            adapter = messageAdapter
        }

    }


    private fun handleBtn() {
        val backbtn = intent.getBooleanExtra("createdCommunity",false)
        binding.backbtn.setOnClickListener {
            if (backbtn){
                navigateDashboard(2)
            }else
            {
                finish()
            }

        }

        binding.ivEventIcon.setOnClickListener {
            startActivity(
                Intent(this, EventActivity::class.java)
                    .putExtra("communityId", communityId)
            )
        }

        binding.audioCall.setOnClickListener {
            startActivity(
                Intent(this, GroupAudioCallActivity::class.java)
                    .putExtra("communityId", communityId)
            )
        }

        binding.communitydetailLayout.setOnClickListener {
            startActivity(
                Intent(this, CommunityDetailsActivity::class.java).putExtra(
                    "communityId",
                    communityId
                )
            )
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

            collectApiResultOnStarted(viewModel.groupMessageResponse) { response ->
                Log.d("GroupChat", "Response messages count: ${response.messages.size}")
                messages.clear()
                messages.addAll(response.messages.map { msg ->
                    val isSender = msg.senderId._id == currentUserId
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

                Log.d("GroupChatMessage", "Response messages count: $messages")
                messageAdapter.notifyDataSetChanged()
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
        val slideDown =
            AnimationUtils.loadAnimation(this, com.careavatar.core_ui.R.anim.slide_down_fade_out)
        binding.selectImageMenuLayout.startAnimation(slideDown)
        binding.selectImageMenuLayout.postDelayed({
            binding.selectImageMenuLayout.visibility = View.GONE
        }, 200)
        isMenuVisible = false
    }
}
