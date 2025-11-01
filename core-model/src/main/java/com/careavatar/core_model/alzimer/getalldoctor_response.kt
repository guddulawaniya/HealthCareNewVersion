package com.careavatar.core_model.alzimer

data class getalldoctor_response(
    val success: Boolean,
    val msg: String,
    val data: List<Doctor>
)

data class Doctor(
    val status: String,
    val _id: String,
    val name: String,
    val specialist: String,
    val image: String,
    val experience: Int,
    val rating: Double,
    val about: String,
    val appointments: List<Appointment> = emptyList(),
    val __v: Int,
    val totalPatients: Int
)

data class Appointment(
    val _id: String,
    val patientId: String,
    val doctor: List<String>,
    val appointmentDate: String,
    val appointmentTime: String,
    val __v: Int
)
