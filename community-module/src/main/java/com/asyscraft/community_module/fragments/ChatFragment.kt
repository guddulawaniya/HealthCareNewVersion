package com.asyscraft.community_module.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.asyscraft.community_module.CommunityMessageActivity
import com.asyscraft.community_module.adpaters.ChatUsersAdapter
import com.asyscraft.community_module.databinding.FragmentChatBinding
import com.careavatar.core_model.ChatuserModel
import com.careavatar.core_network.base.BaseFragment
import com.careavatar.core_ui.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : BaseFragment() {
    private lateinit var binding: FragmentChatBinding
    private lateinit var adapter: ChatUsersAdapter
    private var dataList = mutableListOf<ChatuserModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {

        dataList.add(
            ChatuserModel(
                "Mikey Janes",
                "Hey, can we connect?",
                "09:00 AM",
                R.drawable.sample_image,
                "4"
            )
        )
        dataList.add(
            ChatuserModel(
                "Mikey Janes",
                "Hey, can we connect?",
                "09:00 AM",
                R.drawable.sample_image,
                "4"
            )
        )
        dataList.add(
            ChatuserModel(
                "Mikey Janes",
                "Hey, can we connect?",
                "09:00 AM",
                R.drawable.sample_image,
                "4"
            )
        )
        dataList.add(
            ChatuserModel(
                "Mikey Janes",
                "Hey, can we connect?",
                "09:00 AM",
                R.drawable.sample_image,
                "4"
            )
        )
        dataList.add(
            ChatuserModel(
                "Mikey Janes",
                "Hey, can we connect?",
                "09:00 AM",
                R.drawable.sample_image,
                "4"
            )
        )
        dataList.add(
            ChatuserModel(
                "Mikey Janes",
                "Hey, can we connect?",
                "09:00 AM",
                R.drawable.sample_image,
                "4"
            )
        )
        dataList.add(
            ChatuserModel(
                "Mikey Janes",
                "Hey, can we connect?",
                "09:00 AM",
                R.drawable.sample_image,
                "4"
            )
        )
        dataList.add(
            ChatuserModel(
                "Mikey Janes",
                "Hey, can we connect?",
                "09:00 AM",
                R.drawable.sample_image,
                "4"
            )
        )

        adapter = ChatUsersAdapter(requireContext(), dataList) {
            // Handle item click here
            startActivity(Intent(requireContext(), CommunityMessageActivity::class.java))
        }
        binding.chatRecyclerView.adapter = adapter
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())

    }

}