package com.asyscraft.community_module.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.asyscraft.community_module.adpaters.CallHistoryListAdapter
import com.asyscraft.community_module.databinding.FragmentAllCallHistoryBinding
import com.careavatar.core_model.CallHistoryModel
import com.careavatar.core_network.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AllCallHistoryFragment : BaseFragment() {
    private lateinit var binding: FragmentAllCallHistoryBinding
    private lateinit var adapter: CallHistoryListAdapter
    private val datalist = mutableListOf<CallHistoryModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAllCallHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecyclerview()

    }

    private fun setRecyclerview() {
        datalist.add(
            CallHistoryModel(
                "1", "Sophia Carter", "10:45 AM", "Missed", "Audio",
                com.careavatar.core_ui.R.drawable.service_userprofile_image_sample
            )
        )
        datalist.add(
            CallHistoryModel(
                "2", "Ethan Carter", "Yesterday", "Incoming", "Audio",
                com.careavatar.core_ui.R.drawable.profile_1
            )
        )
        datalist.add(
            CallHistoryModel(
                "3", "Ethan Carter", "Yesterday", "Outgoing", "Video",
                com.careavatar.core_ui.R.drawable.profile_1
            )
        )
        datalist.add(
            CallHistoryModel(
                "3", "Ethan Carter", "Yesterday", "Missed Video", "Video",
                com.careavatar.core_ui.R.drawable.profile_1
            )
        )

        adapter = CallHistoryListAdapter(requireContext(), datalist)
        binding.callHistoryRecylerview.adapter = adapter
        binding.callHistoryRecylerview.layoutManager = LinearLayoutManager(requireContext())
    }

}