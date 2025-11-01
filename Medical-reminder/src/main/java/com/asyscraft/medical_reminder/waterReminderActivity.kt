package com.asyscraft.medical_reminder

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.asyscraft.medical_reminder.adapters.DateGroupedHistory
import com.asyscraft.medical_reminder.adapters.WaterLogAdapter
import com.asyscraft.medical_reminder.databinding.ActivityWaterReminderBinding
import com.asyscraft.medical_reminder.viewModels.MedicalViewModel
import com.careavatar.core_model.medicalReminder.History
import com.careavatar.core_model.medicalReminder.WaterData
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class waterReminderActivity : BaseActivity() {

    private lateinit var binding: ActivityWaterReminderBinding
    private val viewModel: MedicalViewModel by viewModels()
    private lateinit var  adapter : WaterLogAdapter
    private val dataList = mutableListOf<DateGroupedHistory>()
    private var waterReminderId: String?=null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWaterReminderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.circularProgress.setProgressTextAdapter { "" }
        binding.btnBack.setOnClickListener { finish() }
        binding.editBtn.setOnClickListener {
            startActivity(Intent(this, CreateWaterReminderActivity::class.java)
                .putExtra("update","update")
                .putExtra("id",waterReminderId)
            )
        }

        setUpRecyclerview()
        observeData()
    }

    override fun onResume() {
        super.onResume()
        hitFetchDetails()
    }


    private fun setUpRecyclerview() {
        adapter = WaterLogAdapter(this,dataList)
        binding.waterLogRecyclerview.adapter = adapter
        binding.waterLogRecyclerview.layoutManager = LinearLayoutManager(this)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun groupHistoryByDay(historyList: List<History>): Map<String, List<History>> {
        if (historyList.isEmpty()) return emptyMap()

        val today = java.time.LocalDate.now()
        val yesterday = today.minusDays(1)

        val formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

        return historyList.groupBy { history ->
            try {
                val entryDate = java.time.LocalDate.parse(history.time, formatter)
                when (entryDate) {
                    today -> "Today"
                    yesterday -> "Yesterday"
                    else -> entryDate.toString() // e.g. “2025-10-28”
                }
            } catch (e: Exception) {
                "Unknown"
            }
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun observeData() {
        collectApiResultOnStarted(viewModel.waterHistoryResponse) { it ->
            if (it.success) {
                dataList.clear()

                waterReminderId = it.data._id
                val historyList = it.data.history

                // 🟢 Group history by date (using ISO date from "time")
                val groupedHistory = groupHistoryByDay(historyList)

                val groupedList = groupedHistory.map { (dateGroup, list) ->
                    DateGroupedHistory(dateGroup, list)
                }.toMutableList()

                val todayTotal = groupedHistory["Today"]
                    ?.filter { it.isTaken }
                    ?.sumOf { it.perSip.toInt() }
                    ?: 0


                binding.circularProgress.apply {
                    setMaxProgress(it.data.targetWaterIntake.toDouble())
                    setCurrentProgress(todayTotal.toDouble())
                }



                val displayAmount = if (todayTotal >= 1000) {
                    String.format("%.1fL", todayTotal / 1000.0)
                } else {
                    "${todayTotal}"
                }

                binding.tvWaterAmount.text = displayAmount

                // Update adapter
                dataList.addAll(groupedList)
                adapter.notifyDataSetChanged()
            }
        }
    }



    private fun hitFetchDetails(){
        launchIfInternetAvailable {
            viewModel.hitWaterHistory()
        }
    }
}