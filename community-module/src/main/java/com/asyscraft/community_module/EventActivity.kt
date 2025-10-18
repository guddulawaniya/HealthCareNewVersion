package com.asyscraft.community_module

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.asyscraft.community_module.adpaters.UpcomingEventAdapter
import com.asyscraft.community_module.databinding.ActivityEventBinding
import com.asyscraft.community_module.viewModels.SocialMeetViewmodel
import com.careavatar.core_model.UpcomingTodayEventList
import com.careavatar.core_network.base.BaseActivity
import com.prolificinteractive.materialcalendarview.CalendarDay
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class EventActivity : BaseActivity() {
    private lateinit var binding: ActivityEventBinding
    private lateinit var adapter: UpcomingEventAdapter
    private  var eventList = mutableListOf<UpcomingTodayEventList>()
    private lateinit var selectedDate: String
    private val viewModel : SocialMeetViewmodel by viewModels()

    private val sdfFull = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    private val sdfDay = SimpleDateFormat("d", Locale.ENGLISH)
    private val sdfWeekday = SimpleDateFormat("E", Locale.ENGLISH)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        binding.btnAddEvent.setOnClickListener {
            val communityId =intent.getStringExtra("communityId").toString()
            startActivity(Intent(this, CreateEventActivity::class.java).putExtra("communityId",communityId))
        }

        binding.materialCalendarView.selectedDate = CalendarDay.today()

        // Optional: also highlight it
        binding.materialCalendarView.setDateSelected(CalendarDay.today(), true)

        setUpRecyclerView()

        selectedDate = sdfFull.format(Calendar.getInstance().time)

        binding.materialCalendarView.setOnDateChangedListener { widget, date, selected ->
            if (selected) {
                // date is a CalendarDay object
                val year = date.year
                val month = date.month + 1 // months are 0-indexed
                val day = date.day

                 selectedDate = "$year-$month-$day" // format as needed
                upComingEvent()
            }
        }


        observer()

    }

    override fun onResume() {
        super.onResume()
        upComingEvent()
    }
    private fun upComingEvent(){
        val communityId =intent.getStringExtra("communityId").toString()

        launchIfInternetAvailable {
            viewModel.getUpcomingEventList(communityId,selectedDate)

        }
    }
    private fun observer(){
        collectApiResultOnStarted(viewModel.upComingEventList){
            eventList.clear()
            eventList.addAll(it.events)
            adapter.notifyDataSetChanged()

        }
    }

    private fun setUpRecyclerView(){
        adapter = UpcomingEventAdapter(this, eventList,onItemClicked = {
            val intent = Intent(this, EventDetailsActivity::class.java)
            intent.putExtra("event", it)
            startActivity(intent)
        })
        binding.rvEvents.adapter = adapter
        binding.rvEvents.layoutManager = LinearLayoutManager(this)
    }
}