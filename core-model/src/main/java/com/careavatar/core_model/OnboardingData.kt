package com.careavatar.core_model

import java.io.File

data class OnboardingData(
    val name: String? = null,
    val email: String? = null,
    val gender: String? = null,
    val age: String? = null,
    val height: String? = null,
    val weight: String? = null,
    val bloodPressure: String? = null,
    var sugarLevel: String? = null,
    val emergencyContacts: String? = null,
    val imageFile: File? = null
)

data class OnboardingUserhobbies(
    var address: String? = null,
    var userPreferences: List<String> = emptyList()
)
