package com.careavatar.core_model.alzimer

data class RadiusResponse(
    val success: Boolean,
    val data: PatientLocationData
)

data class PatientLocationData(
//    val _id: String,
//    val type: String,
//    val fullName: String,
//    val location: String,
//    val latitude: Double,
//    val longitude: Double,
    val radius: Int
)