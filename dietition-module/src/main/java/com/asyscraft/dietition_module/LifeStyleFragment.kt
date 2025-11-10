package com.asyscraft.dietition_module

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.asyscraft.dietition_module.databinding.FragmentLifeStyleBinding
import com.asyscraft.dietition_module.utils.DietFormHolder
import com.asyscraft.dietition_module.viewModels.DietitionViewModel
import com.careavatar.core_network.base.BaseFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LifeStyleFragment : BaseFragment() {

    private lateinit var binding: FragmentLifeStyleBinding
    private val viewModel: DietitionViewModel by viewModels()

    // To store selections
    private var selectedDietType: String? = null
    private var selectedAllergyId: String? = null
    private var specificDietId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLifeStyleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDietTypeSelection()
        hitFoodAllergies()
        observer()

        binding.nextBtn.buttonNext.text = "Next"
        binding.nextBtn.buttonNext.isEnabled = false
        binding.nextBtn.buttonNext.setOnClickListener {
            if (isInputValid()) {
                // Save data to holder
                DietFormHolder.activitydataId = "680739b6db79d58348f8ac62"
                DietFormHolder.workoutdataId = "68073bca972ada44defe93bd"
                DietFormHolder.FamilyHistorydataId = "6847bac99e0a44bd7a953cd9"
                DietFormHolder.FollowspecifidietId = "680733c3fd543fb27a9bbd96"
                DietFormHolder.SpecifidietId = specificDietId
                DietFormHolder.FoodallergiesId = selectedAllergyId
                DietFormHolder.consumeAlcohol = binding.switchAlcohol.isChecked

                hitPostDataQuestion()

            }
        }
    }

    private fun updateNextButtonState() {
        val isDietSelected = !selectedDietType.isNullOrEmpty()
        val isAllergySelected = !selectedAllergyId.isNullOrEmpty()

        binding.nextBtn.buttonNext.isEnabled = isDietSelected && isAllergySelected
    }

    private fun hitPostDataQuestion() {
        launchIfInternetAvailable {
            val requestdata = DietFormHolder.toRequest()
            viewModel.hitPostQuestionData(requestdata)

//            if (false) {
//                    viewModel.hitPostQuestiondataUpdate(requestdata)
//            } else {
//
//            }

        }
    }

    // ✅ Handle Veg / Non-Veg / Vegan selection
    private fun setupDietTypeSelection() {
        val options = listOf(
            Triple(
                binding.layoutVegetarian,
                "Vegetarian",
                com.careavatar.core_ui.R.drawable.vegitable_icon
            ),
            Triple(binding.layoutNonVeg, "Non-Veg", com.careavatar.core_ui.R.drawable.non_veg_icon),
            Triple(
                binding.layoutVegan,
                "Vegan",
                com.careavatar.core_ui.R.drawable.lucide_vegan_icon
            )
        )

        options.forEach { (layout, label, _) ->
            layout.setOnClickListener {
                // Reset all layouts
                options.forEach { (l, _, _) ->
                    l.setBackgroundResource(com.careavatar.core_ui.R.drawable.medical_types_bg)
                    for (i in 0 until l.childCount) {
                        val child = l.getChildAt(i)
                        if (child is android.widget.TextView) {
                            child.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    com.careavatar.core_ui.R.color.hintColor
                                )
                            )
                        }
                        if (child is android.widget.ImageView) {
                            child.setColorFilter(
                                ContextCompat.getColor(
                                    requireContext(),
                                    com.careavatar.core_ui.R.color.hintColor
                                ),
                                android.graphics.PorterDuff.Mode.SRC_IN
                            )
                        }
                    }
                }

                // Highlight selected
                layout.setBackgroundResource(com.careavatar.core_ui.R.drawable.medical_types_selected_bg)
                for (i in 0 until layout.childCount) {
                    val child = layout.getChildAt(i)
                    if (child is android.widget.TextView) {
                        child.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                com.careavatar.core_ui.R.color.primaryColor
                            )
                        )
                    }
                    if (child is android.widget.ImageView) {
                        child.setColorFilter(
                            ContextCompat.getColor(
                                requireContext(),
                                com.careavatar.core_ui.R.color.primaryColor
                            ),
                            android.graphics.PorterDuff.Mode.SRC_IN
                        )
                    }
                }
                // Animation
                layout.animate().scaleX(0.95f).scaleY(0.95f).setDuration(80).withEndAction {
                    layout.animate().scaleX(1f).scaleY(1f).setDuration(80)
                }

                selectedDietType = label
                updateNextButtonState()
            }
        }
    }


    // ✅ Fetch allergies from API
    private fun hitFoodAllergies() {
        launchIfInternetAvailable {
            viewModel.hitFoodAllergies()
            viewModel.hitSpecificDiet()
        }
    }

    private fun observer() {

        collectApiResultOnStarted(viewModel.getDietFoodAllergiesResponse) { response ->
            val allergyList = response.data.map { it.foodAllergies to it._id }
            populateChipGroup(binding.chipGroup, allergyList) { selectedId ->
                selectedAllergyId = selectedId
                updateNextButtonState()
            }
        }


        collectApiResultOnStarted(viewModel.postQuestionDataResponse) { response ->
            showToast(response.msg.toString())
            if (response.success) {
                val intent = Intent(requireContext(), DietDashboardActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }

        }

        collectApiResultOnStarted(viewModel.specificDietResponseList) { response ->

            if (response.success){
                val specificDiet = response.data.map { it.specificDiet to it._id }
                populateChipGroup(binding.chipGroupdislike, specificDiet) { selectedId ->
                    specificDietId = selectedId
                    updateNextButtonState()
                }
            }

        }
    }

    private fun populateChipGroup(
        chipGroup: ChipGroup,
        data: List<Pair<String, String>>,
        onSelected: (String) -> Unit
    ) {
        chipGroup.isSingleSelection = true
        chipGroup.removeAllViews()

        data.forEach { (name, id) ->
            val chip = Chip(requireContext()).apply {
                text = name
                isCheckable = true
                isClickable = true
                isCheckedIconVisible = true
                checkedIcon = ContextCompat.getDrawable(
                    requireContext(),
                    com.careavatar.core_ui.R.drawable.complete_icon
                )
                chipCornerRadius = 50f
                chipBackgroundColor = ContextCompat.getColorStateList(
                    requireContext(),
                    com.careavatar.core_ui.R.color.chip_selector_bg
                )
                setTextColor(
                    ContextCompat.getColorStateList(
                        requireContext(),
                        com.careavatar.core_ui.R.color.chip_selector_text
                    )
                )
            }

            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    onSelected(id)   // ✅ return selected id
                }
            }

            chipGroup.addView(chip)
        }
    }



    // ✅ Validation
    private fun isInputValid(): Boolean {
        return when {
            selectedDietType.isNullOrEmpty() -> {
                showToast("Please select your dietary lifestyle")
                false
            }

//            selectedAllergies.isEmpty() -> {
//                showToast("Please select at least one food allergy")
//                false
//            }
//
//            selectedDislikes.isEmpty() -> {
//                showToast("Please select at least one food dislike")
//                false
//            }

            else -> true
        }
    }
}
