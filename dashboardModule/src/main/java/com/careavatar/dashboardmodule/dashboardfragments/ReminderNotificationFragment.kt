package com.careavatar.dashboardmodule.dashboardfragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.asyscraft.community_module.adpaters.ReminderNotificationAdapter
import com.asyscraft.community_module.databinding.FragmentReminderNotificationBinding
import com.careavatar.core_network.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ReminderNotificationFragment : BaseFragment() {
    private lateinit var binding : FragmentReminderNotificationBinding
    private lateinit var adapter : ReminderNotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReminderNotificationBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()

    }

    private fun setUpRecyclerView(){

        binding.reminderRecyclerview.layoutManager = LinearLayoutManager(requireContext())
    }

}