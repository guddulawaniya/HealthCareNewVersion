package com.asyscraft.medical_reminder.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.asyscraft.medical_reminder.databinding.FragmentScanMedicineBinding
import com.careavatar.core_network.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.Executor


@AndroidEntryPoint
class ScanMedicineFragment : BaseFragment() {

    private lateinit var binding: FragmentScanMedicineBinding
    private lateinit var optionList: List<TextView>
    private lateinit var dayViews: List<TextView>
    private val selectedDays = mutableSetOf<TextView>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScanMedicineBinding.inflate(inflater, container, false)
        return binding.root
    }

    // ✅ Permission Launcher
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) startCamera()
            else Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 🔹 Initialize camera preview
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        // 🔹 Initialize reminder setup
        setupReminderOptions()
        setupDaysSelection()
    }

    /** =======================
     * 🎥 Camera Preview Setup
     * ======================= */
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner, // ✅ use lifecycle-aware owner
                    cameraSelector,
                    preview
                )
            } catch (exc: Exception) {
                exc.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    /** =======================
     * 🩺 Reminder Type Options
     * ======================= */
    private fun setupReminderOptions() {
        optionList = listOf(binding.tvOneTime, binding.tvEveryday, binding.tvCustomdays)

        optionList.forEach { option ->
            option.setOnClickListener {
                selectOption(option)
                when (option) {
                    binding.tvOneTime -> enableOneTimeMode()
                    binding.tvEveryday -> enableEverydayMode()
                    binding.tvCustomdays -> enableCustomMode()
                }
            }
        }

        // Default selection
        selectOption(binding.tvOneTime)
        enableOneTimeMode()
    }

    /** 🟦 One-time mode: only one selectable day */
    private fun enableOneTimeMode() {
        clearAllDaySelections()
        dayViews.forEach { day ->
            day.isEnabled = true
            day.setOnClickListener {
                clearAllDaySelections()
                selectDay(day)
            }
        }
    }

    /** 🟩 Everyday mode: auto-select all days */
    private fun enableEverydayMode() {
        selectedDays.clear()
        dayViews.forEach { day ->
            selectDay(day)
            day.isEnabled = false
        }
    }

    /** 🟨 Custom mode: allow multiple manual selections */
    private fun enableCustomMode() {
        dayViews.forEach { day ->
            day.isEnabled = true
            day.setOnClickListener { toggleDaySelection(day) }
        }
    }

    /** ✅ Update selected option UI */
    private fun selectOption(selected: TextView) {
        optionList.forEach { option ->
            if (option == selected) {
                option.setBackgroundResource(com.careavatar.core_ui.R.drawable.button_bg)
                option.setTextColor(ContextCompat.getColor(requireContext(), com.careavatar.core_ui.R.color.white))
            } else {
                option.setBackgroundResource(0)
                option.setTextColor(Color.parseColor("#5E6960"))
            }
        }
    }

    /** 🔄 Clear all selections */
    private fun clearAllDaySelections() {
        selectedDays.forEach { day ->
            day.setBackgroundResource(com.careavatar.core_ui.R.drawable.water_log_card_bg)
            day.setTextColor(ContextCompat.getColor(requireContext(), com.careavatar.core_ui.R.color.primaryColor))
        }
        selectedDays.clear()
    }

    /** ✅ Select a day */
    private fun selectDay(day: TextView) {
        day.setBackgroundResource(com.careavatar.core_ui.R.drawable.circle)
        day.setTextColor(ContextCompat.getColor(requireContext(), com.careavatar.core_ui.R.color.white))
        selectedDays.add(day)
    }

    /** 🔁 Setup weekday click listeners */
    private fun setupDaysSelection() {
        dayViews = listOf(
            binding.tvMon, binding.tvTue, binding.tvWed,
            binding.tvThu, binding.tvFri, binding.tvSat, binding.tvSun
        )
        dayViews.forEach { day -> day.setOnClickListener { toggleDaySelection(day) } }
    }

    /** 🧠 Toggle day selection */
    private fun toggleDaySelection(dayView: TextView) {
        if (selectedDays.contains(dayView)) {
            dayView.setBackgroundResource(com.careavatar.core_ui.R.drawable.water_log_card_bg)
            dayView.setTextColor(ContextCompat.getColor(requireContext(), com.careavatar.core_ui.R.color.primaryColor))
            selectedDays.remove(dayView)
        } else {
            selectDay(dayView)
        }
    }
}
