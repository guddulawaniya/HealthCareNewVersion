package com.careavatar.core_model.alzimer

data class AlzheimerDetailResponse(

    val success: Boolean,
    val data: Patient,
    val msg: String
)


data class Patient(
    val _id: String,
    val type: String,
    val fullName: String,
    val age: Int,
    val gender: String,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val phoneNumber: String,
    val image: String
)