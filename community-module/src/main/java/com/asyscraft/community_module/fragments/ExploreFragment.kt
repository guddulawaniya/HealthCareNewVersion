package com.asyscraft.community_module.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.asyscraft.community_module.CommunityMessageActivity
import com.asyscraft.community_module.adpaters.ExploreCommunityAdapter
import com.asyscraft.community_module.databinding.FragmentExploreBinding
import com.asyscraft.community_module.viewModels.SocialMeetViewmodel
import com.careavatar.core_network.base.BaseFragment
import com.careavatar.core_ui.R
import com.careavatar.core_model.ExploreCommunityModel

import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue


@AndroidEntryPoint
class ExploreFragment : BaseFragment() {

    private lateinit var binding : FragmentExploreBinding
    private lateinit var adapter : ExploreCommunityAdapter
    private val datalist = mutableListOf<ExploreCommunityModel.CommunityData>()
    private val viewModel: SocialMeetViewmodel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExploreBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        hitAllCommunityList()
        observeViewModel()
    }

    private fun observeViewModel() = with(viewModel) {
        collectApiResultOnStarted(exploreCommunityModel) {
            datalist.clear()
            datalist.addAll(it.data)
            adapter.notifyDataSetChanged()

            if (datalist.isEmpty()) {
                binding.notFoundText.visibility = View.VISIBLE
                binding.exploreRecyclerView.visibility = View.GONE
            } else {
                binding.notFoundText.visibility = View.GONE
                binding.exploreRecyclerView.visibility = View.VISIBLE
            }
        }


    }

    private fun setUpRecyclerView(){
        adapter = ExploreCommunityAdapter(requireContext(), datalist, onItemClick = {array ->
            val memberIds = ArrayList<String>()

            for (member in array.members) {
                memberIds.add(member)
            }

            startActivity(Intent(requireContext(), CommunityMessageActivity::class.java)
                .putExtra("communityId", array._id)
                .putExtra("creatorId", array.creatorId)
                .putExtra("creatorName", array.creatorId)
                .putStringArrayListExtra("members", memberIds)
                .putExtra("communityName", array.name)
                .putExtra("communityImage", array.communityLogo)
                .putExtra("count", array.members.size.toString())
                .putExtra("type1", array.type))
        })
        binding.exploreRecyclerView.adapter = adapter
        binding.exploreRecyclerView.layoutManager = LinearLayoutManager(requireContext())

    }

    private fun hitAllCommunityList(){
        launchIfInternetAvailable {
            viewModel.hitAllCommunityList()
        }
    }
}