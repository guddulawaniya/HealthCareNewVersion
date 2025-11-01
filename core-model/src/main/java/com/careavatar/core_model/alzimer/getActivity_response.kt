package com.careavatar.core_model.alzimer

data class getActivity_response(
    val success: Boolean,
    val data: List<ImageTaskData> // Replace `Any` with the actual data type if known
)

data class ImageTaskData(
    val _id: String,
    val user: String,
    val activityId: ActivityDetails,
    val music: String,
    val thumbnail: List<String>,
    val title: String,
    val isCompleted: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int
)



data class ActivityDetails(
    val _id: String,
    val title: String
)