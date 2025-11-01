package com.asyscraft.medical_reminder.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class SaveDataViewModel @Inject constructor(
    private val repository: CreateReminderRepository
) : ViewModel() {

    private val _formData = MutableStateFlow(ReminderData())
    val formData: StateFlow<ReminderData> = _formData

    fun updateDiseaseId(name: String) = updateData { copy(diseaseId = name) }
    fun updateReminderId(id: String) = updateData { copy(reminderId = id) }
    fun updateReminderName(name: String) = updateData { copy(reminderName = name) }

    private fun updateData(update: ReminderData.() -> ReminderData) {
        val newData = _formData.value.update()
        _formData.value = newData

        // Optional persistence
        viewModelScope.launch {
            repository.saveReminderData(newData)
        }
    }

    fun loadReminderData() {
        viewModelScope.launch {
            val data = repository.loadReminderData()
            _formData.value = data
        }
    }
}



@Singleton
class CreateReminderRepository @Inject constructor() {

    suspend fun saveReminderData(data: ReminderData) {}

    suspend fun loadReminderData(): ReminderData {
        return ReminderData()
    }
}


