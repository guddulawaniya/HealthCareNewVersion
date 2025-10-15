package com.careavatar.core_service.repository.viewModels

import androidx.lifecycle.ViewModel
import com.careavatar.core_model.OnboardingData
import com.careavatar.core_model.OnboardingUserhobbies
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class OnboardingViewModel @Inject constructor(   private val repository: OnboardingRepository) : ViewModel() {

    // Use StateFlow or LiveData, here I'll use StateFlow
    private val _formData = MutableStateFlow(OnboardingData())
    val formData: StateFlow<OnboardingData> = _formData

    private fun updateData(update: OnboardingData.() -> OnboardingData) {
        _formData.value = _formData.value.update()
        repository.onboardingData = _formData.value
    }

    fun updateName(name: String) = updateData { copy(name = name) }
    fun updateEmail(email: String) = updateData { copy(email = email) }
    fun updateGender(gender: String) = updateData { copy(gender = gender) }
    fun updateAge(age: String) = updateData { copy(age = age) }
    fun updateHeight(height: String) = updateData { copy(height = height) }
    fun updateWeight(weight: String) = updateData { copy(weight = weight) }
    fun updateBloodPressure(bp: String) = updateData { copy(bloodPressure = bp) }
    fun updateSugarLevel(sugar: String) = updateData { copy(sugarLevel = sugar) }
    fun updateEmergencyContacts(contact: String) = updateData { copy(emergencyContacts = contact) }
    fun updateImage(file: File) = updateData { copy(imageFile = file) }
}


@Singleton
class OnboardingRepository @Inject constructor() {
    var onboardingData: OnboardingData = OnboardingData()
}

@Singleton
class OnboardingRepositoryUserHobbies @Inject constructor() {
    var onboardingInfo = OnboardingUserhobbies()
}

