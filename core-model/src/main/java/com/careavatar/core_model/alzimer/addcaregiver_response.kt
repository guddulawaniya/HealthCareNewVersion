package com.careavatar.core_model.alzimer

data class addcaregiver_response(
    val success: Boolean,
    val msg: String,
    val isRegister: Boolean,
    val isAssessmentComplete: Boolean,
    val caregiverId: String
)
