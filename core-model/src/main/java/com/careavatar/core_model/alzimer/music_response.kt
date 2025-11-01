package com.careavatar.core_model.alzimer

data class music_response(

    val success: Boolean,
    val msg: String,
    val data: List<MeditationItem>
)

data class MeditationItem(
    val _id: String,
    val title: String,
    val musicUrl: String,
    val thumbnailUrl: String,
    val backgroundImageUrl: String,
    val duration: Int,
    val isDefault: Boolean,
    val __v: Int
)