package com.asyscraft.community_module

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.asyscraft.community_module.adpaters.LeaveCommunityMemberListAdapter
import com.asyscraft.community_module.databinding.ActivityLeaveCommunityBinding
import com.asyscraft.community_module.viewModels.SocialMeetViewmodel
import com.careavatar.core_model.CommnityMemberListResponse
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LeaveCommunityActivity : BaseActivity() {
    private lateinit var binding: ActivityLeaveCommunityBinding
    private val viewModel: SocialMeetViewmodel by viewModels()
    private var userId: String? = null
    private var isCurrentUserAdmin: Boolean = false
    private var creatorId: String? = null
    private val memberList = mutableListOf<CommnityMemberListResponse.CommnityMemberListResponseItem.Community.Member>()
    private lateinit var memeberlistcommunity: CommnityMemberListResponse.CommnityMemberListResponseItem.Community
    private lateinit var adapterMember: LeaveCommunityMemberListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaveCommunityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            userId = userPref.userId.first()
        }
        binding.leaveGroupbtn.buttonNext.text= "Transfer & Leave"
        binding.leaveGroupbtn.buttonNext.setOnClickListener {
            hitLeaveCommunity()

        }

        binding.backbtn.setOnClickListener { finish() }
        setUpRecyclerViewMember()
        hitGetCommunityMember()
        setupSearchFilter()
        observer()

    }


    private fun hitLeaveCommunity() {
        val communityId = intent.getStringExtra("communityId").toString()

        launchIfInternetAvailable {
            viewModel.hitLeaveCommunity(communityId)

        }
    }

    private fun setupSearchFilter() {
        binding.customSearchbar.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                val filteredCategories = if (query.isEmpty()) {
                    memberList
                } else {
                    memberList.filter { it.name.contains(query, ignoreCase = true) }
                }
                adapterMember.updateList(filteredCategories.toMutableList())

            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setUpRecyclerViewMember() {


        adapterMember = LeaveCommunityMemberListAdapter(this, userId.toString(), memberList) { item ->

        }
        binding.communityMemberRv.layoutManager = LinearLayoutManager(this)
        binding.communityMemberRv.adapter = adapterMember
    }

    private fun observer() {


        collectApiResultOnStarted(viewModel.deleteCommunitiesResponse) {
            val item = adapterMember.getSelectedUser()
            startActivity(Intent(this, CommunityCreatedActivity::class.java)
                .putExtra("leaveCommunity",true)
                .putExtra("adminName", item?.name)
            )
        }


        collectApiResultOnStarted(viewModel.commnityMemberListResponse) { it ->

            memeberlistcommunity = it[0].community

            val community = it[0].community
            val hobbies = community.category.name
             creatorId = community.creators._id
             isCurrentUserAdmin = creatorId == userId

            binding.tvTitle.text = "Leaving “ ${community.name} “"

            memberList.clear()

            val sortedMembers = community.members
                .distinctBy { it._id } // remove duplicates by _id
                .sortedWith(compareByDescending { it._id == userId })

            memberList.addAll(sortedMembers)

            adapterMember.updateList(newList = memberList,)

        }

    }

    private fun hitGetCommunityMember() {
        val communityId = intent.getStringExtra("communityId").toString()

        launchIfInternetAvailable {
            viewModel.hitGetCommunityMember(communityId)

        }
    }
}