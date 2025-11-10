package com.careavatar.core_model.medicalReminder

data class HistoryReminderResponse(
    val message: String,
    val data: List<HistoryData>,
)

data class HistoryData(
    val userId: String,
    val day: String,
    val dosage: Long,
    val time: String,
    val type: String,
    val takenStatus: String,
    val historyDate: String,
    val medicine: Medicine,
    val disease: DiseaseHistory,
    val medicineForm: MedicineForm,
)

data class Medicine(
    val _id: String,
    val name: String,
    val manufacturer_name: String,
    val short_composition1: String,
    val short_composition2: String,
)

data class DiseaseHistory(
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

data class MedicineForm(
    val _id: String,
    val name: String,
    val type: String,
    val __v: Long,
)

