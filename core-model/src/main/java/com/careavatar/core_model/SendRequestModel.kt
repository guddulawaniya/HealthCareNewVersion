package com.careavatar.core_model



data class SendRequestModel(
    val success: Boolean,
    val message: String,
)


data class SendRequestModelRequestBody(
    val receiverId: String,
)
