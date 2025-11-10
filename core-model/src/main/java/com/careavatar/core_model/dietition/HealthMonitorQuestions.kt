package com.careavatar.core_model.dietition

data class HealthMonitorQuestions(
    val services: List<Service>,
    val success: Boolean,
)

data class Service(
    val _id: String,
    val name: String,
    val categoryIcon: String,
    val __v: Long,
    val isFirstTime: Long,
)
