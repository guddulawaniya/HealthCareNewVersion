package com.asyscraft.community_module

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.asyscraft.community_module.adpaters.CommunityMemberListAdapter
import com.asyscraft.community_module.adpaters.EventAdapter
import com.asyscraft.community_module.adpaters.GalleryImageAdapter
import com.asyscraft.community_module.databinding.ActivityCommunityProfileActivtiyBinding
import com.asyscraft.community_module.viewModels.SocialMeetViewmodel
import com.bumptech.glide.Glide
import com.careavatar.core_model.AddToCommunityRequest
import com.careavatar.core_model.CommnityMemberListResponse
import com.careavatar.core_model.UpcomingTodayEventList
import com.careavatar.core_network.base.BaseActivity
import com.careavatar.core_utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class CommunityDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityCommunityProfileActivtiyBinding
    private var eventList = mutableListOf<UpcomingTodayEventList>()
    private val viewModel: SocialMeetViewmodel by viewModels()
    private lateinit var adapter: EventAdapter
    private lateinit var adapterMember: CommunityMemberListAdapter
    private val sdfFull = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    private lateinit var memeberlistcommunity: CommnityMemberListResponse.CommnityMemberListResponseItem.Community
    private lateinit var imageAdapter: GalleryImageAdapter
    private var userId: String? = null
    var userSelectedId: ArrayList<String> = ArrayList()
    private val memberList =
        mutableListOf<CommnityMemberListResponse.CommnityMemberListResponseItem.Community.Member>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommunityProfileActivtiyBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.backbtn.setOnClickListener { finish() }

        lifecycleScope.launch {
            userId = userPref.userId.first()
        }

        setUpRecyclerViewMember()
        setUpRecyclerView()
        setupHandleBtn()
        observer()
        setUpImageRecyclerView()


    }

    override fun onResume() {
        super.onResume()
        hitGetCommunityMember()
        upComingEvent()
    }

    private fun upComingEvent() {
        val communityId = intent.getStringExtra("communityId").toString()

        val selectedDate = sdfFull.format(Calendar.getInstance().time)
        launchIfInternetAvailable {
            viewModel.getUpcomingEventList(communityId, selectedDate)

        }
    }

    private fun hitLeaveCommunity() {
        val communityId = intent.getStringExtra("communityId").toString()

        launchIfInternetAvailable {
            viewModel.hitLeaveCommunity(communityId)

        }
    }

    private fun hitGetCommunityMember() {
        val communityId = intent.getStringExtra("communityId").toString()

        launchIfInternetAvailable {
            viewModel.hitGetCommunityMember(communityId)

        }
    }

    private fun hitDeleteCommunity() {
        val communityId = intent.getStringExtra("communityId").toString()

        launchIfInternetAvailable {
            viewModel.hitDeleteCommunity(communityId)

        }
    }

    private fun observer() {
        collectApiResultOnStarted(viewModel.upComingEventList) {
            eventList.clear()
            eventList.addAll(it.events)
            adapter.notifyDataSetChanged()
            if (eventList.isEmpty()) {
                binding.tvUpcomingEvents.visibility = View.GONE
                binding.rvEvents.visibility = View.GONE
            } else {
                binding.tvUpcomingEvents.visibility = View.VISIBLE
                binding.rvEvents.visibility = View.VISIBLE
            }

        }

        collectApiResultOnStarted(viewModel.deleteCommunitiesResponse) {
            finish()
        }

        collectApiResultOnStarted(viewModel.commnityMemberListResponse) { it ->

            memeberlistcommunity = it[0].community

            val community = it[0].community
            val hobbies = community.category.name
            val creatorId = community.creators._id
            val isCurrentUserAdmin = creatorId == userId

            Glide.with(this@CommunityDetailsActivity)
                .load(Constants.IMAGE_BASEURL + community.communityLogo) // full URL
                .placeholder(com.careavatar.core_ui.R.drawable.profile_1) // optional placeholder while loading
                .error(com.careavatar.core_ui.R.drawable.profile_1)             // optional error image if loading fails
                .centerCrop()                              // scale type, optional
                .into(binding.communityImage)


            binding.communityName.text = memeberlistcommunity.name
            binding.tvTotalMember.text = memeberlistcommunity.members.size.toString() + " members"

            if (userId == community.creators._id) {
                binding.addparticipantbtn.visibility = View.VISIBLE
                binding.editbtn.visibility = View.VISIBLE
            }

            memberList.clear()

            val sortedMembers = community.members
                .distinctBy { it._id } // remove duplicates by _id
                .sortedWith(compareByDescending { it._id == userId })

            memberList.addAll(sortedMembers)

            adapterMember.updateList(
                newArray = memberList,
                creatorId = creatorId,
                isCurrentUserAdmin = isCurrentUserAdmin,
                currentUserId = userId.toString()
            )

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

    private fun hitAddToCommunity() {
        launchIfInternetAvailable {
            val request = AddToCommunityRequest(
                memeberlistcommunity._id,
                userSelectedId
            )
            viewModel.hitAddToCommunity(request)

        }
    }

    private fun setUpRecyclerViewMember() {

        adapterMember = CommunityMemberListAdapter(this, userId.toString(), memberList) { position ->
                userSelectedId.add(memberList[position]._id)
                memberList.removeAt(position)
                adapter.notifyDataSetChanged()
                hitAddToCommunity()
            }
        binding.communityMemberRv.layoutManager = LinearLayoutManager(this)
        binding.communityMemberRv.adapter = adapterMember
    }

    private fun setUpRecyclerView() {
        adapter = EventAdapter(this, eventList, onItemClicked = {
            val intent = Intent(this, EventDetailsActivity::class.java)
            intent.putExtra("eventId", it._id)
            startActivity(intent)
        })
        binding.rvEvents.adapter = adapter
        binding.rvEvents.layoutManager = LinearLayoutManager(this , LinearLayoutManager.HORIZONTAL,false)
    }

    private fun setupHandleBtn() {
        val communityId = intent.getStringExtra("communityId").toString()

        binding.leaveGroup.setOnClickListener {
            startActivity(
                Intent(
                    this@CommunityDetailsActivity,
                    LeaveCommunityActivity::class.java
                ).putExtra("communityId", memeberlistcommunity._id)
            )
//            hitLeaveCommunity()
        }

        binding.callbtn.setOnClickListener {
            startActivity(Intent(this@CommunityDetailsActivity, IncomingCallActivity::class.java))
        }

        binding.searchbtn.setOnClickListener {
            startActivity(Intent(this@CommunityDetailsActivity, MemberSeachActivity::class.java).putExtra("communityId",communityId))
        }
        binding.participantsbtn.setOnClickListener {
            startActivity(Intent(this@CommunityDetailsActivity, MemberSeachActivity::class.java).putExtra("communityId",communityId))
        }
        binding.tvUpcomingEvents.setOnClickListener {
            startActivity(Intent(this@CommunityDetailsActivity, EventActivity::class.java)
                .putExtra("communityId",communityId))
        }

        binding.gallerybtn.setOnClickListener {
            startActivity(Intent(this@CommunityDetailsActivity, SharedGalleryActcivity::class.java))
        }

        binding.editbtn.setOnClickListener {
            startActivity(
                Intent(
                    this@CommunityDetailsActivity,
                    CreateCommunityActivity::class.java
                )
            )
        }

        binding.addparticipantbtn.setOnClickListener {
            startActivity(
                Intent(
                    this@CommunityDetailsActivity,
                    AddMemberCommunityActivity::class.java
                ).putExtra(
                    "communityId",
                    communityId,

                    ).putExtra(
                    "AddMember",
                    true

                )
            )
        }

        binding.videocallbtn.setOnClickListener {
            startActivity(Intent(this@CommunityDetailsActivity, VideoCallActivity::class.java))
        }

        binding.eventbtn.setOnClickListener {

            startActivity(
                Intent(this@CommunityDetailsActivity, EventActivity::class.java).putExtra(
                    "communityId",
                    communityId
                )
            )
        }

    }
}