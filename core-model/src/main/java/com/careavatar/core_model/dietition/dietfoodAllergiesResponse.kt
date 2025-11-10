package com.careavatar.core_model.dietition



data class DietFoodAllergiesResponse(
    val success: Boolean,
    val msg: String,
    val data: List<DietFoodAllergiesData>,
)

data class DietFoodAllergiesData(
    val _id: String,
    val foodAllergies: String,
    val __v: Long,
)
