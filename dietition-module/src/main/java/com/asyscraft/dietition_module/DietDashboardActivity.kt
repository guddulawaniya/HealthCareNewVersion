package com.asyscraft.dietition_module

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.asyscraft.dietition_module.adapters.DietRecipeAdapter
import com.asyscraft.dietition_module.adapters.DietitianAdapter
import com.asyscraft.dietition_module.adapters.FamousDietRecipeAdapter
import com.asyscraft.dietition_module.databinding.ActivityDietDashbaordBinding
import com.asyscraft.dietition_module.viewModels.DietitionViewModel
import com.careavatar.core_model.dietition.GetExpertResponse2
import com.careavatar.core_model.dietition.Recipedata
import com.careavatar.core_model.dietition.dietAddCartModelRequest
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DietDashboardActivity : BaseActivity() {

    private lateinit var binding: ActivityDietDashbaordBinding
    private val viewModel: DietitionViewModel by viewModels()
    private lateinit var adapter: DietitianAdapter
    private val dataList = mutableListOf<GetExpertResponse2.Expert>()

    private val recipeList = mutableListOf<Recipedata>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDietDashbaordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        binding.toolbar.btnBack.setOnClickListener { finish() }
        binding.toolbar.tvTitle.text = "Dietician"


        hitExpertList()
        setUpRecyclerview()
        observer()
        handleBtn()
        hitAddIntoCartList()
        setupRecyclerView()
        hitGetRecipe(emptyList(), emptyList())
    }

    private fun setupRecyclerView() {
        binding.recipeRecyclerview.layoutManager = LinearLayoutManager(this,  LinearLayoutManager.HORIZONTAL, false)
        binding.recipeRecyclerview.adapter = recipeAdapter

    }

    private fun hitGetRecipe(dietytpe: List<String>, category: List<String>) {
        launchIfInternetAvailable {
            viewModel.hitRecipe(dietytpe, category, "recipe")
        }
    }

    private val recipeAdapter by lazy {
        FamousDietRecipeAdapter(recipeList) { position, type ->
            when (type) {
                1 -> {
                    val intent = Intent(this, RecipeDetailsActivity::class.java)
                    intent.putExtra("recipeId", recipeList[position].id)
                    startActivity(intent)
                }

                2 -> {
                    hitAddCart(recipeList[position].id)

                }
            }
        }
    }

    private fun hitAddCart(recipeId: String) {
        launchIfInternetAvailable {
            val request = dietAddCartModelRequest(recipeId, 1)
            viewModel.hitAddIntoCart(request)
        }
    }

    private fun handleBtn() {
        binding.BookDieticianButton.setOnClickListener {
            startActivity(Intent(this, BookDieticianActivity::class.java))
        }
        binding.createDietPlanBtn.setOnClickListener {
            startActivity(Intent(this, RecipeListActivity::class.java))
        }

        binding.viewAllExpert.setOnClickListener {
            startActivity(Intent(this, BookDieticianActivity::class.java))
        }
    }

    private fun setUpRecyclerview() {
        adapter = DietitianAdapter(dataList, DietitianAdapter.LAYOUT_ALT) {
            startActivity(
                Intent(this, DietitionDetailsActivity::class.java)
                    .putExtra("expertDetails", it)
            )
        }
        binding.dietitionRecyclerview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.dietitionRecyclerview.adapter = adapter
    }


    private fun hitExpertList() {
        launchIfInternetAvailable {
            viewModel.hitGetFitnessExpert("diet")
        }

    }

    private fun hitAddIntoCartList() {
        launchIfInternetAvailable {
            viewModel.hitAddIntoCartList()

        }
    }


    private fun observer() {
        collectApiResultOnStarted(viewModel.getGetExpertResponse2) {
            dataList.clear()
            dataList.addAll(it.experts)
            adapter.notifyDataSetChanged()
        }

        collectApiResultOnStarted(viewModel.getRecipeResponse) { data ->
            recipeList.clear()
            recipeList.addAll(data.data)
            recipeAdapter.updateList(recipeList)
        }

        collectApiResultOnStarted(viewModel.dietAddCartResponse) { data ->
            showToast("Added to Diet")
        }

    }


}