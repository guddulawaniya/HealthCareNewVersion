package com.careavatar.core_service.repository

import com.careavatar.core_model.AddMemberCommunityRequest
import com.careavatar.core_model.AddMemberCommunityResponse
import com.careavatar.core_model.AddToCommunityRequest
import com.careavatar.core_model.AddToCommunityResponse
import com.careavatar.core_model.ApproveRejectRequest
import com.careavatar.core_model.ApproveRejectResponse
import com.careavatar.core_model.BannerResponse
import com.careavatar.core_model.CategoryListResponse
import com.careavatar.core_model.CategoryListResponsePost
import com.careavatar.core_model.ChatRequestResponse
import com.careavatar.core_model.CommunityPostResponse
import com.careavatar.core_model.CreateCommunityResponse
import com.careavatar.core_model.GetCategoryRquest
import com.careavatar.core_model.GroupMessageResponse
import com.careavatar.core_model.SearchCommunityResponse
import com.careavatar.core_model.SignUpResponse
import com.careavatar.core_model.UpcomingEventModel
import com.careavatar.core_model.UpcommingResponseModel
import com.careavatar.core_model.UserDetailsResponse
import com.careavatar.core_model.UserHobbiesResponse
import com.careavatar.core_model.UserHobbiessResquest
import com.careavatar.core_model.HealthResponse
import com.careavatar.core_model.HightlightPostResponse
import com.careavatar.core_model.LoginRequest
import com.careavatar.core_model.LoginResponse
import com.careavatar.core_model.NotificationResponse
import com.careavatar.core_model.PendingChatRequest
import com.careavatar.core_model.RecentJoinMemberList
import com.careavatar.core_model.VerifyOtpRequest
import com.careavatar.core_model.VerifyOtpResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiServices {

    @POST("send-otp")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("verify-otp")
    suspend fun verifyOtp(
        @Body request: VerifyOtpRequest
    ): Response<VerifyOtpResponse>

    @Multipart
    @POST("register")
    suspend fun registerUser(
        @Part("phoneNumber") phoneNumber: RequestBody?,
        @Part("dob") dob: RequestBody?,
        @Part("name") name: RequestBody?,
        @Part("gender") gender: RequestBody?,
        @Part("emergencycontact") emergencycontact: RequestBody?,
        @Part("height") height: RequestBody?,
        @Part("weight") weight: RequestBody?,
        @Part("email") email: RequestBody?,
        @Part("BP") BP: RequestBody?,
        @Part("sugar") sugar: RequestBody?,
        @Part image: MultipartBody.Part? = null,
        @Part("isBP") isBP: Boolean = false,
        @Part("isSugar") isSugar: Boolean = false,

        ): Response<SignUpResponse>

    @GET("userDetails")
    suspend fun userDetails(): Response<UserDetailsResponse>

    @GET("dashBoardBanner1")
    suspend fun banner(): Response<BannerResponse>

    @GET("dashBoardHealthItem")
    suspend fun dashBoardHealth(): Response<HealthResponse>

    @GET("class/upcoming-classes")
    suspend fun upComingClass(): Response<UpcommingResponseModel>

    @GET("chat/request")
    suspend fun hitChatRequestList(): Response<ChatRequestResponse>


    // Social Meet Apis


    @GET("getAllPendingJoinRequests/{communityId}")
    suspend fun hitPendingRequestList(
        @Path("communityId") communityId: String
    ): Response<PendingChatRequest>

    @GET("notification")
    suspend fun hitNotification(
        @Query("notificationId") notificationId: String
    ): Response<NotificationResponse>

    @POST("communities/handle-join-request")
    suspend fun hitApproveReject(
        @Body request: ApproveRejectRequest
    ): Response<ApproveRejectResponse>

    @GET("eventsCreated")
    suspend fun todayUpcomingEventList(
        @Query("date") date: String
    ): Response<UpcomingEventModel>

    @GET("messageToCategoryMembers")
    suspend fun communityPostList(
    ): Response<CommunityPostResponse>

    @GET("latestUserCountWithinDistance")
    suspend fun hitRecentJoinMember(
        @Query("days") days: String
    ): Response<RecentJoinMemberList>

    @Multipart
    @POST("communities")
    suspend fun createCommunity(
        @Part("name") name: RequestBody?,
        @Part("type") type: RequestBody?,
        @Part("categoryId") categoryId: RequestBody?,
        @Part image: MultipartBody.Part? = null,
    ): Response<CreateCommunityResponse>

    @GET("getCategory")
    suspend fun hitGetCategoryList(
    ): Response<CategoryListResponse>

    @POST("getCategory")
    suspend fun hitGetCategoryList(
        @Body request: GetCategoryRquest
    ): Response<CategoryListResponsePost>

    @POST("communities/showmember")
    suspend fun hitShowMember(
        @Body request: AddMemberCommunityRequest
    ): Response<AddMemberCommunityResponse>

    @POST("communities/addmember")
    suspend fun hitAddToCommunity(
        @Body request: AddToCommunityRequest
    ): Response<AddToCommunityResponse>


    @GET("communityByName/{categoryName}")
    suspend fun hitSearchCategory(
        @Path("categoryName") categoryName: String
    ): Response<SearchCommunityResponse>

    @GET("get-messages/{communityId}")
    suspend fun hitGetGroupMessage(
        @Path("communityId") communityId: String,
    ): Response<GroupMessageResponse>

    @POST("userHobbies")
    suspend fun hitUserHobbies(
        @Body request: UserHobbiessResquest
    ): Response<UserHobbiesResponse>

    @Multipart
    @POST("messageToCategoryMembers")
    suspend fun hitHightLightPost(
        @Part("title") title: RequestBody?,
        @Part("hobbiesId") interestId: RequestBody?,
        @Part("description") description: RequestBody?,
        @Part("address") address: RequestBody?,
        @Part("latitude") latitude: RequestBody?,
        @Part("longitude") longitude: RequestBody?,
        @Part image: MultipartBody.Part? = null,
    ): Response<HightlightPostResponse>
}
