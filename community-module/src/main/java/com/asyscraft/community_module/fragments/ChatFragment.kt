package com.asyscraft.community_module.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.asyscraft.community_module.CommunityMessageActivity
import com.asyscraft.community_module.MessageActivity
import com.asyscraft.community_module.adpaters.ChatUsersAdapter
import com.asyscraft.community_module.databinding.FragmentChatBinding
import com.asyscraft.community_module.viewModels.SocialMeetViewmodel
import com.careavatar.core_model.ChatuserModel
import com.careavatar.core_model.UserChatListResponse
import com.careavatar.core_network.base.BaseFragment
import com.careavatar.core_ui.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.toString

@AndroidEntryPoint
class ChatFragment : BaseFragment() {
    private lateinit var binding: FragmentChatBinding
    private lateinit var adapter: ChatUsersAdapter
    private var dataList = mutableListOf<ChatuserModel>()
    private val viewModel: SocialMeetViewmodel by viewModels()
    var datalist = mutableListOf<UserChatListResponse.UserChatListResponseItem>()

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
        fetchUserChat()
        observer()
    }

    private fun observer(){
        collectApiResultOnStarted(viewModel.userChatListResponse){
            Log.d("userlistdata",it.toString())
            datalist.clear()
            datalist.addAll(it)
            adapter.notifyDataSetChanged()
//            adapter.filterList(datalist)
        }
    }

    private fun fetchUserChat(){

      launchIfInternetAvailable {
          lifecycleScope.launch {
              val userId = userPref.userId.first().toString() // suspend function for DataStore
              viewModel.hitGetChatList(userId)
          }

      }
    }

    private fun setupRecyclerView() {

        adapter = ChatUsersAdapter(requireContext(), dataList) {
            // Handle item click here
            startActivity(Intent(requireContext(), MessageActivity::class.java))
        }
        binding.chatRecyclerView.adapter = adapter
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())

    }

}