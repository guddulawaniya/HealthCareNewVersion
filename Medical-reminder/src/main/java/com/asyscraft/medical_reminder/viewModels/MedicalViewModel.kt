package com.asyscraft.medical_reminder.viewModels

import androidx.lifecycle.ViewModel
import com.careavatar.core_model.medicalReminder.AddVaccinationRequest
import com.careavatar.core_model.medicalReminder.CreateMedicalReminderRequest
import com.careavatar.core_model.medicalReminder.CreateMedicalReminderResponse
import com.careavatar.core_model.medicalReminder.CreatePeriodRequestModel
import com.careavatar.core_model.medicalReminder.CreatePeriodResponseModel
import com.careavatar.core_model.medicalReminder.CreateWaterReminderRequest
import com.careavatar.core_model.medicalReminder.CreateWaterReminderResponse
import com.careavatar.core_model.medicalReminder.GetDiseaseResponse
import com.careavatar.core_model.medicalReminder.PeriodGetAllDataResponse
import com.careavatar.core_model.medicalReminder.VaccinationDetailsResponse
import com.careavatar.core_model.medicalReminder.VaccinationResponse
import com.careavatar.core_model.medicalReminder.WaterHistoryResponse
import com.careavatar.core_service.repository.UserRepository
import com.careavatar.core_utils.ApiResult
import com.careavatar.core_utils.SafeFlowApiCall.safeFlowApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


@HiltViewModel
class MedicalViewModel @Inject constructor(private val repository: UserRepository) :
    ViewModel() {


    private val _VaccinationDetailsResponse =
        MutableStateFlow<ApiResult<VaccinationDetailsResponse>>(ApiResult.Idle)
    val getVaccinationDetailsResponse: StateFlow<ApiResult<VaccinationDetailsResponse>> =
        _VaccinationDetailsResponse

    private val _VaccinationResponse =
        MutableStateFlow<ApiResult<VaccinationResponse>>(ApiResult.Idle)
    val vaccinationResponse: StateFlow<ApiResult<VaccinationResponse>> =
        _VaccinationResponse

    private val _CreatePeriodResponseModel =
        MutableStateFlow<ApiResult<CreatePeriodResponseModel>>(ApiResult.Idle)
    val CreatePeriodResponseModel: StateFlow<ApiResult<CreatePeriodResponseModel>> =
        _CreatePeriodResponseModel

    private val _PeriodGetAllDataResponse =
        MutableStateFlow<ApiResult<PeriodGetAllDataResponse>>(ApiResult.Idle)
    val getPeriodGetAllDataResponse: StateFlow<ApiResult<PeriodGetAllDataResponse>> =
        _PeriodGetAllDataResponse


    private val _WaterHistoryResponse =
        MutableStateFlow<ApiResult<WaterHistoryResponse>>(ApiResult.Idle)
    val waterHistoryResponse: StateFlow<ApiResult<WaterHistoryResponse>> =
        _WaterHistoryResponse


    private val _CreateWaterReminderResponse =
        MutableStateFlow<ApiResult<CreateWaterReminderResponse>>(ApiResult.Idle)
    val getCreateWaterReminderResponse: StateFlow<ApiResult<CreateWaterReminderResponse>> =
        _CreateWaterReminderResponse

    private val _CreateMedicalReminderResponse =
        MutableStateFlow<ApiResult<CreateMedicalReminderResponse>>(ApiResult.Idle)
    val getCreateMedicalReminderResponse: StateFlow<ApiResult<CreateMedicalReminderResponse>> =
        _CreateMedicalReminderResponse

    private val _GetDiseaseResponse =
        MutableStateFlow<ApiResult<GetDiseaseResponse>>(ApiResult.Idle)
    val getDiseaseResponse: StateFlow<ApiResult<GetDiseaseResponse>> =
        _GetDiseaseResponse


    fun hitGetVaccinationList(userId: String) {
        safeFlowApiCall(_VaccinationDetailsResponse) {
            val response = repository.hitGetVaccinationList(userId)
            if (response.isSuccessful) response.body()!!
            else throw Exception(response.message())
        }
    }

    fun hitCreateVaccination(request: AddVaccinationRequest) {
        safeFlowApiCall(_VaccinationResponse) {
            val response = repository.hitCreateVaccination(request)
            if (response.isSuccessful) response.body()!!
            else throw Exception(response.message())
        }
    }

    fun hitCreatePeriodLog(request: CreatePeriodRequestModel) {
        safeFlowApiCall(_CreatePeriodResponseModel) {
            val response = repository.hitCreatePeriodLog(request)
            if (response.isSuccessful) response.body()!!
            else throw Exception(response.message())
        }
    }

    fun hitFetchPeriodDetails() {
        safeFlowApiCall(_PeriodGetAllDataResponse) {
            val response = repository.hitFetchPeriodDetails()
            if (response.isSuccessful) response.body()!!
            else throw Exception(response.message())
        }
    }

    fun hitWaterHistory() {
        safeFlowApiCall(_WaterHistoryResponse) {
            val response = repository.hitWaterHistory()
            if (response.isSuccessful) response.body()!!
            else throw Exception(response.message())
        }
    }

    fun hitWaterReminderCreate(request: CreateWaterReminderRequest) {
        safeFlowApiCall(_CreateWaterReminderResponse) {
            val response = repository.hitWaterReminderCreate(request)
            if (response.isSuccessful) response.body()!!
            else throw Exception(response.message())
        }
    }

    fun hitWaterReminderUpdate(id : String,request: CreateWaterReminderRequest) {
        safeFlowApiCall(_CreateWaterReminderResponse) {
            val response = repository.hitWaterReminderUpdate(id,request)
            if (response.isSuccessful) response.body()!!
            else throw Exception(response.message())
        }
    }

    fun hitCreateMedicalRemainder(request: CreateMedicalReminderRequest) {
        safeFlowApiCall(_CreateMedicalReminderResponse) {
            val response = repository.hitCreateMedicalRemainder(request)
            if (response.isSuccessful) response.body()!!
            else throw Exception(response.message())
        }
    }
    fun hitGetDisease() {
        safeFlowApiCall(_GetDiseaseResponse) {
            val response = repository.hitGetDisease()
            if (response.isSuccessful) response.body()!!
            else throw Exception(response.message())
        }
    }
}