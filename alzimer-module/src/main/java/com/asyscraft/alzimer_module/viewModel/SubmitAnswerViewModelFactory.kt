package com.asyscraft.alzimer_module.viewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.careavatar.core_service.repository.UserRepository

class SubmitAnswerViewModelFactory(
    private val application: Application,
    private val repository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(SubmitAnswerViewModel::class.java)) {
//            return SubmitAnswerViewModel(application, repository) as T
//        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
