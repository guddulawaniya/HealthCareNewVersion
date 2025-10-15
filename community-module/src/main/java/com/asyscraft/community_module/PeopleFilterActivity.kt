package com.asyscraft.community_module

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import com.asyscraft.community_module.databinding.ActivityPeopleFilterBinding
import com.careavatar.core_network.base.BaseActivity

import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PeopleFilterActivity : BaseActivity() {

    private lateinit var binding: ActivityPeopleFilterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPeopleFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.crossbtn.setOnClickListener {
            finish()
        }

        val options = listOf("Yoga Fitness","Autism Care","Alzheimer’s Support","Autism Support","General Wellness","Caregiver Tools","Emergency Alert","Community Support")

        val primarycolor = ContextCompat.getColor(this, com.careavatar.core_ui.R.color.primaryColor)
        val secondarycolor = ContextCompat.getColor(this, com.careavatar.core_ui.R.color.secondaryColor)
        val selectedCardbg = ContextCompat.getColor(this, com.careavatar.core_ui.R.color.selectedCardbg)


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
                        intArrayOf(secondarycolor,primarycolor)
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
                    intArrayOf(secondarycolor,primarycolor)
                )

                // Rounded corners
                shapeAppearanceModel = shapeAppearanceModel.toBuilder()
                    .setAllCornerSizes(15f) // corner radius px
                    .build()
            }
            binding.chipGroup.addView(chip)
        }


        binding.apply {
            chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
                if (checkedIds.isNotEmpty()) {
                    btninclude.buttonNext.isEnabled = true
                    btninclude.buttonNext.backgroundTintList = ColorStateList.valueOf("#4CAF50".toColorInt())


                } else {
                    btninclude.buttonNext.isEnabled = false
                    btninclude.buttonNext.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
                }
            }

            btninclude.buttonNext.setOnClickListener {
                val selectedOptions = chipGroup.checkedChipIds.map { id ->
                    val chip = chipGroup.findViewById<Chip>(id)
                    chip.text.toString()
                }

              finish()
            }
        }


    }
}