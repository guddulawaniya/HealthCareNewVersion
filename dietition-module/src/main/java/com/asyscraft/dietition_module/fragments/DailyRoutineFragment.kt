package com.asyscraft.dietition_module.fragments

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.asyscraft.dietition_module.R
import com.asyscraft.dietition_module.databinding.FragmentDailyRoutineBinding
import com.asyscraft.dietition_module.onBoardingQuestionActivity
import com.asyscraft.dietition_module.utils.DietFormHolder
import com.asyscraft.dietition_module.viewModels.DietitionViewModel
import com.careavatar.core_model.dietition.DailyRoutineData
import com.careavatar.core_model.dietition.Questiondata
import com.careavatar.core_network.base.BaseFragment
import com.careavatar.core_utils.AutoCompleteUtils.setupAutoComplete
import com.careavatar.core_utils.DateTimePickerUtil.pickTime
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DailyRoutineFragment : BaseFragment() {

    private lateinit var binding: FragmentDailyRoutineBinding
    private val viewModel: DietitionViewModel by viewModels()
    private var professionDropDownList = mutableListOf<Questiondata>()
    private var dailyRoutineList = mutableListOf<DailyRoutineData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDailyRoutineBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hitGetProfessionCategory()
        hitDailyRoutineResponseList()
        observer()
        setupTimePickers()

        binding.nextBtn.buttonNext.text = "Next"
        binding.nextBtn.buttonNext.isEnabled = false

        binding.nextBtn.buttonNext.setOnClickListener {
            if (isInputValid()) {
                DietFormHolder.sleepTime = binding.sleepTime.text.toString()
                DietFormHolder.wakeTime = binding.wakeUpTime.text.toString()
                (requireActivity() as? onBoardingQuestionActivity)?.updateStep(2)
                findNavController().navigate(R.id.action_dailyRoutineFragment_to_calculateBMIFragment)
            }
        }

        binding.professionAutocomplete.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = professionDropDownList[position]

            DietFormHolder.professionId = selectedItem._id  // ✅ store ID
            updateNextButtonState()
        }


        setupListenersForValidation()
    }

    // 🔹 Observe all input changes to enable/disable the button dynamically
    private fun setupListenersForValidation() {
        binding.professionAutocomplete.addTextChangedListener { updateNextButtonState() }
        binding.wakeUpTime.addTextChangedListener { updateNextButtonState() }
        binding.sleepTime.addTextChangedListener { updateNextButtonState() }
    }

    private fun updateNextButtonState() {
        val isProfessionSelected = binding.professionAutocomplete.text.toString().trim().isNotEmpty()
        val isDailyRoutineSelected = !DietFormHolder.dailyroutineId.isNullOrEmpty()
        val isWakeTimeSelected = binding.wakeUpTime.text.toString().trim().isNotEmpty()
        val isSleepTimeSelected = binding.sleepTime.text.toString().trim().isNotEmpty()

        binding.nextBtn.buttonNext.isEnabled = isProfessionSelected && isDailyRoutineSelected && isWakeTimeSelected && isSleepTimeSelected
    }

    // ✅ TIME PICKERS
    private fun setupTimePickers() {
        binding.wakeUpTime.setOnClickListener {
            pickTime(requireContext()) {
                binding.wakeUpTime.text = it
                updateNextButtonState()
            }
        }

        binding.sleepTime.setOnClickListener {
            pickTime(requireContext()) {
                binding.sleepTime.text = it
                updateNextButtonState()
            }
        }
    }

    private fun isInputValid(): Boolean {
        return when {
            binding.professionAutocomplete.text.isEmpty() -> {
                showToast("Please select profession")
                false
            }

            DietFormHolder.dailyroutineId.isNullOrEmpty() -> {
                showToast("Please select your daily routine activity")
                false
            }

            binding.wakeUpTime.text.isEmpty() -> {
                showToast("Please select wake up time")
                false
            }

            binding.sleepTime.text.isNullOrEmpty() -> {
                showToast("Please select sleep time")
                false
            }

            else -> true
        }
    }

    private fun observer() {
        collectApiResultOnStarted(viewModel.getProfessionCategoryResponse) { response ->
            professionDropDownList.clear()
            professionDropDownList.addAll(response.data)

            setupAutoComplete(
                binding.professionAutocomplete,
                professionDropDownList.map { it.profession }
            )
        }

        collectApiResultOnStarted(viewModel.getDailyRoutineResponse) { response ->
            dailyRoutineList.clear()
            dailyRoutineList.addAll(response.data)
            setupDailyRoutineOptions()
        }
    }

    private fun setupDailyRoutineOptions() {
        val items = listOf(
            Triple(binding.layoutSedentary, binding.tvSedentary, binding.tvSedentaryIcon),
            Triple(binding.layoutLightActive, binding.tvLightActive, binding.tvLightActiveIcon),
            Triple(binding.layoutModerately, binding.tvModerately, binding.tvModeratelyIcon),
            Triple(binding.layoutVeryActive, binding.tvVeryActive, binding.tvVeryActiveIcon)
        )

        dailyRoutineList.take(items.size).forEachIndexed { index, routine ->
            val (layout, textView, iconView) = items[index]
            textView.text = routine.routine

            layout.setOnClickListener {
                // Reset all cards
                items.forEach { (l, t, icon) ->
                    l.setBackgroundResource(com.careavatar.core_ui.R.drawable.medical_types_bg)
                    t.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            com.careavatar.core_ui.R.color.textColor
                        )
                    )
                    icon.setColorFilter(
                        ContextCompat.getColor(
                            requireContext(),
                            com.careavatar.core_ui.R.color.textColor
                        ),
                        PorterDuff.Mode.SRC_IN
                    )
                }

                // Highlight selected
                layout.setBackgroundResource(com.careavatar.core_ui.R.drawable.medical_types_selected_bg)
                textView.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        com.careavatar.core_ui.R.color.primaryColor
                    )
                )
                iconView.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        com.careavatar.core_ui.R.color.primaryColor
                    ),
                    PorterDuff.Mode.SRC_IN
                )

                // Animate
                layout.animate().scaleX(0.97f).scaleY(0.97f).setDuration(80).withEndAction {
                    layout.animate().scaleX(1f).scaleY(1f).setDuration(80)
                }

                // Save and update button state
                DietFormHolder.dailyroutineId = routine._id
                updateNextButtonState()
            }
        }
    }

    private fun hitGetProfessionCategory() {
        launchIfInternetAvailable { viewModel.hitProfessionCategory() }
    }

    private fun hitDailyRoutineResponseList() {
        launchIfInternetAvailable { viewModel.hitDailyRoutineResponseList() }
    }
}
