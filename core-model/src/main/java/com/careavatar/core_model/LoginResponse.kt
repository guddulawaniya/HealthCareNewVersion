package com.careavatar.core_model

data class LoginResponse(
    val msg: String,
    val otp: String,
    val phoneNumber: String,
    val success: Boolean
)

data class LoginRequest(
    val phoneNumber:String
)

data class VerifyOtpRequest(
    val phoneNumber:String,
    val otp:String,
    val fcmToken:String,
)

data class VerifyOtpResponse(
    val isFirstTime: Boolean,
    val success: Boolean,
    val token: String,
    val msg: String,
    val id: String,
)
