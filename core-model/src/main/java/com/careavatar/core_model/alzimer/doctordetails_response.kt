package com.careavatar.core_model.alzimer

data class doctordetails_response(
    val success: Boolean,
    val msg: String,
    val doctorObj: DoctorDetails
)


data class DoctorDetails(
    val _id: String,
    val name: String,
    val specialist: String,
    val image: String,
    val experience: Int,
    val rating: Double,
    val about: String,
    val __v: Int,
    val totalPatients: Int
)