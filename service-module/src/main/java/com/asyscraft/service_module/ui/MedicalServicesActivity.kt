package com.asyscraft.service_module.ui

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.asyscraft.service_module.databinding.ActivityMedicalServicesBinding
import com.asyscraft.service_module.ui.adapters.MedicalServicesAdapter
import com.careavatar.core_model.BookCaretakerModel
import com.careavatar.core_network.base.BaseActivity
import com.careavatar.core_ui.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MedicalServicesActivity : BaseActivity() {
    private lateinit var binding: ActivityMedicalServicesBinding

    private val datalist = mutableListOf<BookCaretakerModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMedicalServicesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.apply {
            btnBack.setOnClickListener {
                finish()
            }
            tvTitle.text = getString(R.string.medical_services)
        }

        setRecyclerView()
    }

    private fun setRecyclerView() {
        datalist.addAll(
            listOf(
                BookCaretakerModel(
                    "Oxygen Cylinder",
                    "Available",
                    R.drawable.oxygen_cylinder_image
                ),
                BookCaretakerModel(
                    "Wheelchair",
                    "Available",
                    R.drawable.wheelchair_image
                ),
                BookCaretakerModel(
                    "Blood",
                    "Not Available",
                    R.drawable.blood_image
                ),
                BookCaretakerModel(
                    "Medical Bed",
                    "Available",
                    R.drawable.medical_bed_image
                )
            )
        )

        binding.bookRecylerview.adapter = MedicalServicesAdapter(this, datalist, onClickItem = {
            startActivity(Intent(this, OrderDetailsActivity::class.java))
        })
        binding.bookRecylerview.layoutManager = GridLayoutManager(this, 2)
        binding.bookRecylerview.addItemDecoration(
            GridSpacingItemDecoration(2, dpToPx(15), true)
        )
    }

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }

}