package com.careavatar.core_model.alzimer

data class addcaregiver_request(

    val name: String,
    val phoneNumber: String,
    val address: String,
    val relation: String,
    val patientId: String? = "",
    val fcmToken: String? = "",
    val image: String? = ""
)
