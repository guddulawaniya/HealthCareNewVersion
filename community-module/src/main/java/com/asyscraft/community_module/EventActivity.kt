package com.asyscraft.community_module

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.asyscraft.community_module.adpaters.UpcomingEventAdapter
import com.asyscraft.community_module.databinding.ActivityEventBinding
import com.careavatar.core_model.UpcomingTodayEventList
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EventActivity : BaseActivity() {
    private lateinit var binding: ActivityEventBinding
    private lateinit var adapter: UpcomingEventAdapter
    private lateinit var eventList: MutableList<UpcomingTodayEventList>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        binding.btnAddEvent.setOnClickListener {
            startActivity(Intent(this, CreateEventActivity::class.java))
        }

        setUpRecyclerView()

    }

    private fun setUpRecyclerView(){
        adapter = UpcomingEventAdapter(this, eventList)
        binding.rvEvents.adapter = adapter
        binding.rvEvents.layoutManager = LinearLayoutManager(this)
    }
}