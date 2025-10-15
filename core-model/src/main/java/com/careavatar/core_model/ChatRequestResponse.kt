package com.careavatar.core_model

data class ChatRequestResponse(
    val success: Boolean,
    val msg: String,
    val requests: List<Any?>,
)

data class ApproveRejectResponse(
    val message: String
)

data class ApproveRejectRequest(
    val requestId:String,
    val action:String
)