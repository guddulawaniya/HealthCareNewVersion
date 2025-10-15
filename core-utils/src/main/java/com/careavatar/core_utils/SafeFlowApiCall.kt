package com.careavatar.core_utils


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


object SafeFlowApiCall {
    fun <T> ViewModel.safeFlowApiCall(
        stateFlow: MutableStateFlow<ApiResult<T>>,
        apiCall: suspend () -> T
    ) {
        viewModelScope.launch {
            if (stateFlow.value is ApiResult.Loading) return@launch

            stateFlow.value = ApiResult.Loading
            try {
                val response = withContext(Dispatchers.IO) { apiCall() }
                stateFlow.value = ApiResult.Success(response)
            } catch (e: Exception) {
                stateFlow.value = ApiResult.Error(e.message ?: "Unknown error")
            }
        }
    }
}