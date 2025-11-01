package com.careavatar.core_model.medicalReminder

data class WaterHistoryResponse(
    val success: Boolean,
    val msg: String,
    val data: WaterData,
)

data class WaterData(
    val _id: String,
    val startDate: String,
    val targetWaterIntake: String,
    val intervalTime: String,
    val perSip: String,
    val user: String,
    val isBP: Boolean,
    val isSugar: Boolean,
    val BMI: String,
    val isWaterRemainder: Boolean,
    val isTaken: Boolean,
    val takenCount: String,
    val skippedCount: Long,
    val history: List<History>,
    val createdAt: String,
    val updatedAt: String,
    val __v: Long,
    val status: String,
    val flag: Boolean,
    val countTaken: String,
    val countSkipped: String,
    val nextIntervalTime: Any?,
    val takenWater: String,
    val targetWaterMl: String,
)

data class History(
    val isTaken: Boolean,
    val time: String,
    val perSip: String,
)




data class WaterChangeStatusRequest(
    val isTaken: Boolean,
)

data class WaterChangeStatusResponse(
    val success: Boolean,
    val msg: String,
)
