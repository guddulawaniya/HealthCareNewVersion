package com.careavatar.core_model.alzimer

data class albumlist_response(
    val success: Boolean,
    val msg: String,
    val data: List<AlbumData>
)

data class AlbumData(
    val _id: String,
    val userId: String,
    val thumbnail: List<String>,
    val title: String,
    val description: String,
    val __v: Int
)
