package com.careavatar.core_service.repository

import com.careavatar.core_model.AddMemberCommunityRequest
import com.careavatar.core_model.AddToCommunityRequest
import com.careavatar.core_model.ApproveRejectRequest
import com.careavatar.core_model.GetCategoryRquest
import com.careavatar.core_model.UserHobbiessResquest
import com.careavatar.core_model.LoginRequest
import com.careavatar.core_model.VerifyOtpRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class UserRepository(private val apiService: ApiServices) {

    suspend fun loginUser(request: LoginRequest) =
        apiService.login(request)

    suspend fun hitChatRequestList() = apiService.hitChatRequestList()
    suspend fun hitPendingRequestList(communityId: String) =
        apiService.hitPendingRequestList(communityId = communityId)

    suspend fun hitNotification(notificationId: String) =
        apiService.hitNotification(notificationId = notificationId)

    suspend fun hitVerifyOtp(request: VerifyOtpRequest) =
        apiService.verifyOtp(request)

    suspend fun hitTodayUpcomingEventList(date: String) =
        apiService.todayUpcomingEventList(date)

    suspend fun hitCommunityPostList() =
        apiService.communityPostList()

    suspend fun hitGetCategoryList(request: GetCategoryRquest) =
        apiService.hitGetCategoryList(request)

    suspend fun hitGetCategoryList() =
        apiService.hitGetCategoryList()

    suspend fun hitShowMember(request: AddMemberCommunityRequest) =
        apiService.hitShowMember(request)

    suspend fun hitAddToCommunity(request: AddToCommunityRequest) =
        apiService.hitAddToCommunity(request)

    suspend fun hitSearchCategory(categoryName: String) =
        apiService.hitSearchCategory(categoryName)

    suspend fun hitGroupMessage(communityId: String) =
        apiService.hitGetGroupMessage(communityId)

    suspend fun hitGetChatList(userId: String) =
        apiService.hitGetChatList(userId)

    suspend fun hitUserHobbies(resquest: UserHobbiessResquest) =
        apiService.hitUserHobbies(resquest)

    suspend fun hitEventsDelete(eventId: String) =
        apiService.hitEventsDelete(eventId)

    suspend fun hitEventsByDateAndCommunity(communityId: String, date: String) =
        apiService.hitEventsByDateAndCommunity(communityId,date)


    suspend fun hitCreateEventData(
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
        notifiedMembers: RequestBody,
        attachments: List<MultipartBody.Part>?,
    ) =

        apiService.hitCreateEventData(
            communityId.toRequestBody(),
            description.toRequestBody(),
            eventDate.toRequestBody(),
            eventLink.toRequestBody(),
            eventMode.toRequestBody(),
            eventTime.toRequestBody(),
            latitude.toRequestBody(),
            location.toRequestBody(),
            longitude.toRequestBody(),
            title.toRequestBody(),
            visibility.toRequestBody(),
            notifiedMembers,
            attachments
        )


    suspend fun hitCreateCommunity(
        name: RequestBody,
        type: RequestBody,
        categoryId: RequestBody,
        image: MultipartBody.Part?
    ) =
        apiService.createCommunity(
            name = name,
            type = type,
            categoryId = categoryId,
            image = image
        )

    suspend fun hitHightLightPost(
        title: RequestBody,
        interestId: RequestBody,
        description: RequestBody,
        address: RequestBody,
        latitude: RequestBody,
        longitude: RequestBody,
        image: MultipartBody.Part?
    ) =
        apiService.hitHightLightPost(
            title = title,
            interestId = interestId,
            description = description,
            address = address,
            latitude = latitude,
            longitude = longitude,
            image = image
        )


    suspend fun hitRecentJoinMember(days: String) =
        apiService.hitRecentJoinMember(days)

    suspend fun hitApproveReject(request: ApproveRejectRequest) =
        apiService.hitApproveReject(request)

    suspend fun hitUserDetails() = apiService.userDetails()
    suspend fun hitbanner() = apiService.banner()
    suspend fun hitDashBoardHealth() = apiService.dashBoardHealth()
    suspend fun hitUpComingClass() = apiService.upComingClass()
    suspend fun hitRegister(
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
        image: MultipartBody.Part? = null

    ) = apiService.registerUser(
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
        image
    )


}