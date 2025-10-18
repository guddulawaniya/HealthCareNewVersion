package com.asyscraft.community_module.viewModels

import androidx.lifecycle.ViewModel
import com.careavatar.core_model.AddMemberCommunityRequest
import com.careavatar.core_model.AddMemberCommunityResponse
import com.careavatar.core_model.AddToCommunityRequest
import com.careavatar.core_model.AddToCommunityResponse
import com.careavatar.core_model.CategoryListResponse
import com.careavatar.core_model.CategoryListResponsePost
import com.careavatar.core_model.CommunityPostResponse
import com.careavatar.core_model.CreateCommunityResponse
import com.careavatar.core_model.DeleteEventResponse
import com.careavatar.core_model.EventPostResponse
import com.careavatar.core_model.GetCategoryRquest
import com.careavatar.core_model.GroupMessageResponse
import com.careavatar.core_model.HightlightPostResponse
import com.careavatar.core_model.RecentJoinMemberList
import com.careavatar.core_model.SearchCommunityResponse
import com.careavatar.core_model.UpcomingEventModel
import com.careavatar.core_model.UserChatListResponse
import com.careavatar.core_model.UserHobbiesResponse
import com.careavatar.core_model.UserHobbiessResquest
import com.careavatar.core_service.repository.UserRepository
import com.careavatar.core_utils.ApiResult
import com.careavatar.core_utils.SafeFlowApiCall.safeFlowApiCall
import com.careavatar.core_utils.convertFormFileToMultipartBody
import com.careavatar.core_utils.toRequestBody
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SocialMeetViewmodel @Inject constructor(private val repository: UserRepository) :
    ViewModel() {

    private val _upComingEventList = MutableStateFlow<ApiResult<UpcomingEventModel>>(ApiResult.Idle)
    val upComingEventList: StateFlow<ApiResult<UpcomingEventModel>> = _upComingEventList


    private val _communityPostResponseList =
        MutableStateFlow<ApiResult<CommunityPostResponse>>(ApiResult.Idle)
    val communityPostResponseList: StateFlow<ApiResult<CommunityPostResponse>> =
        _communityPostResponseList


    private val _recentJoinMemberList =
        MutableStateFlow<ApiResult<RecentJoinMemberList>>(ApiResult.Idle)
    val recentJoinMemberList: StateFlow<ApiResult<RecentJoinMemberList>> = _recentJoinMemberList

    private val _categoryListResponse =
        MutableStateFlow<ApiResult<CategoryListResponsePost>>(ApiResult.Idle)
    val categoryListResponse: StateFlow<ApiResult<CategoryListResponsePost>> = _categoryListResponse

    private val _addMemberCommunityResponse =
        MutableStateFlow<ApiResult<AddMemberCommunityResponse>>(ApiResult.Idle)
    val addMemberCommunityResponse: StateFlow<ApiResult<AddMemberCommunityResponse>> =
        _addMemberCommunityResponse


    private val _addToCommunityResponse =
        MutableStateFlow<ApiResult<AddToCommunityResponse>>(ApiResult.Idle)
    val addToCommunityResponse: StateFlow<ApiResult<AddToCommunityResponse>> =
        _addToCommunityResponse


    private val _createCommunityResponse =
        MutableStateFlow<ApiResult<CreateCommunityResponse>>(ApiResult.Idle)
    val createCommunityResponse: StateFlow<ApiResult<CreateCommunityResponse>> =
        _createCommunityResponse

    private val _searchCommunityResponse =
        MutableStateFlow<ApiResult<SearchCommunityResponse>>(ApiResult.Idle)
    val searchCommunityResponse: StateFlow<ApiResult<SearchCommunityResponse>> =
        _searchCommunityResponse

    private val _userHobbiesResponse =
        MutableStateFlow<ApiResult<UserHobbiesResponse>>(ApiResult.Idle)
    val userHobbiesResponse: StateFlow<ApiResult<UserHobbiesResponse>> =
        _userHobbiesResponse


    private val _getCategoryListResponse =
        MutableStateFlow<ApiResult<CategoryListResponse>>(ApiResult.Idle)
    val getCategoryListResponse: StateFlow<ApiResult<CategoryListResponse>> =
        _getCategoryListResponse


    private val _groupMessageResponse =
        MutableStateFlow<ApiResult<GroupMessageResponse>>(ApiResult.Idle)
    val groupMessageResponse: StateFlow<ApiResult<GroupMessageResponse>> =
        _groupMessageResponse

    private val _hightlightPostResponse =
        MutableStateFlow<ApiResult<HightlightPostResponse>>(ApiResult.Idle)
    val hightlightPostResponse: StateFlow<ApiResult<HightlightPostResponse>> =
        _hightlightPostResponse

    private val _deleteEventResponse =
        MutableStateFlow<ApiResult<DeleteEventResponse>>(ApiResult.Idle)
    val deleteEventResponse: StateFlow<ApiResult<DeleteEventResponse>> =
        _deleteEventResponse


    private val _eventPostResponse =
        MutableStateFlow<ApiResult<EventPostResponse>>(ApiResult.Idle)
    val eventPostResponse: StateFlow<ApiResult<EventPostResponse>> =
        _eventPostResponse

    private val _userChatListResponse =
        MutableStateFlow<ApiResult<UserChatListResponse>>(ApiResult.Idle)
    val userChatListResponse: StateFlow<ApiResult<UserChatListResponse>> =
        _userChatListResponse


    fun hitGetChatList(userId: String) {
        safeFlowApiCall(_userChatListResponse) {
            val response = repository.hitGetChatList(userId)
            if (response.isSuccessful) response.body()!!
            else throw Exception(response.message())
        }
    }

    fun getUpcomingEventList(communityId: String,date:String) {
        safeFlowApiCall(_upComingEventList) {
            val response = repository.hitEventsByDateAndCommunity(communityId,date)
            if (response.isSuccessful) response.body()!!
            else throw Exception(response.message())
        }
    }

    fun hitCreateEventData(
        communityId: String,
        eventDate: String,
        description: String,
        eventLink: String,
        eventMode: String,
        eventTime: String,
        latitude: String,
        location: String,
        longitude: String,
        title: String,
        visibility: String,
        notifiedMembers: List<String>?,
        attachments: List<MultipartBody.Part>?) {

        safeFlowApiCall(_eventPostResponse) {

            val notifiedJson = notifiedMembers?.let {
                Gson().toJson(it)
            } ?: "[]"

            val response = repository.hitCreateEventData(
                communityId,
                eventDate,
                description,
                eventLink,
                eventMode,
                eventTime,
                latitude,
                location,
                longitude,
                title,
                visibility,
                notifiedJson.toRequestBody("application/json".toMediaTypeOrNull()),
                attachments
            )
            if (response.isSuccessful) response.body()!!
            else throw Exception(response.message())
        }
    }

    fun hitEventsDelete(eventId: String) {
        safeFlowApiCall(_deleteEventResponse) {
            val response = repository.hitEventsDelete(eventId)
            if (response.isSuccessful) response.body()!!
            else throw Exception(response.message())
        }
    }

 fun getGroupMessage(communityId: String) {
        safeFlowApiCall(_groupMessageResponse) {
            val response = repository.hitGroupMessage(communityId)
            if (response.isSuccessful) response.body()!!
            else throw Exception(response.message())
        }
    }

    fun hitHightLightPost(
        title: String,
        interestId: String,
        description: String,
        address: String,
        latitude: String,
        longitude: String,
        image: File?=null
    ) {
        safeFlowApiCall(_hightlightPostResponse) {
            val response = repository.hitHightLightPost(
                title.toRequestBody(),
                interestId.toRequestBody(),
                description.toRequestBody(),
                address.toRequestBody(),
                latitude.toRequestBody(),
                longitude.toRequestBody(),
                convertFormFileToMultipartBody("image", image)
            )
            if (response.isSuccessful) response.body()!!
            else throw Exception(response.message())
        }
    }

    fun hitSearchCategory(categoryName: String) {
        safeFlowApiCall(_searchCommunityResponse) {
            val response = repository.hitSearchCategory(categoryName)
            if (response.isSuccessful) response.body()!!
            else throw Exception(response.message())
        }
    }

    fun hitUserHobbies(categoryName: UserHobbiessResquest) {
        safeFlowApiCall(_userHobbiesResponse) {
            val response = repository.hitUserHobbies(categoryName)
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

    fun hitGetCategoryList() {
        safeFlowApiCall(_getCategoryListResponse) {
            val response = repository.hitGetCategoryList()
            if (response.isSuccessful) response.body()!!
            else throw Exception(response.message())
        }
    }

    fun hitCreateCommunity(name: String, type: String, categoryId: String, image: File? = null) {
        safeFlowApiCall(_createCommunityResponse) {
            val response = repository.hitCreateCommunity(
                name.toRequestBody(),
                type.toRequestBody(),
                categoryId.toRequestBody(),
                convertFormFileToMultipartBody("image", image)
            )
            if (response.isSuccessful) response.body()!!
            else throw Exception(response.message())
        }
    }

    fun hitShowMember(request: AddMemberCommunityRequest) {
        safeFlowApiCall(_addMemberCommunityResponse) {
            val response = repository.hitShowMember(request)
            if (response.isSuccessful) response.body()!!
            else throw Exception(response.message())
        }
    }

    fun hitAddToCommunity(request: AddToCommunityRequest) {
        safeFlowApiCall(_addToCommunityResponse) {
            val response = repository.hitAddToCommunity(request)
            if (response.isSuccessful) response.body()!!
            else throw Exception(response.message())
        }
    }

    fun hitTodayUpcomingEventList(request: String) {
        safeFlowApiCall(_upComingEventList) {
            val response = repository.hitTodayUpcomingEventList(request)
            if (response.isSuccessful) response.body()!!
            else throw Exception(response.message())
        }
    }

    fun hitCommunityPostList() {
        safeFlowApiCall(_communityPostResponseList) {
            val response = repository.hitCommunityPostList()
            if (response.isSuccessful) response.body()!!
            else throw Exception(response.message())
        }
    }

    fun hitRecentJoinMember(days: String) {
        safeFlowApiCall(_recentJoinMemberList) {
            val response = repository.hitRecentJoinMember(days)
            if (response.isSuccessful) response.body()!!
            else throw Exception(response.message())
        }
    }

}