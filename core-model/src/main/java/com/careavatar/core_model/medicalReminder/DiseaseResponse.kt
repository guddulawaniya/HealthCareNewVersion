package com.careavatar.core_model.medicalReminder

data class DiseaseResponse(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val diseaseDescription: String,
    val diseaseDuration: String,
    val diseaseName: String,
    val diseaseType: String,
    val updatedAt: String,
    val user: String
)


data class GetDiseaseResponse(
    val success: Boolean,
    val diseases: List<Disease>,
)

data class Disease(
    val _id: String,
    val diseaseType: String,
    val diseaseName: String,
    val diseaseDuration: String,
    val diseaseDescription: String,
    val user: String,
    val createdAt: String,
    val updatedAt: String,
    val __v: Long,
)