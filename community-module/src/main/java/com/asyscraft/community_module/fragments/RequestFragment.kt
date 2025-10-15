package com.asyscraft.community_module.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.asyscraft.community_module.adpaters.RequestChatUsersAdapter
import com.asyscraft.community_module.databinding.FragmentRequestBinding
import com.careavatar.core_model.RequestChatUserModel
import com.careavatar.core_network.base.BaseFragment
import com.careavatar.core_ui.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RequestFragment : BaseFragment() {
    private lateinit var binding : FragmentRequestBinding
    private lateinit var adapter: RequestChatUsersAdapter
    private var dataList = mutableListOf<RequestChatUserModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRequestBinding.inflate(inflater, container, false)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }
    private fun setupRecyclerView() {
        dataList.add(
            RequestChatUserModel(
                "Mikey Janes",
                "Hey, can we connect?",
                "09:00 AM",
                R.drawable.sample_image,
                "4"
            )
        )
        dataList.add(
            RequestChatUserModel(
                "Mikey Janes",
                "Hey, can we connect?",
                "09:00 AM",
                R.drawable.sample_image,
                "4"
            )
        )

        adapter = RequestChatUsersAdapter(requireContext(), dataList) { item, accept ->
            // Handle item click here
        }
        binding.chatRecyclerView.adapter = adapter
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())


    }

}