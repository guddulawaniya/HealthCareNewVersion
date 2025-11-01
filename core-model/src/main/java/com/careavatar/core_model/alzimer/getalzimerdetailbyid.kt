package com.careavatar.core_model.alzimer

data class getalzimerdetailbyid(
    val success: Boolean,
    val message: String,
    val data: AlzheimerData
)

data class AlzheimerData(
    val _id: String,
    val user: AlzheimerUser,
    val type: String,
    val fullName: String,
    val age: Int,
    val gender: String,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Int,
    val phoneNumber: String,
    val careGiver: List<CareGiver>,
    val image: String,
    val taskAssignedDays: Int,
    val mainUser: String,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int
)

data class AlzheimerUser(
    val _id: String,
    val email: String,
    val name: String
)

data class CareGiver(
    val _id: String,
    val name: String,
    val phoneNumber: String,
    val relation: String,
    val address: String,
    val image: String? // Nullable since it can be null
)