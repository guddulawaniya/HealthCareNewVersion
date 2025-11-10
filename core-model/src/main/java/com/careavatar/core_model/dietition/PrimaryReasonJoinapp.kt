package com.careavatar.core_model.dietition


data class PrimaryReasonJoinApp(
    val success: Boolean,
    val msg: String,
    val data: List<PrimaryReasonData>,
)

data class PrimaryReasonData(
    val _id: String,
    val primaryReason: String,
    val __v: Long,
)
