package com.careavatar.core_model.alzimer

data class login_response (
    val success: Boolean,
    val msg: String,
    val otp: String,
    val phoneNumber: String

)


data class login_request(
    val phoneNumber: String
)


data class otp_response(
    val success: Boolean,
    val token: String,
    val isFirstTime: Boolean,
    val id : String
)


data class otp_request(
    val phoneNumber: String,
    val otp: String,
    val latitude: String,
    val longitude: String,
    val fcmToken: String
)