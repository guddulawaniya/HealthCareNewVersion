package com.careavatar.core_model.alzimer

data class QuestionAnswerResponse(

    val msg: String,
    val data: ResponseData,
    val success: Boolean
)

data class ResponseData(
    val user: String,
    val answers: List<Answernew>,
    val totalQuestions: Int,
    val attemptQuestion: Int,
    val correct: Int,
    val wrong: Int,
    val skipped: Int,
    val points: Int,
    val totalPoints: Int,
    val submittedAt: String,
    val _id: String,
    val __v: Int
)


data class Answernew(
    val question: String,
    val isCorrect: Boolean,
    val points: Int,
    val takenTime: String,
    val isSkipped: Boolean,
    val _id: String
)
