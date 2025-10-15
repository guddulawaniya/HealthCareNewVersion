package com.careavatar.dashboardmodule.dashboardfragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.careavatar.core_network.base.BaseFragment
import com.careavatar.dashboardmodule.databinding.FragmentAnnouncementNotificationBinding

import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AnnouncementNotificationFragment : BaseFragment() {
    private lateinit var binding : FragmentAnnouncementNotificationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAnnouncementNotificationBinding.inflate(inflater, container, false)


        return binding.root
    }

}