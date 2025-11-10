package com.careavatar.core_service.repository

import android.util.Log
import com.careavatar.core_model.AddMemberCommunityRequest
import com.careavatar.core_model.AddToCommunityRequest
import com.careavatar.core_model.ApproveRejectRequest
import com.careavatar.core_model.GetCategoryRquest
import com.careavatar.core_model.GetUserByCategoryRequest
import com.careavatar.core_model.LoginRequest
import com.careavatar.core_model.SendRequestModelRequestBody
import com.careavatar.core_model.UserHobbiessResquest
import com.careavatar.core_model.VerifyOtpRequest
import com.careavatar.core_model.alzimer.AlzheimerDetailResponse
import com.careavatar.core_model.alzimer.AlzheimerProgressResponse
import com.careavatar.core_model.alzimer.FormRequest
import com.careavatar.core_model.alzimer.FormResponse
import com.careavatar.core_model.alzimer.Getalzimerlocation_response
import com.careavatar.core_model.alzimer.PatientListResponse
import com.careavatar.core_model.alzimer.QuestionAnswerRequest
import com.careavatar.core_model.alzimer.QuestionAnswerResponse
import com.careavatar.core_model.alzimer.QuestionListNewResponse
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File

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

    suspend fun hitLeaveCommunity(communityId: String) =
        apiService.hitLeaveCommunity(communityId)

    suspend fun hitDeleteCommunity(communityId: String) =
        apiService.hitDeleteCommunity(communityId)

    suspend fun hitGetCommunityMember(communityId: String) =
        apiService.hitGetCommunityMember(communityId)

    suspend fun hitGroupMessage(communityId: String) =
        apiService.hitGetGroupMessage(communityId)

    suspend fun hitGetChatList(userId: String) =
        apiService.hitGetChatList(userId)

    suspend fun hitUserHobbies(resquest: UserHobbiessResquest) =
        apiService.hitUserHobbies(resquest)

    suspend fun hitGetUserByCategory(resquest: GetUserByCategoryRequest) =
        apiService.hitGetUserByCategory(resquest)

    suspend fun hitSendRequest(request: SendRequestModelRequestBody) =
        apiService.hitSendRequest(request)

    suspend fun hitUserDetailsbyId(userId: String) =
        apiService.hitUserDetailsbyId(userId)

    suspend fun hitEventsDelete(eventId: String) =
        apiService.hitEventsDelete(eventId)

    suspend fun hitGetEventdetailbyid(eventId: String) =
        apiService.hitGetEventdetailbyid(eventId)

    suspend fun hitEventsByDateAndCommunity(communityId: String, date: String) =
        apiService.hitEventsByDateAndCommunity(communityId, date)


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

    // alzimer apis


    suspend fun fetchQuestionData(): Response<QuestionListNewResponse> {
        return apiService.getQuestionData()
    }

    suspend fun hitUserDetails() = apiService.userDetails()

    suspend fun FormData(
//        cookie: String,
        request: FormRequest
    ): Response<FormResponse> {
        return apiService.submitForm(request)
    }


    suspend fun NewSubmitAnswer(
        user: String? = null,
        request: QuestionAnswerRequest
    ): Response<QuestionAnswerResponse> {
        return apiService.NewSubmitAnswer(user, request)
    }


    suspend fun NewProgress(): Response<AlzheimerProgressResponse> {
        return apiService.NewProgress()
    }


    suspend fun AlzimerDetails(): Response<AlzheimerDetailResponse> {
        return apiService.AlzimerDetails()
    }

    suspend fun Addcaregiver(
        name: RequestBody,
        phoneNumber: RequestBody,
        address: RequestBody,
        relation: RequestBody,
        patientId: RequestBody,
        fcmToken: RequestBody,
        image: MultipartBody.Part? = null
    ): Response<addcaregiver_response> {
        return apiService.Addcaregiver(

            name,
            phoneNumber,
            address,
            relation,
            patientId,
            fcmToken,
            image
        )
    }


    suspend fun GetAllCaregiver(): Response<getallcaregiver_response> {
        return apiService.GetAllCaregiver()
    }


    suspend fun CaregiverDetails(id: String): Response<caregiverdetail_response> {
        return apiService.CaregiverDetails(id)
    }


    suspend fun UpdateCaregiverDetails(
        id: String,
        name: RequestBody,
        phoneNumber: RequestBody,
        address: RequestBody,
        relation: RequestBody,
        patientId: RequestBody,
        fcmToken: RequestBody,
        image: MultipartBody.Part?
    ): Response<updatecaregiver_response> {
        return apiService.UpdateCaregiverDetails(

            id,
            name,
            phoneNumber,
            address,
            relation,
            patientId,
            fcmToken,
            image
        )
    }


    suspend fun DeleteCaregiver(id: String): Response<updatecaregiver_response> {
        return apiService.DeleteCaregiver(id)
    }


    suspend fun UpdateAlzheimerDetails(
        id: String,
        fullName: RequestBody,
        age: RequestBody,
        gender: RequestBody,
        phoneNumber: RequestBody,
        image: MultipartBody.Part?
    ): Response<updatecaregiver_response> {
        return apiService.UpdateAlzheimerDetails(
            id,
            fullName, age, gender, phoneNumber, image
        )
    }


    suspend fun Updateradius(
        id: String, request: updateradius_request
    ): Response<updatecaregiver_response> {
        return apiService.Updateradius(id, request)
    }


    suspend fun GetallDoctors(
        specialist: String? = null,
        name: String? = null,
        patientId: String
    ): Response<getalldoctor_response> {
        return apiService.GetallDoctors(specialist, name, patientId)
    }


    suspend fun Createtodos(
        request: createtodos_request
    ): Response<updatecaregiver_response> {
        return apiService.Createtodos(request)
    }


    suspend fun GetallTodos(): Response<getalltodo_response> {
        return apiService.GetallTodos()
    }


    suspend fun Updatetodos(
        id: String,
        request: updatetodo_request
    ): Response<updatecaregiver_response> {
        return apiService.Updatetodos(id, request)
    }


    suspend fun Deletetodos(
        todoId: String? = null
    ): Response<updatecaregiver_response> {
        return apiService.Deletetodos(todoId)
    }


    suspend fun Bookappointment(
        request: appointment_request
    ): Response<updatecaregiver_response> {
        return apiService.Bookappointment(request)
    }


    suspend fun DoctorsDetails(id: String): Response<doctordetails_response> {
        return apiService.DoctorsDetails(id)
    }


    suspend fun Updatelocation(
        request: updatelocation_request
    ): Response<updatecaregiver_response> {
        return apiService.Updatelocation(request)
    }


    suspend fun GetallVideos(
        sort: String? = null,
        title: String? = null
    ): Response<getallvideo_response> {
        return apiService.Getallvideos(sort, title)
    }


    suspend fun GetvideosDetails(id: String): Response<videodetails_response> {
        return apiService.GetvideoDetails(id)
    }


    suspend fun Getmusic(): Response<music_response> {
        return apiService.GetMusic()
    }


    suspend fun Login(
        request: login_request
    ): Response<login_response> {
        return apiService.Login(request)
    }


    suspend fun VerifyOtp(
        request: otp_request
    ): Response<otp_response> {
        return apiService.VerifyOtp(request)
    }


    suspend fun CreateAccount(
        image: File,
        name: String,
        phoneNumber: String,
        email: String,
        dob: String,
        latitude: String,
        longitude: String,
        gender: String,
        height: String,
        weight: String,
        relativecontact: String,
        startDate: String,
        startTime: String,
        endTime: String,
        timeGap: String
    ): Response<registration_response> {
        val namePart = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val phonePart = phoneNumber.toRequestBody("text/plain".toMediaTypeOrNull())
        val emailPart = email.toRequestBody("text/plain".toMediaTypeOrNull())
        val dobPart = dob.toRequestBody("text/plain".toMediaTypeOrNull())
        val latitudePart = latitude.toRequestBody("text/plain".toMediaTypeOrNull())
        val longitudePart = longitude.toRequestBody("text/plain".toMediaTypeOrNull())
        val genderPart = gender.toRequestBody("text/plain".toMediaTypeOrNull())
        val heightPart = height.toRequestBody("text/plain".toMediaTypeOrNull())
        val weightPart = weight.toRequestBody("text/plain".toMediaTypeOrNull())
        val relativePart = relativecontact.toRequestBody("text/plain".toMediaTypeOrNull())
        val startDatePart = startDate.toRequestBody("text/plain".toMediaTypeOrNull())
        val startTimePart = startTime.toRequestBody("text/plain".toMediaTypeOrNull())
        val endTimePart = endTime.toRequestBody("text/plain".toMediaTypeOrNull())
        val timeGapPart = timeGap.toRequestBody("text/plain".toMediaTypeOrNull())

        val imagePart = image.asRequestBody("image/*".toMediaTypeOrNull())
        val imageMultipart = MultipartBody.Part.createFormData("image", image.name, imagePart)

        return apiService.createaccount(

            imageMultipart,
            namePart,
            phonePart,
            emailPart,
            dobPart,
            latitudePart,
            longitudePart,
            genderPart,
            heightPart,
            weightPart,
            relativePart,
            startDatePart,
            startTimePart,
            endTimePart,
            timeGapPart
        )
    }


    suspend fun CreateAlbum(
        title: String,
        description: String,
        files: List<File>
    ): Response<updatecaregiver_response> {

        // 🔹 Log file details
        files.forEachIndexed { index, file ->
            Log.d(
                "UPLOAD_DEBUG",
                "File $index: name=${file.name}, path=${file.absolutePath}, size=${file.length()} bytes"
            )
        }

        val titlePart = title.toRequestBody("text/plain".toMediaTypeOrNull())
        val descPart = description.toRequestBody("text/plain".toMediaTypeOrNull())

        val multipartImages = files.map {
            val reqFile = it.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("thumbnail", it.name, reqFile)
        }

        // Optional: log part details
        multipartImages.forEachIndexed { index, part ->
            Log.d(
                "PART_DEBUG",
                "Part $index: name=${part.headers}, bodySize=${part.body.contentLength()}"
            )
        }

        return apiService.CreateAlbum(titlePart, descPart, multipartImages)
    }


    suspend fun GetAlbumlist(): Response<albumlist_response> {
        return apiService.GetAlbumlist()
    }


    suspend fun GetAlbumDetails(id: String): Response<SingleAlbumResponse> {
        return apiService.GetAlbumDetails(id)
    }


    suspend fun UpdateAlbumDetails(
        id: String,
        title: String,
        description: String,
        files: List<File>
    ): Response<updatecaregiver_response> {

        // 🔹 Log file details
        files.forEachIndexed { index, file ->
            Log.d(
                "UPLOAD_DEBUG",
                "File $index: name=${file.name}, path=${file.absolutePath}, size=${file.length()} bytes"
            )
        }

        val titlePart = title.toRequestBody("text/plain".toMediaTypeOrNull())
        val descPart = description.toRequestBody("text/plain".toMediaTypeOrNull())

        val multipartImages = files.map {
            val reqFile = it.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("thumbnail", it.name, reqFile)
        }

        // Optional: log part details
        multipartImages.forEachIndexed { index, part ->
            Log.d(
                "PART_DEBUG",
                "Part $index: name=${part.headers}, bodySize=${part.body.contentLength()}"
            )
        }

        return apiService.UpdateAlbumDetails(id, titlePart, descPart, multipartImages)
    }


    suspend fun DeleteAlbum(id: String): Response<updatecaregiver_response> {
        return apiService.DeleteAlbum(id)
    }


    suspend fun CreateMemory(
        title: String,
        thumbnail: File,
        color: String
    ): Response<updatecaregiver_response> {

        val titlePart = title.toRequestBody("text/plain".toMediaTypeOrNull())
        val colorPart = color.toRequestBody("text/plain".toMediaTypeOrNull())

        // 🔹 Prepare single thumbnail part
        val thumbnailReqFile = thumbnail.asRequestBody("image/*".toMediaTypeOrNull())
        val thumbnailPart =
            MultipartBody.Part.createFormData("thumbnail", thumbnail.name, thumbnailReqFile)



        return apiService.CreateMemory(
            title = titlePart,
            thumbnail = thumbnailPart,
            color = colorPart
        )
    }


    suspend fun GetMemory(): Response<memory_response> {
        return apiService.GetMemorylist()
    }


    suspend fun CreateVoice(
//                            userId: String,
        userType: String,
        music: File
    ): Response<updatecaregiver_response> {

//        val userId = userId.toRequestBody("text/plain".toMediaTypeOrNull())
        val userType = userType.toRequestBody("text/plain".toMediaTypeOrNull())


        // 🔹 Prepare single music part
        val musicReqFile = music.asRequestBody("audio/*".toMediaTypeOrNull())
        val musicPart = MultipartBody.Part.createFormData("music", music.name, musicReqFile)

        // Optional: log debug info
        Log.d("UPLOAD_DEBUG", "Music: ${music.name}, size=${music.length()}")

        return apiService.CreateVoice(
            userType = userType,
            music = musicPart
        )
    }


    suspend fun GetVoicelist(): Response<voice_response> {
        return apiService.GetVoicelist()
    }


    suspend fun Createmood(
        request: mood_request
    ): Response<updatecaregiver_response> {
        return apiService.Createmood(request)
    }


    suspend fun GetMood(user: String? = null): Response<mood_response> {
        return apiService.GetMood(user)
    }


    suspend fun Createmessage(
        request: createmessage_request
    ): Response<updatecaregiver_response> {
        return apiService.Createmessage(request)
    }


    suspend fun Getmessage(): Response<getmessage_response> {
        return apiService.GetMessage()
    }


    suspend fun GetTasklist(): Response<tasklist_response> {
        return apiService.GetTasklist()
    }


    suspend fun ActivitySubmit(
        title: String,
        music: File?,                  // Nullable
        thumbnail: List<File>?,              // Nullable
        isCompleted: Boolean,
        activityId: String
    ): Response<updatecaregiver_response> {

        val titlePart = title.toRequestBody("text/plain".toMediaTypeOrNull())
        val isCompletedPart = isCompleted.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val activityIdPart = activityId.toRequestBody("text/plain".toMediaTypeOrNull())

        // Create nullable parts
        val musicPart: MultipartBody.Part? = music?.let {
            val reqFile = it.asRequestBody("audio/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("music", it.name, reqFile)
        }

        val thumbnailParts: List<MultipartBody.Part>? = thumbnail?.map { file ->
            val reqFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("thumbnail", file.name, reqFile)
        }

        // Log debug info
        music?.let { Log.d("UPLOAD_DEBUG", "Music: ${it.name}, size=${it.length()}") }
//        thumbnail?.let { Log.d("UPLOAD_DEBUG", "Thumbnail: ${it.name}, size=${it.length()}") }

        return apiService.ActivitySubmit(
            title = titlePart,
            music = musicPart,
            thumbnail = thumbnailParts,
            isCompleted = isCompletedPart,
            activityId = activityIdPart
        )
    }


    suspend fun GetActivityResult(
        user: String? = null
    ): Response<getActivity_response> {
        return apiService.GetActivityResult(user)
    }


    suspend fun GetUserDetails(): Response<getuserdetail_response> {
        return apiService.GetUserDetails()
    }


    suspend fun GetPatientlist(): Response<PatientListResponse> {
        return apiService.GetPatientlist()
    }


    suspend fun GetAlzimerbyid(id: String): Response<getalzimerdetailbyid> {
        return apiService.GetAlzimerbyid(id)
    }


    suspend fun GetCaregiverbyid(id: String): Response<getcaregiverbyidresponse> {
        return apiService.GetCaregiverbyid(id)
    }

    suspend fun GetAlzimerlocation(
        alzheimerId: String? = null,
    ): Response<Getalzimerlocation_response> {
        return apiService.GetAlzimerlocation(alzheimerId)
    }


    suspend fun Getradiussafezone(id: String): Response<RadiusResponse> {
        return apiService.Getradiussafezone(id)
    }


    suspend fun SoSNotification(
    ): Response<updatecaregiver_response> {
        return apiService.SoSNotification()
    }

    suspend fun hitGetVaccinationList(
        userId: String
    ): Response<VaccinationDetailsResponse> {
        return apiService.hitGetVaccinationList(userId)
    }

    suspend fun hitCreateVaccination(
        request: AddVaccinationRequest
    ): Response<VaccinationResponse> {
        return apiService.hitCreateVaccination(request)
    }

    suspend fun hitCreatePeriodLog(
        request: CreatePeriodRequestModel
    ): Response<CreatePeriodResponseModel> {
        return apiService.hitCreatePeriodLog(request)
    }

    suspend fun hitFetchPeriodDetails(): Response<PeriodGetAllDataResponse> {
        return apiService.hitFetchPeriodDetails()
    }

    suspend fun hitWaterHistory(): Response<WaterHistoryResponse> {
        return apiService.hitWaterHistory()
    }

    suspend fun hitWaterReminderCreate(request: CreateWaterReminderRequest): Response<CreateWaterReminderResponse> {
        return apiService.hitWaterReminderCreate(request)
    }

    suspend fun hitWaterReminderUpdate(
        id: String,
        request: CreateWaterReminderRequest
    ): Response<CreateWaterReminderResponse> {
        return apiService.hitWaterReminderUpdate(id, request)
    }

    suspend fun hitCreateMedicalRemainder(request: CreateMedicalReminderRequest): Response<CreateMedicalReminderResponse> {
        return apiService.hitCreateMedicalRemainder(request)
    }

    suspend fun hitGetDisease(): Response<GetDiseaseResponse> {
        return apiService.hitGetDisease()
    }

    suspend fun hitReminderHistory(type: String): Response<HistoryReminderResponse> {
        return apiService.hitHistoryReminder(type)
    }

    suspend fun hitProfessionCategory(): Response<ProfessionCategoryResponse> {
        return apiService.hitProfessionCategory()
    }

    suspend fun hitDailyRoutineResponseList(): Response<DailyRoutineResponse> {
        return apiService.hitDailyRoutineResponseList()
    }

    suspend fun hitPrimaryReason(): Response<PrimaryReasonJoinApp> {
        return apiService.hitPrimaryReason()
    }

    suspend fun hitFoodAllergies(): Response<DietFoodAllergiesResponse> {
        return apiService.hitFoodAllergies()
    }

    suspend fun hitSpecificDiet(): Response<SpecificDietResponse> {
        return apiService.hitSpecificDiet()
    }

    suspend fun hitAddIntoCart(request: dietAddCartModelRequest): Response<DietAddCartModelResponse> {
        return apiService.hitAddIntoCart(request)
    }

    suspend fun hitRecipeById(id: String): Response<RecipeResponsedetails> {
        return apiService.hitRecipeById(id)
    }


    suspend fun hitGetFitnessExpert(category: String): Response<GetExpertResponse2> {
        return apiService.hitGetFitnessExpert(category)
    }

    suspend fun hitRecipeList(
        dietType: List<String>,
        category: List<String>,
        typeOfReceipe: String,
        dietChartId: String? = "",
    ): Response<RecipeResponse> {
        return apiService.hitRecipeList(
            categories = category,
            dietType = dietType,
            typeOfReceipe = typeOfReceipe,
            dietChartId = dietChartId
        )
    }

    suspend fun hitAllMedicine(
        target: String,
        page: Int,
        pageSize: Int,
    ): Response<GetAllMedicineResponse> {
        return apiService.hitAllMedicine(
            target = target,
            page = page,
            pageSize = pageSize,
        )
    }

    suspend fun hitGetAvailableSlot(request: ExpertRequest): Response<ExpertBookingAvailableResponse> {
        return apiService.hitGetAvailableSlot(request)
    }

    suspend fun hitQuestionPost(request: DietQuestionPostRequest): Response<DietQuestionResponse> {
        return apiService.hitQuestionPost(request)
    }

    suspend fun hitCategoryWithSubcategories(): Response<HealthMonitorQuestions> {
        return apiService.hitCategoryWithSubcategories()
    }

    suspend fun hitCategoryList(): Response<DietCategoryResponse> {
        return apiService.hitCategoryList()
    }

    suspend fun hitAddIntoCartList(): Response<AddCartListResponse> {
        return apiService.hitAddIntoCartList()
    }
}