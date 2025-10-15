package com.careavatar.userapploginmodule.model

data class HealthData(
    var gender: String? = null,
    var age: Int? = null,
    var height: Int? = null,
    var weight: Int? = null,
    var bloodPressure: String? = null,
    var sugarLevel: String? = null,
    var emergencyContacts: List<String> = emptyList()
)
