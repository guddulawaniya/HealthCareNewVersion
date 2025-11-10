package com.careavatar.core_model.dietition

data class GetAllMedicineResponse(
    val data: ArrayList<MedicineData>,
    val page: Int,
    val pageSize: Int,
    val totalResults: Int
) {
    data class MedicineData(
        val _id: String,
        val manufacturer_name: String,
        val name: String,
        val short_composition1: String,
        val short_composition2: String
    )
}