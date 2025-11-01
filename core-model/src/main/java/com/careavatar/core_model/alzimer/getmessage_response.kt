package com.careavatar.core_model.alzimer

data class getmessage_response(
    val success: Boolean,
    val count: Int,
    val messages: List<MessageData>
)


data class MessageData(
    val _id: String,
//    val alzheimerId: String,
//    val careGiverId: String,
    val message: String,
    val scheduleTime: String?, // Nullable, since it can be null
    val sent: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val image: String,
    val __v: Int,
    val senderInfo: SenderInfo
)


data class SenderInfo(
    val userType: String,
    val _id: String,
    val name: String,
    val image: String
)