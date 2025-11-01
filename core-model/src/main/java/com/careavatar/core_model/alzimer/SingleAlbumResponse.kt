package com.careavatar.core_model.alzimer

data class SingleAlbumResponse(
    val success: Boolean,
    val msg: String,
    val data: Album
)

data class Album(
    val _id: String,
    val userId: String,
    val thumbnail: List<String>,
    val title: String,
    val description: String,
    val __v: Int
)
