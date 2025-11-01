package com.careavatar.core_model.alzimer

data class FormRequest(
    val fullName: String,
    val age: Int,
    val gender: String,
    val location: String? = "",
    val type: String? = "",
    val latitude: String? = "",
    val longitude: String? = "",
    val phoneNumber: String? = "",
    val image: String? = "",
    val fcmToken: String

)
