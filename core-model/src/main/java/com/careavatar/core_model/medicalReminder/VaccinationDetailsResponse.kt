package com.careavatar.core_model.medicalReminder

data class  VaccinationDetailsResponse(
    val data: List<Data>,
    val success: Boolean,
    val msg: String
) {
    data class Data(
        val _id: String,
        val label: String?,
        val next_date: String?,
        val taken_date: String?
    )
}

