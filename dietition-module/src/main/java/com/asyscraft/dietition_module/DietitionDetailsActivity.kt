package com.asyscraft.dietition_module

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.asyscraft.dietition_module.databinding.ActivityDietitionDetailsBinding
import com.asyscraft.dietition_module.utils.TodayDecorator
import com.asyscraft.dietition_module.viewModels.DietitionViewModel
import com.bumptech.glide.Glide
import com.careavatar.core_model.dietition.ExpertBookingAvailableResponse
import com.careavatar.core_model.dietition.ExpertRequest
import com.careavatar.core_model.dietition.GetExpertResponse2
import com.careavatar.core_network.base.BaseActivity
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class DietitionDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityDietitionDetailsBinding
    private val viewModel: DietitionViewModel by viewModels()
    private var availableSlots = mutableListOf<ExpertBookingAvailableResponse.AvailableSlot>()
    private lateinit var selectedSlotId: String
    private val sdfFull = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDietitionDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            toolbar.tvTitle.text = "Dietician Profile"
            toolbar.btnBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }

        setDataUi()
        val today = sdfFull.format(Calendar.getInstance().time)

        fetchAvailableSlot(today)
        observer()
        setupStaticDecorators()


        binding.includebtn.buttonNext.setOnClickListener {
            startActivity(Intent(this, PreConsultationFormActivity::class.java))
        }

        binding.includebtn.buttonNext.isEnabled = false


    }

    private fun setupStaticDecorators() {

        binding.materialCalendarView.apply {
            removeDecorators()
            addDecorator(TodayDecorator())
        }

        binding.materialCalendarView.setOnDateChangedListener { widget, date, selected ->
            if (selected) {
                val selectedDate = "${date.year}-${date.month + 1}-${date.day}"
                Log.d("CALENDAR_DATE", selectedDate)
                fetchAvailableSlot(selectedDate)
            }
        }

    }

    private fun observer() {
        collectApiResultOnStarted(viewModel.getExpertBookingAvailableResponse) {
            if (it.success) {
                availableSlots.clear()
                availableSlots.addAll(it.availableSlots)
                binding.morningTimeChipGroup.removeAllViews()

                populateChipGroup(binding.morningTimeChipGroup, availableSlots) { selectedId ->
                    selectedSlotId = selectedId
                    updateNextButtonState()
                }
            }
        }
    }

    private fun updateNextButtonState() {
        binding.includebtn.buttonNext.isEnabled = ::selectedSlotId.isInitialized
    }


    private fun populateChipGroup(
        chipGroup: ChipGroup,
        data: List<ExpertBookingAvailableResponse.AvailableSlot>,
        onSelected: (String) -> Unit
    ) {

        chipGroup.isSingleSelection = true
        chipGroup.removeAllViews()

        data.forEach { slot ->

            val chip = Chip(this@DietitionDetailsActivity).apply {
                text = slot.startTime   // ✅ Show Time/Name
                isCheckable = true
                isClickable = true
                isCheckedIconVisible = true

                checkedIcon = ContextCompat.getDrawable(
                    this@DietitionDetailsActivity,
                    com.careavatar.core_ui.R.drawable.complete_icon
                )

                chipCornerRadius = 50f
                chipBackgroundColor = ContextCompat.getColorStateList(
                    this@DietitionDetailsActivity,
                    com.careavatar.core_ui.R.color.chip_selector_bg
                )

                setTextColor(
                    ContextCompat.getColorStateList(
                        this@DietitionDetailsActivity,
                        com.careavatar.core_ui.R.color.chip_selector_text
                    )
                )
            }

            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    onSelected(slot._id)     // ✅ Return correct ID
                }
            }

            chipGroup.addView(chip)
        }
    }

    private fun fetchAvailableSlot(date: String) {

        val data = intent.getParcelableExtra<GetExpertResponse2.Expert>("expertDetails")


        if (data != null) {
            val request = ExpertRequest(data.expert._id, date)
            launchIfInternetAvailable {
                viewModel.hitGetAvailableSlot(request)
            }
        }


    }

    private fun setDataUi() {
        val data = intent.getParcelableExtra<GetExpertResponse2.Expert>("expertDetails")

        if (data != null) {
            binding.apply {
                tvName.text = data.expert.name
                tvRole.text = data.expert.categoryName
                tvAboutDesc.text = data.expert.description

                if (data.expert.category.isNotEmpty()) {
                    tvRole.text = data.expert.category[0].name
                }

                Glide.with(this@DietitionDetailsActivity)
                    .load(data.expert.avatar)
                    .placeholder(com.careavatar.core_ui.R.drawable.profile_1)
                    .into(ivProfile)

            }
        }

    }
}