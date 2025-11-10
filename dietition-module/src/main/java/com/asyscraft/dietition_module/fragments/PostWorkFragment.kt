package com.asyscraft.dietition_module.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.asyscraft.dietition_module.R
import com.asyscraft.dietition_module.RecipeDetailsActivity
import com.asyscraft.dietition_module.adapters.DietCategoryAdapter
import com.asyscraft.dietition_module.adapters.DietRecipeAdapter
import com.asyscraft.dietition_module.databinding.FragmentPostWorkBinding
import com.asyscraft.dietition_module.viewModels.DietitionViewModel
import com.careavatar.core_model.dietition.DietCategoryData
import com.careavatar.core_model.dietition.Recipedata
import com.careavatar.core_model.dietition.dietAddCartModelRequest
import com.careavatar.core_network.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class PostWorkFragment : BaseFragment() {

    private lateinit var binding: FragmentPostWorkBinding
    private val viewModel: DietitionViewModel by viewModels()
    private val postWorkList = mutableListOf<Recipedata>()
    private val categoryList = mutableListOf<DietCategoryData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostWorkBinding.inflate(inflater, container, false)
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
        binding.recipeRecyclerview.layoutManager = GridLayoutManager(requireContext(),2, LinearLayoutManager.VERTICAL, false)
        binding.recipeRecyclerview.adapter = recipeAdapter

        binding.CategoryRecyclerview.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.CategoryRecyclerview.adapter = categoryAdapter
    }
    private val categoryAdapter by lazy {
        DietCategoryAdapter(requireContext(),categoryList) { position ->
            hitGetRecipe(emptyList(), listOf(categoryList[position]._id))
        }
    }

    private val recipeAdapter by lazy {
        DietRecipeAdapter(postWorkList) { position, type ->
            when (type) {
                1 -> {
                    val intent = Intent(requireContext(), RecipeDetailsActivity::class.java)
                    intent.putExtra("recipeId", postWorkList[position].id)
                    startActivity(intent)
                }

                2 -> {
                    hitAddCart(postWorkList[position].id)

                }
            }
        }
    }

    private fun observeRecipeResponse() {
        collectApiResultOnStarted(viewModel.getRecipeResponse) { data ->
            postWorkList.clear()
            postWorkList.addAll(data.data)
            recipeAdapter.updateList(postWorkList)
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


    private fun hitCategoryList() {
        launchIfInternetAvailable {
            viewModel.hitCategoryList()
        }
    }

    fun hitGetRecipe(dietytpe: List<String>, category: List<String>) {
        launchIfInternetAvailable {
            viewModel.hitRecipe(dietytpe, category,"postworkout")
        }
    }

}