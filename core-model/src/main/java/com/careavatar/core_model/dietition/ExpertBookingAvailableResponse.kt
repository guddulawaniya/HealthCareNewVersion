package com.careavatar.core_model.dietition

data class ExpertBookingAvailableResponse(
    val availableSlots: List<AvailableSlot>,
    val message: String,
    val success: Boolean
) {
    data class AvailableSlot(
        val __v: Int,
        val _id: String,
        val createdAt: String,
        val creatorId: String,
        val day: String,
        val endTime: String,
        val startTime: String,
        val status: String,
        val updatedAt: String
    )
}

class ExpertRequest (
    val expertId: String,
    val date: String
)