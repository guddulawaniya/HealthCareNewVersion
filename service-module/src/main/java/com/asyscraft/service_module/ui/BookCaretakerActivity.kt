package com.asyscraft.service_module.ui

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.asyscraft.service_module.databinding.ActivityBookCaretakerBinding
import com.asyscraft.service_module.ui.adapters.BookCaretakerAdapter
import com.careavatar.core_model.BookCaretakerModel
import com.careavatar.core_network.base.BaseActivity
import com.careavatar.core_ui.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookCaretakerActivity : BaseActivity() {
    private lateinit var binding : ActivityBookCaretakerBinding
    private val dataList = mutableListOf<BookCaretakerModel>()
    private lateinit var adapter: BookCaretakerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookCaretakerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.btnBack.setOnClickListener {
            finish()
        }
        binding.toolbar.tvTitle.text = getString(R.string.book_caretaker)

        setRecyclerView()
    }

    private fun setRecyclerView(){

        dataList.addAll(
            listOf(
                BookCaretakerModel(
                    "For Elderly People",
                    "specialised care for seniors, focusing on their comfort and well-being",
                    R.drawable.elderly_carebook_icon
                ),
                BookCaretakerModel(
                    "For Patient",
                    "Comprehensive care for patient ensuring their health and recovery",
                    R.drawable.patient_carebook_icon
                ),
                BookCaretakerModel(
                    "For Kids",
                    "Dedicated care for children, focusing on their safety and development",
                    R.drawable.kid_carebook_icon
                ),
                BookCaretakerModel(
                    "Post Surgery Care",
                    "Focused care for post operative patients aiding in their recovery process",
                    R.drawable.hospital_support_icon
                )
            )
        )

        adapter = BookCaretakerAdapter(this,dataList,onClickListener = {
            startActivity(Intent(this, CareTakersActivity::class.java))
        })
        binding.bookRecylerview.adapter = adapter
        binding.bookRecylerview.layoutManager = LinearLayoutManager(this)
    }
}