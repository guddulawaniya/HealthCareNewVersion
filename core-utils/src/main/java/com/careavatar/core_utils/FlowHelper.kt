package com.careavatar.core_utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow



fun <T> createStateFlowPair(initialValue: ApiResult<T> = ApiResult.Idle): Pair<
        MutableStateFlow<ApiResult<T>>,
        StateFlow<ApiResult<T>>
        > {
    val mutableFlow = MutableStateFlow(initialValue)
    return mutableFlow to mutableFlow
}
