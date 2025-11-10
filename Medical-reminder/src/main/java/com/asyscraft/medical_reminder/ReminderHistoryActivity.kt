package com.asyscraft.medical_reminder

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.asyscraft.medical_reminder.adapters.ReminderHistoryAdapter
import com.asyscraft.medical_reminder.databinding.ActivityReminderHistoryBinding
import com.asyscraft.medical_reminder.viewModels.MedicalViewModel
import com.careavatar.core_model.medicalReminder.HistoryData
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReminderHistoryActivity : BaseActivity() {
    private lateinit var binding: ActivityReminderHistoryBinding
    private val viewModel: MedicalViewModel by viewModels()
    private val dataList = mutableListOf<HistoryData>()
    private lateinit var filterButtons: List<TextView>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReminderHistoryBinding.inflate(layoutInflater)

        binding.toolbar.btnBack.setOnClickListener { finish() }
        binding.toolbar.tvTitle.text = "History"

        setUpRecyclerview()
        hitReminderHistory()
        observeReminderHistory()

        filterButtons = listOf(
            binding.tvOneTime,
            binding.tvEveryday,
            binding.tvCustomdays,
            binding.tvMissed
        )


        // Set click listeners for all filters
        filterButtons.forEach { button ->
            button.setOnClickListener {
                updateSelectedFilter(button)
            }
        }
        updateSelectedFilter(binding.tvOneTime)

    }

    private fun updateSelectedFilter(selectedButton: TextView) {
        filterButtons.forEach { button ->
            if (button == selectedButton) {
                // Selected state
                button.setBackgroundResource(com.careavatar.core_ui.R.drawable.button_bg)
                button.setTextColor(ContextCompat.getColor(this, com.careavatar.core_ui.R.color.white))

                // Hit API based on selected filter
//                when (button.id) {
//                    R.id.tvOneTime -> hitReminderHistory("all")
//                    R.id.tvEveryday -> hitReminderHistory("taken")
//                    R.id.tvCustomdays -> hitReminderHistory("skipped")
//                    R.id.tvMissed -> hitReminderHistory("missed")
//                }
            } else {
                // Unselected state
                button.background = null
                button.setTextColor(ContextCompat.getColor(this, com.careavatar.core_ui.R.color.primaryColor))
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpRecyclerview(){
        binding.rvReminderHistory.adapter = ReminderHistoryAdapter(dataList)
        binding.rvReminderHistory.layoutManager = LinearLayoutManager(this)
    }

    private fun hitReminderHistory(type : String = "-1"){
        launchIfInternetAvailable {
            viewModel.hitGetReminderHistory(type)

        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun observeReminderHistory(){
        collectApiResultOnStarted(viewModel.getHistoryReminderResponse){
            if (it.data.isNotEmpty()){
                dataList.clear()
                dataList.addAll(it.data)
                binding.rvReminderHistory.adapter?.notifyDataSetChanged()
                if (dataList.isNotEmpty()){
                    binding.rvReminderHistory.visibility= View.VISIBLE
                    binding.emptyImageView.visibility = View.GONE
                }else {
                    binding.rvReminderHistory.visibility = View.GONE
                    binding.emptyImageView.visibility = View.VISIBLE
                }


            }

        }
    }
}