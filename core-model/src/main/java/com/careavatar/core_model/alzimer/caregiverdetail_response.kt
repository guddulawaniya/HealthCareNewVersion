package com.careavatar.core_model.alzimer

data class caregiverdetail_response(
    val success: Boolean,
    val msg: String,
    val data: Caregiverdetails
)

data class Caregiverdetails(
    val _id: String,
    val name: String,
    val phoneNumber: String,
    val relation: String,
    val address: String,
    val image: String?, // Nullable, as it can be null
//    val patientId: String,
    val fcmToken: String,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int
)