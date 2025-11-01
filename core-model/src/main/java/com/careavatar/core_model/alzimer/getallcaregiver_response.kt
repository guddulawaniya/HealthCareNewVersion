package com.careavatar.core_model.alzimer



data class getallcaregiver_response(
    val success: Boolean,
    val msg: String,
    val data: List<Caregiverres>
)

data class Caregiverres(
    val _id: String,
    val name: String,
    val phoneNumber: String,
    val relation: String,
    val address: String,
    val image: String? = null,
//    val patientId: String,
    val fcmToken: String,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int
)
