package com.asyscraft.dietition_module.fragments

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
import com.asyscraft.dietition_module.databinding.FragmentMainGoalBinding
import com.asyscraft.dietition_module.utils.DietFormHolder
import com.asyscraft.dietition_module.viewModels.DietitionViewModel
import com.careavatar.core_model.dietition.GetAllMedicineResponse
import com.careavatar.core_model.dietition.PrimaryReasonData
import com.careavatar.core_network.base.BaseFragment
import com.careavatar.core_utils.AutoCompleteUtils.setupAutoComplete
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainGoalFragment : BaseFragment() {

    private lateinit var binding: FragmentMainGoalBinding
    private val viewModel: DietitionViewModel by viewModels()

    private var primaryReasonDataList = mutableListOf<PrimaryReasonData>()
    private var medicineNameDataList = mutableListOf<GetAllMedicineResponse.MedicineData>()

    private var selectedGoalId: String? = null
    private var MedicineId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainGoalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hitPrimaryReason()
        hitAllMedicine("")
        observer()
        setupGoalSelection()
        setupMedicineAutocomplete()

        binding.nextBtn.buttonNext.text = "Next"
        binding.nextBtn.buttonNext.isEnabled = false

        binding.nextBtn.buttonNext.setOnClickListener {
            if (isInputValid()) {
                DietFormHolder.fitnessGoalId = selectedGoalId
                DietFormHolder.MedicineId = MedicineId
                findNavController().navigate(R.id.action_mainGoalFragment_to_lifeStyleFragment)
            }
        }
    }

    private fun updateNextButtonState() {
        val isGoalSelected = !selectedGoalId.isNullOrEmpty()
        val isMedicineEntered = binding.medicineAutocomplete.text.toString().trim().isNotEmpty()

        binding.nextBtn.buttonNext.isEnabled = isGoalSelected && isMedicineEntered
    }


    private fun setupGoalSelection() {
        val goalItems = listOf(
            Triple(binding.layoutSedentary, binding.tvLoseWeight, binding.ivLoseWeightIcon),
            Triple(binding.layoutMedicine, binding.weightGain, binding.tvGainMuscleIcon),
            Triple(binding.layoutModerately, binding.tvMaintain, binding.ivMaintainIcon),
            Triple(binding.layoutVeryActive, binding.tvImproveHealth, binding.tvImproveHealthIcon),
            Triple(binding.layoutManageDiet, binding.tvManageDiet, binding.tvManageDietIcon)
        )

        goalItems.forEachIndexed { index, (layout, textView, iconView) ->
            layout.setOnClickListener {
                // Reset all
                goalItems.forEach { (l, t, icon) ->
                    l.setBackgroundResource(com.careavatar.core_ui.R.drawable.medical_types_bg)
                    t.setTextColor(ContextCompat.getColor(requireContext(), com.careavatar.core_ui.R.color.textColor))
                    icon.setColorFilter(
                        ContextCompat.getColor(requireContext(), com.careavatar.core_ui.R.color.textColor),
                        PorterDuff.Mode.SRC_IN
                    )
                }

                // Highlight selected
                layout.setBackgroundResource(com.careavatar.core_ui.R.drawable.medical_types_selected_bg)
                textView.setTextColor(ContextCompat.getColor(requireContext(), com.careavatar.core_ui.R.color.primaryColor))
                iconView.setColorFilter(
                    ContextCompat.getColor(requireContext(), com.careavatar.core_ui.R.color.primaryColor),
                    PorterDuff.Mode.SRC_IN
                )

                // ✅ Get ID by position safely
                selectedGoalId = primaryReasonDataList.getOrNull(index)?._id
                updateNextButtonState()

                // Animation
                layout.animate().scaleX(0.95f).scaleY(0.95f).setDuration(80).withEndAction {
                    layout.animate().scaleX(1f).scaleY(1f).setDuration(80)
                }
            }
        }
    }



    private fun setupMedicineAutocomplete() {

        binding.medicineAutocomplete.setOnItemClickListener { _, _, position, _ ->
            val selectedMedicine = medicineNameDataList.getOrNull(position)
            if (selectedMedicine != null) {
                binding.medicineAutocomplete.setText(selectedMedicine.name)
                MedicineId = selectedMedicine._id
            }
        }

        // Optional: Search filter
        binding.medicineAutocomplete.addTextChangedListener { text ->
//            hitAllMedicine(text.toString())
            updateNextButtonState()
        }
    }


    // ✅ Validation
    private fun isInputValid(): Boolean {
        return when {
            selectedGoalId.isNullOrEmpty() -> {
                showToast("Please select your main goal")
                false
            }

            else -> true
        }
    }

    // ✅ Observers for API
    private fun observer() {
        collectApiResultOnStarted(viewModel.getPrimaryReasonJoinApp) { response ->
            primaryReasonDataList.clear()
            primaryReasonDataList.addAll(response.data)
        }

        collectApiResultOnStarted(viewModel.getAllMedicineResponse) { response ->
            medicineNameDataList.clear()
            medicineNameDataList.addAll(response.data)
            setupAutoComplete(
                binding.medicineAutocomplete,
                medicineNameDataList.map { it.name })
        }
    }

    // ✅ API calls
    private fun hitPrimaryReason() {
        launchIfInternetAvailable {
            viewModel.hitPrimaryReason()
        }
    }

    private fun hitAllMedicine(query: String) {
        launchIfInternetAvailable {
            viewModel.hitAllMedicine(query, 1, 100)
        }
    }
}
