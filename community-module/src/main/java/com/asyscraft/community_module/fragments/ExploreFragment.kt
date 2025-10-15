package com.asyscraft.community_module.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.asyscraft.community_module.adpaters.ExploreCommunityAdapter
import com.asyscraft.community_module.databinding.FragmentExploreBinding
import com.careavatar.core_network.base.BaseFragment
import com.careavatar.core_ui.R
import com.careavatar.userapploginmodule.model.ExploreCommunityModel

import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ExploreFragment : BaseFragment() {

    private lateinit var binding : FragmentExploreBinding
    private lateinit var adapter : ExploreCommunityAdapter
    private val datalist = mutableListOf<ExploreCommunityModel>()

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
    }

    private fun setUpRecyclerView(){

        datalist.add(ExploreCommunityModel("Women’s Wellness Circle", "Interest: Sunrise Yoga Routines & Mindfulness", true, R.drawable.group_sample))
        datalist.add(ExploreCommunityModel("Women’s Wellness Circle", "Interest: Sunrise Yoga Routines & Mindfulness", false, R.drawable.sample_image))
        datalist.add(ExploreCommunityModel("Women’s Wellness Circle", "Interest: Sunrise Yoga Routines & Mindfulness", false, R.drawable.sample_image))
        datalist.add(ExploreCommunityModel("Women’s Wellness Circle", "Interest: Sunrise Yoga Routines & Mindfulness", false, R.drawable.sample_image))
        datalist.add(ExploreCommunityModel("Women’s Wellness Circle", "Interest: Sunrise Yoga Routines & Mindfulness", false, R.drawable.sample_image))
        datalist.add(ExploreCommunityModel("Women’s Wellness Circle", "Interest: Sunrise Yoga Routines & Mindfulness", false, R.drawable.sample_image))
        datalist.add(ExploreCommunityModel("Women’s Wellness Circle", "Interest: Sunrise Yoga Routines & Mindfulness", false, R.drawable.sample_image))


        adapter = ExploreCommunityAdapter(requireContext(), datalist)
        binding.exploreRecyclerView.adapter = adapter
        binding.exploreRecyclerView.layoutManager = LinearLayoutManager(requireContext())

    }
}