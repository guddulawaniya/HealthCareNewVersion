package com.careavatar.core_utils

sealed class ApiResult<out T> {
    object Idle : ApiResult<Nothing>()
    object Loading : ApiResult<Nothing>()
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String?, val throwable: Throwable? = null) : ApiResult<Nothing>()
}