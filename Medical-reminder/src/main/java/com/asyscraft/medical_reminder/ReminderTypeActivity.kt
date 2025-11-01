package com.asyscraft.medical_reminder

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.asyscraft.medical_reminder.databinding.ActivityReminderTypeBinding
import com.asyscraft.medical_reminder.medicalReminder.CreateMedicalReminderActivity
import com.asyscraft.medical_reminder.viewModels.MedicalViewModel
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class ReminderTypeActivity : BaseActivity() {
    private lateinit var binding: ActivityReminderTypeBinding
    private var selectedView: View? = null
    private var selectedActivityClass: Class<*>? = null
    private var selectedTypeName: String? = null
    private val viewModel: MedicalViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReminderTypeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener { finish() }

        // Map each layout to its destination Activity
        val typeLayouts = mapOf(
            binding.layoutMedicine to Pair(CreateMedicalReminderActivity::class.java, "Medicine"),
            binding.layoutActivity to Pair(NewReminderActivity::class.java, "Activity"),
            binding.layoutVaccination to Pair(MedicalReminderDashboardActivity::class.java, "Vaccination"),
            binding.layoutWater to Pair(CreateWaterReminderActivity::class.java, "Water"),
            binding.layoutPeriod to Pair(MedicalReminderDashboardActivity::class.java, "Period")
        )

        // Handle type selection
        typeLayouts.forEach { (layout, pair) ->
            layout.setOnClickListener {
                selectType(layout, typeLayouts.keys)
                selectedActivityClass = pair.first
                selectedTypeName = pair.second
            }
        }

        binding.includedBtn.buttonNext.setOnClickListener {
            if (selectedActivityClass != null && selectedTypeName != null) {
                if (selectedTypeName == "Water") {
                    hitGetWaterHistory()
                } else {
                    navigateTo(selectedActivityClass!!, selectedTypeName!!)
                }
            } else {
                showToast("Please select a reminder type")
            }
        }
        observeViewModel()
    }

    private fun observeViewModel() {

        collectApiResultOnStarted(viewModel.waterHistoryResponse) { response ->
            if (response.success && response.data.history.isNotEmpty()) {
                startActivity(
                    Intent(this, MedicalReminderDashboardActivity::class.java)
                        .putExtra("reminder_type", "Water")
                )
            } else {
                startActivity(
                    Intent(this, CreateWaterReminderActivity::class.java)

                )
            }
        }

    }

    private fun hitGetWaterHistory(){
        launchIfInternetAvailable {
            viewModel.hitWaterHistory()
        }
    }

    private fun selectType(selected: View, allLayouts: Set<View>) {
        allLayouts.forEach { layout ->
            layout.setBackgroundResource(com.careavatar.core_ui.R.drawable.medical_types_bg)
        }
        selected.setBackgroundResource(com.careavatar.core_ui.R.drawable.medical_types_selected_bg)
        selectedView = selected
    }

    private fun navigateTo(activityClass: Class<*>, typeName: String) {
        val intent = Intent(this, activityClass)
        intent.putExtra("reminder_type", typeName)
        startActivity(intent)
        finish()
    }
}
