package com.careavatar.core_model.alzimer

data class registration_response(
    val success: Boolean,
    val msg: String,
    val user: User
)


data class User(
    val _id: String,
    val phoneNumber: String,
    val name: String,
    val isFirstTime: Boolean,
    val hobbies: List<Any>,
    val DonateItemList: List<Any>,
    val donatePageFirstTime: Boolean,
    val communityPageFirstTime: Boolean,
    val latitude: String,
    val longitude: String,
    val sendMessages: List<Any>,
    val receivedMessages: List<Any>,
    val communityMessages: List<Any>,
    val address: String,
    val gender: String,
    val timeZone: String,
    val status: Boolean,
    val isBP: Boolean,
    val isSugar: Boolean,
    val userType: String,
    val subscriptions: List<Any>,
    val recentItems: List<Any>,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int,
    val avatar: String,
    val dob: String,
    val email: String,
    val height: Double,
    val relativecontact: String,
    val weight: Double
)