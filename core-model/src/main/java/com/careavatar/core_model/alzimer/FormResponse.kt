package com.careavatar.core_model.alzimer

data class FormResponse(
    val success: Boolean,
    val msg: String,
    val isRegister: Boolean,
    val isAssessmentComplete: Boolean,
    val alzheimer: Alzheimer? = null,
)

data class Alzheimer(
    val user: String,
    val type: String,
    val fullName: String,
    val age: Int,
    val gender: String,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Int,
    val phoneNumber: String,
    val careGiver: List<String>,
    val taskAssignedDays: Int,
    val _id: String,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int
)