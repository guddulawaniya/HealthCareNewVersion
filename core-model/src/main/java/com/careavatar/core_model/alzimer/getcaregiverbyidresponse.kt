package com.careavatar.core_model.alzimer

data class getcaregiverbyidresponse(

    val success: Boolean,
    val message: String,
    val data: List<CaregiverItem>
)

data class CaregiverItem(
    val _id: String,
    val name: String,
    val phoneNumber: String,
    val relation: String,
    val address: String,
    val image: String?,
//    val patientId: PatientInfo,
    val fcmToken: String,
    val user: CaregiverUser,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int
)

data class PatientInfo(
    val _id: String,
    val fullName: String,
    val image: String?
)

data class CaregiverUser(
    val _id: String,
    val name: String
)