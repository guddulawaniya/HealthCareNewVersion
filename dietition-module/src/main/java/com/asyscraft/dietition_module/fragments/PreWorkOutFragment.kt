package com.asyscraft.dietition_module.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.asyscraft.dietition_module.RecipeDetailsActivity
import com.asyscraft.dietition_module.adapters.DietCategoryAdapter
import com.asyscraft.dietition_module.adapters.DietRecipeAdapter
import com.asyscraft.dietition_module.databinding.FragmentPreWorkOutBinding
import com.asyscraft.dietition_module.viewModels.DietitionViewModel
import com.careavatar.core_model.dietition.DietCategoryData
import com.careavatar.core_model.dietition.Recipedata
import com.careavatar.core_model.dietition.dietAddCartModelRequest
import com.careavatar.core_network.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PreWorkOutFragment : BaseFragment() {

    private lateinit var binding: FragmentPreWorkOutBinding
    private val viewModel: DietitionViewModel by viewModels()
    private val preWorkList = mutableListOf<Recipedata>()
    private val categoryList = mutableListOf<DietCategoryData>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPreWorkOutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        observeRecipeResponse()

        setupRecyclerView()
        hitCategoryList()
        hitGetRecipe(emptyList(), emptyList())

    }

    private fun setupRecyclerView() {
        binding.preWorkRecyclerview.layoutManager =
            GridLayoutManager(requireContext(), 2, LinearLayoutManager.VERTICAL, false)
        binding.preWorkRecyclerview.adapter = recipeAdapter

        binding.CategoryRecyclerview.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.CategoryRecyclerview.adapter = categoryAdapter
    }

    private val categoryAdapter by lazy {
        DietCategoryAdapter(requireContext(), categoryList) { position ->
            hitGetRecipe(emptyList(), listOf(categoryList[position]._id))
        }
    }
    private val recipeAdapter by lazy {
        DietRecipeAdapter(preWorkList) { position, type ->
            when (type) {
                1 -> {
                    val intent = Intent(requireContext(), RecipeDetailsActivity::class.java)
                    intent.putExtra("recipeId", preWorkList[position].id)
                    startActivity(intent)
                }

                2 -> {
                    hitAddCart(preWorkList[position].id)

                }
            }
        }
    }


    private fun hitCategoryList() {
        launchIfInternetAvailable {
            viewModel.hitCategoryList()
        }
    }

    private fun observeRecipeResponse() {
        collectApiResultOnStarted(viewModel.getRecipeResponse) { data ->
            preWorkList.clear()
            preWorkList.addAll(data.data)
            recipeAdapter.updateList(preWorkList)
        }

        collectApiResultOnStarted(viewModel.dietAddCartResponse) { data ->
            showToast("Added to Diet")
        }

        collectApiResultOnStarted(viewModel.getDietCategoryResponse) { data ->
            categoryList.clear()
            categoryList.addAll(data.data)
            categoryAdapter.notifyDataSetChanged()
        }

    }


    private fun hitAddCart(recipeId: String) {
        launchIfInternetAvailable {
            val request = dietAddCartModelRequest(recipeId, 1)
            viewModel.hitAddIntoCart(request)
        }
    }

    fun hitGetRecipe(dietytpe: List<String>, category: List<String>) {
        launchIfInternetAvailable {
            viewModel.hitRecipe(dietytpe, category, "preworkout")
        }
    }

}