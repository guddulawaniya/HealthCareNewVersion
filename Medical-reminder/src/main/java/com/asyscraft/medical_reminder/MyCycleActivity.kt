package com.asyscraft.medical_reminder

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.graphics.toColorInt
import androidx.lifecycle.lifecycleScope
import com.asyscraft.medical_reminder.databinding.ActivityMyCycleBinding
import com.asyscraft.medical_reminder.utils.DefaultBackgroundDecorator
import com.asyscraft.medical_reminder.utils.RangeColorDecorator
import com.asyscraft.medical_reminder.utils.TodayDecorator
import com.asyscraft.medical_reminder.viewModels.MedicalViewModel
import com.careavatar.core_network.base.BaseActivity
import com.prolificinteractive.materialcalendarview.CalendarDay
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class MyCycleActivity : BaseActivity() {

    private lateinit var binding: ActivityMyCycleBinding
    private val viewModel: MedicalViewModel by viewModels()
    private var periodRangeDecorator: RangeColorDecorator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyCycleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupStaticDecorators()

        fetchPeriodDetails()
        observePeriodDetails()
    }

    override fun onResume() {
        super.onResume()
        fetchPeriodDetails()
    }

    private fun setupUI() {
        binding.backbtn.setOnClickListener { finish() }

        binding.buttonNext.setOnClickListener {
            startActivity(Intent(this, LogYourPeriodActivity::class.java))
        }

        binding.addPeriodLog.setOnClickListener {
            startActivity(Intent(this, LogYourPeriodActivity::class.java))
        }
    }

    private fun setupStaticDecorators() {
        binding.materialCalendarView.apply {
            // Clean up first to avoid duplicates
            removeDecorators()
            addDecorator(DefaultBackgroundDecorator( "#3370E977".toColorInt()))
            addDecorator(TodayDecorator()) // pink circle for today
        }
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun observePeriodDetails() {
        collectApiResultOnStarted(viewModel.getPeriodGetAllDataResponse) { response ->
            response.data.firstOrNull()?.let { periodData ->

                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val lastPeriodDate = periodData.lastPeriodDate?.let { dateFormat.parse(it) }
                val periodDurationDays = periodData.periodDuration?.split(" ")?.firstOrNull()?.toIntOrNull() ?: 0

                if (lastPeriodDate != null && periodDurationDays > 0) {

                    // 🩸 Calculate start date by subtracting (duration - 1) days from the lastPeriodDate
                    val cal = Calendar.getInstance()
                    cal.time = lastPeriodDate
                    cal.add(Calendar.DAY_OF_MONTH, -(periodDurationDays - 1))
                    val startDate = cal.time
                    val endDate = lastPeriodDate

                    // 🗓 Format UI text like: "Period: Oct 11–14 (4 days)"
                    val startMonth = SimpleDateFormat("MMM", Locale.getDefault()).format(startDate)
                    val startDay = SimpleDateFormat("d", Locale.getDefault()).format(startDate)
                    val endMonth = SimpleDateFormat("MMM", Locale.getDefault()).format(endDate)
                    val endDay = SimpleDateFormat("d", Locale.getDefault()).format(endDate)

                    val dateRangeText = if (startMonth == endMonth) {
                        "$startMonth $startDay–$endDay"
                    } else {
                        "$startMonth $startDay – $endMonth $endDay"
                    }

                    binding.textViewdateshow.text =
                        "Period: $dateRangeText (${periodData.averageCycleLength} day cycle)"

                    // 🌙 Highlight range on calendar
                    lifecycleScope.launch(Dispatchers.Default) {
                        val rangePeriodDates = getDateRange(startDate, endDate)
                        withContext(Dispatchers.Main) {
                            updateDynamicDecorators(rangePeriodDates)
                        }
                    }
                }
            }
        }
    }


    private fun updateDynamicDecorators(rangePeriodDates: List<CalendarDay>) {
        val calendar = binding.materialCalendarView

        // If empty range -> remove existing decorator and return
        if (rangePeriodDates.isEmpty()) {
            periodRangeDecorator?.let {
                calendar.removeDecorator(it)
                periodRangeDecorator = null
                calendar.post { calendar.invalidateDecorators() }
            }
            return
        }

        // Remove previous decorator if present
        periodRangeDecorator?.let {
            calendar.removeDecorator(it)
        }

        // Create new decorator and keep reference
        val newDecorator = RangeColorDecorator(rangePeriodDates, Color.parseColor("#FFE0F6"))

        periodRangeDecorator = newDecorator
        calendar.addDecorator(newDecorator)

        // Force a single repaint on the UI thread
        calendar.post { calendar.invalidateDecorators() }
    }


    private fun getDateRange(startDate: Date, endDate: Date): List<CalendarDay> {
        val dates = mutableListOf<CalendarDay>()
        val calendar = Calendar.getInstance()
        calendar.time = startDate

        while (!calendar.time.after(endDate)) {
            dates.add(
                CalendarDay.from(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
            )
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return dates
    }

    private fun fetchPeriodDetails() {
        launchIfInternetAvailable {
            viewModel.hitFetchPeriodDetails()
        }
    }
}
