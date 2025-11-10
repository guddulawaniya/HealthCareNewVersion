package com.asyscraft.dietition_module.viewModels

import androidx.lifecycle.ViewModel
import com.careavatar.core_model.dietition.AddCartListResponse
import com.careavatar.core_model.dietition.DailyRoutineResponse
import com.careavatar.core_model.dietition.DietAddCartModelResponse
import com.careavatar.core_model.dietition.DietCategoryResponse
import com.careavatar.core_model.dietition.DietFoodAllergiesResponse
import com.careavatar.core_model.dietition.DietQuestionPostRequest
import com.careavatar.core_model.dietition.DietQuestionResponse
import com.careavatar.core_model.dietition.ExpertBookingAvailableResponse
import com.careavatar.core_model.dietition.ExpertRequest
import com.careavatar.core_model.dietition.GetAllMedicineResponse
import com.careavatar.core_model.dietition.GetExpertResponse2
import com.careavatar.core_model.dietition.PrimaryReasonJoinApp
import com.careavatar.core_model.dietition.ProfessionCategoryResponse
import com.careavatar.core_model.dietition.RecipeResponse
import com.careavatar.core_model.dietition.RecipeResponsedetails
import com.careavatar.core_model.dietition.SpecificDietResponse
import com.careavatar.core_model.dietition.dietAddCartModelRequest
import com.careavatar.core_service.repository.UserRepository
import com.careavatar.core_utils.ApiResult
import com.careavatar.core_utils.SafeFlowApiCall.safeFlowApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


@HiltViewModel
class DietitionViewModel @Inject constructor(private val repository: UserRepository) :
    ViewModel() {


    private val _ProfessionCategoryResponse =
        MutableStateFlow<ApiResult<ProfessionCategoryResponse>>(ApiResult.Idle)
    val getProfessionCategoryResponse: StateFlow<ApiResult<ProfessionCategoryResponse>> =
        _ProfessionCategoryResponse

    private val _DailyRoutineResponse =
        MutableStateFlow<ApiResult<DailyRoutineResponse>>(ApiResult.Idle)
    val getDailyRoutineResponse: StateFlow<ApiResult<DailyRoutineResponse>> =
        _DailyRoutineResponse

    private val _PrimaryReasonJoinApp =
        MutableStateFlow<ApiResult<PrimaryReasonJoinApp>>(ApiResult.Idle)
    val getPrimaryReasonJoinApp: StateFlow<ApiResult<PrimaryReasonJoinApp>> =
        _PrimaryReasonJoinApp

    private val _GetAllMedicineResponse =
        MutableStateFlow<ApiResult<GetAllMedicineResponse>>(ApiResult.Idle)
    val getAllMedicineResponse: StateFlow<ApiResult<GetAllMedicineResponse>> =
        _GetAllMedicineResponse


    private val _DietFoodAllergiesResponse =
        MutableStateFlow<ApiResult<DietFoodAllergiesResponse>>(ApiResult.Idle)
    val getDietFoodAllergiesResponse: StateFlow<ApiResult<DietFoodAllergiesResponse>> =
        _DietFoodAllergiesResponse

    private val _GetExpertResponse2 =
        MutableStateFlow<ApiResult<GetExpertResponse2>>(ApiResult.Idle)
    val getGetExpertResponse2: StateFlow<ApiResult<GetExpertResponse2>> =
        _GetExpertResponse2

    private val _RecipeResponse =
        MutableStateFlow<ApiResult<RecipeResponse>>(ApiResult.Idle)
    val getRecipeResponse: StateFlow<ApiResult<RecipeResponse>> =
        _RecipeResponse

    private val _dietAddCartResponse =
        MutableStateFlow<ApiResult<DietAddCartModelResponse>>(ApiResult.Idle)
    val dietAddCartResponse: StateFlow<ApiResult<DietAddCartModelResponse>> = _dietAddCartResponse


    private val _ExpertBookingAvailableResponse =
        MutableStateFlow<ApiResult<ExpertBookingAvailableResponse>>(ApiResult.Idle)
    val getExpertBookingAvailableResponse: StateFlow<ApiResult<ExpertBookingAvailableResponse>> =
        _ExpertBookingAvailableResponse


    private val _postQestionDataResponse =
        MutableStateFlow<ApiResult<DietQuestionResponse>>(ApiResult.Idle)
    val postQuestionDataResponse: StateFlow<ApiResult<DietQuestionResponse>> =
        _postQestionDataResponse

    private val _specificDietResponseList =
        MutableStateFlow<ApiResult<SpecificDietResponse>>(ApiResult.Idle)
    val specificDietResponseList: StateFlow<ApiResult<SpecificDietResponse>> =
        _specificDietResponseList

    private val _DietCategoryResponse =
        MutableStateFlow<ApiResult<DietCategoryResponse>>(ApiResult.Idle)
    val getDietCategoryResponse: StateFlow<ApiResult<DietCategoryResponse>> = _DietCategoryResponse


    private val _cartListResponse = MutableStateFlow<ApiResult<AddCartListResponse>>(ApiResult.Idle)
    val cartListResponse: StateFlow<ApiResult<AddCartListResponse>> = _cartListResponse

    private val _RecipeResponseDetails =
        MutableStateFlow<ApiResult<RecipeResponsedetails>>(ApiResult.Idle)
    val recipeResponseDetails: StateFlow<ApiResult<RecipeResponsedetails>> = _RecipeResponseDetails


    fun hitAddIntoCartList() {
        safeFlowApiCall(_cartListResponse) {
            val response = repository.hitAddIntoCartList()
            if (response.isSuccessful) response.body()!!
            else throw kotlin.Exception(response.message())
        }
    }

    fun hitCategoryList() {
        safeFlowApiCall(_DietCategoryResponse) {
            val response = repository.hitCategoryList()
            if (response.isSuccessful) response.body()!!
            else throw kotlin.Exception(response.message())
        }
    }

    fun hitPostQuestionData(request: DietQuestionPostRequest) {
        safeFlowApiCall(_postQestionDataResponse) {
            val response = repository.hitQuestionPost(request)
            if (response.isSuccessful) response.body()!!
            else throw Exception(response.message())
        }
    }


    fun hitGetAvailableSlot(request: ExpertRequest) {
        safeFlowApiCall(_ExpertBookingAvailableResponse) {
            val response = repository.hitGetAvailableSlot(request)
            if (response.isSuccessful) response.body()!!
            else throw Exception(response.message())
        }
    }

    fun hitAddIntoCart(request: dietAddCartModelRequest) {
        safeFlowApiCall(_dietAddCartResponse) {
            val response = repository.hitAddIntoCart(request)
            if (response.isSuccessful) response.body()!!
            else throw Exception(response.message())
        }
    }


    fun hitRecipeByid(recipeId: String) {
        safeFlowApiCall(_RecipeResponseDetails) {
            val response = repository.hitRecipeById(recipeId)
            if (response.isSuccessful) response.body()!!
            else throw Exception(response.message())
        }
    }



    fun hitRecipe(
        dietType: List<String>,
        category: List<String>,
        typeOfReceipe: String,
        dietChartId: String? = "",
    ) {
        safeFlowApiCall(_RecipeResponse) {
            val response = repository.hitRecipeList(
                category = category,
                dietType = dietType,
                typeOfReceipe = typeOfReceipe,
                dietChartId = dietChartId
            )
            if (response.isSuccessful) response.body()!!
            else throw Exception(response.message())
        }
    }

    fun hitFoodAllergies() {
        safeFlowApiCall(_DietFoodAllergiesResponse) {
            val response = repository.hitFoodAllergies()
            if (response.isSuccessful) response.body()!!
            else throw Exception(response.message())
        }
    }

    fun hitSpecificDiet() {
        safeFlowApiCall(_specificDietResponseList) {
            val response = repository.hitSpecificDiet()
            if (response.isSuccessful) response.body()!!
            else throw Exception(response.message())
        }
    }

    fun hitGetFitnessExpert(category: String) {
        safeFlowApiCall(_GetExpertResponse2) {
            val response = repository.hitGetFitnessExpert(category)
            if (response.isSuccessful) response.body()!!
            else throw Exception(response.message())
        }
    }

    fun hitAllMedicine(
        target: String,
        page: Int,
        pageSize: Int
    ) {
        safeFlowApiCall(_GetAllMedicineResponse) {
            val response = repository.hitAllMedicine(
                target = target,
                page = page,
                pageSize = pageSize,
            )
            if (response.isSuccessful) response.body()!!
            else throw Exception(response.message())
        }
    }

    fun hitProfessionCategory() {
        safeFlowApiCall(_ProfessionCategoryResponse) {
            val response = repository.hitProfessionCategory()
            if (response.isSuccessful) response.body()!!
            else throw Exception(response.message())
        }
    }

    fun hitDailyRoutineResponseList() {
        safeFlowApiCall(_DailyRoutineResponse) {
            val response = repository.hitDailyRoutineResponseList()
            if (response.isSuccessful) response.body()!!
            else throw Exception(response.message())
        }
    }

    fun hitPrimaryReason() {
        safeFlowApiCall(_PrimaryReasonJoinApp) {
            val response = repository.hitPrimaryReason()
            if (response.isSuccessful) response.body()!!
            else throw Exception(response.message())
        }
    }

}