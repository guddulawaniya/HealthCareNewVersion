package com.careavatar.core_model.alzimer

data class getuserdetail_response(
    val success: Boolean,
    val msg: String,
    val user: getUser
)


data class getUser(
    val _id: String,
    val phoneNumber: String,
    val isFirstTime: Boolean,
//    val donatePageFirstTime: Boolean,
//    val communityPageFirstTime: Boolean,
    val latitude: String,
    val longitude: String,
//    val timeZone: String,
    val fcmToken: String,
//    val status: Boolean,
//    val isBP: Boolean,
//    val isSugar: Boolean,
    val userType: String,
//    val createdAt: String,
//    val updatedAt: String,
//    val __v: Int,
//    val avatar: String,
    val dob: String,
//    val email: String,
    val gender: String,
//    val height: Int,
    val name: String
//    val relativecontact: String,
//    val weight: Int,
//    val address: String,
//    val diseasePageFirstTime: Boolean,
//    val parentalControlePageFirstTime: Boolean,
//    val fitnessPageFirstTime: Boolean,
//    val isParent: Boolean,
//    val isChild: Boolean,
//    val notificationCount: Int
)