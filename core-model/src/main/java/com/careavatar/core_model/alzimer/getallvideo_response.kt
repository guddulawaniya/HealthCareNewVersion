package com.careavatar.core_model.alzimer

data class getallvideo_response(
    val success: Boolean,
    val msg: String,
    val data: List<VideoItem>
)
data class VideoItem(
    val _id: String,
    val title: String,
    val video: String,
    val thumbnail: String,
    val uploadedBy: String,
    val createdAt: String,
    val totalViews: Int
)