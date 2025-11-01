package com.careavatar.core_model.medicalReminder

data class VaccinationResponse(
    val `data`: Data,
    val message: String,
    val success: Boolean
) {
    data class Data(
        val __v: Int,
        val _id: String,
        val createdAt: String,
        val updatedAt: String,
        val userId: String,
        val vaccinations: List<Vaccination>
    ) {
        data class Vaccination(
            val _id: String,
            val label: String,
            val next_date: String,
            val taken_date: String
        )
    }
}

data class AddVaccinationRequest (
    val taken_date: String,
    val next_date: String,
    val label: String)