package com.asyscraft.alzimer_module.viewModel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asyscraft.alzimer_module.singltondata.AnswerCollector
import com.asyscraft.alzimer_module.utils.Resource
import com.careavatar.core_model.UserDetailsResponse
import com.careavatar.core_model.alzimer.AlzheimerDetailResponse
import com.careavatar.core_model.alzimer.AlzheimerProgressResponse
import com.careavatar.core_model.alzimer.FormRequest
import com.careavatar.core_model.alzimer.FormResponse
import com.careavatar.core_model.alzimer.Getalzimerlocation_response
import com.careavatar.core_model.alzimer.PatientListResponse
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
import com.careavatar.core_service.repository.UserRepository
import com.careavatar.core_utils.ApiResult
import com.careavatar.core_utils.SafeFlowApiCall.safeFlowApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject

@HiltViewModel
class YourViewModel @Inject constructor(private val repository: UserRepository) : ViewModel() {

    private val _dataQuestion = MutableLiveData<Resource<QuestionListNewResponse>>()
    val dataQuestion: LiveData<Resource<QuestionListNewResponse>> get() = _dataQuestion


    // Form
    private val _formResponse = MutableLiveData<FormResponse>()
    val formResponse: LiveData<FormResponse> get() = _formResponse


    private val _Newprogress = MutableLiveData<Resource<AlzheimerProgressResponse>>()
    val Newprogress: LiveData<Resource<AlzheimerProgressResponse>> get() = _Newprogress


    private val _AlzimerDetails = MutableLiveData<Resource<AlzheimerDetailResponse>>()
    val AlzimerDetails: LiveData<Resource<AlzheimerDetailResponse>> get() = _AlzimerDetails


    private val _addcaregiver = MutableLiveData<Resource<addcaregiver_response>>()
    val addcaregiver: LiveData<Resource<addcaregiver_response>> get() = _addcaregiver


    private val _GetAllCaregiver = MutableLiveData<Resource<getallcaregiver_response>>()
    val GetAllCaregiver: LiveData<Resource<getallcaregiver_response>> get() = _GetAllCaregiver


    private val _CaregiverDetails = MutableLiveData<Resource<caregiverdetail_response>>()
    val CaregiverDetails: LiveData<Resource<caregiverdetail_response>> get() = _CaregiverDetails


    private val _UpdateCaregiverDetails = MutableLiveData<Resource<updatecaregiver_response>>()
    val UpdateCaregiverDetails: LiveData<Resource<updatecaregiver_response>> get() = _UpdateCaregiverDetails


    private val _DeleteCaregiver = MutableLiveData<Resource<updatecaregiver_response>>()
    val DeleteCaregiver: LiveData<Resource<updatecaregiver_response>> get() = _DeleteCaregiver


    private val _UpdateAlzheimerDetails = MutableLiveData<Resource<updatecaregiver_response>>()
    val UpdateAlzheimerDetails: LiveData<Resource<updatecaregiver_response>> get() = _UpdateAlzheimerDetails


    private val _Updateradius = MutableLiveData<Resource<updatecaregiver_response>>()
    val Updateradius: LiveData<Resource<updatecaregiver_response>> get() = _Updateradius


    private val _GetallDoctor = MutableLiveData<Resource<getalldoctor_response>>()
    val GetallDoctor: LiveData<Resource<getalldoctor_response>> get() = _GetallDoctor


    private val _createtodo = MutableLiveData<Resource<updatecaregiver_response>>()
    val createtodo: LiveData<Resource<updatecaregiver_response>> get() = _createtodo


    private val _GetallTodo = MutableLiveData<Resource<getalltodo_response>>()
    val GetallTodo: LiveData<Resource<getalltodo_response>> get() = _GetallTodo


    private val _Updatetodos = MutableLiveData<Resource<updatecaregiver_response>>()
    val Updatetodos: LiveData<Resource<updatecaregiver_response>> get() = _Updatetodos


    private val _Deletetodos = MutableLiveData<Resource<updatecaregiver_response>>()
    val Deletetodos: LiveData<Resource<updatecaregiver_response>> get() = _Deletetodos


    private val _bookappointment = MutableLiveData<Resource<updatecaregiver_response>>()
    val bookappointment: LiveData<Resource<updatecaregiver_response>> get() = _bookappointment


    private val _Doctorsdetails = MutableLiveData<Resource<doctordetails_response>>()
    val Doctorsdetails: LiveData<Resource<doctordetails_response>> get() = _Doctorsdetails


    private val _updatelocation = MutableLiveData<updatecaregiver_response>()
    val updatelocation: LiveData<updatecaregiver_response> get() = _updatelocation


    private val _Getallvideos = MutableLiveData<Resource<getallvideo_response>>()
    val Getallvideos: LiveData<Resource<getallvideo_response>> get() = _Getallvideos


    private val _GetvideosDetails = MutableLiveData<Resource<videodetails_response>>()
    val GetvideosDetails: LiveData<Resource<videodetails_response>> get() = _GetvideosDetails


    private val _Getmusic = MutableLiveData<Resource<music_response>>()
    val Getmusic: LiveData<Resource<music_response>> get() = _Getmusic

    private val _userDetailsResponse =
        MutableStateFlow<ApiResult<UserDetailsResponse>>(ApiResult.Idle)
    val userDetailsResponse: StateFlow<ApiResult<UserDetailsResponse>> = _userDetailsResponse


    private val _login = MutableLiveData<Resource<login_response>>()
    val login: LiveData<Resource<login_response>> get() = _login


    private val _verifyotp = MutableLiveData<Resource<otp_response>>()
    val verifyotp: LiveData<Resource<otp_response>> get() = _verifyotp


    private val _submissionResult = MutableLiveData<Response<QuestionAnswerResponse>>()
    val submissionResult: LiveData<Response<QuestionAnswerResponse>> = _submissionResult



    fun userDetails() {
        safeFlowApiCall(_userDetailsResponse) {
            val response = repository.hitUserDetails()
            if (response.isSuccessful) response.body()!!
            else throw kotlin.Exception(response.message())
        }
    }

    fun submitAllAnswers(user: String? = null) {
        val request = AnswerCollector.getFinalRequest()

        viewModelScope.launch {
            try {
                val response = repository.NewSubmitAnswer(user, request)
                _submissionResult.postValue(response)

                if (response.isSuccessful) {
                    AnswerCollector.clearAnswers()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun loadQuestionData() {

        viewModelScope.launch {
            _dataQuestion.postValue(Resource.Loading()) // Indicate loading state

            try {
                val response = repository.fetchQuestionData()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _dataQuestion.postValue(Resource.Success(body))
                    } else {
                        _dataQuestion.postValue(Resource.Error("Empty response from server"))
                    }
                } else {
                    _dataQuestion.postValue(
                        Resource.Error("Failed with code ${response.code()}: ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                _dataQuestion.postValue(Resource.Error("Exception: ${e.localizedMessage ?: "Unknown error"}"))
            }
        }
    }


    fun FormData(
        fullName: String,
        age: Int,
        gender: String,
        location: String,
        type: String,
        phoneNumber: String? = "",
        latitude: String? = "",
        longitude: String? = "",
        fcmtoken: String
    ) {

        val request = FormRequest(
            fullName = fullName,
            age = age,
            gender = gender,
            location = location,
            type = type,
            phoneNumber = phoneNumber,
            longitude = longitude,
            latitude = latitude,
            fcmToken = fcmtoken
        )

        viewModelScope.launch {
            try {
                val response = repository.FormData(request)

                if (response.isSuccessful && response.body() != null) {
                    _formResponse.postValue(response.body())
                } else {
                    // Try to parse the error body (usually JSON)
                    val errorJson = response.errorBody()?.string()

                    if (!errorJson.isNullOrEmpty()) {
                        try {
                            val gson = com.google.gson.Gson()
                            val errorResponse = gson.fromJson(errorJson, FormResponse::class.java)

                            _formResponse.postValue(errorResponse)

                        } catch (jsonException: Exception) {
                            jsonException.printStackTrace()
                            _formResponse.postValue(
                                FormResponse(
                                    success = false,
                                    msg = "Unexpected error format",
                                    isRegister = false,
                                    isAssessmentComplete = false,

                                    )
                            )
                        }
                    } else {
                        _formResponse.postValue(
                            FormResponse(
                                success = false,
                                msg = "Unknown error",
                                isRegister = false,
                                isAssessmentComplete = false
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _formResponse.postValue(
                    FormResponse(
                        success = false,
                        msg = "Network error: ${e.localizedMessage}",
                        isRegister = false,
                        isAssessmentComplete = false
                    )
                )
            }
        }

    }


    fun NewProgress() {
        viewModelScope.launch {
            _Newprogress.postValue(Resource.Loading()) // Indicate loading state

            try {
                val response = repository.NewProgress()

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _Newprogress.postValue(Resource.Success(body))
                    } else {
                        _Newprogress.postValue(Resource.Error("Empty response from server"))
                    }
                } else {
                    _Newprogress.postValue(
                        Resource.Error("Failed with code ${response.code()}: ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _Newprogress.postValue(Resource.Error("Exception: ${e.localizedMessage ?: "Unknown error"}"))
            }
        }
    }


    fun AlzimerDetails() {
        viewModelScope.launch {
            _AlzimerDetails.postValue(Resource.Loading()) // Indicate loading state
            try {
                val response = repository.AlzimerDetails()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _AlzimerDetails.postValue(Resource.Success(body))
                    } else {
                        _AlzimerDetails.postValue(Resource.Error("Empty response from server"))
                    }
                } else {
                    _AlzimerDetails.postValue(
                        Resource.Error("Failed with code ${response.code()}: ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _AlzimerDetails.postValue(Resource.Error("Exception: ${e.localizedMessage ?: "Unknown error"}"))
            }
        }
    }


    fun Addcaregiver(
        fullName: String,
        phonenumber: String,
        address: String,
        relation: String,
        patientid: String,
        fcmtoken: String,
        imageUri: Uri?,
        context: Context
    ) {
        val namePart = fullName.toRequestBody("text/plain".toMediaTypeOrNull())
        val phonePart = phonenumber.toRequestBody("text/plain".toMediaTypeOrNull())
        val addressPart = address.toRequestBody("text/plain".toMediaTypeOrNull())
        val relationPart = relation.toRequestBody("text/plain".toMediaTypeOrNull())
        val patientIdPart = patientid.toRequestBody("text/plain".toMediaTypeOrNull())
        val fcmTokenPart = fcmtoken.toRequestBody("text/plain".toMediaTypeOrNull())

        var imagePart: MultipartBody.Part? = null

        if (imageUri != null) {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(imageUri)
            val file = File.createTempFile("temp_image", ".jpg", context.cacheDir)
            inputStream?.use { input -> file.outputStream().use { output -> input.copyTo(output) } }

            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)
        }

        viewModelScope.launch {
            _addcaregiver.postValue(Resource.Loading())

            try {
                val response = repository.Addcaregiver(
                    namePart,
                    phonePart,
                    addressPart,
                    relationPart,
                    patientIdPart,
                    fcmTokenPart,
                    imagePart
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _addcaregiver.postValue(Resource.Success(body))
                    } else {
                        _addcaregiver.postValue(Resource.Error("Empty response from server"))
                    }
                } else {
                    _addcaregiver.postValue(
                        Resource.Error("Failed with code ${response.code()}: ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _addcaregiver.postValue(
                    Resource.Error("Network error: ${e.localizedMessage ?: "Unknown error"}")
                )
            }
        }
    }


    fun getallcaregiver() {
        viewModelScope.launch {
            _GetAllCaregiver.postValue(Resource.Loading()) // Indicate loading state

            try {
                val response = repository.GetAllCaregiver()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _GetAllCaregiver.postValue(Resource.Success(body))
                    } else {
                        _GetAllCaregiver.postValue(Resource.Error("Empty response from server"))
                    }
                } else {
                    _GetAllCaregiver.postValue(
                        Resource.Error("Failed with code ${response.code()}: ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _GetAllCaregiver.postValue(Resource.Error("Network error: ${e.localizedMessage ?: "Unknown error"}"))
            }
        }
    }


    fun CaregiverDetails(id: String) {
        viewModelScope.launch {
            _CaregiverDetails.postValue(Resource.Loading()) // Indicate loading state

            try {
                val response = repository.CaregiverDetails(id)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _CaregiverDetails.postValue(Resource.Success(body))
                    } else {
                        _CaregiverDetails.postValue(Resource.Error("Empty response from server"))
                    }
                } else {
                    _CaregiverDetails.postValue(
                        Resource.Error("Failed with code ${response.code()}: ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _CaregiverDetails.postValue(Resource.Error("Network error: ${e.localizedMessage ?: "Unknown error"}"))
            }
        }
    }


    fun UpdateCaregiverDetails(
        context: Context,
        id: String,
        fullName: String,
        phonenumber: String,
        address: String,
        relation: String,
        patientId: String,
        fcmtoken: String,
        imageUri: Uri?
    ) {
        val namePart = fullName.toRequestBody("text/plain".toMediaTypeOrNull())
        val phonePart = phonenumber.toRequestBody("text/plain".toMediaTypeOrNull())
        val addressPart = address.toRequestBody("text/plain".toMediaTypeOrNull())
        val relationPart = relation.toRequestBody("text/plain".toMediaTypeOrNull())
        val patientIdPart = patientId.toRequestBody("text/plain".toMediaTypeOrNull())
        val fcmTokenPart = fcmtoken.toRequestBody("text/plain".toMediaTypeOrNull())

        var imagePart: MultipartBody.Part? = null

        if (imageUri != null) {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(imageUri)
            val file = File.createTempFile("updated_image", ".jpg", context.cacheDir)
            inputStream?.use { input -> file.outputStream().use { output -> input.copyTo(output) } }

            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)
        }

        viewModelScope.launch {
            _UpdateCaregiverDetails.postValue(Resource.Loading()) // Indicate loading state

            try {
                val response = repository.UpdateCaregiverDetails(
                    id = id,
                    name = namePart,
                    phoneNumber = phonePart,
                    address = addressPart,
                    relation = relationPart,
                    patientId = patientIdPart,
                    fcmToken = fcmTokenPart,
                    image = imagePart
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _UpdateCaregiverDetails.postValue(Resource.Success(body))
                    } else {
                        _UpdateCaregiverDetails.postValue(Resource.Error("Empty response from server"))
                    }
                } else {
                    _UpdateCaregiverDetails.postValue(
                        Resource.Error("Failed with code ${response.code()}: ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _UpdateCaregiverDetails.postValue(
                    Resource.Error("Network error: ${e.localizedMessage ?: "Unknown error"}")
                )
            }
        }
    }


    fun DeleteCaregiverdetails(id: String) {
        viewModelScope.launch {
            _DeleteCaregiver.postValue(Resource.Loading()) // Indicate loading state

            try {
                val response = repository.DeleteCaregiver(id)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _DeleteCaregiver.postValue(Resource.Success(body))
                    } else {
                        _DeleteCaregiver.postValue(Resource.Error("Empty response from server"))
                    }
                } else {
                    _DeleteCaregiver.postValue(
                        Resource.Error("Failed with code ${response.code()}: ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _DeleteCaregiver.postValue(
                    Resource.Error("Network error: ${e.localizedMessage ?: "Unknown error"}")
                )
            }
        }
    }


    fun UpdateAlzheimerDetails(
        id: String,
        fullName: String,
        age: Int,
        gender: String,
        phoneNumber: String,
        imageUri: Uri?,
        context: Context
    ) {
        val fullNamePart = fullName.toRequestBody("text/plain".toMediaTypeOrNull())
        val agePart = age.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val genderPart = gender.toRequestBody("text/plain".toMediaTypeOrNull())
        val phonePart = phoneNumber.toRequestBody("text/plain".toMediaTypeOrNull())

        var imagePart: MultipartBody.Part? = null
        if (imageUri != null) {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(imageUri)
            val file = File.createTempFile("alzheimer_image", ".jpg", context.cacheDir)
            inputStream?.use { input -> file.outputStream().use { output -> input.copyTo(output) } }

            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)
        }

        viewModelScope.launch {
            _UpdateAlzheimerDetails.postValue(Resource.Loading()) // Indicate loading state

            try {
                val response = repository.UpdateAlzheimerDetails(

                    id,
                    fullNamePart,
                    agePart,
                    genderPart,
                    phonePart,
                    imagePart
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _UpdateAlzheimerDetails.postValue(Resource.Success(body))
                    } else {
                        _UpdateAlzheimerDetails.postValue(Resource.Error("Empty response from server"))
                    }
                } else {
                    _UpdateAlzheimerDetails.postValue(
                        Resource.Error("Failed with code ${response.code()}: ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _UpdateAlzheimerDetails.postValue(
                    Resource.Error("Network error: ${e.localizedMessage ?: "Unknown error"}")
                )
            }
        }
    }


    fun Updateradius(id: String, radius: Double) {
        val request = updateradius_request(radius = radius)

        viewModelScope.launch {
            _Updateradius.postValue(Resource.Loading()) // Indicate loading state

            try {
                val response = repository.Updateradius(id, request)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _Updateradius.postValue(Resource.Success(body))
                    } else {
                        _Updateradius.postValue(Resource.Error("Empty response from server"))
                    }
                } else {
                    _Updateradius.postValue(
                        Resource.Error("Failed with code ${response.code()}: ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _Updateradius.postValue(
                    Resource.Error("Network error: ${e.localizedMessage ?: "Unknown error"}")
                )
            }
        }
    }


    fun GetallDoctors(
        specialist: String? = null,
        name: String? = null,
        patientId: String
    ) {
        viewModelScope.launch {
            _GetallDoctor.postValue(Resource.Loading()) // Indicate loading state

            try {
                val response = repository.GetallDoctors(specialist, name, patientId)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _GetallDoctor.postValue(Resource.Success(body))
                    } else {
                        _GetallDoctor.postValue(Resource.Error("Empty response from server"))
                    }
                } else {
                    _GetallDoctor.postValue(
                        Resource.Error("Failed with code ${response.code()}: ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _GetallDoctor.postValue(
                    Resource.Error("Network error: ${e.localizedMessage ?: "Unknown error"}")
                )
            }
        }
    }


    fun Createtodo(title: String) {
        val request = createtodos_request(title = title)

        viewModelScope.launch {
            _createtodo.postValue(Resource.Loading()) // Notify UI of loading

            try {
                val response = repository.Createtodos(request)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _createtodo.postValue(Resource.Success(body))
                    } else {
                        _createtodo.postValue(Resource.Error("Empty response from server"))
                    }
                } else {
                    _createtodo.postValue(
                        Resource.Error("Failed with code ${response.code()}: ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _createtodo.postValue(
                    Resource.Error("Network error: ${e.localizedMessage ?: "Unknown error"}")
                )
            }
        }
    }


    fun GetallTodos() {
        viewModelScope.launch {
            _GetallTodo.postValue(Resource.Loading()) // Notify UI of loading state

            try {
                val response = repository.GetallTodos()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _GetallTodo.postValue(Resource.Success(body))
                    } else {
                        _GetallTodo.postValue(Resource.Error("Empty response from server"))
                    }
                } else {
                    _GetallTodo.postValue(
                        Resource.Error("Failed with code ${response.code()}: ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _GetallTodo.postValue(
                    Resource.Error("Network error: ${e.localizedMessage ?: "Unknown error"}")
                )
            }
        }
    }


    fun Updatetodos(id: String, title: String, status: String) {
        val request = updatetodo_request(
            title = title,
            status = status
        )

        viewModelScope.launch {
            _Updatetodos.postValue(Resource.Loading()) // Notify UI that request is starting

            try {
                val response = repository.Updatetodos(id, request)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _Updatetodos.postValue(Resource.Success(body))
                    } else {
                        _Updatetodos.postValue(Resource.Error("Empty response from server"))
                    }
                } else {
                    _Updatetodos.postValue(
                        Resource.Error("Failed with code ${response.code()}: ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _Updatetodos.postValue(
                    Resource.Error("Network error: ${e.localizedMessage ?: "Unknown error"}")
                )
            }
        }
    }


    fun Deletetodos(todoId: String? = null) {
        viewModelScope.launch {
            _Deletetodos.postValue(Resource.Loading()) // Notify UI of loading state

            try {
                val response = repository.Deletetodos(todoId)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _Deletetodos.postValue(Resource.Success(body))
                    } else {
                        _Deletetodos.postValue(Resource.Error("Empty response from server"))
                    }
                } else {
                    _Deletetodos.postValue(
                        Resource.Error("Failed with code ${response.code()}: ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _Deletetodos.postValue(
                    Resource.Error("Network error: ${e.localizedMessage ?: "Unknown error"}")
                )
            }
        }
    }


    fun Bookappointment(
        Patientid: String,
        doctorsId: List<String>,
        appointmentDate: String,
        appointmentTime: String
    ) {
        val request = appointment_request(
            patientId = Patientid,
            doctorIds = doctorsId,
            appointmentDate = appointmentDate,
            appointmentTime = appointmentTime
        )

        viewModelScope.launch {
            _bookappointment.postValue(Resource.Loading()) // Notify UI that loading has started

            try {
                val response = repository.Bookappointment(request)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _bookappointment.postValue(Resource.Success(body))
                    } else {
                        _bookappointment.postValue(Resource.Error("Empty response from server"))
                    }
                } else {
                    _bookappointment.postValue(
                        Resource.Error("Failed with code ${response.code()}: ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _bookappointment.postValue(
                    Resource.Error("Network error: ${e.localizedMessage ?: "Unknown error"}")
                )
            }
        }
    }


    fun DoctorsDetails(id: String) {
        viewModelScope.launch {
            _Doctorsdetails.postValue(Resource.Loading()) // Notify UI of loading state

            try {
                val response = repository.DoctorsDetails(id)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _Doctorsdetails.postValue(Resource.Success(body))
                    } else {
                        _Doctorsdetails.postValue(Resource.Error("Empty response from server"))
                    }
                } else {
                    _Doctorsdetails.postValue(
                        Resource.Error("Failed with code ${response.code()}: ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _Doctorsdetails.postValue(
                    Resource.Error("Network error: ${e.localizedMessage ?: "Unknown error"}")
                )
            }
        }
    }


    fun Updatelocation(Patientid: String, latitude: String, longitude: String) {

        val request = updatelocation_request(
            patient_id = Patientid,
            lat = latitude,
            lng = longitude
        )

        viewModelScope.launch {
            val response = repository.Updatelocation(request)
            if (response.isSuccessful) {
                _updatelocation.postValue(response.body())
            } else {
                Log.e("POST_ERROR", "Code: ${response.code()} | Message: ${response.message()}")
            }
        }
    }


    fun Getallvideos(sort: String? = null, title: String? = null) {
        viewModelScope.launch {
            _Getallvideos.postValue(Resource.Loading()) // Notify UI of loading state

            try {
                val response = repository.GetallVideos(sort, title)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _Getallvideos.postValue(Resource.Success(body))
                    } else {
                        _Getallvideos.postValue(Resource.Error("Empty response from server"))
                    }
                } else {
                    _Getallvideos.postValue(
                        Resource.Error("Failed with code ${response.code()}: ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _Getallvideos.postValue(
                    Resource.Error("Network error: ${e.localizedMessage ?: "Unknown error"}")
                )
            }
        }
    }


    fun GetvideosDetails(id: String) {
        viewModelScope.launch {
            _GetvideosDetails.postValue(Resource.Loading()) // Notify UI that data is loading

            try {
                val response = repository.GetvideosDetails(id)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _GetvideosDetails.postValue(Resource.Success(body))
                    } else {
                        _GetvideosDetails.postValue(Resource.Error("Empty response from server"))
                    }
                } else {
                    _GetvideosDetails.postValue(
                        Resource.Error("Failed with code ${response.code()}: ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _GetvideosDetails.postValue(
                    Resource.Error("Network error: ${e.localizedMessage ?: "Unknown error"}")
                )
            }
        }
    }


    fun Getmusic() {
        viewModelScope.launch {
            _Getmusic.postValue(Resource.Loading()) // Notify UI that loading has started

            try {
                val response = repository.Getmusic()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _Getmusic.postValue(Resource.Success(body))
                    } else {
                        _Getmusic.postValue(Resource.Error("Empty response from server"))
                    }
                } else {
                    _Getmusic.postValue(
                        Resource.Error("Failed with code ${response.code()}: ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _Getmusic.postValue(
                    Resource.Error("Network error: ${e.localizedMessage ?: "Unknown error"}")
                )
            }
        }
    }


    fun Login(phonenumber: String) {
        val request = login_request(phoneNumber = phonenumber)

        viewModelScope.launch {
            _login.postValue(Resource.Loading()) // notify UI that loading started

            try {
                val response = repository.Login(request)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _login.postValue(Resource.Success(body))
                    } else {
                        _login.postValue(Resource.Error("Login response was empty"))
                    }
                }
            } catch (e: Exception) {
                _login.postValue(Resource.Error("Network error: ${e.localizedMessage ?: "Unknown error"}"))
            }
        }
    }


    fun Verifyotp(
        phonenumber: String,
        otp: String,
        latitude: String,
        longitude: String,
        fcmToken: String
    ) {
        val request = otp_request(
            phoneNumber = phonenumber,
            otp = otp,
            latitude = latitude,
            longitude = longitude,
            fcmToken = fcmToken

        )

        viewModelScope.launch {
            _verifyotp.postValue(Resource.Loading()) // Notify UI to show loading state

            try {
                val response = repository.VerifyOtp(request)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _verifyotp.postValue(Resource.Success(body))
                    } else {
                        _verifyotp.postValue(Resource.Error("Empty response from server"))
                    }
                } else {
                    _verifyotp.postValue(
                        Resource.Error("Failed with code ${response.code()}: ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _verifyotp.postValue(
                    Resource.Error("Network error: ${e.localizedMessage ?: "Unknown error"}")
                )
            }
        }
    }


    private val _createaccount = MutableLiveData<Resource<registration_response>>()
    val createaccount: LiveData<Resource<registration_response>> = _createaccount


    fun Createaccount(
        token: String,
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
    ) {
        viewModelScope.launch {
            _createaccount.postValue(Resource.Loading()) // Show loading state

            try {
                val response = repository.CreateAccount(
                    image, name, phoneNumber, email, dob,
                    latitude, longitude, gender, height, weight,
                    relativecontact, startDate, startTime, endTime, timeGap
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _createaccount.postValue(Resource.Success(body))
                    } else {
                        _createaccount.postValue(Resource.Error("Empty response from server"))
                    }
                } else {
                    _createaccount.postValue(
                        Resource.Error("Upload failed: ${response.code()} ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _createaccount.postValue(Resource.Error("Exception: ${e.localizedMessage ?: "Unknown error"}"))
            }
        }
    }


    private val _createalbum = MutableLiveData<Resource<updatecaregiver_response>>()
    val createalbum: LiveData<Resource<updatecaregiver_response>> = _createalbum

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error


    fun CreateAlbum(title: String, description: String, images: List<File>) {
        viewModelScope.launch {
            _createalbum.postValue(Resource.Loading()) // Show loading state

            try {
                val response = repository.CreateAlbum(title, description, images)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _createalbum.postValue(Resource.Success(body))
                    } else {
                        _createalbum.postValue(Resource.Error("Empty response from server"))
                    }
                } else {
                    _createalbum.postValue(
                        Resource.Error("Upload failed: ${response.code()} ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _createalbum.postValue(Resource.Error("Exception: ${e.localizedMessage ?: "Unknown error"}"))
            }
        }
    }


    private val _GetAlbumlist = MutableLiveData<Resource<albumlist_response>>()
    val GetAlbumlist: LiveData<Resource<albumlist_response>> get() = _GetAlbumlist


    fun GetAlbumlist() {
        viewModelScope.launch {
            _GetAlbumlist.postValue(Resource.Loading()) // Notify UI of loading state

            try {
                val response = repository.GetAlbumlist()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _GetAlbumlist.postValue(Resource.Success(body))
                    } else {
                        _GetAlbumlist.postValue(Resource.Error("Empty response from server"))
                    }
                } else {
                    _GetAlbumlist.postValue(
                        Resource.Error("Failed: ${response.code()} ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _GetAlbumlist.postValue(
                    Resource.Error("Exception: ${e.localizedMessage ?: "Unknown error"}")
                )
            }
        }
    }


    private val _GetAlbumDetail = MutableLiveData<Resource<SingleAlbumResponse>>()
    val GetAlbumDetail: LiveData<Resource<SingleAlbumResponse>> get() = _GetAlbumDetail


    fun GetAlbumDetails(id: String) {
        viewModelScope.launch {
            _GetAlbumDetail.postValue(Resource.Loading()) // Notify UI of loading state

            try {
                val response = repository.GetAlbumDetails(id)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _GetAlbumDetail.postValue(Resource.Success(body))
                    } else {
                        _GetAlbumDetail.postValue(Resource.Error("Empty response from server"))
                    }
                } else {
                    _GetAlbumDetail.postValue(
                        Resource.Error("Failed: ${response.code()} ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _GetAlbumDetail.postValue(
                    Resource.Error("Exception: ${e.localizedMessage ?: "Unknown error"}")
                )
            }
        }
    }


    private val _updatealbumdetail = MutableLiveData<Resource<updatecaregiver_response>>()
    val updatealbumdetail: LiveData<Resource<updatecaregiver_response>> = _updatealbumdetail


    fun UpdateAlbumDetails(id: String, title: String, description: String, images: List<File>) {
        viewModelScope.launch {
            _updatealbumdetail.postValue(Resource.Loading()) // Indicate loading

            try {
                val response = repository.UpdateAlbumDetails(id, title, description, images)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _updatealbumdetail.postValue(Resource.Success(body))
                    } else {
                        _updatealbumdetail.postValue(Resource.Error("Empty response from server"))
                    }
                } else {
                    _updatealbumdetail.postValue(
                        Resource.Error("Failed: ${response.code()} ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                _updatealbumdetail.postValue(
                    Resource.Error("Exception: ${e.localizedMessage ?: "Unknown error"}")
                )
            }
        }
    }

    private val _deletealbum = MutableLiveData<Resource<updatecaregiver_response>>()
    val deletealbum: LiveData<Resource<updatecaregiver_response>> = _deletealbum


    fun DeleteAlbum(id: String) {
        viewModelScope.launch {
            _deletealbum.postValue(Resource.Loading()) // Notify UI loading started

            try {
                val response = repository.DeleteAlbum(id)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _deletealbum.postValue(Resource.Success(body))
                    } else {
                        _deletealbum.postValue(Resource.Error("Empty response from server"))
                    }
                } else {
                    _deletealbum.postValue(
                        Resource.Error("Failed: ${response.code()} ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                _deletealbum.postValue(
                    Resource.Error("Exception: ${e.localizedMessage ?: "Unknown error"}")
                )
            }
        }
    }


    private val _creatememory = MutableLiveData<Resource<updatecaregiver_response>>()
    val creatememory: LiveData<Resource<updatecaregiver_response>> = _creatememory


    fun CreateMemory(title: String, thumbnail: File, color: String) {
        viewModelScope.launch {
            _creatememory.postValue(Resource.Loading()) // Notify UI of loading state

            try {
                val response = repository.CreateMemory(title, thumbnail, color)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _creatememory.postValue(Resource.Success(body))
                    } else {
                        _creatememory.postValue(Resource.Error("Empty response from server"))
                    }
                } else {
                    _creatememory.postValue(
                        Resource.Error("Failed: ${response.code()} ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                _creatememory.postValue(
                    Resource.Error("Exception: ${e.localizedMessage ?: "Unknown error"}")
                )
            }
        }
    }


    private val _GetMemorylist = MutableLiveData<Resource<memory_response>>()
    val GetMemorylist: LiveData<Resource<memory_response>> get() = _GetMemorylist


    fun GetMemorylist() {
        viewModelScope.launch {
            _GetMemorylist.postValue(Resource.Loading()) // Notify UI about loading state

            try {
                val response = repository.GetMemory()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _GetMemorylist.postValue(Resource.Success(body))
                    } else {
                        _GetMemorylist.postValue(Resource.Error("Empty response from server"))
                    }
                } else {
                    _GetMemorylist.postValue(
                        Resource.Error("Failed: ${response.code()} ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                _GetMemorylist.postValue(
                    Resource.Error("Exception: ${e.localizedMessage ?: "Unknown error"}")
                )
            }
        }
    }


    private val _createvoice = MutableLiveData<Resource<updatecaregiver_response>>()
    val createvoice: LiveData<Resource<updatecaregiver_response>> = _createvoice


    fun CreateVoice(
        userType: String,
        music: File
    ) {
        viewModelScope.launch {
            _createvoice.postValue(Resource.Loading()) // Notify UI about loading state

            try {

                val response = repository.CreateVoice(userType, music)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _createvoice.postValue(Resource.Success(body))
                    } else {
                        _createvoice.postValue(Resource.Error("Empty response from server"))
                    }
                } else {
                    _createvoice.postValue(
                        Resource.Error("Upload failed: ${response.code()} ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                _createvoice.postValue(
                    Resource.Error("Exception: ${e.localizedMessage ?: "Unknown error"}")
                )
            }
        }
    }


    private val _GetVoicelist = MutableLiveData<Resource<voice_response>>()
    val GetVoicelist: LiveData<Resource<voice_response>> get() = _GetVoicelist


    fun GetVoicelist() {
        viewModelScope.launch {
            _GetVoicelist.postValue(Resource.Loading())

            try {

                val response = repository.GetVoicelist()

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _GetVoicelist.postValue(Resource.Success(body))
                    } else {
                        _GetVoicelist.postValue(Resource.Error("Empty response from server"))
                    }
                } else {
                    _GetVoicelist.postValue(
                        Resource.Error("Failed: ${response.code()} ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                _GetVoicelist.postValue(
                    Resource.Error("Exception: ${e.localizedMessage ?: "Unknown error"}")
                )
            }
        }
    }


    private val _createmood = MutableLiveData<Resource<updatecaregiver_response>>()
    val createmood: LiveData<Resource<updatecaregiver_response>> get() = _createmood


    fun CreateMood(title: String, description: String) {
        val request = mood_request(
            title = title,
            description = description
        )

        viewModelScope.launch {
            _createmood.postValue(Resource.Loading())

            try {

                val response = repository.Createmood(request)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _createmood.postValue(Resource.Success(body))
                    } else {
                        _createmood.postValue(Resource.Error("Empty response from server"))
                    }
                } else {
                    _createmood.postValue(
                        Resource.Error("Failed: ${response.code()} ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                _createmood.postValue(
                    Resource.Error("Exception: ${e.localizedMessage ?: "Unknown error"}")
                )
            }
        }
    }


    private val _GetMood = MutableLiveData<mood_response>()
    val GetMood: LiveData<mood_response> get() = _GetMood


    fun GetMood(user: String? = null) {

        viewModelScope.launch {
            val response = repository.GetMood(user)
            if (response.isSuccessful) {
                _GetMood.postValue(response.body())
            } else {
                // handle error

                Log.e("API_ERROR", "Code: ${response.code()} | Message: ${response.message()}")
            }
        }
    }


    private val _createmessage = MutableLiveData<Resource<updatecaregiver_response>>()
    val createmessage: LiveData<Resource<updatecaregiver_response>> get() = _createmessage


    fun CreateMessage(message: String, scheduleTime: String) {
        viewModelScope.launch {
            _createmessage.postValue(Resource.Loading())

            try {


                val request = createmessage_request(
                    message = message,
                    scheduleTime = scheduleTime
                )

                val response = repository.Createmessage(request)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _createmessage.postValue(Resource.Success(body))
                    } else {
                        _createmessage.postValue(Resource.Error("Empty response from server"))
                    }
                } else {
                    _createmessage.postValue(
                        Resource.Error("Error ${response.code()}: ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                _createmessage.postValue(
                    Resource.Error("Exception: ${e.localizedMessage ?: "Unexpected error"}")
                )
            }
        }
    }


    private val _getmessage = MutableLiveData<Resource<getmessage_response>>()
    val getmessage: LiveData<Resource<getmessage_response>> = _getmessage


    fun Getmessage() {
        viewModelScope.launch {
            _getmessage.postValue(Resource.Loading())

            try {

                val response = repository.Getmessage()

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _getmessage.postValue(Resource.Success(body))
                    } else {
                        _getmessage.postValue(Resource.Error("Empty response from server"))
                    }
                } else {
                    _getmessage.postValue(
                        Resource.Error("Error ${response.code()}: ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                _getmessage.postValue(
                    Resource.Error("Exception: ${e.localizedMessage ?: "Unexpected error"}")
                )
            }
        }
    }


    private val _gettasklist = MutableLiveData<Resource<tasklist_response>>()
    val gettasklist: LiveData<Resource<tasklist_response>> = _gettasklist


    fun GetTasklist() {
        viewModelScope.launch {
            _gettasklist.postValue(Resource.Loading())

            try {

                val response = repository.GetTasklist()

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _gettasklist.postValue(Resource.Success(body))
                    } else {
                        _gettasklist.postValue(Resource.Error("Empty response from server"))
                    }
                } else {
                    _gettasklist.postValue(Resource.Error("Error ${response.code()}: ${response.message()}"))
                }
            } catch (e: Exception) {
                _gettasklist.postValue(Resource.Error("Exception: ${e.localizedMessage ?: "Unknown error"}"))
            }
        }
    }

    private val _activitysubmit = MutableLiveData<Resource<updatecaregiver_response>>()
    val activitysubmit: LiveData<Resource<updatecaregiver_response>> = _activitysubmit


    fun ActivitySubmit(
        title: String,
        music: File?,            // Nullable
        thumbnail: List<File>?,        // Nullable
        isCompleted: Boolean,
        activityId: String
    ) {
        viewModelScope.launch {
            _activitysubmit.postValue(Resource.Loading())

            try {


                val response = repository.ActivitySubmit(

                    title = title,
                    music = music,
                    thumbnail = thumbnail,
                    isCompleted = isCompleted,
                    activityId = activityId
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _activitysubmit.postValue(Resource.Success(body))
                    } else {
                        _activitysubmit.postValue(Resource.Error("Empty response body"))
                    }
                } else {
                    _activitysubmit.postValue(Resource.Error("Upload failed: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                _activitysubmit.postValue(Resource.Error("Exception: ${e.localizedMessage ?: "Unknown error"}"))
            }
        }
    }


    private val _getactivityresult = MutableLiveData<Resource<getActivity_response>>()
    val getactivityresult: LiveData<Resource<getActivity_response>> = _getactivityresult


    fun GetActivityResult(user: String? = null) {
        viewModelScope.launch {
            _getactivityresult.postValue(Resource.Loading())

            try {


                val response = repository.GetActivityResult(user)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _getactivityresult.postValue(Resource.Success(body))
                    } else {
                        _getactivityresult.postValue(Resource.Error("Empty response body"))
                    }
                } else {
                    _getactivityresult.postValue(Resource.Error("API failed: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                _getactivityresult.postValue(Resource.Error("Exception: ${e.localizedMessage ?: "Unknown error"}"))
            }
        }
    }


    private val _getuserdetails = MutableLiveData<Resource<getuserdetail_response>>()
    val getuserdetails: LiveData<Resource<getuserdetail_response>> = _getuserdetails


    fun GetUserDetails() {
        viewModelScope.launch {
            _getuserdetails.postValue(Resource.Loading())

            try {

                val response = repository.GetUserDetails()

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _getuserdetails.postValue(Resource.Success(body))
                    } else {
                        _getuserdetails.postValue(Resource.Error("Empty response body"))
                    }
                } else {
                    _getuserdetails.postValue(Resource.Error("API failed: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                _getuserdetails.postValue(Resource.Error("Exception: ${e.localizedMessage ?: "Unknown error"}"))
            }
        }
    }


    private val _getpatientlist = MutableLiveData<Resource<PatientListResponse>>()
    val getpatientlist: LiveData<Resource<PatientListResponse>> = _getpatientlist


    fun GetPatientList() {
        viewModelScope.launch {
            _getpatientlist.postValue(Resource.Loading())

            try {


                val response = repository.GetPatientlist()

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _getpatientlist.postValue(Resource.Success(body))
                    } else {
                        _getpatientlist.postValue(Resource.Error("Empty response body"))
                    }
                } else {
                    _getpatientlist.postValue(Resource.Error("API failed: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                _getpatientlist.postValue(Resource.Error("Exception: ${e.localizedMessage ?: "Unknown error"}"))
            }
        }
    }


    private val _getalzimerdetails = MutableLiveData<Resource<getalzimerdetailbyid>>()
    val getalzimerdetails: LiveData<Resource<getalzimerdetailbyid>> = _getalzimerdetails


    fun GetAlzimerbyid(id: String) {
        viewModelScope.launch {
            _getalzimerdetails.postValue(Resource.Loading())

            try {


                val response = repository.GetAlzimerbyid(id)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _getalzimerdetails.postValue(Resource.Success(body))
                    } else {
                        _getalzimerdetails.postValue(Resource.Error("Empty response body"))
                    }
                } else {
                    _getalzimerdetails.postValue(Resource.Error("Error ${response.code()}: ${response.message()}"))
                }
            } catch (e: Exception) {
                _getalzimerdetails.postValue(Resource.Error("Exception: ${e.localizedMessage ?: "Unknown error"}"))
            }
        }
    }


    private val _getcaregiverdetails = MutableLiveData<Resource<getcaregiverbyidresponse>>()
    val getcaregiverdetails: LiveData<Resource<getcaregiverbyidresponse>> = _getcaregiverdetails


    fun GetCaregiverbyid(id: String) {
        viewModelScope.launch {
            _getcaregiverdetails.postValue(Resource.Loading())

            try {


                val response = repository.GetCaregiverbyid(id)

                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        _getcaregiverdetails.postValue(Resource.Success(responseBody))
                    } ?: run {
                        _getcaregiverdetails.postValue(Resource.Error("Empty response body"))
                    }
                } else {
                    _getcaregiverdetails.postValue(Resource.Error("Error ${response.code()}: ${response.message()}"))
                }
            } catch (e: Exception) {
                _getcaregiverdetails.postValue(Resource.Error("Exception: ${e.localizedMessage ?: "Unknown error"}"))
            }
        }
    }


    private val _getalzimerlocation = MutableLiveData<Resource<Getalzimerlocation_response>>()
    val getalzimerlocation: LiveData<Resource<Getalzimerlocation_response>> = _getalzimerlocation


    fun GetAlzimerLocation(alzheimerId: String? = null) {
        viewModelScope.launch {
            _getalzimerlocation.postValue(Resource.Loading())

            try {


                val response = repository.GetAlzimerlocation(alzheimerId)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _getalzimerlocation.postValue(Resource.Success(body))
                    } else {
                        _getalzimerlocation.postValue(Resource.Error("Empty response body"))
                    }
                } else {
                    _getalzimerlocation.postValue(Resource.Error("API failed: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                _getalzimerlocation.postValue(Resource.Error("Exception: ${e.localizedMessage ?: "Unknown error"}"))
            }
        }
    }


    private val _getradius = MutableLiveData<Resource<RadiusResponse>>()
    val getradius: LiveData<Resource<RadiusResponse>> = _getradius


    fun Getradiussafezone(id: String) {
        viewModelScope.launch {
            _getradius.postValue(Resource.Loading())

            try {


                val response = repository.Getradiussafezone(id)

                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        _getradius.postValue(Resource.Success(responseBody))
                    } ?: run {
                        _getradius.postValue(Resource.Error("Empty response body"))
                    }
                } else {
                    _getradius.postValue(Resource.Error("Error ${response.code()}: ${response.message()}"))
                }
            } catch (e: Exception) {
                _getradius.postValue(Resource.Error("Exception: ${e.localizedMessage ?: "Unknown error"}"))
            }
        }
    }


    private val _sosnotification = MutableLiveData<Resource<updatecaregiver_response>>()
    val sosnotification: LiveData<Resource<updatecaregiver_response>> = _sosnotification


    fun SoSNotification() {
        viewModelScope.launch {
            _sosnotification.postValue(Resource.Loading())

            try {


                val response = repository.SoSNotification()

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _sosnotification.postValue(Resource.Success(body))
                    } else {
                        _sosnotification.postValue(Resource.Error("Empty response from server"))
                    }
                } else {
                    _sosnotification.postValue(
                        Resource.Error("Error ${response.code()}: ${response.message()}")
                    )
                }
            } catch (e: Exception) {
                _sosnotification.postValue(
                    Resource.Error("Exception: ${e.localizedMessage ?: "Unexpected error"}")
                )
            }
        }
    }
}