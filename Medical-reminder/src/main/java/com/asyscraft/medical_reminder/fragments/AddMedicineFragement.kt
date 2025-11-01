package com.asyscraft.medical_reminder.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.asyscraft.medical_reminder.R
import com.asyscraft.medical_reminder.adapters.TabsPagerAdapter
import com.asyscraft.medical_reminder.databinding.FragmentAddMedicineFragementBinding
import com.careavatar.core_network.base.BaseFragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddMedicineFragement : BaseFragment() {
    private lateinit var binding: FragmentAddMedicineFragementBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddMedicineFragementBinding.inflate(inflater, container, false)
        return binding.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = TabsPagerAdapter(requireActivity())
        binding.viewPager.adapter = adapter

        // Connect TabLayout with ViewPager2
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = adapter.getTitle(position)
        }.attach()
    }
}