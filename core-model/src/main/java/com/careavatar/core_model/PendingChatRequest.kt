package com.careavatar.core_model

data class PendingChatRequest(
    val success: Boolean,
    val data: List<Pendingdata>,
)

data class Pendingdata(
    val _id: String,
    val community: String,
    val communityId: String,
    val requester: Requester,
    val status: String,
    val joininingId: String,
    val requestDate: String,
)

data class Requester(
    val _id: String,
    val name: String,
    val email: String,
    val profileImage: String,
)
