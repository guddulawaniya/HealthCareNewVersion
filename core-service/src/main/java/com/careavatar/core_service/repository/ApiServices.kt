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
import com.careavatar.core_model.CommnityMemberListResponse
import com.careavatar.core_model.CommunityPostResponse
import com.careavatar.core_model.CreateCommunityResponse
import com.careavatar.core_model.DeleteCommunitesResponse
import com.careavatar.core_model.DeleteEventResponse
import com.careavatar.core_model.EventPostResponse
import com.careavatar.core_model.GetCategoryRquest
import com.careavatar.core_model.GetEventdataById
import com.careavatar.core_model.GetUserByCategoryRequest
import com.careavatar.core_model.GetUserByCategoryResponse
import com.careavatar.core_model.GroupMessageResponse
import com.careavatar.core_model.SearchCommunityResponse
import com.careavatar.core_model.SignUpResponse
import com.careavatar.core_model.UpcomingEventModel
import com.careavatar.core_model.UpcommingResponseModel
import com.careavatar.core_model.UserHobbiesResponse
import com.careavatar.core_model.UserHobbiessResquest
import com.careavatar.core_model.HealthResponse
import com.careavatar.core_model.HightlightPostResponse
import com.careavatar.core_model.LoginRequest
import com.careavatar.core_model.LoginResponse
import com.careavatar.core_model.NotificationResponse
import com.careavatar.core_model.PendingChatRequest
import com.careavatar.core_model.RecentJoinMemberList
import com.careavatar.core_model.SendRequestModel
import com.careavatar.core_model.SendRequestModelRequestBody
import com.careavatar.core_model.UserChatListResponse
import com.careavatar.core_model.UserDetailsbyIdResponse
import com.careavatar.core_model.VerifyOtpRequest
import com.careavatar.core_model.VerifyOtpResponse
import com.careavatar.core_model.alzimer.QuestionListNewResponse
import com.careavatar.core_model.alzimer.AlzheimerDetailResponse
import com.careavatar.core_model.alzimer.AlzheimerProgressResponse
import com.careavatar.core_model.alzimer.FormRequest
import com.careavatar.core_model.alzimer.FormResponse
import com.careavatar.core_model.alzimer.Getalzimerlocation_response
import com.careavatar.core_model.alzimer.PatientListResponse
import com.careavatar.core_model.alzimer.QuestionAnswerRequest
import com.careavatar.core_model.alzimer.QuestionAnswerResponse
import com.careavatar.core_model.alzimer.RadiusResponse
import com.careavatar.core_model.alzimer.SingleAlbumResponse
import com.careavatar.core_model.alzimer.addcaregiver_response
import com.careavatar.core_model.alzimer.albumlist_response
import com.careavatar.core_model.alzimer.appointment_request
import com.careavatar.core_model.alzimer.caregiverdetail_response
import com.careavatar.core_model.alzimer.createmessage_request
import com.careavatar.core_model.alzimer.createtodos_request
import com.careavatar.core_model.alzimer.doctordetails_response
import com.careavatar.core_model.alzimer.getActivity_response
import com.careavatar.core_model.alzimer.getallcaregiver_response
import com.careavatar.core_model.alzimer.getalldoctor_response
import com.careavatar.core_model.alzimer.getalltodo_response
import com.careavatar.core_model.alzimer.getallvideo_response
import com.careavatar.core_model.alzimer.getalzimerdetailbyid
import com.careavatar.core_model.alzimer.getcaregiverbyidresponse
import com.careavatar.core_model.alzimer.getmessage_response
import com.careavatar.core_model.alzimer.getuserdetail_response
import com.careavatar.core_model.alzimer.login_request
import com.careavatar.core_model.alzimer.login_response
import com.careavatar.core_model.alzimer.memory_response
import com.careavatar.core_model.alzimer.mood_request
import com.careavatar.core_model.alzimer.mood_response
import com.careavatar.core_model.alzimer.music_response
import com.careavatar.core_model.alzimer.otp_request
import com.careavatar.core_model.alzimer.otp_response
import com.careavatar.core_model.alzimer.registration_response
import com.careavatar.core_model.alzimer.tasklist_response
import com.careavatar.core_model.alzimer.updatecaregiver_response
import com.careavatar.core_model.alzimer.updatelocation_request
import com.careavatar.core_model.alzimer.updateradius_request
import com.careavatar.core_model.alzimer.updatetodo_request
import com.careavatar.core_model.alzimer.videodetails_response
import com.careavatar.core_model.alzimer.voice_response
import com.careavatar.core_model.UserDetailsResponse
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
import com.careavatar.core_model.dietition.HealthMonitorQuestions
import com.careavatar.core_model.dietition.PrimaryReasonJoinApp
import com.careavatar.core_model.dietition.ProfessionCategoryResponse
import com.careavatar.core_model.dietition.RecipeResponse
import com.careavatar.core_model.dietition.RecipeResponsedetails
import com.careavatar.core_model.dietition.SpecificDietResponse
import com.careavatar.core_model.dietition.dietAddCartModelRequest
import com.careavatar.core_model.medicalReminder.AddVaccinationRequest
import com.careavatar.core_model.medicalReminder.CreateMedicalReminderRequest
import com.careavatar.core_model.medicalReminder.CreateMedicalReminderResponse
import com.careavatar.core_model.medicalReminder.CreatePeriodRequestModel
import com.careavatar.core_model.medicalReminder.CreatePeriodResponseModel
import com.careavatar.core_model.medicalReminder.CreateWaterReminderRequest
import com.careavatar.core_model.medicalReminder.CreateWaterReminderResponse
import com.careavatar.core_model.medicalReminder.GetDiseaseResponse
import com.careavatar.core_model.medicalReminder.HistoryReminderResponse
import com.careavatar.core_model.medicalReminder.PeriodGetAllDataResponse
import com.careavatar.core_model.medicalReminder.VaccinationDetailsResponse
import com.careavatar.core_model.medicalReminder.VaccinationResponse
import com.careavatar.core_model.medicalReminder.WaterHistoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
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

    @GET("get-initial-contacts/{userId}")
    suspend fun hitGetChatList(
        @Path("userId") userId: String,
    ): Response<UserChatListResponse>

    @POST("userHobbies")
    suspend fun hitUserHobbies(
        @Body request: UserHobbiessResquest
    ): Response<UserHobbiesResponse>

    @GET("event/{eventid}")
    suspend fun hitGetEventdetailbyid(
        @Path("eventid") eventid: String,
    ): Response<GetEventdataById>

    @DELETE("event/{eventId}")
    suspend fun hitEventsDelete(
        @Path("eventId") eventId: String
    ): Response<DeleteEventResponse>

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

    @GET("eventsByDate/{communityId}")
    suspend fun hitEventsByDateAndCommunity(
        @Path("communityId") communityId: String,
        @Query("date") date: String
    ): Response<UpcomingEventModel>

    @POST("getUserByCategory")
    suspend fun hitGetUserByCategory(
        @Body request: GetUserByCategoryRequest
    ): Response<GetUserByCategoryResponse>


    @POST("chat/request")
    suspend fun hitSendRequest(
        @Body receiverId: SendRequestModelRequestBody?
    ): Response<SendRequestModel>

    @GET("communities/members/list/{userId}")
    suspend fun hitUserDetailsbyId(
        @Path("userId") userId: String,
    ): Response<UserDetailsbyIdResponse>

    @GET("communityMembers/{communityId}")
    suspend fun hitGetCommunityMember(
        @Path("communityId") communityId: String,
    ): Response<CommnityMemberListResponse>


    @Multipart
    @POST("event")
    suspend fun hitCreateEventData(
        @Part("communityId") communityId: RequestBody,
        @Part("description") description: RequestBody,
        @Part("eventDate") eventDate: RequestBody,
        @Part("eventLink") eventLink: RequestBody,
        @Part("eventMode") eventMode: RequestBody,
        @Part("eventTime") eventTime: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("location") location: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part("title") title: RequestBody,
        @Part("visibility") visibility: RequestBody,
        @Part("notifiedMembers") notifiedMembers: RequestBody,
        @Part attachments: List<MultipartBody.Part>?,

    ): Response<EventPostResponse>

    @Multipart
    @PATCH("event/{eventid}")
    suspend fun UpdateEventdata(
        @Path("eventid") eventid: String,
        @Part("communityId") communityId: RequestBody,
        @Part("description") description: RequestBody,
        @Part("eventDate") eventDate: RequestBody,
        @Part("eventLink") eventLink: RequestBody,
        @Part("eventMode") eventMode: RequestBody,
        @Part("eventTime") eventTime: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("location") location: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part("title") title: RequestBody,
        @Part("visibility") visibility: RequestBody,
//        @Part attachments: List<MultipartBody.Part>?,
    ): Response<EventPostResponse>


    @GET("leaveCommunity/{communityId}")
    suspend fun hitLeaveCommunity(
        @Path("communityId") communityId: String
    ): Response<DeleteCommunitesResponse>

    @DELETE("communities/{communityId}")
    suspend fun hitDeleteCommunity(
        @Path("communityId") communityId: String
    ): Response<DeleteCommunitesResponse>


    // Alzimer apis


    @GET("assesment/alzheimer/question/list")
    suspend fun getQuestionData(
    ): Response<QuestionListNewResponse>

    @POST("assesment/alzheimer")
    suspend fun submitForm(
        @Body requestBody: FormRequest
    ): Response<FormResponse>


    @POST("assesment/alzheimer/question/answer")
    suspend fun NewSubmitAnswer(
        @Query("user") user: String? = null,
        @Body requestBody: QuestionAnswerRequest
    ):Response<QuestionAnswerResponse>


    @GET("assesment/alzheimer/user/progress")
    suspend fun NewProgress(
    ): Response<AlzheimerProgressResponse>



    @GET("assesment/alzheimer")
    suspend fun AlzimerDetails(
    ): Response<AlzheimerDetailResponse>



    @Multipart
    @POST("assesment/alzheimer/caregiver")
    suspend fun Addcaregiver(
        @Part("name") name: RequestBody,
        @Part("phoneNumber") phoneNumber: RequestBody,
        @Part("address") address: RequestBody,
        @Part("relation") relation: RequestBody,
        @Part("patientId") patientId: RequestBody,
        @Part("fcmToken") fcmToken: RequestBody,
        @Part image: MultipartBody.Part? // Optional image file
    ): Response<addcaregiver_response>


    @GET("assesment/alzheimer/caregivers/list")
    suspend fun GetAllCaregiver(
    ): Response<getallcaregiver_response>



    @GET("assesment/alzheimer/caregiverId/{id}")
    suspend fun CaregiverDetails(
        @Path("id") id: String
    ): Response<caregiverdetail_response>

    @Multipart
    @PATCH("assesment/alzheimer/caregiver/{id}")
    suspend fun UpdateCaregiverDetails(
        @Path("id") id: String,
        @Part("name") name: RequestBody,
        @Part("phoneNumber") phoneNumber: RequestBody,
        @Part("address") address: RequestBody,
        @Part("relation") relation: RequestBody,
        @Part("patientId") patientId: RequestBody,
        @Part("fcmToken") fcmToken: RequestBody,
        @Part image: MultipartBody.Part?
    ): Response<updatecaregiver_response>




    @DELETE("assesment/alzheimer/caregiver/{id}")
    suspend fun DeleteCaregiver(
        @Path("id") id: String
    ): Response<updatecaregiver_response>




    @Multipart
    @PATCH("assesment/alzheimer/{id}")
    suspend fun UpdateAlzheimerDetails(
        @Path("id") id: String,
        @Part("fullName") fullName: RequestBody,
        @Part("age") age: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("phoneNumber") phoneNumber: RequestBody,
        @Part image: MultipartBody.Part? // This handles the image
    ): Response<updatecaregiver_response>








    @PATCH("assesment/alzheimer/update/radius/{id}")
    suspend fun Updateradius(
        @Path("id") id: String,
        @Body requestBody: updateradius_request
    ): Response<updatecaregiver_response>




    @GET("assesment/alzheimer/doctors/list")
    suspend fun GetallDoctors(
        @Query("specialist") specialist: String? = null, // optional parameter
        @Query("name") name: String? = null, // optional parameter
        @Query("patientId") patientId: String
    ): Response<getalldoctor_response>




    @POST("assesment/alzheimer/todos")
    suspend fun Createtodos(
        @Body requestBody: createtodos_request
    ): Response<updatecaregiver_response>



    @GET("assesment/alzheimer/todos/list")
    suspend fun GetallTodos(
    ): Response<getalltodo_response>



    @PUT("assesment/alzheimer/todos/{id}")
    suspend fun Updatetodos(
        @Path("id") id: String,
        @Body requestBody: updatetodo_request
    ): Response<updatecaregiver_response>


    @DELETE("assesment/alzheimer/todos")
    suspend fun Deletetodos(
        @Query("todoId") todoId: String? = null // optional parameter
    ): Response<updatecaregiver_response>





    @POST("assesment/alzheimer/doctors/appointment")
    suspend fun Bookappointment(
        @Body requestBody: appointment_request
    ): Response<updatecaregiver_response>



    @GET("assesment/alzheimer/doctors/{id}")
    suspend fun DoctorsDetails(
        @Path("id") id: String
    ): Response<doctordetails_response>




    @POST("assesment/alzheimer/update/location")
    suspend fun Updatelocation(
        @Body requestBody: updatelocation_request
    ): Response<updatecaregiver_response>




    @GET("assesment/alzheimer/videos/list")
    suspend fun Getallvideos(
        @Query("sort") sort: String? = null, // optional parameter
        @Query("search") title: String? = null // optional parameter
    ): Response<getallvideo_response>



    @GET("assesment/alzheimer/videos/{id}")
    suspend fun GetvideoDetails(
        @Path("id") id: String
    ): Response<videodetails_response>



    @GET("assesment/alzheimer/meditation/list")
    suspend fun GetMusic(
    ): Response<music_response>


    @POST("send-otp")
    suspend fun Login(
        @Body requestBody: login_request
    ): Response<login_response>


    @POST("verify-otp")
    suspend fun VerifyOtp(
        @Body requestBody: otp_request
    ): Response<otp_response>


    @Multipart
    @POST("register")
    suspend fun createaccount(
        @Part image: MultipartBody.Part,
        @Part("name") name: RequestBody,
        @Part("phoneNumber") phoneNumber: RequestBody,
        @Part("email") email: RequestBody,
        @Part("dob") dob: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("height") height: RequestBody,
        @Part("weight") weight: RequestBody,
        @Part("relativecontact") relativecontact: RequestBody,
        @Part("startDate") startDate: RequestBody,
        @Part("startTime") startTime: RequestBody, // <-- Add this
        @Part("endTime") endTime: RequestBody,
        @Part("timeGap") timeGap: RequestBody,
    ): Response<registration_response>



    @Multipart
    @POST("assesment/album/create")
    suspend fun CreateAlbum(
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part thumbnails: List<MultipartBody.Part>
    ): Response<updatecaregiver_response>


    @GET("assesment/album/list")
    suspend fun GetAlbumlist(
    ): Response<albumlist_response>




    @GET("assesment/album/{id}")
    suspend fun GetAlbumDetails(
        @Path("id") id: String
    ): Response<SingleAlbumResponse>



    @Multipart
    @PATCH("assesment/album/{id}")
    suspend fun UpdateAlbumDetails(
        @Path("id") id: String,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part thumbnails: List<MultipartBody.Part>
    ): Response<updatecaregiver_response>


    @DELETE("assesment/album/{id}")
    suspend fun DeleteAlbum(
        @Path("id") id: String,
    ): Response<updatecaregiver_response>


    @Multipart
    @POST("assesment/memory/create")
    suspend fun CreateMemory(
        @Part("title") title: RequestBody,
        @Part thumbnail: MultipartBody.Part,
        @Part("color") color: RequestBody // ✅ add the name
    ): Response<updatecaregiver_response>





    @GET("assesment/memory/list")
    suspend fun GetMemorylist(
    ): Response<memory_response>




    @Multipart
    @POST("assesment/voice/create")
    suspend fun CreateVoice(
        @Part music: MultipartBody.Part,
        @Part("userType") userType: RequestBody
    ): Response<updatecaregiver_response>

    @GET("assesment/my-voices")
    suspend fun GetVoicelist(
    ): Response<voice_response>



    @POST("assesment/mood/create")
    suspend fun Createmood(
        @Body requestBody: mood_request
    ): Response<updatecaregiver_response>



    @GET("assesment/mood")
    suspend fun GetMood(
        @Query("user") user: String? = null
    ): Response<mood_response>




    @POST("assesment/message/create")
    suspend fun Createmessage(
        @Body requestBody: createmessage_request
    ): Response<updatecaregiver_response>




    //    @GET("assesment/message/{id}")
    @GET("assesment/message")
    suspend fun GetMessage(
//        @Path("id") id: String,
    ): Response<getmessage_response>



    @GET("assesment/all/task")
    suspend fun GetTasklist(
    ): Response<tasklist_response>



    @Multipart
    @POST("assesment/activities/submit")
    suspend fun ActivitySubmit(
        @Part ("title") title: RequestBody,
        @Part music: MultipartBody.Part?,       // 🔹 Nullable
        @Part thumbnail: List<MultipartBody.Part>?,   // 🔹 Nullable
        @Part ("isCompleted") isCompleted: RequestBody,
        @Part("activityId") activityId: RequestBody
    ): Response<updatecaregiver_response>





    @GET("assesment/activities/result")
    suspend fun GetActivityResult(
        @Query("users") user: String? = null
    ): Response<getActivity_response>




    @GET("userDetails")
    suspend fun GetUserDetails(
    ): Response<getuserdetail_response>



    @GET("assesment/alzheimer/caregiver/patients/list")
    suspend fun GetPatientlist(
    ): Response<PatientListResponse>



    @GET("assesment/alzheimer/{id}")
    suspend fun GetAlzimerbyid(
        @Path("id") id: String,
    ): Response<getalzimerdetailbyid>



    @GET("assesment/alzheimer/caregiver/{id}")
    suspend fun GetCaregiverbyid(
        @Path("id") id: String,
    ): Response<getcaregiverbyidresponse>




    @GET("assesment/alzheimer/location/list")
    suspend fun GetAlzimerlocation(
        @Query("alzheimerId") alzheimerId: String? = null,
    ): Response<Getalzimerlocation_response>


    @GET("assesment/alzheimer/{id}/radius-location")
    suspend fun Getradiussafezone(
        @Path("id") id: String,
    ): Response<RadiusResponse>


    @POST("assesment/patient/send-sos")
    suspend fun SoSNotification(
    ): Response<updatecaregiver_response>

    // medical Reminder

    @GET("vaccination/vaccination/{userId}")
    suspend fun hitGetVaccinationList(
        @Path("userId") userId: String,
    ): Response<VaccinationDetailsResponse>

    @POST("vaccination/vaccination")
    suspend fun hitCreateVaccination(
        @Body request: AddVaccinationRequest
    ): Response<VaccinationResponse>

    @POST("harmonal/create")
    suspend fun hitCreatePeriodLog(
        @Body request: CreatePeriodRequestModel
    ): Response<CreatePeriodResponseModel>

    @GET("harmonal/list")
    suspend fun hitFetchPeriodDetails(
    ): Response<PeriodGetAllDataResponse>

    @GET("waterIntake/history")
    suspend fun hitWaterHistory(
    ): Response<WaterHistoryResponse>

    @POST("WaterIntake/add")
    suspend fun hitWaterReminderCreate(
        @Body request: CreateWaterReminderRequest
    ): Response<CreateWaterReminderResponse>

    @PATCH("waterIntake/{id}")
    suspend fun hitWaterReminderUpdate(
        @Path("id") id: String,
        @Body request: CreateWaterReminderRequest
    ): Response<CreateWaterReminderResponse>

    @POST("medicalRemainder")
    suspend fun hitCreateMedicalRemainder(
        @Body request: CreateMedicalReminderRequest
    ): Response<CreateMedicalReminderResponse>

    @GET("preDifine/deases")
    suspend fun hitGetDisease(
    ): Response<GetDiseaseResponse>


    @GET("medicalRemainderHistory")
    suspend fun hitHistoryReminder(
        @Query("type") type: String
    ): Response<HistoryReminderResponse>

    @GET("diechart/profession")
    suspend fun hitProfessionCategory(
    ): Response<ProfessionCategoryResponse>

    @GET("diechart/daily-routine")
    suspend fun hitDailyRoutineResponseList(
    ): Response<DailyRoutineResponse>

    @GET("diechart/primary-reason")
    suspend fun hitPrimaryReason(
    ): Response<PrimaryReasonJoinApp>

    @GET("medicines")
    suspend fun hitAllMedicine(
        @Query("target") target: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Response<GetAllMedicineResponse>

    @GET("diechart/foodAllergies")
    suspend fun hitFoodAllergies(): Response<DietFoodAllergiesResponse>

    @GET("diechart/specific-diet")
    suspend fun hitSpecificDiet(): Response<SpecificDietResponse>

    @GET("fitness/fitnessExpert")
    suspend fun hitGetFitnessExpert(
        @Query("category") category: String
    ): Response<GetExpertResponse2>

    @GET("diechart/receipe")
    suspend fun hitRecipeList(
        @Query("meal_category") categories: List<String>,
        @Query("type") dietType: List<String>,
        @Query("typeOfReceipe") typeOfReceipe: String?="",
        @Query("chartId") dietChartId: String?=""
    ): Response<RecipeResponse>

    @POST("diechart/receipe/addToCart")
    suspend fun hitAddIntoCart(
        @Body request: dietAddCartModelRequest
    ): Response<DietAddCartModelResponse>


    @POST("fitness/availableSlots")
    suspend fun hitGetAvailableSlot(
        @Body request: ExpertRequest
    ): Response<ExpertBookingAvailableResponse>

    @GET("fitness/fitnessCategory")
    suspend fun hitCategoryWithSubcategories(
    ): Response<HealthMonitorQuestions>

    @GET("diechart/diet-category")
    suspend fun hitCategoryList(
    ): Response<DietCategoryResponse>

    @POST("diechart/dietQuestion")
    suspend fun hitQuestionPost(@Body request: DietQuestionPostRequest
    ): Response<DietQuestionResponse>

    @GET("diechart/cartList")
    suspend fun hitAddIntoCartList(): Response<AddCartListResponse>

    @GET("diechart/receipe/{recipeId}")
    suspend fun hitRecipeById(
        @Path("recipeId") id: String,
    ): Response<RecipeResponsedetails>

}
