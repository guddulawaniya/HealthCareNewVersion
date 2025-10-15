package com.asyscraft.service_module.ui

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.asyscraft.service_module.databinding.ActivityCareTakersBinding
import com.asyscraft.service_module.ui.adapters.CareTakerAdapter
import com.careavatar.core_model.CareTakerModel
import com.careavatar.core_network.base.BaseActivity
import com.careavatar.core_ui.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CareTakersActivity : BaseActivity() {
    private lateinit var binding: ActivityCareTakersBinding
    private lateinit var adapter: CareTakerAdapter
    private val dataList = mutableListOf<CareTakerModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCareTakersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.btnBack.setOnClickListener {
            finish()
        }
        binding.toolbar.tvTitle.text = getString(R.string.care_takers)


        setRecyclerView()
    }
    private fun setRecyclerView(){

        dataList.addAll(
            listOf(
                CareTakerModel(
                    "Emily Pakston",
                    "Available Mon-Fri",
                    "3+",
                    "35",
                    "5",
                    R.drawable.service_userprofile_image_sample
                ), CareTakerModel(
                    "Emily Pakston",
                    "Available Mon-Fri",
                    "3+",
                    "35",
                    "5",
                    R.drawable.sample_image
                ),
                CareTakerModel(
                    "Emily Pakston",
                    "Available Mon-Fri",
                    "3+",
                    "35",
                    "5",
                    R.drawable.service_userprofile_image_sample
                ), CareTakerModel(
                    "Emily Pakston",
                    "Available Mon-Fri",
                    "3+",
                    "35",
                    "5",
                    R.drawable.sample_image
                ),
                CareTakerModel(
                    "Emily Pakston",
                    "Available Mon-Fri",
                    "3+",
                    "35",
                    "5",
                    R.drawable.group_sample
                ), CareTakerModel(
                    "Emily Pakston",
                    "Available Mon-Fri",
                    "3+",
                    "35",
                    "5",
                    R.drawable.sample_image
                ),
                CareTakerModel(
                    "Emily Pakston",
                    "Available Mon-Fri",
                    "3+",
                    "35",
                    "5",
                    R.drawable.service_userprofile_image_sample
                ), CareTakerModel(
                    "Emily Pakston",
                    "Available Mon-Fri",
                    "3+",
                    "35",
                    "5",
                    R.drawable.sample_image
                ),
            )
        )


        adapter = CareTakerAdapter(this,dataList,onClickListener = {
            startActivity(Intent(this, careTaker_DetailsActivity::class.java))
        })
        binding.careTakerRecyclerview.adapter = adapter
        binding.careTakerRecyclerview.layoutManager = GridLayoutManager(this,2)

    }
}