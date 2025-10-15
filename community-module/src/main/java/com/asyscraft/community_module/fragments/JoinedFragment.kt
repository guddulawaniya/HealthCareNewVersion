package com.asyscraft.community_module.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.asyscraft.community_module.adpaters.SearchCommunityAdapter
import com.asyscraft.community_module.databinding.FragmentJoinedBinding
import com.asyscraft.community_module.viewModels.SocialMeetViewmodel
import com.careavatar.core_model.SearchCommunityResponse
import com.careavatar.core_network.base.BaseFragment
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.asyscraft.community_module.CommunityMessageActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class JoinedFragment : BaseFragment() {
    private lateinit var binding: FragmentJoinedBinding
    private lateinit var joinedAdapter: SearchCommunityAdapter
    private val dataList = mutableListOf<SearchCommunityResponse.Community>()
    private val viewModel: SocialMeetViewmodel by viewModels()
    private var searchDataList: MutableList<SearchCommunityResponse.Community> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentJoinedBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclerview()
        hitCommunityList()
        observeViewModel()
    }



    private fun setRecyclerview() {

        joinedAdapter = SearchCommunityAdapter(requireContext(), dataList, onItemClick = { array ->

            val memberIds = ArrayList<String>()

            for (member in array.members) {
                memberIds.add(member)
            }

            startActivity(Intent(requireContext(), CommunityMessageActivity::class.java)
                .putExtra("communityId", array._id)
                    .putExtra("creatorId", array.creator)
                    .putStringArrayListExtra("members", memberIds)
                    .putExtra("communityName", array.name)
                    .putExtra("communityImage", array.communityLogo)
                    .putExtra("count", array.memberCount.toString())
                    .putExtra("type1", array.type)

            )
        })
        binding.joinedRecyclervew.layoutManager = LinearLayoutManager(requireContext())
        binding.joinedRecyclervew.adapter = joinedAdapter
    }


    private fun observeViewModel() = with(viewModel) {
        collectApiResultOnStarted(searchCommunityResponse) {
            searchDataList.clear()
            searchDataList.addAll(it.communities.toMutableList())
            joinedAdapter.filterList(searchDataList)

//            if (searchDataList.isEmpty()) {
//                binding.nofounddata.visibility = View.VISIBLE
//                binding.communityListRecycler.visibility = View.GONE
//            } else {
//                binding.nofounddata.visibility = View.GONE
//                binding.communityListRecycler.visibility = View.VISIBLE
//            }
        }


    }

    private fun hitCommunityList(){
        launchIfInternetAvailable {
            viewModel.hitSearchCategory("")
        }
    }
}