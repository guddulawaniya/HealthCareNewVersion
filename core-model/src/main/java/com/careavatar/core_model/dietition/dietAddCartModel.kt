package com.careavatar.core_model.dietition


data class DietAddCartModelResponse(
    val success: Boolean,
    val msg: String,
    val totalCalories: Double,
    val cartItemCount: Long,
    val userNeedCalories: Long,
)



data class dietAddCartModelRequest(
    val recipeId: String,
    val quantity: Long,
)

data class dietRemoveRecipeFromchartRequest(
    val chartId: String,
    val receipeId: String,
    val action: String,
)


data class AddCartListResponse(
    val success: Boolean,
    val totalCalories: Double,
    val userNeedCalories: Double,
    val data: List<CartListdata>,
)

data class CartListdata(
    val recipeId: Recipedata,
    val quantity: Long,
    val _id: String,

)




