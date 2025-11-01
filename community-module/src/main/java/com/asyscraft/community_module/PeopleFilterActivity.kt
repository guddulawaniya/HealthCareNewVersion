package com.asyscraft.community_module

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.asyscraft.community_module.databinding.ActivityPeopleFilterBinding
import com.asyscraft.community_module.viewModels.SocialMeetViewmodel
import com.careavatar.core_model.CategoryPost
import com.careavatar.core_model.GetCategoryRquest
import com.careavatar.core_network.base.BaseActivity
import com.careavatar.core_ui.R
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PeopleFilterActivity : BaseActivity() {

    private lateinit var binding: ActivityPeopleFilterBinding
    private val viewModel: SocialMeetViewmodel by viewModels()

    private val categoryList = mutableListOf<CategoryPost>()

    private var selectedDistance: Int = 50
    private var selectedCategory: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPeopleFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btninclude.buttonNext.text = "Apply"

        binding.crossbtn.setOnClickListener {
            finish()
        }
        binding.btninclude.buttonNext.setOnClickListener {

            if (selectedCategory.isNullOrEmpty()) {
                showToast("Please select category")
                return@setOnClickListener
            }

            val resultIntent = Intent().apply {
                putExtra("distance", selectedDistance)
                putExtra("categoryId", selectedCategory)

            }

            setResult(RESULT_OK, resultIntent)
            finish()
        }


        binding.distanceSeekBar.addOnChangeListener { slider, value, fromUser ->
            if (fromUser) {

                selectedDistance = value.toInt()
                binding.distanceTextView.text = "$selectedDistance km"
                lifecycleScope.launch {
                    userPref.setRaduisCircle(selectedDistance.toString())
                }
            }
        }

        fetchCategoryData()
        observeViewModel()

    }

    private fun observeViewModel() = with(viewModel) {

        collectApiResultOnStarted(categoryListResponse) {
            if (it.status) {
                categoryList.clear()
                categoryList.addAll(it.categories)
                showOption()

            }
        }

    }

    private fun showOption() {
        val primaryColor = ContextCompat.getColor(this, com.careavatar.core_ui.R.color.primaryColor)
        val secondaryColor = ContextCompat.getColor(this, com.careavatar.core_ui.R.color.secondaryColor)
        val selectedCardBg = ContextCompat.getColor(this, R.color.selectedCardbg)

        binding.chipGroup.removeAllViews() // Clear old chips before adding new ones
        binding.chipGroup.isSingleSelection = true // ✅ Only one chip can be selected

        categoryList.forEach { category ->
            val chip = Chip(this).apply {
                text = category.name
                isCheckable = true
                checkedIcon = null
                tag = category.id // ✅ Store category ID in tag

                // Text color states
                setTextColor(
                    ColorStateList(
                        arrayOf(
                            intArrayOf(android.R.attr.state_checked),
                            intArrayOf(-android.R.attr.state_checked)
                        ),
                        intArrayOf(secondaryColor, primaryColor)
                    )
                )

                // Background colors
                chipBackgroundColor = ColorStateList(
                    arrayOf(
                        intArrayOf(android.R.attr.state_checked),
                        intArrayOf(-android.R.attr.state_checked)
                    ),
                    intArrayOf(selectedCardBg, Color.WHITE)
                )

                // Outline stroke
                chipStrokeWidth = 2f
                chipStrokeColor = ColorStateList(
                    arrayOf(
                        intArrayOf(android.R.attr.state_checked),
                        intArrayOf(-android.R.attr.state_checked)
                    ),
                    intArrayOf(secondaryColor, primaryColor)
                )

                // Rounded corners
                shapeAppearanceModel = shapeAppearanceModel.toBuilder()
                    .setAllCornerSizes(15f)
                    .build()
            }

            binding.chipGroup.addView(chip)
        }

        // ✅ Listener for selection
        binding.chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val checkedChipId = checkedIds[0]
                val chip = group.findViewById<Chip>(checkedChipId)
                val selectedCategoryId = chip.tag as String
                val selectedCategoryName = chip.text.toString()

                // Use this ID as needed
                Log.d("SelectedCategory", "ID = $selectedCategoryId, Name = $selectedCategoryName")

                // Example: store in a variable if needed
                selectedCategory = selectedCategoryId
            }
        }
    }


    private fun fetchCategoryData() {

        val request = GetCategoryRquest("0")
        launchIfInternetAvailable {
            viewModel.hitGetCategoryList(request)
        }
    }
}