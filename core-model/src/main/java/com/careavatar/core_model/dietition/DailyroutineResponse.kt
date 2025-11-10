package com.careavatar.core_model.dietition


data class DailyRoutineResponse(
    val success: Boolean,
    val msg: String,
    val data: List<DailyRoutineData>,
)

data class DailyRoutineData(
    val _id: String,
    val routine: String,
    val __v: Long,
)
