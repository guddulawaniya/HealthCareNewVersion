package com.careavatar.core_model.alzimer

data class voice_response(
    val success: Boolean,
    val data: List<VoiceItem>,
    val msg: String
)


data class VoiceItem(
    val _id: String,
    val userId: String,
    val music: String,
    val userType: String,
    val createdAt: String,
    val updatedAt: String,
    val sender: Sender,
    val __v: Int,
    val image: String? // nullable because some items have null
)


data class Sender(
    val _id: String,
    val name: String,
    val image: String?,       // Nullable, since sometimes image is null
    val userType: String
)