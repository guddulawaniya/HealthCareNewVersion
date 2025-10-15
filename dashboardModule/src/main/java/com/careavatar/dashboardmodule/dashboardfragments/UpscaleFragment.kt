package com.careavatar.dashboardmodule.dashboardfragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.asyscraft.upscale_module.adapters.ViewPagerAdapterUpscale
import com.careavatar.dashboardmodule.databinding.FragmentUpscaleBinding
import com.careavatar.core_network.base.BaseFragment

import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpscaleFragment : BaseFragment() {
    private lateinit var binding: FragmentUpscaleBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUpscaleBinding.inflate(inflater, container, false)

        // Setup ViewPager with adapter
        val adapter = ViewPagerAdapterUpscale(requireActivity())
        binding.viewPager.adapter = adapter

        // Attach TabLayout with ViewPager
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Upload Item"
                1 -> tab.text = "Browse Items"
            }
        }.attach()

        return binding.root
    }

}