package com.asyscraft.dietition_module

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.asyscraft.dietition_module.databinding.ActivityPreConsultationFormBinding
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PreConsultationFormActivity : BaseActivity() {
    private lateinit var binding: ActivityPreConsultationFormBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreConsultationFormBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setupSelectorClicks()
    }

    private fun setupSelectorClicks() {

        val recipe = binding.tvPoor
        val average = binding.tvAverage

        fun select(textView: TextView, other: TextView) {

            // ✅ Selected
            textView.setBackgroundResource(com.careavatar.core_ui.R.drawable.pre_constltant_form_bg)
            textView.setTextColor(ContextCompat.getColor(this, com.careavatar.core_ui.R.color.primaryColor))

            // ✅ Unselected
            other.setBackgroundResource(android.R.color.transparent)
            other.setTextColor(ContextCompat.getColor(this, com.careavatar.core_ui.R.color.hintColor))
        }

        // ✅ Default selected = Recipe
        select(recipe, average)

        recipe.setOnClickListener {
            select(recipe, average)
        }

        average.setOnClickListener {
            select(average, recipe)
        }
    }

}