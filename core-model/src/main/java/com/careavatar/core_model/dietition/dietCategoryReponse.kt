package com.careavatar.core_model.dietition


data class DietCategoryResponse(
    val success: Boolean,
    val data: List<DietCategoryData>,
)

data class DietCategoryData(

    val _id: String,
    val name: String,
    val description: String,
    val image: String,
    val __v: Long,
)

data class TakenRecipeResponse(
    val success: Boolean,
    val msg : String,
)


data class TakenRecipeRequest(
    val dietChartId: String,
    val recipeId: String,
    val status: Int,
)
