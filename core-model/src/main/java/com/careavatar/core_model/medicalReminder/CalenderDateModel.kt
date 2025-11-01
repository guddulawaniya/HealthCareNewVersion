package com.careavatar.core_model.medicalReminder


data class CalenderDateModel(
    val date: String,
    val day: String,
    val fullDate: String, // yyyy-MM-dd format
    var isSelected: Boolean = false
)
