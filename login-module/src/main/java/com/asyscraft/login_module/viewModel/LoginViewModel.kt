package com.asyscraft.login_module.viewModel

import androidx.lifecycle.ViewModel
import com.careavatar.core_model.CategoryListResponse
import com.careavatar.core_model.CategoryListResponsePost
import com.careavatar.core_model.GetCategoryRquest
import com.careavatar.core_model.LoginRequest
import com.careavatar.core_model.LoginResponse
import com.careavatar.core_model.SignUpResponse
import com.careavatar.core_model.UserHobbiesResponse
import com.careavatar.core_model.UserHobbiessResquest
import com.careavatar.core_model.VerifyOtpRequest
import com.careavatar.core_model.VerifyOtpResponse
import com.careavatar.core_utils.ApiResult
import com.careavatar.core_utils.SafeFlowApiCall.safeFlowApiCall
import com.careavatar.core_utils.convertFormFileToMultipartBody
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import com.careavatar.core_service.repository.UserRepository

import kotlinx.coroutines.flow.StateFlow
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _LoginResponse = MutableStateFlow<ApiResult<LoginResponse>>(ApiResult.Idle)
    val loginResponse: StateFlow<ApiResult<LoginResponse>> = _LoginResponse

    private val _verifyOtpResponse = MutableStateFlow<ApiResult<VerifyOtpResponse>>(ApiResult.Idle)
    val verifyOtpResponse: StateFlow<ApiResult<VerifyOtpResponse>> = _verifyOtpResponse


     private val _signUpResponse = MutableStateFlow<ApiResult<SignUpResponse>>(ApiResult.Idle)
    val signUpResponse: StateFlow<ApiResult<SignUpResponse>> = _signUpResponse

    private val _categoryListResponse =
        MutableStateFlow<ApiResult<CategoryListResponsePost>>(ApiResult.Idle)
    val categoryListResponse: StateFlow<ApiResult<CategoryListResponsePost>> = _categoryListResponse

    private val _userHobbiesResponse =
        MutableStateFlow<ApiResult<UserHobbiesResponse>>(ApiResult.Idle)
    val userHobbiesResponse: StateFlow<ApiResult<UserHobbiesResponse>> =
        _userHobbiesResponse


    private val _getCategoryListResponse =
        MutableStateFlow<ApiResult<CategoryListResponse>>(ApiResult.Idle)
    val getCategoryListResponse: StateFlow<ApiResult<CategoryListResponse>> =
        _getCategoryListResponse


    fun resetLoginState() {
        _LoginResponse.value = ApiResult.Idle  // 👈 This clears it
    }

    fun hitUserHobbies(categoryName: UserHobbiessResquest) {
        safeFlowApiCall(_userHobbiesResponse) {
            val response = repository.hitUserHobbies(categoryName)
            if (response.isSuccessful) response.body()!!
            else throw Exception(response.message())
        }
    }


    fun hitGetCategoryList() {
        safeFlowApiCall(_getCategoryListResponse) {
            val response = repository.hitGetCategoryList()
            if (response.isSuccessful) response.body()!!
            else throw Exception(response.message())
        }
    }

    fun hitGetCategoryList(request: GetCategoryRquest) {
        safeFlowApiCall(_categoryListResponse) {
            val response = repository.hitGetCategoryList(request)
            if (response.isSuccessful) response.body()!!
            else throw Exception(response.message())
        }
    }


    fun hitLogin(request : LoginRequest) {
        safeFlowApiCall(_LoginResponse) {
            val response = repository.loginUser(request)
            if (response.isSuccessful) response.body()!!
            else throw Exception(response.message())
        }
    }

    fun hitUserRegister(
        phoneNumber: RequestBody?,
        dob: RequestBody?,
        name: RequestBody?,
        gender: RequestBody?,
        emergencycontact: RequestBody?,
        height: RequestBody?,
        weight: RequestBody?,
        email: RequestBody?,
        BP: RequestBody?,
        sugar: RequestBody?,
        image: File? = null
    ) {
        safeFlowApiCall(_signUpResponse) {
            val response = repository.hitRegister(
                phoneNumber,
                dob,
                name,
                gender,
                emergencycontact,
                height,
                weight,
                email,
                BP,
                sugar,
                convertFormFileToMultipartBody("image", image)
            )
            if (response.isSuccessful) response.body()!!
            else throw Exception(response.message())
        }
    }

    fun hitVerifyOtp(request : VerifyOtpRequest) {
        safeFlowApiCall(_verifyOtpResponse) {
            val response = repository.hitVerifyOtp(request)
            if (response.isSuccessful) response.body()!!
            else throw Exception(response.message())
        }
    }

}