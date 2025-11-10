package com.careavatar.core_model.dietition

data class SpecificDietResponse(
    val success: Boolean,
    val msg: String,
    val data: List<SpecificDietdata>,
)

data class SpecificDietdata(
    val _id: String,
    val specificDiet: String,
    val __v: Long,
)
