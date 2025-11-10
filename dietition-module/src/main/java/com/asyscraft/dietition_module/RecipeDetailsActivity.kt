package com.asyscraft.dietition_module

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.asyscraft.dietition_module.adapters.DietRecipeAdapter
import com.asyscraft.dietition_module.databinding.ActivityRecipeDetailsBinding
import com.asyscraft.dietition_module.viewModels.DietitionViewModel
import com.bumptech.glide.Glide
import com.careavatar.core_model.dietition.Recipedata
import com.careavatar.core_model.dietition.dietAddCartModelRequest
import com.careavatar.core_network.base.BaseActivity
import com.careavatar.core_utils.Constants.Companion.IMAGE_BASEURL
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipeDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityRecipeDetailsBinding
    private val viewModel: DietitionViewModel by viewModels()
    private val recipeList = mutableListOf<Recipedata>()
    private var videoUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.tvTitle.text = "Recipe Details"
        binding.includeBtn.buttonNext.text = "Add Meal"
        binding.toolbar.btnBack.setOnClickListener {
            finish()
        }

        binding.videoThaumnail.setOnClickListener {
            if (!videoUrl.isNullOrEmpty())
                startActivity(
                    Intent(this, VideoPlayerActivity::class.java).putExtra(
                        "Video_URL",
                        videoUrl
                    )
                )
        }

        observeRecipeResponse()
        setupRecyclerView()
        hitGetRecipe(emptyList(), emptyList())
        hitGetRecipeDetails(intent.getStringExtra("recipeId") ?: "")
        setupSelectorClicks()
    }

    private fun setupSelectorClicks() {

        val recipe = binding.tvPoor
        val average = binding.tvAverage

        fun select(textView: TextView, other: TextView) {

            // ✅ Selected
            textView.setBackgroundResource(com.careavatar.core_ui.R.drawable.pre_constltant_form_bg)
            textView.setTextColor(
                ContextCompat.getColor(
                    this,
                    com.careavatar.core_ui.R.color.primaryColor
                )
            )

            // ✅ Unselected
            other.setBackgroundResource(android.R.color.transparent)
            other.setTextColor(
                ContextCompat.getColor(
                    this,
                    com.careavatar.core_ui.R.color.hintColor
                )
            )
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

    private fun setupRecyclerView() {
        binding.recipeRecyclerview.layoutManager =
            GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false)
        binding.recipeRecyclerview.adapter = recipeAdapter

    }

    private val recipeAdapter by lazy {
        DietRecipeAdapter(recipeList) { position, type ->
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


    private fun hitGetRecipe(dietytpe: List<String>, category: List<String>) {
        launchIfInternetAvailable {
            viewModel.hitRecipe(dietytpe, category, "recipe")
        }
    }

    private fun observeRecipeResponse() {
        collectApiResultOnStarted(viewModel.getRecipeResponse) { data ->
            recipeList.clear()
            recipeList.addAll(data.data)
            recipeAdapter.updateList(recipeList)
        }

        collectApiResultOnStarted(viewModel.dietAddCartResponse) { data ->
            showToast("Added to Diet")
        }
        collectApiResultOnStarted(viewModel.recipeResponseDetails) { apiResult ->
            updateUI(apiResult.data)
        }

    }

    private fun updateUI(data: Recipedata) {
        videoUrl = data.video.toString()
        val diseases = data.restrictedInDisease.mapNotNull { it }.joinToString(", ")

        binding.apply {
            titlename.text = data.recipeName.orEmpty() + data.quantity.toString()
            tvDesciption.text =
                data.idealTime.joinToString(" , ") + " " + data.recipeStep + "Disease : " + diseases + "  Gut Problem Issue : " + data.giProblemCausing.toString()
            " inflammation Issue : " + data.inflammationCausing.toString()


            Glide.with(this@RecipeDetailsActivity)
                .load(IMAGE_BASEURL + data.image.trim())
                .placeholder(com.careavatar.core_ui.R.drawable.diet_image)
                .into(videoThaumnail)
        }

    }


    private fun hitGetRecipeDetails(recipeId: String) {
        launchIfInternetAvailable {
            viewModel.hitRecipeByid(recipeId)
        }
    }


    private fun hitAddCart(recipeId: String) {
        launchIfInternetAvailable {
            val request = dietAddCartModelRequest(recipeId, 1)
            viewModel.hitAddIntoCart(request)
        }
    }
}