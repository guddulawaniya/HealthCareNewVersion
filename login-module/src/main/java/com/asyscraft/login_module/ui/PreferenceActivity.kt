package com.asyscraft.login_module.ui

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import com.asyscraft.login_module.databinding.ActivityPreferenceBinding
import com.asyscraft.login_module.viewModel.LoginViewModel
import com.careavatar.core_model.Category
import com.careavatar.core_model.CategoryPost
import com.careavatar.core_model.GetCategoryRquest

import com.careavatar.core_network.base.BaseActivity
import com.careavatar.core_service.repository.viewModels.OnboardingRepositoryUserHobbies
import com.careavatar.core_ui.R
import com.careavatar.core_service.repository.viewModels.OnboardingViewModel
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject

@AndroidEntryPoint
class PreferenceActivity : BaseActivity() {
    private lateinit var binding: ActivityPreferenceBinding
    private val viewModelLogin: LoginViewModel by viewModels()
    private val categoryList = mutableListOf<Category>()


    @Inject
    lateinit var repository: OnboardingRepositoryUserHobbies

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreferenceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fetchCategoryData()
        observeViewModel()
        handleSelectOptions()

    }

    private fun handleSelectOptions(){
        binding.apply {
            chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
                if (checkedIds.isNotEmpty()) {
                    btnNext.isEnabled = true
                    btnNext.backgroundTintList = ColorStateList.valueOf("#4CAF50".toColorInt())


                } else {
                    btnNext.isEnabled = false
                    btnNext.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
                }
            }

            btnNext.setOnClickListener {
                val selectedOptions = chipGroup.checkedChipIds.map { id ->
                    val chip = chipGroup.findViewById<Chip>(id)
                    chip.text.toString()
                }

                repository.onboardingInfo.userPreferences = selectedOptions

                Log.d("SelectedChipsFinal", selectedOptions.toString())

                startActivity(Intent(this@PreferenceActivity, LoadingActivity::class.java))
            }
        }
    }

    private fun observeViewModel() = with(viewModelLogin) {

        collectApiResultOnStarted(getCategoryListResponse) {
            if (it.status) {
                categoryList.clear()
                categoryList.addAll(it.categories)
                showOption()

            }
        }

    }

    private fun showOption(){
        val primarycolor = ContextCompat.getColor(this, R.color.primaryColor)
        val secondarycolor = ContextCompat.getColor(this, R.color.secondaryColor)
        val selectedCardbg = ContextCompat.getColor(this, R.color.selectedCardbg)


        val options = categoryList.map { it.name }

        options.forEach { text ->
            val chip = Chip(this).apply {
                this.text = text
                isCheckable = true
                checkedIcon = null

                // Text color states
                setTextColor(
                    ColorStateList(
                        arrayOf(
                            intArrayOf(android.R.attr.state_checked),
                            intArrayOf(-android.R.attr.state_checked)
                        ),
                        intArrayOf(secondarycolor, primarycolor)
                    )
                )

                // Background colors
                chipBackgroundColor = ColorStateList(
                    arrayOf(
                        intArrayOf(android.R.attr.state_checked),
                        intArrayOf(-android.R.attr.state_checked)
                    ),
                    intArrayOf(selectedCardbg, Color.WHITE)
                )

                // Outline stroke
                chipStrokeWidth = 2f
                chipStrokeColor = ColorStateList(
                    arrayOf(
                        intArrayOf(android.R.attr.state_checked),
                        intArrayOf(-android.R.attr.state_checked)
                    ),
                    intArrayOf(secondarycolor, primarycolor)
                )

                // Rounded corners
                shapeAppearanceModel = shapeAppearanceModel.toBuilder()
                    .setAllCornerSizes(15f) // corner radius px
                    .build()
            }
            binding.chipGroup.addView(chip)
        }
    }

    private fun fetchCategoryData() {
        launchIfInternetAvailable {
            viewModelLogin.hitGetCategoryList()
        }
    }
}