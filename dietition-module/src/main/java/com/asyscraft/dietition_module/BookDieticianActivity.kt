package com.asyscraft.dietition_module

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.asyscraft.dietition_module.adapters.DietitianAdapter
import com.asyscraft.dietition_module.databinding.ActivityBookDieticianBinding
import com.asyscraft.dietition_module.viewModels.DietitionViewModel
import com.careavatar.core_model.dietition.GetExpertResponse2
import com.careavatar.core_network.base.BaseActivity
import com.careavatar.core_utils.setupSearchFilter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookDieticianActivity : BaseActivity() {
    private lateinit var binding: ActivityBookDieticianBinding
    private val viewModel: DietitionViewModel by viewModels()
    private lateinit var adapter: DietitianAdapter
    private val dataList = mutableListOf<GetExpertResponse2.Expert>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookDieticianBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.tvTitle.text = "Book a Dietician"
        binding.toolbar.btnBack.setOnClickListener { finish() }

//        binding.includedbtn.buttonNext.text = "Select Session"
//        binding.includedbtn.buttonNext.isEnabled = false
//        binding.includedbtn.buttonNext.setOnClickListener {
//            if (::expertData.isInitialized){
//                startActivity(Intent(this, BookDieticianActivity::class.java)
//                    .putExtra("expertDetails",expertData))
//            }
//        }

        setupSearchFilter()
        hitExpertList()
        setUpRecyclerview()
        observer()
    }

    private fun setupSearchFilter() {
        binding.searchBar.etSearch.hint = "Search recipes, ingredients..."
        binding.searchBar.etSearch.setupSearchFilter(
            dataList,
            filterCondition = { item, query ->
                item.expert.name.orEmpty().contains(query, ignoreCase = true)

            }
        ) { filteredList ->
            adapter.updateList(filteredList.toMutableList())
        }

    }

    private fun observer() {
        collectApiResultOnStarted(viewModel.getGetExpertResponse2) {
            dataList.clear()
            dataList.addAll(it.experts)
           adapter.notifyDataSetChanged()
        }

    }

    private fun hitExpertList() {
        launchIfInternetAvailable {
            viewModel.hitGetFitnessExpert("diet")
        }

    }

    private fun setUpRecyclerview() {
        adapter = DietitianAdapter(dataList, DietitianAdapter.LAYOUT_NORMAL){
            startActivity(Intent(this, DietitionDetailsActivity::class.java)
                    .putExtra("expertDetails",it))


        }
        binding.recyclerview.adapter = adapter
        binding.recyclerview.layoutManager = LinearLayoutManager(this)

    }
}