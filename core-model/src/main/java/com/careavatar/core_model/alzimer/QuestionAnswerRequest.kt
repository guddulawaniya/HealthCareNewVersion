package com.careavatar.core_model.alzimer

data class QuestionAnswerRequest(
    val answers: List<AnswerRequest>
)

data class AnswerRequest(
    val question: String,
    val isCorrect: Boolean,
    val points: Int,
    val isSkipped: Boolean? = null // optional field
)