package com.careavatar.dashboardmodule.viewModels

import androidx.lifecycle.ViewModel
import com.careavatar.core_model.BannerResponse
import com.careavatar.core_model.HealthResponse
import com.careavatar.core_model.UpcommingResponseModel
import com.careavatar.core_model.UserDetailsResponse
import com.careavatar.core_model.dietition.DietCategoryResponse
import com.careavatar.core_model.dietition.HealthMonitorQuestions
import com.careavatar.core_service.repository.UserRepository
import com.careavatar.core_utils.ApiResult
import com.careavatar.core_utils.SafeFlowApiCall.safeFlowApiCall

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class DashBoardViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _userDetailsResponse =
        MutableStateFlow<ApiResult<UserDetailsResponse>>(ApiResult.Idle)
    val userDetailsResponse: StateFlow<ApiResult<UserDetailsResponse>> = _userDetailsResponse

    private val _bannerResponse =
        MutableStateFlow<ApiResult<BannerResponse>>(ApiResult.Idle)
    val bannerResponse: StateFlow<ApiResult<BannerResponse>> = _bannerResponse

    private val _healthResponse = MutableStateFlow<ApiResult<HealthResponse>>(ApiResult.Idle)
    val healthResponse: StateFlow<ApiResult<HealthResponse>> = _healthResponse


    private val _upComingResponseModel =
        MutableStateFlow<ApiResult<UpcommingResponseModel>>(ApiResult.Idle)
    val upComingResponseModel: StateFlow<ApiResult<UpcommingResponseModel>> = _upComingResponseModel


    private val _HealthMonitorQuestions =
        MutableStateFlow<ApiResult<HealthMonitorQuestions>>(ApiResult.Idle)
    val getHealthMonitorQuestions: StateFlow<ApiResult<HealthMonitorQuestions>> = _HealthMonitorQuestions



    fun hitCategoryWithSubcategories() {
        safeFlowApiCall(_HealthMonitorQuestions) {
            val response = repository.hitCategoryWithSubcategories()
            if (response.isSuccessful) response.body()!!
            else throw kotlin.Exception(response.message())
        }
    }

    fun userDetails() {
        safeFlowApiCall(_userDetailsResponse) {
            val response = repository.hitUserDetails()
            if (response.isSuccessful) response.body()!!
            else throw kotlin.Exception(response.message())
        }
    }

    fun hitBanner() {
        safeFlowApiCall(_bannerResponse) {
            val response = repository.hitbanner()
            if (response.isSuccessful) response.body()!!
            else throw kotlin.Exception(response.message())
        }
    }

    fun hitDashBoardHealth() {
        safeFlowApiCall(_healthResponse) {
            val response = repository.hitDashBoardHealth()
            if (response.isSuccessful) response.body()!!
            else throw kotlin.Exception(response.message())
        }
    }

    fun hitGetUpComingClass() {
        safeFlowApiCall(_upComingResponseModel) {
            val response = repository.hitUpComingClass()
            if (response.isSuccessful) response.body()!!
            else throw kotlin.Exception(response.message())
        }
    }


}