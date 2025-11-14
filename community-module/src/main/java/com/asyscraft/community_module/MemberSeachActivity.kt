package com.asyscraft.community_module

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.asyscraft.community_module.adpaters.LeaveCommunityMemberListAdapter
import com.asyscraft.community_module.databinding.ActivityMemberSeachBinding
import com.asyscraft.community_module.viewModels.SocialMeetViewmodel
import com.careavatar.core_model.CommnityMemberListResponse
import com.careavatar.core_network.base.BaseActivity
import com.careavatar.core_utils.setupSearchFilter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class MemberSeachActivity : BaseActivity() {
    private lateinit var binding: ActivityMemberSeachBinding
    private lateinit var adapterMember: LeaveCommunityMemberListAdapter
    private val memberList = mutableListOf<CommnityMemberListResponse.CommnityMemberListResponseItem.Community.Member>()
    private val viewModel: SocialMeetViewmodel by viewModels()
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMemberSeachBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            userId = userPref.userId.first()
        }

        val isParticipants = intent.getBooleanExtra("isParticipants", false)

        if (isParticipants) {
            binding.toolbar.tvTitle.text ="Participants"
        }else{
            binding.toolbar.tvTitle.text ="Search"
        }

        binding.toolbar.btnBack.setOnClickListener { finish() }
        setUpRecyclerViewMember()
        hitGetCommunityMember()
        setupSearchFilter()
        observer()



    }



    private fun setupSearchFilter() {

        binding.customSearchbar.etSearch.setupSearchFilter(
            memberList,
            filterCondition = { item, query ->
                item.name?.contains(query, ignoreCase = true) == true
            }
        ) { filteredList ->
            adapterMember.updateList(filteredList.toMutableList())
        }

    }


    private fun setUpRecyclerViewMember() {


        adapterMember = LeaveCommunityMemberListAdapter(this, userId.toString(), memberList,true) { dataList ->
            startActivity(Intent(this, ChatDetailsActivity::class.java).putExtra("userId", dataList._id))
        }
        binding.communityMemberRv.layoutManager = LinearLayoutManager(this)
        binding.communityMemberRv.adapter = adapterMember
    }

    private fun observer() {


        collectApiResultOnStarted(viewModel.commnityMemberListResponse) { it ->


            val community = it[0].community

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