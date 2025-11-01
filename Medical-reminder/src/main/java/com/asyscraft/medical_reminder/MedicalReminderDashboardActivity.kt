package com.asyscraft.medical_reminder

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.asyscraft.medical_reminder.adapters.DateAdapter
import com.asyscraft.medical_reminder.adapters.DateGroupedHistory
import com.asyscraft.medical_reminder.databinding.ActivityMedicalReminderDashboardBinding
import com.asyscraft.medical_reminder.viewModels.MedicalViewModel
import com.careavatar.core_model.medicalReminder.CalenderDateModel
import com.careavatar.core_model.medicalReminder.History
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.filter
import kotlin.getValue

@AndroidEntryPoint
class MedicalReminderDashboardActivity : BaseActivity() {
    private lateinit var binding: ActivityMedicalReminderDashboardBinding
    private val viewModel: MedicalViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMedicalReminderDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        val reminderType = intent.getStringExtra("reminder_type")

        binding.upcommingVaccination.visibility = View.GONE
        binding.upcomingPeriodDate.visibility = View.GONE

        if (reminderType=="Vaccination"){
            binding.circularProgressVaccination.setProgressTextAdapter { "" }
            binding.upcommingVaccination.visibility = View.VISIBLE
        }else if (reminderType=="Water"){
            binding.circularProgress.setProgressTextAdapter { "" }
            binding.waterReminderCard.visibility = View.VISIBLE
            binding.upcommingVaccination.visibility = View.GONE
            binding.upcomingPeriodDate.visibility = View.GONE

        }else {
            binding.circularProgressPeriod.setProgressTextAdapter { "" }
            binding.upcomingPeriodDate.visibility = View.VISIBLE
        }

        binding.fabAdd.setOnClickListener {
          val intent =   if (reminderType=="Vaccination"){
              Intent(this, MyVaccinationActivity::class.java)
            }else {
              Intent(this, MyCycleActivity::class.java)
            }
            startActivity(intent)
        }

        val dateList = generateUpcomingDates(30) // Next 30 days
        val adapter = DateAdapter(dateList) { selectedDate ->

            showToast("Selected: ${selectedDate.fullDate}")
        }
        binding.dateRecyclerView.adapter = adapter


        binding.upcomingPeriodDate.setOnClickListener {
            val intent = Intent(this, MyCycleActivity::class.java)
            startActivity(intent)
        }

        binding.upcommingVaccination.setOnClickListener {
            val intent = Intent(this, MyVaccinationActivity::class.java)
            startActivity(intent)
        }

        binding.waterReminderCard.setOnClickListener {
            val intent = Intent(this, waterReminderActivity::class.java)
            startActivity(intent)
        }

        setupTabs()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        val reminderType = intent.getStringExtra("reminder_type")
        if (reminderType=="Water"){
            hitGetWaterHistory()
        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun observeViewModel() {

        collectApiResultOnStarted(viewModel.waterHistoryResponse) {
            if (it.success) {

                if (it.data.history.isNotEmpty()){
                    val historyList = it.data.history
                    // 🟢 Group history by date (using ISO date from "time")
                    val groupedHistory = groupHistoryByDay(historyList)

                    val todayTotal = groupedHistory["Today"]
                        ?.filter { it.isTaken }
                        ?.sumOf { it.perSip.toInt() }
                        ?: 0
                    binding.tvDesciption.text = "You ‘ve had $todayTotal ml of water"
                    binding.circularProgress.apply {
                        setMaxProgress(it.data.targetWaterIntake.toDouble())
                        setCurrentProgress(todayTotal.toDouble())
                    }
                    binding.tvWaterAmount.text = "${todayTotal}ml"
                    binding.tvWaterAmountTarget.text = it.data.targetWaterIntake+"ml"
                }
                else{
                    binding.tvDesciption.text = "You ‘ve had 0 ml of water"

                    binding.circularProgress.apply {
                        setMaxProgress(it.data.targetWaterIntake.toDouble())
                        setCurrentProgress(0.0)
                    }
                    binding.tvWaterAmount.text = "0ml"
                    binding.tvWaterAmountTarget.text = it.data.targetWaterIntake+"ml"
                }



            }
        }

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

    private fun hitGetWaterHistory(){
        launchIfInternetAvailable {
            viewModel.hitWaterHistory()
        }
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setupTabs() {
        val tabs = listOf(
            binding.tabAll,
            binding.tabMedicine,
            binding.tabNormal,
            binding.tabWater
        )

        var selectedTab: LinearLayout? = binding.tabAll

        tabs.forEach { tab ->
            tab.setOnClickListener {
                // Reset all tabs to default state
                tabs.forEach { t ->
                    t.background = null

                    // Reset text colors
                    t.findViewById<TextView>(R.id.textAll)?.setTextColor(getColor(com.careavatar.core_ui.R.color.hintColor))
                    t.findViewById<TextView>(R.id.textMedicine)?.setTextColor(getColor(com.careavatar.core_ui.R.color.hintColor))
                    t.findViewById<TextView>(R.id.textNormal)?.setTextColor(getColor(com.careavatar.core_ui.R.color.hintColor))
                    t.findViewById<TextView>(R.id.textWater)?.setTextColor(getColor(com.careavatar.core_ui.R.color.hintColor))

                    // Reset count text color + background
                    t.findViewById<TextView>(R.id.countAll)?.apply {
                        setTextColor(getColor(com.careavatar.core_ui.R.color.hintColor))
                        background = null
                    }
                    t.findViewById<TextView>(R.id.countMedicine)?.apply {
                        setTextColor(getColor(com.careavatar.core_ui.R.color.hintColor))
                        background = null
                    }
                    t.findViewById<TextView>(R.id.countNormal)?.apply {
                        setTextColor(getColor(com.careavatar.core_ui.R.color.hintColor))
                        background = null
                    }
                    t.findViewById<TextView>(R.id.countWater)?.apply {
                        setTextColor(getColor(com.careavatar.core_ui.R.color.hintColor))
                        background = null
                    }
                }

                // Highlight selected tab
                tab.background = getDrawable(com.careavatar.core_ui.R.drawable.button_bg)

                // Highlight selected tab text
                tab.findViewById<TextView>(R.id.textAll)?.setTextColor(getColor(com.careavatar.core_ui.R.color.white))
                tab.findViewById<TextView>(R.id.textMedicine)?.setTextColor(getColor(com.careavatar.core_ui.R.color.white))
                tab.findViewById<TextView>(R.id.textNormal)?.setTextColor(getColor(com.careavatar.core_ui.R.color.white))
                tab.findViewById<TextView>(R.id.textWater)?.setTextColor(getColor(com.careavatar.core_ui.R.color.white))

                // Highlight selected tab count bg + color
                tab.findViewById<TextView>(R.id.countAll)?.apply {
                    setTextColor(getColor(com.careavatar.core_ui.R.color.white))
                    background = getDrawable(com.careavatar.core_ui.R.drawable.medical_tab_count_bg)
                }
                tab.findViewById<TextView>(R.id.countMedicine)?.apply {
                    setTextColor(getColor(com.careavatar.core_ui.R.color.white))
                    background = getDrawable(com.careavatar.core_ui.R.drawable.medical_tab_count_bg)
                }
                tab.findViewById<TextView>(R.id.countNormal)?.apply {
                    setTextColor(getColor(com.careavatar.core_ui.R.color.white))
                    background = getDrawable(com.careavatar.core_ui.R.drawable.medical_tab_count_bg)
                }
                tab.findViewById<TextView>(R.id.countWater)?.apply {
                    setTextColor(getColor(com.careavatar.core_ui.R.color.white))
                    background = getDrawable(com.careavatar.core_ui.R.drawable.medical_tab_count_bg)
                }

                selectedTab = tab

                // Custom logic per tab
                when (tab.id) {
                    R.id.tabAll -> handleTabClick("All")
                    R.id.tabMedicine -> handleTabClick("Medicine")
                    R.id.tabNormal -> handleTabClick("Normal")
                    R.id.tabWater -> handleTabClick("Water")
                }
            }
        }
    }


    private fun handleTabClick(tabName: String) {
        // Perform your action based on the selected tab
        Toast.makeText(this, "Selected: $tabName", Toast.LENGTH_SHORT).show()
    }


    private fun generateUpcomingDates(daysCount: Int): List<CalenderDateModel> {
        val dateList = mutableListOf<CalenderDateModel>()
        val calendar = Calendar.getInstance()
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault()) // e.g. Mon
        val dateFormat = SimpleDateFormat("dd", Locale.getDefault()) // e.g. 21
        val fullFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        repeat(daysCount) { i ->
            val day = dayFormat.format(calendar.time)
            val date = dateFormat.format(calendar.time)
            val fullDate = fullFormat.format(calendar.time)

            dateList.add(
                CalenderDateModel(
                    date = date,
                    day = day,
                    fullDate = fullDate,
                    isSelected = i == 0 // First date selected by default
                )
            )
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        return dateList
    }

}





