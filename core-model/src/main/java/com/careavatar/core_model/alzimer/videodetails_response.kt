package com.careavatar.core_model.alzimer

data class videodetails_response(

    val success: Boolean,
    val msg: String,
    val video: VideoDetails,
    val totalViews: Int
)

data class VideoDetails(
    val _id: String,
    val title: String,
    val video: String,
    val thumbnail: String,
    val uploadedBy: String,
    val viewers: List<String>,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int
)
