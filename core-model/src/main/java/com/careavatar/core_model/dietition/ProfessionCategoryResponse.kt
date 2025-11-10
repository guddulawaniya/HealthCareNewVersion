package com.careavatar.core_model.dietition



data class ProfessionCategoryResponse(
    val success: Boolean,
    val msg: String,
    val data: List<Questiondata>,
)

data class Questiondata(
    val _id: String,
    val profession: String,
    val type: String,
    val __v: Long,
)

