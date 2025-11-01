package com.careavatar.core_model.alzimer

data class QuestionListNewResponse(
    val msg: String,
    val data: List<QuestionData>,
    val success: Boolean
)

data class QuestionData(
    val _id: String,
    val questionCategory: QuestionCategoryNew,
    val index: Int,
    val question: String,
    val points: Int,
    val instruction: String?, // nullable
    val image: Map<String, String>,
    val correctObject: Map<String, String>,
    val options: Map<String, String>,
    val __v: Int
)

data class QuestionCategoryNew(
    val _id: String,
    val name: String
)
