package com.careavatar.core_model.alzimer

data class PatientListResponse(
    val success: Boolean,
    val count: Int,
    val patients: List<Patientlist>
)


data class Patientlist(
    val _id: String,
    val user: String,
//    val type: String,
    val fullName: String,
    val image: String,
//    val age: Int,
//    val gender: String,
//    val location: String,
//    val latitude: Double,
//    val longitude: Double,
//    val radius: Int,
//    val phoneNumber: String,
//    val careGiver: List<String>,
//    val taskAssignedDays: Int,
//    val mainUser: String,
//    val createdAt: String,
//    val updatedAt: String,
    val isAssessmentComplete: Boolean,
    val __v: Int,
)