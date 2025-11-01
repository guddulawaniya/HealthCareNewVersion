package com.careavatar.core_model.alzimer

data class appointment_request(
    val patientId: String,
    val doctorIds: List<String>,
    val appointmentDate: String,
    val appointmentTime: String
)
