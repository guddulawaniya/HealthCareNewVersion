package com.careavatar.core_model.alzimer

data class mood_response(

    val success: Boolean,
    val msg: String,
    val data: MoodData
)


data class MoodData(
    val _id: String,
    val userId: String,
    val title: String,
    val description: String,
    val isComplete: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int
)